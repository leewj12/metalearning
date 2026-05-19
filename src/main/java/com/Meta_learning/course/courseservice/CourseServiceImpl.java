package com.Meta_learning.course.courseservice;

import com.Meta_learning.course.coursecontroller.dto.response.CourseViewResponse;
import com.Meta_learning.course.coursecontroller.dto.update.CourseUpdateRequestDTO;
import com.Meta_learning.course.coursecontroller.dto.update.CourseUpdateResponseDTO;
import com.Meta_learning.course.courseentity.*;
import com.Meta_learning.course.courserepository.*;
import com.Meta_learning.course.courserepository.cart.CartItemRepository;
import com.Meta_learning.course.courserepository.order.OrderDetailRepository;
import com.Meta_learning.course.courseservice.requset.CourseCreateServiceRequest;
import com.Meta_learning.user.userentity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService{

    private final CourseRepository courseRepository;
    private final CourseDetailRepository courseDetailRepository;
    private final CourseFileRepository courseFileRepository;
    private final CourseVideoRepository courseVideoRepository;
    private final CourseDescriptFileRepository courseDescriptFileRepository;
    private final CourseDescriptRepository courseDescriptRepository;

    private final OrderDetailRepository orderDetailRepository;
    private final CartItemRepository cartItemRepository;
    private final InstrRepository instrRepository;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    // 1. 강의 기본 정보 조회
    public CourseEntity getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다. ID: " + courseId));
    }

    @Transactional
    @Override
    public CourseEntity saveCourse(CourseCreateServiceRequest serviceRequest) {
        // 1. 파일 저장 경로 설정
        String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/static/uploads/course";
        Path uploadPath = Paths.get(uploadDir);

        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. 썸네일 파일 처리
            MultipartFile thumbnailFile = serviceRequest.getCourseThumbnail();
            String uuidFileName = null;

            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                String originalFileName = thumbnailFile.getOriginalFilename();
                uuidFileName = UUID.randomUUID() + "_" + originalFileName;
                Path filePath = uploadPath.resolve(uuidFileName);

                // 파일 저장
                Files.write(filePath, thumbnailFile.getBytes());
            }

            // 3. CourseEntity 생성 및 저장
            CourseEntity courseEntity = CourseEntity.builder()
                    .instr(serviceRequest.getInstr())
                    .courseThumbnail(uuidFileName) // 저장된 UUID 파일명 설정
                    .courseTitle(serviceRequest.getCourseTitle())
                    .courseDescript(serviceRequest.getCourseDescript())
                    .coursePrice(serviceRequest.getCoursePrice())
                    .courseCategory(serviceRequest.getCourseCategory())
                    .courseDifficulty(serviceRequest.getCourseDifficulty())
                    .courseStatus(serviceRequest.getCourseStatus())
                    .build();

            // Entity 저장 및 반환
            return courseRepository.save(courseEntity);

        } catch (IOException e) {
            throw new RuntimeException("썸네일 파일 저장에 실패했습니다.", e);
        }
    }

//    @Transactional
//    @Override
//    public CourseEntity updateCourse(CourseUpdateServiceRequest request) {
//        // 1. 강의 정보 조회
//        CourseEntity existingCourse = courseRepository.findById(request.getCourseId())
//                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 강의를 찾을 수 없습니다. ID: " + request.getCourseId()));
//
//        // 2. 썸네일 저장 및 경로 처리 (선택적)
//        String thumbnailPath = null;
//        if (request.getCourseThumbnail() != null && !request.getCourseThumbnail().isEmpty()) {
//            thumbnailPath = saveFile(request.getCourseThumbnail());
//        }
//
//
//        // 3. 강의 정보 업데이트
//        existingCourse.update(
//                thumbnailPath,                      // 썸네일 경로
//                request.getCourseTitle(),           // 강의 제목
//                request.getCourseDescript(),        // 강의 설명
//                request.getCoursePrice(),           // 강의 가격
//                null,                               // 강의 커리큘럼
//                request.getCourseCategory(),        // 강의 카테고리
//                request.getCourseDifficulty(),      // 강의 난이도
//                request.getCourseStatus()           // 강의 상태
//        );
//
//        // 4. 변경 감지를 통해 업데이트 (엔티티 직접 반환)
////        return courseRepository.save(existingCourse);
//        return existingCourse;
//    }

    @Transactional
    @Override
    public CourseEntity updateCourse(CourseUpdateRequestDTO request) {
        // 1. 강의 정보 조회
        CourseEntity existingCourse = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 강의를 찾을 수 없습니다. ID: " + request.getCourseId()));

        // 2. 썸네일 저장 및 경로 처리 (선택적)
        String thumbnailPath = null;
        if (request.getCourseThumbnail() != null && !request.getCourseThumbnail().isEmpty()) {
            thumbnailPath = saveFile(request.getCourseThumbnail());
        }


        // 3. 강의 정보 업데이트
        existingCourse.update(
                thumbnailPath,                      // 썸네일 경로
                request.getCourseTitle(),           // 강의 제목
                request.getCourseDescript(),        // 강의 설명
                request.getCoursePrice(),           // 강의 가격
                null,                               // 강의 커리큘럼
                request.getCourseCategory(),        // 강의 카테고리
                request.getCourseDifficulty(),      // 강의 난이도
                null          // 강의 상태
        );

        // 4. 변경 감지를 통해 업데이트 (엔티티 직접 반환)
//        return courseRepository.save(existingCourse);
        return existingCourse;
    }

    public String saveFile(MultipartFile file) {
        String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/static/uploads/course";
        try {
            // 1. 업로드 디렉토리 설정
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. 파일 처리
            if (file != null && !file.isEmpty()) {
                String originalFileName = file.getOriginalFilename();
                String uuidFileName = UUID.randomUUID() + "_" + originalFileName;
                Path filePath = uploadPath.resolve(uuidFileName);

                // 파일 저장
                Files.write(filePath, file.getBytes());
                return uuidFileName; // 저장된 파일 이름 반환
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
        return null; // 파일이 없거나 저장 실패 시 null 반환
    }



//    @Override
//    public MultipartFile convertFileToMultipart(String fileName) {
//        try {
//            // 저장 경로 설정
//            String uploadDir = "src/main/resources/static/uploads/course/";
//            Path filePath = Paths.get(uploadDir + fileName);
//
//            // 파일 정보 읽기
//            byte[] content = Files.readAllBytes(filePath);
//            String contentType = Files.probeContentType(filePath);
//
//            return new MockMultipartFile(fileName, fileName, contentType, content);
//        } catch (IOException e) {
//            throw new RuntimeException("파일을 MultipartFile로 변환하는 데 실패했습니다: " + fileName, e);
//        }
//    }

    @Override
    public List<CourseEntity> getCoursesByInstructor(InstrEntity instrEntity) {
        return courseRepository.findByInstr(instrEntity);
    }

    // 승인된 강의 조회
    public List<CourseEntity> getApprovedCourses() {
        return courseRepository.findByCourseStatus(CourseStatus.APPROVED);
    }

    @Override
    public Long getCourseCount(UserEntity user) {
        InstrEntity instrEntity = instrRepository.findByUserEntityUserId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 강사 정보가 존재하지 않습니다."));

        return courseRepository.countByInstr(instrEntity);
    }

    @Override
    public List<Map<String, Object>> getMonthlyUploadCourseCount() {
        List<Object[]> results = courseRepository.countCoursesByMonth();

        List<Map<String, Object>> monthlyCourseCounts = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("year", result[0]);    // 연도
            map.put("month", result[1]);  // 월
            map.put("courseCount", result[2]); // 강의 수
            monthlyCourseCounts.add(map);
        }
        return monthlyCourseCounts;
    }

    @Override
    public List<CourseEntity> getPendingCourses() {
        return courseRepository.findByCourseStatus(CourseStatus.PENDING);
    }

    @Transactional
    @Override
    public void updateCourseStatus(Long courseId, CourseStatus status) {
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("ID가 " + courseId + "인 강의를 찾을 수 없습니다."));

        // 상태 업데이트
        course.updateCourseStatus(status);

        // @Transactional을 사용하므로 save 호출은 생략 가능 (변경 사항 자동 감지)
    }

    @Transactional
    @Override
    public void deleteCourseById(Long courseId) {
        // 1️⃣ 강의 존재 여부 확인
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다. ID: " + courseId));

        // 2️⃣ 해당 강의가 주문된 적이 있는지 확인
        if (orderDetailRepository.existsByCourse(course)) {
            throw new IllegalStateException("이 강의는 이미 구매한 사용자가 있어 삭제할 수 없습니다.");
        }

        // 3️⃣ 장바구니에서 해당 강의 삭제
        cartItemRepository.deleteAllByCourse(course);

        // 4️⃣ 강의 상세 정보 삭제
        List<CourseDetailEntity> courseDetails = courseDetailRepository.findAllByCourse(course);
        for (CourseDetailEntity detail : courseDetails) {
            Long detailId = detail.getCourseDetailId();

            // 강의 파일 삭제
            List<CourseFileEntity> courseFiles = courseFileRepository.findByCourseDetailIn(List.of(detail));
            for (CourseFileEntity file : courseFiles) {
                deleteFileFromServer("tomcat/webapps/ROOT/WEB-INF/classes/static/uploads/course_files", file.getCourseFileUUID());
            }
            courseFileRepository.deleteAll(courseFiles);

            // 강의 동영상 삭제
            List<CourseVideoEntity> courseVideos = courseVideoRepository.findByCourseDetailIn(List.of(detail));
            for (CourseVideoEntity video : courseVideos) {
                if(!video.getCourseVideoType().equals("url")) {
                    try {
                        String key = extractS3Key(video.getCourseVideoUUID());
                        s3Client.deleteObject(b -> b.bucket(bucket).key(key).build());
                    } catch (Exception ignored) {}
                }
            }
            courseVideoRepository.deleteAll(courseVideos);

            // 강의 상세 정보 삭제
            courseDetailRepository.delete(detail);
        }

        // 3️⃣ 강의 설명 파일 및 설명 삭제 (Optional 처리)
        courseDescriptRepository.findByCourse(course).ifPresent(courseDescript -> {
            List<CourseDescriptFileEntity> descriptFiles = courseDescriptFileRepository.findByCourseDescript(courseDescript);
            for (CourseDescriptFileEntity file : descriptFiles) {
                deleteFileFromServer("tomcat/webapps/ROOT/WEB-INF/classes/static/uploads/course", file.getCourseDescriptFileUUID());
            }
            if (!descriptFiles.isEmpty()) {
                courseDescriptFileRepository.deleteAll(descriptFiles);
            }
            courseDescriptRepository.delete(courseDescript);
        });

        // 4️⃣ 강의 삭제
        courseRepository.delete(course);
    }

    @Override
    public void deleteCourseVideoById(Long courseVideoId) {
        // 강의 영상 찾기
        CourseVideoEntity courseVideo = courseVideoRepository.findById(courseVideoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 영상을 찾을 수 없습니다. ID: " + courseVideoId));

        // 강의 상세 정보 찾기
        CourseDetailEntity courseDetail = courseVideo.getCourseDetail();

        if(!courseVideo.getCourseVideoType().equals("url")){
            try {
                String key = extractS3Key(courseVideo.getCourseVideoUUID());
                s3Client.deleteObject(b -> b.bucket(bucket).key(key).build());
            } catch (Exception ignored) {}
        }

        // 강의 영상 DB 삭제
        courseVideoRepository.delete(courseVideo);

        // 강의 상세 정보 삭제
        courseDetailRepository.delete(courseDetail);
    }


    private String extractS3Key(String s3Url) {
        int startIndex = s3Url.indexOf("course/videos/");
        if (startIndex == -1) {
            throw new IllegalArgumentException("Invalid S3 URL: " + s3Url);
        }
        return s3Url.substring(startIndex);
    }

    /**
     * 서버에서 파일 삭제
     */
    private void deleteFileFromServer(String baseDir, String fileName) {
        if (fileName != null) {
            Path filePath = Paths.get(baseDir, fileName);
            try {
                Files.deleteIfExists(filePath);
//            log.info("파일 삭제 완료: {}", filePath);
            } catch (IOException e) {
//            log.error("파일 삭제 중 오류 발생: {}", e.getMessage());
            }
        }
    }




    @Override
    public CourseViewResponse getCourseViewByDetailId(Long courseDetailId) {
// CourseDetail 조회
        Optional<CourseDetailEntity> courseDetail = courseDetailRepository.findByCourseDetailId(courseDetailId);
        if (!courseDetail.isPresent()) {
            throw new RuntimeException("Course detail not found for ID: " + courseDetailId);
        }

        // CourseVideo 조회
        Optional<CourseVideoEntity> courseVideo = courseVideoRepository.findByCourseDetail_CourseDetailId(courseDetailId);
        if (!courseVideo.isPresent()) {
            throw new RuntimeException("Course video not found for CourseDetail ID: " + courseDetailId);
        }

        // CourseViewResponse DTO 생성하여 반환
        return CourseViewResponse.builder()
                .courseDetailTitle(courseDetail.get().getCourseDetailTitle())
                .courseVideoType(courseVideo.get().getCourseVideoType())
                .courseVideoUuid(courseVideo.get().getCourseVideoUUID())
                .build();
    }

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

}
