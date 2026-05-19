package com.Meta_learning.course.courseservice;

import com.Meta_learning.course.coursecontroller.dto.response.CourseDetailUpdateResponse;
import com.Meta_learning.course.coursecontroller.dto.update.CourseDetailUpdateRequestDTO;
import com.Meta_learning.course.courseentity.CourseDetailEntity;
import com.Meta_learning.course.courseentity.CourseEntity;
import com.Meta_learning.course.courseentity.CourseFileEntity;
import com.Meta_learning.course.courseentity.CourseVideoEntity;
import com.Meta_learning.course.courserepository.CourseDetailRepository;
import com.Meta_learning.course.courserepository.CourseFileRepository;
import com.Meta_learning.course.courserepository.CourseVideoRepository;
import com.Meta_learning.course.courseservice.requset.CourseDetailCreateServiceRequest;
import com.Meta_learning.course.courseservice.requset.CourseDetailUpdateServiceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseDetailServiceImpl implements CourseDetailService {

    private final CourseDetailRepository courseDetailRepository;
    private final CourseFileRepository courseFileRepository;
    private final CourseVideoRepository courseVideoRepository; // 동영상 파일 저장소 추가
    private final S3Client s3Client;
    private final Region awsRegion;

    private static final String LOCAL_UPLOAD_DIR = "tomcat/webapps/ROOT/WEB-INF/classes/static/uploads/course_files";
    private static final String LOCAL_UPLOAD_VIDEO_DIR = "tomcat/webapps/ROOT/WEB-INF/classes/static/uploads/course_videos";

    @Value("${cloud.aws.s3.bucket}")
    private String bucket; // "my-bucket-name"만 포함

    @Override
    // 4. 강의로 강의 상세 정보 조회
    public List<CourseDetailEntity> getCourseDetails(CourseEntity courseEntity) {
        return courseDetailRepository.findByCourse(courseEntity);
    }

    @Override
    // 5. 강의 상세 정보로 강의 동영상 조회
    public List<CourseVideoEntity> getCourseVideos(List<CourseDetailEntity> courseDetails) {
        return courseVideoRepository.findByCourseDetailIn(courseDetails);
    }

    @Override
    // 6. 강의 상세 정보로 강의 파일 조회
    public List<CourseFileEntity> getCourseFiles(List<CourseDetailEntity> courseDetails) {
        return courseFileRepository.findByCourseDetailIn(courseDetails);
    }

    @Override
    public List<CourseDetailUpdateResponse> convertCourseDetailsToUpdateResponses(List<CourseDetailEntity> courseDetailEntities) {
        // 강의 파일 및 동영상 정보 초기화
        List<CourseFileEntity> courseFiles = getCourseFiles(courseDetailEntities);
        List<CourseVideoEntity> courseVideos = getCourseVideos(courseDetailEntities);

        return courseDetailEntities.stream()
                .map(detail -> {
                    // 1. 파일 정보 추출 (UUID와 ID)
                    CourseFileEntity matchingFile = courseFiles.stream()
                            .filter(file -> file.getCourseDetail().equals(detail))
                            .findFirst()
                            .orElse(null);

                    String fileUUID = matchingFile != null ? matchingFile.getCourseFileUUID() : null;
                    Long courseFileId = matchingFile != null ? matchingFile.getCourseFileId() : null;

                    // 2. 동영상 정보 추출 (URL과 ID)
                    CourseVideoEntity matchingVideo = courseVideos.stream()
                            .filter(video -> video.getCourseDetail().equals(detail))
                            .findFirst()
                            .orElse(null);

                    String videoUrl = matchingVideo != null ? matchingVideo.getCourseVideoUUID() : null;
                    Long courseVideoId = matchingVideo != null ? matchingVideo.getCourseVideoId() : null;

                    // 3. CourseDetailUpdateRequest 생성
                    return CourseDetailUpdateResponse.builder()
                            .courseDetailId(detail.getCourseDetailId())
                            .courseDetailOutline(detail.getCourseDetailOutline())
                            .courseDetailTitle(detail.getCourseDetailTitle())
                            .courseDetailContent(detail.getCourseDetailContent())
                            .courseDetailFileUUID(fileUUID) // 파일 UUID 추가
                            .courseFileId(courseFileId) // 파일 ID 추가
                            .courseVideoUrl(videoUrl) // S3 URL 추가
                            .courseVideoId(courseVideoId) // 동영상 ID 추가
                            .build();
                })
                .toList();
    }

//    @Override
//    public List<CourseDetailUpdateRequest> convertCourseDetailsToUpdateRequests(List<CourseDetailEntity> courseDetailEntities) {
//        // 강의 파일 및 동영상 정보 초기화
//        List<CourseFileEntity> courseFiles = getCourseFiles(courseDetailEntities);
//        List<CourseVideoEntity> courseVideos = getCourseVideos(courseDetailEntities);
//
//
//        List<CourseDetailUpdateRequest> courseDetails = courseDetailEntities.stream()
//                .map(detail -> {
//                    // 1. 파일 정보 추출
//                    MultipartFile detailFile = courseFiles.stream()
//                            .filter(file -> file.getCourseDetail().equals(detail))
//                            .map(file -> convertFileToMultipart("src/main/resources/static/uploads/course_files/" + file.getCourseFileUUID()))
//                            .findFirst()
//                            .orElse(null);
//
//                    // 2. 동영상 URL 정보 추출
//                    String videoUrl = courseVideos.stream()
//                            .filter(video -> video.getCourseDetail().equals(detail))
//                            .map(CourseVideoEntity::getCourseVideoUUID) // S3 URL 직접 사용
//                            .findFirst()
//                            .orElse(null);
//
//                    // 3. CourseDetailRequest 생성
//                    return CourseDetailUpdateRequest.builder()
//                            .courseDetailOutline(detail.getCourseDetailOutline())
//                            .courseDetailTitle(detail.getCourseDetailTitle())
//                            .courseDetailContent(detail.getCourseDetailContent())
//                            .courseDetailFileUUID(detailFile) // 파일 추가
//                            .courseVideoUrl(videoUrl) // S3 URL 추가
//                            .build();
//                })
//                .toList();
//        return courseDetails;
//    }

//    public MultipartFile convertFileToMultipart(String filePath) {
//        try {
//            // 파일 경로를 Path 객체로 변환
//            Path path = Paths.get(filePath);
//
//            // 파일 이름 추출
//            String fileName = path.getFileName().toString();
//
//            // 파일의 MIME 타입 추출
//            String contentType = Files.probeContentType(path);
//
//            // 파일 내용을 바이트 배열로 읽어들임
//            byte[] content = Files.readAllBytes(path);
//
//            // MockMultipartFile 객체 생성
//            return new MockMultipartFile(fileName, fileName, contentType, content);
//        } catch (IOException e) {
//            throw new RuntimeException("파일을 MultipartFile로 변환하는 데 실패했습니다: " + filePath, e);
//        }
//    }

    @Transactional
    @Override
    public void saveCourseDetails(List<CourseDetailCreateServiceRequest> requests) {
        for (CourseDetailCreateServiceRequest request : requests) {

            Integer maxOrder = courseDetailRepository
                    .findTopByCourse_CourseIdOrderByCourseDetailOrderDesc(request.getCourseEntity().getCourseId())
                    .map(CourseDetailEntity::getCourseDetailOrder)
                    .orElse(0);
            Integer nextOrder = maxOrder + 1;
//            Integer maxOrder = courseDetailRepository.findTopByCourseCourseIdOrderByCourseDetailOrderDesc(request.getCourseEntity().getCourseId());
//            Integer nextOrder = (maxOrder == null) ? 1 : maxOrder + 1;

            // 1. CourseDetailEntity 저장
            CourseDetailEntity courseDetailEntity = CourseDetailEntity.builder()
                    .course(request.getCourseEntity())
                    .courseDetailOutline(request.getCourseDetailOutline())
                    .courseDetailTitle(request.getCourseDetailTitle())
                    .courseDetailContent(request.getCourseDetailContent())
                    .courseDetailOrder(nextOrder) // 추가
                    .build();

            CourseDetailEntity savedDetail = courseDetailRepository.save(courseDetailEntity);
            String videoUrl = request.getVideoUrl();

            if (videoUrl != null && !videoUrl.trim().isEmpty()) {
                // 유튜브 URL 또는 외부 URL 저장
                CourseVideoEntity courseVideoEntity = CourseVideoEntity.builder()
                        .courseDetail(savedDetail)
                        .courseVideoFile(videoUrl)
                        .courseVideoUUID(videoUrl)
                        .courseVideoSize(0L)
                        .courseVideoType("url")
                        .courseVideoPlaytime(0L)
                        .build();
                courseVideoRepository.save(courseVideoEntity);
            } else if (request.getCourseDetailFile() != null && !request.getCourseDetailFile().isEmpty()) {
                // 2. 파일 처리
                MultipartFile detailFile = request.getCourseDetailFile();
                if (detailFile != null && !detailFile.isEmpty()) {
                    handleFileUpload(savedDetail, detailFile);
                }
            } else {
                // 2-3. 업로드 파일도 없고, 동영상 URL도 없으면 저장하지 않음 (오류 방지)
                throw new IllegalArgumentException("파일 업로드 또는 유효한 동영상 URL이 필요합니다.");
            }
        }
    }

    @Transactional
    @Override
    public void updateCourseDetails(List<CourseDetailUpdateServiceRequest> serviceRequests) {
        // 1. 요청으로부터 기존 세부 정보의 ID를 추출
        List<Long> incomingDetailIds = serviceRequests.stream()
                .map(CourseDetailUpdateServiceRequest::getCourseDetailId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 2. DB에서 기존 세부 정보를 가져옴
        List<CourseDetailEntity> existingDetails = courseDetailRepository.findAllByCourse(serviceRequests.get(0).getCourseEntity());

        // 3. 삭제 처리: 요청에 없는 ID는 삭제
        List<CourseDetailEntity> detailsToRemove = existingDetails.stream()
                .filter(detail -> !incomingDetailIds.contains(detail.getCourseDetailId()))
                .collect(Collectors.toList());


        // 삭제 처리
        for (CourseDetailEntity detail : detailsToRemove) {
            Long detailId = detail.getCourseDetailId();

            // 1. CourseVideo 삭제 처리
            List<CourseVideoEntity> courseVideos = courseVideoRepository.findByCourseDetailIn(List.of(detail));
            for (CourseVideoEntity video : courseVideos) {
                String videoUrl = video.getCourseVideoUUID(); // 저장된 동영상 URL (S3 또는 유튜브)
                String videoType = video.getCourseVideoType();

//                if (videoUrl.contains("youtube.com") || videoUrl.contains("youtu.be")) {
                if (videoType.equals("url")) {
                    courseVideoRepository.delete(video);
                } else {
                    try {
                        String s3Key = extractS3Key(videoUrl);
                        s3Client.deleteObject(builder -> builder.bucket(bucket).key(s3Key).build());
                    } catch (Exception e) {
                        throw new IllegalStateException("S3 파일 삭제에 실패했습니다.", e);
                    }
                    courseVideoRepository.delete(video);
                }
            }

            // 2. 로컬 파일 삭제 (먼저 삭제 후 DB에서 제거)
            courseFileRepository.findByCourseDetailIn(List.of(detail))
                    .forEach(file -> {
                        Path filePath = Paths.get(LOCAL_UPLOAD_DIR, file.getCourseFileUUID());
                        try {
                            Files.deleteIfExists(filePath);
                        } catch (IOException e) {
                            // 로깅 가능
                        }
                    });

            // 3. course_file 데이터 삭제 (파일 삭제 성공 후)
            courseFileRepository.deleteByCourseDetail_CourseDetailId(detailId);

            // 4. course_detail 데이터 삭제
            courseDetailRepository.delete(detail);
        }

        // 새로운 세부 정보 저장 처리
        List<CourseDetailCreateServiceRequest> newDetails = serviceRequests.stream()
                .map(request -> CourseDetailCreateServiceRequest.builder()
                        .courseEntity(request.getCourseEntity())
                        .courseDetailOutline(request.getCourseDetailOutline())
                        .courseDetailTitle(request.getCourseDetailTitle())
                        .courseDetailContent(request.getCourseDetailContent())
                        .courseDetailFile(request.getCourseDetailFile())
                        .videoUrl(request.getVideoUrl()) // 유튜브 URL 포함
                        .build())
                .collect(Collectors.toList());

        // 새로운 세부 정보 저장 처리
        if (!newDetails.isEmpty() && newDetails.stream().anyMatch(d -> d.getCourseDetailFile() != null)) {
            saveCourseDetails(newDetails);
        }

    }

    @Transactional
    @Override
    public void updateCourseDetailFile(CourseDetailUpdateRequestDTO request) {
        CourseFileEntity courseFile = courseFileRepository.findById(request.getCourseFileId())
                .orElseThrow(() -> new IllegalArgumentException("해당 강의 자료에 대한 파일이 없습니다."));

        if (!request.getUploadType().equals("none")) {
            Path filePath = Paths.get(LOCAL_UPLOAD_DIR, courseFile.getCourseFileUUID());
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // 로깅 가능
            }

            MultipartFile detailFile = request.getCourseDetailFile();
            if (detailFile != null && !detailFile.isEmpty()) {
                handleFileUpdate(courseFile, detailFile);
            }
        }

        if (!request.getCourseDetailTitle().equals(courseFile.getCourseDetail().getCourseDetailTitle())) {
            courseFile.getCourseDetail().updateDetailTitle(request.getCourseDetailTitle());
        }
    }

    @Transactional
    @Override
    public void updateCourseDetailVideo(CourseDetailUpdateRequestDTO request) {
        CourseVideoEntity courseVideo = courseVideoRepository.findById(request.getCourseVideoId())
                .orElseThrow(() -> new IllegalArgumentException("해당 강의 영상에 대한 파일이 없습니다."));

        // detail title 변경
        if (!request.getCourseDetailTitle().equals(courseVideo.getCourseDetail().getCourseDetailTitle())) {
            courseVideo.getCourseDetail().updateDetailTitle(request.getCourseDetailTitle());
        }

        // 새로운 영상을 추가하는 경우
        if (!request.getUploadType().equals("none")) {
            if(!courseVideo.getCourseVideoType().equals("url")){
                try {
                    String s3Key = extractS3Key(courseVideo.getCourseVideoUUID());
                    s3Client.deleteObject(builder -> builder.bucket(bucket).key(s3Key).build());
                } catch (Exception e) {
                    // S3 삭제 실패는 무시하고 계속 진행
                }
            }
            if(request.getUploadType().equals("file")) {
                MultipartFile detailFile = request.getCourseDetailFile();
                if (detailFile != null && !detailFile.isEmpty()) {
                    handleVideoUpdate(courseVideo, detailFile);
                }
            }else if(request.getUploadType().equals("embed")){
                courseVideo.updateCourseVideo(request.getCourseVideoUrl(), request.getCourseVideoUrl(), 0L, "url" );
            }
        }
    }

    private void handleVideoUpdate(CourseVideoEntity courseVideo, MultipartFile file){
        String fileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(fileName);
        String uuidFileName = UUID.randomUUID() + "_" + fileName;

        try {
            String s3Url = uploadToS3(file, uuidFileName);
            courseVideo.updateCourseVideo(file.getOriginalFilename(), s3Url, file.getSize(), file.getContentType());
        } catch (IOException e) {
            throw new IllegalStateException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }


    private void handleFileUpdate(CourseFileEntity courseFile, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(fileName);
        String uuidFileName = UUID.randomUUID() + "_" + fileName;

        try {
//            if (isVideoFile(fileExtension)) {
//                // 동영상 파일 처리
//                //String s3Url = uploadToS3(file, uuidFileName);
//                //saveCourseVideoEntity(courseDetail, file, s3Url);
//                String localPath = uploadVideoToLocal(file, uuidFileName);
//
//                saveCourseVideoEntity(courseDetail, file, uuidFileName);
//            } else {
                /// 기타 파일 처리
                String localPath = uploadToLocal(file, uuidFileName);
                courseFile.updateCourseFile(file.getOriginalFilename(), uuidFileName, file.getSize(), file.getContentType());
//            }
        } catch (IOException e) {
            throw new IllegalStateException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }




    private void handleFileUpload(CourseDetailEntity courseDetail, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(fileName);
        String uuidFileName = UUID.randomUUID() + "_" + fileName;

        try {
            if (isVideoFile(fileExtension)) {
                String s3Url = uploadToS3(file, uuidFileName);
                saveCourseVideoEntity(courseDetail, file, s3Url);
            } else {
                /// 기타 파일 처리
                String localPath = uploadToLocal(file, uuidFileName);
                saveCourseFileEntity(courseDetail, file, uuidFileName);
            }
        } catch (IOException e) {
            throw new IllegalStateException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    private String uploadToS3(MultipartFile file, String fileName) throws IOException {
        String s3FileName = "course/videos/" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(s3FileName)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, awsRegion.id(), s3FileName);
    }

    private String uploadVideoToLocal(MultipartFile file, String fileName) throws IOException {
        Path uploadPath = Paths.get(LOCAL_UPLOAD_VIDEO_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        return "uploads/course_videos/" + fileName; // 로컬에서 접근 가능한 경로 반환
    }

    private String uploadToLocal(MultipartFile file, String fileName) throws IOException {
        Path uploadPath = Paths.get(LOCAL_UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        return "uploads/course_files/" + fileName; // 로컬에서 접근 가능한 경로 반환
    }

    // S3용 업로드
//    private void saveCourseVideoEntity(CourseDetailEntity courseDetail, MultipartFile file, String s3Url) {
//        long videoDuration = getVideoDuration(file);
//
//        CourseVideoEntity courseVideoEntity = CourseVideoEntity.builder()
//                .courseDetail(courseDetail)
//                .courseVideoFile(file.getOriginalFilename())
//                .courseVideoUUID(s3Url) // S3 URL 저장
//                .courseVideoSize(file.getSize())
//                .courseVideoType(file.getContentType())
//                .courseVideoPlaytime(videoDuration)
//                .build();
//
//        courseVideoRepository.save(courseVideoEntity);
//    }

    private void saveCourseVideoEntity(CourseDetailEntity courseDetail, MultipartFile file, String uuidFileName) {
        long videoDuration = getVideoDuration(file);

        CourseVideoEntity courseVideoEntity = CourseVideoEntity.builder()
                .courseDetail(courseDetail)
                .courseVideoFile(file.getOriginalFilename())
                .courseVideoUUID(uuidFileName)
                .courseVideoSize(file.getSize())
                .courseVideoType(file.getContentType())
                .courseVideoPlaytime(videoDuration)
                .build();

        courseVideoRepository.save(courseVideoEntity);
    }

    private void saveCourseFileEntity(CourseDetailEntity courseDetail, MultipartFile file, String uuidFileName) {
        CourseFileEntity courseFileEntity = CourseFileEntity.builder()
                .courseDetail(courseDetail)
                .courseFileName(file.getOriginalFilename())
                .courseFileUUID(uuidFileName)
                .courseFileSize(file.getSize())
                .courseFileType(file.getContentType())
                .build();

        courseFileRepository.save(courseFileEntity);
    }


    private long getVideoDuration(MultipartFile file) {
        try {
            Tika tika = new Tika();
            Metadata metadata = new Metadata();
            tika.parse(file.getInputStream(), metadata);

            // 우선 xmpDM:duration 필드 확인
            String duration = metadata.get("xmpDM:duration");

            // 다른 필드에서 시간 정보 검색 (예: dcterms:extent)
            if (duration == null) {
                duration = metadata.get("dcterms:extent");
            }

            if (duration != null) {
                return Long.parseLong(duration) / 1000; // 초 단위로 변환
            } else {
//                log.warn("동영상 시간 정보를 가져올 수 없습니다.");
                return 0L;
            }
        } catch (Exception e) {
//            log.error("동영상 시간 추출 실패", e);
            return 0L;
        }
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

   /* private long getVideoDuration(MultipartFile file) {
        try {
            // 임시 파일 생성
            Path tempFile = Files.createTempFile("video", getFileExtension(file.getOriginalFilename()));
            file.transferTo(tempFile.toFile());

            // FFprobe 초기화
            //FFprobe ffprobe = new FFprobe("/path/to/ffprobe"); // ffprobe 경로 설정
            // 설치된 경로로 바꿔줘야함
            FFprobe ffprobe = new FFprobe("/opt/homebrew/bin/ffprobe");
//            FFprobe ffprobe = new FFprobe("D:\\bin\\ffmpeg-7.1-full_build-shared\\bin\\ffprobe.exe");

            // 동영상 파일 분석
            FFmpegProbeResult probeResult = ffprobe.probe(tempFile.toAbsolutePath().toString());

            // 동영상 길이를 초 단위로 반환
            return (long) probeResult.getFormat().duration;
        } catch (Exception e) {
            return 0L;
        }
    }*/

    private boolean isVideoFile(String fileExtension) {
        List<String> videoExtensions = List.of(".mp4", ".avi", ".mov", ".mkv", ".flv", ".wmv");
        return videoExtensions.contains(fileExtension.toLowerCase());
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("잘못된 파일명입니다: " + fileName);
        }
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }

    // 헬퍼 메서드: S3 URL 키 추출
    private String extractS3Key(String s3Url) {
        int startIndex = s3Url.indexOf("course/videos/");
        if (startIndex == -1) {
            throw new IllegalArgumentException("Invalid S3 URL: " + s3Url);
        }
        return s3Url.substring(startIndex);
    }

}
