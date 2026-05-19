
package com.Meta_learning.s3.service;


import com.Meta_learning.KDT.KDTentity.KDTCourseVideoEntity.KDTCourseVideoEntity;
import com.Meta_learning.KDT.KDTrepository.KDTCourseVideoRepository.KDTCourseVideoRepository;
import com.Meta_learning.course.courseentity.CourseVideoEntity;
import com.Meta_learning.s3.service.request.VideoUpdateRequest;
import com.Meta_learning.s3.service.request.VideoUploadRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3VideoService {

    private final KDTCourseVideoRepository kdtCourseVideoRepository;
    private final S3Client s3Client;
    private final Region awsRegion;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket; // "my-bucket-name"만 포함

    public List<String> upload(List<MultipartFile> multipartFiles) {
        List<String> videoUrlList = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            String fileName = createFileName(file.getOriginalFilename());

            try (InputStream inputStream = file.getInputStream()) {
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                        .bucket(bucket + "/post/video")
//                        .key(fileName)
//                        .acl(ObjectCannedACL.PUBLIC_READ.toString())
                        .bucket(bucket)
                        .key("post/video/" + fileName)
                        .build();

                s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));

                videoUrlList.add(String.format("https://%s.s3.%s.amazonaws.com/post/video/%s", bucket, awsRegion.id(), fileName));
            } catch (IOException e) {
                throw new IllegalArgumentException("동영상 업로드에 실패했습니다", e);
            }
        }

        return videoUrlList;
    }

    @Transactional
    public Long uploadAndSaveVideo(VideoUploadRequest request) {
        if (!request.isFileUpload()) {
            throw new IllegalArgumentException("파일이 존재하지 않습니다.");
        }

        MultipartFile file = request.getFile();
        String fileName = createFileName(file.getOriginalFilename());
        String videoUrl;

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket) // 버킷 이름만
                    .key("post/video/" + fileName) // 경로는 key로 지정
//                    .bucket(bucket + "/post/video")
//                    .key(fileName)
//                    .acl(ObjectCannedACL.PUBLIC_READ.toString())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));

            videoUrl = String.format("https://%s.s3.%s.amazonaws.com/post/video/%s", bucket, awsRegion.id(), fileName);
        } catch (IOException e) {
            throw new IllegalArgumentException("동영상 업로드에 실패했습니다.", e);
        }

        // 동영상 시간 계산
        long videoDuration = getVideoDuration(file);

        KDTCourseVideoEntity courseVideo = KDTCourseVideoEntity.builder()
                .kdtCourseOutlineEntity(request.getKdtCourseOutlineEntity())
                .userEntity(request.getUserEntity())
                .kdtCourseVideoCategory(request.getCategory())
                .kdtCourseVideoTitle(request.getTitle())
                .kdtCourseVideoFile(file.getOriginalFilename())
                .kdtCourseVideoUUID(videoUrl)
                .kdtCourseVideoSize(file.getSize())
                .kdtCourseVideoType(file.getContentType())
                .kdtCourseVideoTime(videoDuration)
                .build();

        return kdtCourseVideoRepository.save(courseVideo).getKdtCourseVideoId();
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

    /*private long getVideoDuration(MultipartFile file) {
        try {
            // 임시 파일 생성
            Path tempFile = Files.createTempFile("video", getFileExtension(file.getOriginalFilename()));
            file.transferTo(tempFile.toFile());

            // FFprobe 초기화
            //FFprobe ffprobe = new FFprobe("/path/to/ffprobe"); // ffprobe 경로 설정
            // 설치된 경로로 바꿔줘야함
//            FFprobe ffprobe = new FFprobe("/opt/homebrew/bin/ffprobe");
            FFprobe ffprobe = new FFprobe("D:\\bin\\ffmpeg-7.1-full_build-shared\\bin\\ffprobe.exe");

            // 동영상 파일 분석
            FFmpegProbeResult probeResult = ffprobe.probe(tempFile.toAbsolutePath().toString());

            // 동영상 길이를 초 단위로 반환
            return (long) probeResult.getFormat().duration;
        } catch (Exception e) {
            return 0L;
        }
    }*/


    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        if (fileName.isEmpty()) {
            throw new IllegalArgumentException("동영상 파일은 반드시 있어야 합니다");
        }

        List<String> validExtensions = List.of(".mp4", ".avi", ".mov", ".mkv", ".flv", ".wmv");
        String fileExtension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        if (!validExtensions.contains(fileExtension)) {
            throw new IllegalArgumentException("지원하지 않는 동영상 파일 형식입니다: " + fileExtension);
        }

        return fileExtension;
    }

    @Transactional
    public Long updateCourseVideo(VideoUpdateRequest request) {
        // DB에서 기존 동영상 데이터 조회
        KDTCourseVideoEntity existingVideo = kdtCourseVideoRepository.findById(request.getKdtCourseVideoId())
                .orElseThrow(() -> new IllegalArgumentException("Video not found with ID: " + request.getKdtCourseVideoId()));

        // 강의 영상 수정 있을 경우
        if (!request.getUploadType().equals("none")) {

            // 기존 것이 파일인 경우
            if (!existingVideo.getKdtCourseVideoType().equals("url")){
                // 새 파일 업로드 및 URL 업데이트
                MultipartFile newFile = request.getFile();
                String newFileName = createFileName(newFile.getOriginalFilename());
                String newVideoUrl;

                try (InputStream inputStream = newFile.getInputStream()) {
                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(bucket)
                            .key("post/video/" + newFileName)
                            .build();

                    s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, newFile.getSize()));

                    newVideoUrl = String.format("https://%s.s3.%s.amazonaws.com/post/video/%s", bucket, awsRegion.id(), newFileName);
                } catch (IOException e) {
                    throw new IllegalArgumentException("새 동영상 업로드에 실패했습니다.", e);
                }

                // 동영상 세부 정보 업데이트
                // 새 동영상 시간 계산
                long newVideoDuration = getVideoDuration(newFile);
                // 기존 동영상 엔티티 업데이트
                existingVideo.updateVideoDetails(
                        newFile.getOriginalFilename(),
                        newVideoUrl,
                        newFile.getSize(),
                        newFile.getContentType(),
                        newVideoDuration
                );
                existingVideo.updateKdtCourseVideoTitle(request.getTitle());
                existingVideo.updateKdtCourseVideoCategory(request.getCategory());
            } // 기존 설정이 url인 경우
            else {
                String newVideoUrl = request.getVideoUrl();
                // 기존 동영상 엔티티 업데이트
                existingVideo.updateVideoDetails(
                        newVideoUrl,
                        newVideoUrl,
                        0L,
                        "url",
                        0L
                );
                existingVideo.updateKdtCourseVideoTitle(request.getTitle());
                existingVideo.updateKdtCourseVideoCategory(request.getCategory());
            }

        } else {
            // 새 파일이 없으면 제목과 카테고리만 업데이트
            existingVideo.updateVideoDetails(
                    existingVideo.getKdtCourseVideoFile(),
                    existingVideo.getKdtCourseVideoUUID(),
                    existingVideo.getKdtCourseVideoSize(),
                    existingVideo.getKdtCourseVideoType(),
                    existingVideo.getKdtCourseVideoTime()
            );
            existingVideo.updateKdtCourseVideoTitle(request.getTitle());
            existingVideo.updateKdtCourseVideoCategory(request.getCategory());
        }

        return existingVideo.getKdtCourseVideoId();
    }

    private void deleteFileFromS3(String key) {
        try {
            s3Client.deleteObject(builder -> builder.bucket(bucket).key(key).build());
//            log.info("S3에서 파일 삭제 완료: {}", key);
        } catch (Exception e) {
//            log.error("S3 파일 삭제 실패: {}", key, e);
            throw new IllegalStateException("S3 파일 삭제에 실패했습니다.", e);
        }
    }

    private String extractKeyFromUrl(String url) {
        int startIndex = url.indexOf("post/video/");
        if (startIndex == -1) {
            throw new IllegalArgumentException("Invalid S3 URL: " + url);
        }
        return url.substring(startIndex);
    }

    @Transactional
    public void deleteKdtCourseVideoWithDB(Long kdtCourseVideoId) {
        // DB에서 기존 동영상 데이터 조회
        try {
            KDTCourseVideoEntity existingVideo = kdtCourseVideoRepository.findById(kdtCourseVideoId)
                    .orElseThrow(() -> new IllegalArgumentException("Video not found with ID: " + kdtCourseVideoId));

            if(!existingVideo.getKdtCourseVideoType().equals("url")) {
                // S3에서 기존 동영상 파일 삭제
                String existingFileKey = extractKeyFromUrl(existingVideo.getKdtCourseVideoUUID());
                deleteFileFromS3(existingFileKey);
            }

            // DB 데이터 삭제
            kdtCourseVideoRepository.delete(existingVideo);

//            log.info("Successfully deleted video with ID: {}", kdtCourseVideoId);
        } catch (Exception e) {
//            log.error("Failed to delete video with ID: {}", kdtCourseVideoId, e);
            throw e; // 예외를 다시 던져 상위 계층에서 처리하도록 위임
        }
    }

    @Transactional
    public void deleteKdtCourseVideo(Long kdtCourseVideoId) {
        // DB에서 기존 동영상 데이터 조회
        try {
            KDTCourseVideoEntity existingVideo = kdtCourseVideoRepository.findById(kdtCourseVideoId)
                    .orElseThrow(() -> new IllegalArgumentException("Video not found with ID: " + kdtCourseVideoId));

            if(!existingVideo.getKdtCourseVideoType().equals("url")) {
                // S3에서 기존 동영상 파일 삭제
                String existingFileKey = extractKeyFromUrl(existingVideo.getKdtCourseVideoUUID());
                deleteFileFromS3(existingFileKey);
            }

            // DB 데이터 삭제
//            kdtCourseVideoRepository.delete(existingVideo);

//            log.info("Successfully deleted video with ID: {}", kdtCourseVideoId);
        } catch (Exception e) {
//            log.error("Failed to delete video with ID: {}", kdtCourseVideoId, e);
            throw e; // 예외를 다시 던져 상위 계층에서 처리하도록 위임
        }
    }
}
