package com.Meta_learning.course.courseservice;

import com.Meta_learning.course.coursecontroller.dto.response.CourseDescriptFileUpdateResponse;
import com.Meta_learning.course.coursecontroller.dto.update.CourseDescriptUpdateRequestDTO;
import com.Meta_learning.course.courseentity.CourseDescriptEntity;
import com.Meta_learning.course.courseentity.CourseDescriptFileEntity;
import com.Meta_learning.course.courseentity.CourseEntity;
import com.Meta_learning.course.courserepository.CourseDescriptFileRepository;
import com.Meta_learning.course.courserepository.CourseDescriptRepository;
import com.Meta_learning.course.courseservice.requset.CourseDescriptCreateServiceRequest;
import com.Meta_learning.course.courseservice.requset.CourseDescriptUpdateServiceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseDescriptServiceImpl implements CourseDescriptService{

    private final CourseDescriptRepository courseDescriptRepository;
    private final CourseDescriptFileRepository courseDescriptFileRepository;

    @Override
    // 2. 강의로 강의 설명 조회
    public CourseDescriptEntity getCourseDescript(CourseEntity courseEntity) {
        return courseDescriptRepository.findByCourse(courseEntity)
                .orElseThrow(() -> new IllegalArgumentException("강의 설명을 찾을 수 없습니다. Course ID: " + courseEntity.getCourseId()));
    }

    @Override
    // 3. 강의설명으로 강의설명파일 조회
    public List<CourseDescriptFileEntity> getCourseDescriptFiles(CourseDescriptEntity courseDescript) {
        return courseDescriptFileRepository.findByCourseDescript(courseDescript);
    }

    @Override
    public List<CourseDescriptFileUpdateResponse> convertDescriptFilesToFileNames(List<CourseDescriptFileEntity> descriptFileEntities) {
        // CourseDescriptFileEntity에서 필요한 정보를 추출하여 CourseDescriptFileUpdateResponse로 변환
        return descriptFileEntities.stream()
                .map(entity -> CourseDescriptFileUpdateResponse.builder()
                        .courseDescriptFileId(entity.getCourseDescriptFileId()) // Entity에서 ID 추출
                        .courseDescriptFiles(entity.getCourseDescriptFileUUID()) // Entity에서 파일 UUID 추출
                        .build())
                .collect(Collectors.toList());
    }


//    @Override
//    public MultipartFile convertFileToMultipart(String filePath) {
//        try {
//            Path path = Paths.get(filePath);
//            String fileName = path.getFileName().toString();
//            String contentType = Files.probeContentType(path); // MIME 타입 추출
//            byte[] content = Files.readAllBytes(path);
//
//            return new MockMultipartFile(fileName, fileName, contentType, content);
//        } catch (IOException e) {
//            throw new RuntimeException("파일을 MultipartFile로 변환하는 데 실패했습니다: " + filePath, e);
//        }
//    }
//
//    @Override
//    public List<MultipartFile> convertDescriptFilesToMultipart(List<CourseDescriptFileEntity> descriptFileEntities) {
//        String basePath = "src/main/resources/static/uploads/course/";
//        return descriptFileEntities.stream()
//                .map(fileEntity -> {
//                    String filePath = basePath + fileEntity.getCourseDescriptFileUUID();
//                    return convertFileToMultipart(filePath);
//                })
//                .collect(Collectors.toList());
//    }

    @Override
    @Transactional
    public CourseDescriptEntity saveCourseDescript(CourseDescriptCreateServiceRequest request) {
        // 1. CourseDescriptEntity 생성 및 저장
        CourseDescriptEntity courseDescriptEntity = CourseDescriptEntity.builder()
                .course(request.getCourseEntity())
                .courseDescriptContent(request.getCourseDescriptContent())
                .build();

        CourseDescriptEntity savedDescript = courseDescriptRepository.save(courseDescriptEntity);

        // 2. 파일 저장 경로 설정
        String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/static/uploads/course";
        Path uploadPath = Paths.get(uploadDir);

        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 3. CourseDescriptFileEntity 생성 및 저장
            for (MultipartFile file : request.getCourseDescriptFiles()) {
                if (file.isEmpty()) {
//                    log.warn("빈 파일이 업로드 요청됨: {}", file.getOriginalFilename());
                    continue;
                }

                String originalFileName = file.getOriginalFilename();
                String uuidFileName = UUID.randomUUID() + "_" + originalFileName;
                Path filePath = uploadPath.resolve(uuidFileName);

                // 파일 저장
                Files.write(filePath, file.getBytes());

                // 파일 엔티티 생성
                CourseDescriptFileEntity fileEntity = CourseDescriptFileEntity.builder()
                        .courseDescript(savedDescript)
                        .courseDescriptFileName(originalFileName)
                        .courseDescriptFileUUID(uuidFileName)
                        .courseDescriptFileSize(file.getSize())
                        .courseDescriptFileType(file.getContentType())
                        .build();

                // 파일 엔티티 저장
                courseDescriptFileRepository.save(fileEntity);
            }
        } catch (IOException e) {
//            log.error("파일 저장 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
        return savedDescript;
    }

    @Transactional
    @Override
    public CourseDescriptEntity updateCourseDescript(CourseDescriptUpdateRequestDTO request) {
        // 1. 강의 설명 정보 조회
        CourseDescriptEntity existingDescript = courseDescriptRepository.findById(request.getCourseDescriptId())
                .orElseThrow(() -> new IllegalArgumentException("해당 강의에 대한 설명이 없습니다."));

        // 2. 강의 설명 정보 업데이트
        existingDescript.updateCourseDescriptContent(request.getCourseDescriptContent());

        // 3. 삭제 요청된 파일 ID 로그 출력
//        log.info("삭제 요청된 파일 ID 목록: {}", request.getFilesToDelete());

        // 3. 기존 파일 삭제 처리
        if (request.getFilesToDelete() != null && !request.getFilesToDelete().isEmpty()) {
            // 중복 제거
            Set<Long> uniqueFileIds = new HashSet<>(request.getFilesToDelete());
//            log.info("중복 제거된 파일 ID 목록: {}", uniqueFileIds);

//            for (Long fileId : request.getFilesToDelete()) {
//                CourseDescriptFileEntity fileEntity = courseDescriptFileRepository.findById(fileId)
//                        .orElseThrow(() -> new IllegalArgumentException("삭제하려는 파일을 찾을 수 없습니다. ID: " + fileId));
//
//                // 실제 파일 삭제
//                Path filePath = Paths.get("src/main/resources/static/uploads/course", fileEntity.getCourseDescriptFileUUID());
//                try {
//                    Files.deleteIfExists(filePath);
//                } catch (IOException e) {
//                    log.error("파일 삭제 중 오류 발생: {}", e.getMessage());
//                }
//
//                // 데이터베이스에서 파일 엔티티 삭제
//                courseDescriptFileRepository.delete(fileEntity);
//            }
            for (Long fileId : uniqueFileIds) {
                courseDescriptFileRepository.findById(fileId).ifPresentOrElse(
                        fileEntity -> {
                            Path filePath = Paths.get("tomcat/webapps/ROOT/WEB-INF/classes/static/uploads/course", fileEntity.getCourseDescriptFileUUID());
                            try {
                                Files.deleteIfExists(filePath);
//                                log.info("파일 삭제 완료: {}", fileEntity.getCourseDescriptFileUUID());
                            } catch (IOException e) {
//                                log.error("파일 삭제 중 오류 발생: {}", e.getMessage());
                            }
                            courseDescriptFileRepository.delete(fileEntity);
                        },
                        () -> log.warn("삭제 요청된 파일 ID를 찾을 수 없습니다. ID: {}", fileId)
                );
            }
        }

        // 4. 새 파일 저장 처리
        String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/static/uploads/course";
        Path uploadPath = Paths.get(uploadDir);
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            if (request.getCourseDescriptFiles() != null && !request.getCourseDescriptFiles().isEmpty()) {
                for (MultipartFile file : request.getCourseDescriptFiles()) {
                    if (file.isEmpty()) {
//                        log.warn("빈 파일이 업로드 요청됨: {}", file.getOriginalFilename());
                        continue;
                    }

                    String originalFileName = file.getOriginalFilename();
                    String uuidFileName = UUID.randomUUID() + "_" + originalFileName;
                    Path filePath = uploadPath.resolve(uuidFileName);

                    // 파일 저장
                    Files.write(filePath, file.getBytes());

                    // 파일 엔티티 생성 및 저장
                    CourseDescriptFileEntity fileEntity = CourseDescriptFileEntity.builder()
                            .courseDescript(existingDescript)
                            .courseDescriptFileName(originalFileName)
                            .courseDescriptFileUUID(uuidFileName)
                            .courseDescriptFileSize(file.getSize())
                            .courseDescriptFileType(file.getContentType())
                            .build();

                    courseDescriptFileRepository.save(fileEntity);
                }
            }
        } catch (IOException e) {
//            log.error("파일 저장 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }

        // 5. 업데이트된 엔티티 반환
        return existingDescript;

    }
//    @Transactional
//    @Override
//    public CourseDescriptEntity updateCourseDescript(CourseDescriptUpdateServiceRequest request) {
//        // 1. 강의 설명 정보 조회
//        CourseDescriptEntity existingDescript = courseDescriptRepository.findByCourse(request.getCourseEntity())
//                .orElseThrow(() -> new IllegalArgumentException("해당 강의에 대한 설명이 없습니다."));
//
//        // 2. 강의 설명 정보 업데이트
//        existingDescript.updateCourseDescriptContent(request.getCourseDescriptContent());
//
//        // 3. 삭제 요청된 파일 ID 로그 출력
////        log.info("삭제 요청된 파일 ID 목록: {}", request.getFilesToDelete());
//
//        // 3. 기존 파일 삭제 처리
//        if (request.getFilesToDelete() != null && !request.getFilesToDelete().isEmpty()) {
//            // 중복 제거
//            Set<Long> uniqueFileIds = new HashSet<>(request.getFilesToDelete());
////            log.info("중복 제거된 파일 ID 목록: {}", uniqueFileIds);
//
////            for (Long fileId : request.getFilesToDelete()) {
////                CourseDescriptFileEntity fileEntity = courseDescriptFileRepository.findById(fileId)
////                        .orElseThrow(() -> new IllegalArgumentException("삭제하려는 파일을 찾을 수 없습니다. ID: " + fileId));
////
////                // 실제 파일 삭제
////                Path filePath = Paths.get("src/main/resources/static/uploads/course", fileEntity.getCourseDescriptFileUUID());
////                try {
////                    Files.deleteIfExists(filePath);
////                } catch (IOException e) {
////                    log.error("파일 삭제 중 오류 발생: {}", e.getMessage());
////                }
////
////                // 데이터베이스에서 파일 엔티티 삭제
////                courseDescriptFileRepository.delete(fileEntity);
////            }
//            for (Long fileId : uniqueFileIds) {
//                courseDescriptFileRepository.findById(fileId).ifPresentOrElse(
//                        fileEntity -> {
//                            Path filePath = Paths.get("tomcat/webapps/ROOT/WEB-INF/classes/static/uploads/course", fileEntity.getCourseDescriptFileUUID());
//                            try {
//                                Files.deleteIfExists(filePath);
////                                log.info("파일 삭제 완료: {}", fileEntity.getCourseDescriptFileUUID());
//                            } catch (IOException e) {
////                                log.error("파일 삭제 중 오류 발생: {}", e.getMessage());
//                            }
//                            courseDescriptFileRepository.delete(fileEntity);
//                        },
//                        () -> log.warn("삭제 요청된 파일 ID를 찾을 수 없습니다. ID: {}", fileId)
//                );
//            }
//        }
//
//        // 4. 새 파일 저장 처리
//        String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/static/uploads/course";
//        Path uploadPath = Paths.get(uploadDir);
//        try {
//            if (!Files.exists(uploadPath)) {
//                Files.createDirectories(uploadPath);
//            }
//
//            if (request.getNewFiles() != null && !request.getNewFiles().isEmpty()) {
//                for (MultipartFile file : request.getNewFiles()) {
//                    if (file.isEmpty()) {
////                        log.warn("빈 파일이 업로드 요청됨: {}", file.getOriginalFilename());
//                        continue;
//                    }
//
//                    String originalFileName = file.getOriginalFilename();
//                    String uuidFileName = UUID.randomUUID() + "_" + originalFileName;
//                    Path filePath = uploadPath.resolve(uuidFileName);
//
//                    // 파일 저장
//                    Files.write(filePath, file.getBytes());
//
//                    // 파일 엔티티 생성 및 저장
//                    CourseDescriptFileEntity fileEntity = CourseDescriptFileEntity.builder()
//                            .courseDescript(existingDescript)
//                            .courseDescriptFileName(originalFileName)
//                            .courseDescriptFileUUID(uuidFileName)
//                            .courseDescriptFileSize(file.getSize())
//                            .courseDescriptFileType(file.getContentType())
//                            .build();
//
//                    courseDescriptFileRepository.save(fileEntity);
//                }
//            }
//        } catch (IOException e) {
////            log.error("파일 저장 중 오류 발생: {}", e.getMessage());
//            throw new RuntimeException("파일 저장에 실패했습니다.", e);
//        }
//
//        // 5. 업데이트된 엔티티 반환
//        return existingDescript;
//    }
}
