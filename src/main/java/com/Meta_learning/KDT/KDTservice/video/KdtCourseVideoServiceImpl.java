package com.Meta_learning.KDT.KDTservice.video;

import com.Meta_learning.KDT.KDTentity.KDTCourseVideoEntity.KDTCourseVideoEntity;
import com.Meta_learning.KDT.KDTrepository.KDTCourseVideoRepository.KDTCourseVideoRepository;
import com.Meta_learning.KDT.KDTservice.response.KDTCourseVideoResponse;
import com.Meta_learning.course.courseentity.CourseDetailEntity;
import com.Meta_learning.course.courseentity.CourseVideoEntity;
import com.Meta_learning.s3.service.request.VideoUpdateRequest;
import com.Meta_learning.s3.service.request.VideoUploadRequest;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class KdtCourseVideoServiceImpl implements KdtCourseVideoService{

    private static final String LOCAL_UPLOAD_KDT_VIDEO_DIR = "tomcat/webapps/ROOT/WEB-INF/classes/KDT/course";

    private final KDTCourseVideoRepository kdtCourseVideoRepository;

    @Override
    public List<KDTCourseVideoResponse> getAllKdtCourseVideoByCourseOutlineId(Long kdtCourseOutlineId) {
        List<KDTCourseVideoEntity> entities = kdtCourseVideoRepository.findByKdtCourseOutlineEntityKdtCourseOutlineId(kdtCourseOutlineId);
        return entities.stream()
                .map(KDTCourseVideoResponse::of)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long saveVideoUrl(VideoUploadRequest request) {
        KDTCourseVideoEntity courseVideo = KDTCourseVideoEntity.builder()
                .kdtCourseOutlineEntity(request.getKdtCourseOutlineEntity())
                .userEntity(request.getUserEntity())
                .kdtCourseVideoCategory(request.getCategory())
                .kdtCourseVideoTitle(request.getTitle())
                .kdtCourseVideoFile(request.getVideoUrl())  // URL을 파일명으로 저장
                .kdtCourseVideoUUID(request.getVideoUrl()) // UUID 대신 URL 저장
                .kdtCourseVideoSize(0L)  // URL이므로 파일 크기 없음
                .kdtCourseVideoType("url")  // URL 타입으로 저장
                .kdtCourseVideoTime(0L)  // 동영상 길이 저장 안 함
                .build();

        return kdtCourseVideoRepository.save(courseVideo).getKdtCourseVideoId();
    }

    @Override
    @Transactional
    public void saveVideoFile(VideoUploadRequest request) {

        // 파일 확인
        MultipartFile kdtCourseVideo = request.getFile();
        if (kdtCourseVideo != null && !kdtCourseVideo.isEmpty()) {
            // Local file upload
            handleFileUpload(request, kdtCourseVideo);
        }

    }

    @Override
    @Transactional
    public void deleteKdtCourseVideo(Long kdtCourseVideoId) {
        // DB에서 기존 동영상 데이터 조회
        try {
            KDTCourseVideoEntity existingVideo = kdtCourseVideoRepository.findById(kdtCourseVideoId)
                    .orElseThrow(() -> new IllegalArgumentException("Video not found with ID: " + kdtCourseVideoId));

            if(!existingVideo.getKdtCourseVideoType().equals("url")) {
                // S3에서 기존 동영상 파일 삭제
                deleteFileFromServer("tomcat/webapps/ROOT/WEB-INF/classes/KDT/course", existingVideo.getKdtCourseVideoUUID());
            }

            // DB 데이터 삭제
            kdtCourseVideoRepository.delete(existingVideo);

//            log.info("Successfully deleted video with ID: {}", kdtCourseVideoId);
        } catch (Exception e) {
//            log.error("Failed to delete video with ID: {}", kdtCourseVideoId, e);
            throw e; // 예외를 다시 던져 상위 계층에서 처리하도록 위임
        }
    }

    @Override
    @Transactional
    public void updateCourseVideo(VideoUpdateRequest request) {
        KDTCourseVideoEntity existingVideo = kdtCourseVideoRepository.findById(request.getKdtCourseVideoId())
                .orElseThrow(() -> new IllegalArgumentException("해당 강의 영상에 대한 파일이 없습니다."));

        // 카테고리 변경
        if (!request.getCategory().equals(existingVideo.getKdtCourseVideoCategory())) {
            existingVideo.updateKdtCourseVideoCategory(request.getCategory());
        }

        // 타이틀 변경
        if (!request.getTitle().equals(existingVideo.getKdtCourseVideoTitle())) {
            existingVideo.updateKdtCourseVideoTitle(request.getTitle());
        }

        // 새로운 영상을 추가하는 경우
        if (!request.getUploadType().equals("none")) {
            if(!existingVideo.getKdtCourseVideoType().equals("url")){
                Path filePath = Paths.get(LOCAL_UPLOAD_KDT_VIDEO_DIR, existingVideo.getKdtCourseVideoUUID());
                try {
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    // 로깅 가능
                }
            }
            if(request.getUploadType().equals("file")) {
                MultipartFile detailFile = request.getFile();
                if (detailFile != null && !detailFile.isEmpty()) {
                    handleVideoUpdate(existingVideo, detailFile);
                }
            }else if(request.getUploadType().equals("embed")){
                existingVideo.updateVideoDetails(request.getVideoUrl(), request.getVideoUrl(), 0L, "url", 0L);
            }
        }


    }

    private void handleFileUpload(VideoUploadRequest request, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(fileName);
        String uuidFileName = UUID.randomUUID() + "_" + fileName;

        try {
            if (isVideoFile(fileExtension)) {
                // 동영상 파일 처리
                //String s3Url = uploadToS3(file, uuidFileName);
                //saveCourseVideoEntity(courseDetail, file, s3Url);
                uploadVideoToLocal(file, uuidFileName);
                saveKDTCourseVideoEntity(request, file, uuidFileName);
            }
        } catch (IOException e) {
            throw new IllegalStateException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    private void handleVideoUpdate(KDTCourseVideoEntity kdtCourseVideo, MultipartFile file){
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
            uploadVideoToLocal(file, uuidFileName);
            kdtCourseVideo.updateVideoDetails(file.getOriginalFilename(), uuidFileName, file.getSize(), file.getContentType(), getVideoDuration(file));
//            }
        } catch (IOException e) {
            throw new IllegalStateException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }


    private void saveKDTCourseVideoEntity(VideoUploadRequest request, MultipartFile file, String uuidFileName) {
        long videoDuration = getVideoDuration(file);

        KDTCourseVideoEntity courseVideo = KDTCourseVideoEntity.builder()
                .kdtCourseOutlineEntity(request.getKdtCourseOutlineEntity())
                .userEntity(request.getUserEntity())
                .kdtCourseVideoCategory(request.getCategory())
                .kdtCourseVideoTitle(request.getTitle())
                .kdtCourseVideoFile(file.getOriginalFilename())
                .kdtCourseVideoUUID(uuidFileName)
                .kdtCourseVideoSize(file.getSize())
                .kdtCourseVideoType(file.getContentType())
                .kdtCourseVideoTime(videoDuration)
                .build();

        kdtCourseVideoRepository.save(courseVideo);
    }


    private void uploadVideoToLocal(MultipartFile file, String fileName) throws IOException {
        Path uploadPath = Paths.get(LOCAL_UPLOAD_KDT_VIDEO_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        //고쳐야함
        //return "uploads/course_files/" + fileName; // 로컬에서 접근 가능한 경로 반환
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
}
