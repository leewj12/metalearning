package com.Meta_learning.KDT.KDTservice.detail;

import com.Meta_learning.KDT.KDTentity.KDTDetailEntity.KDTDetailEntity;
import com.Meta_learning.KDT.KDTentity.KDTDetailFileEntity.KDTDetailFileEntity;
import com.Meta_learning.KDT.KDTrepository.KDTDetailFileRepository.KDTDetailFileRepository;
import com.Meta_learning.KDT.KDTrepository.KDTDetailRepository.KDTDetailRepository;
import com.Meta_learning.KDT.KDTservice.request.KDTDetailCreateServiceRequest;
import com.Meta_learning.KDT.KDTservice.request.KDTDetailUpdateServiceRequest;
import com.Meta_learning.KDT.KDTservice.response.KDTDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


@RequiredArgsConstructor
@Transactional
@Service
public class KdtDetailServiceImpl implements KdtDetailService {

    private final KDTDetailRepository kdtDetailRepository;
    private final KDTDetailFileRepository kdtDetailFileRepository; // 파일 레포지토리 추가

    @Override
    public KDTDetailResponse getKdtDetail(Long kdtDetailId) {
        KDTDetailEntity kdtDetailEntity = kdtDetailRepository.findById(kdtDetailId)
                .orElseThrow(() -> new IllegalArgumentException("KDTDetailEntity not found with id: " + kdtDetailId));

        List<KDTDetailFileEntity> files = kdtDetailFileRepository.findByKdtDetailEntity(kdtDetailEntity);

        // Response 객체 생성
        return KDTDetailResponse.of(kdtDetailEntity);
    }

    @Override
    public KDTDetailResponse createKdtDetail(KDTDetailCreateServiceRequest request) {
        // 요청 객체를 엔티티로 변환
        KDTDetailEntity kdtDetailEntity = KDTDetailEntity.builder()
                .kdtSessionEntity(request.getKdtSessionEntity())
                .userEntity(request.getUserEntity())
                .kdtDetailContent(request.getKdtDetailContent())
                .build();

        // 파일 엔티티와 연관 관계 설정
        for (KDTDetailFileEntity fileEntity : request.getFileEntities()) {
            kdtDetailEntity.addFile(fileEntity); // 연관 관계 설정 (addFile 활용)
        }

        // KDTDetailEntity 및 파일 엔티티 저장 (Cascade.ALL에 의해 자동 저장)
        KDTDetailEntity savedDetailEntity = kdtDetailRepository.save(kdtDetailEntity);

        // 저장된 엔티티를 Response로 변환하여 반환
        return KDTDetailResponse.of(savedDetailEntity);
    }

    @Override
    public void updateKdtDetail(Long kdtDetailId, KDTDetailUpdateServiceRequest request, MultipartFile[] newFiles, List<Long> deleteFileIds) throws IOException {
        KDTDetailEntity detailEntity = kdtDetailRepository.findById(kdtDetailId)
                .orElseThrow(() -> new IllegalArgumentException("Detail not found"));

        // 내용 업데이트
        detailEntity.updateContent(request.getKdtDetailContent());

        // 삭제할 파일 처리
        if (deleteFileIds != null) {
//            detailEntity.getFiles().removeIf(file -> deleteFileIds.contains(file.getKdtDetailFileId()));
            for (Long fileId : deleteFileIds) {
                KDTDetailFileEntity fileEntity = detailEntity.getFiles().stream()
                        .filter(file -> file.getKdtDetailFileId().equals(fileId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("File not found with ID: " + fileId));

                // 서버에서 파일 삭제
                Path filePath = Paths.get("tomcat/webapps/ROOT/WEB-INF/classes/static/images/uploads", fileEntity.getKdtDetailFileUUID());
                try {
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
//                    log.error("Failed to delete file: {}", filePath, e);
                }

                // DB에서 파일 제거
//                detailEntity.getFiles().remove(fileEntity);
                detailEntity.removeFile(fileEntity);
            }
            kdtDetailFileRepository.deleteAllById(deleteFileIds);
        }

        // 새로운 파일 추가
        if (newFiles != null) {
            String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/static/images/uploads";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            for (MultipartFile file : newFiles) {
                if (file.isEmpty()) {
//                    log.warn("Skipped empty file upload: {}", file.getOriginalFilename());
                    continue;
                }

                String originalFileName = file.getOriginalFilename();
                String uuidFileName = UUID.randomUUID() + "_" + originalFileName;
                Path filePath = uploadPath.resolve(uuidFileName);

                try {
                    Files.write(filePath, file.getBytes());
//                    log.info("Uploaded file: {}", filePath);
                } catch (IOException e) {
//                    log.error("Failed to upload file: {}", filePath, e);
                    continue;
                }

                KDTDetailFileEntity newFile = KDTDetailFileEntity.builder()
                        .kdtDetailEntity(detailEntity)
                        .kdtDetailFileName(originalFileName)
                        .kdtDetailFileUUID(uuidFileName)
                        .kdtDetailFileSize(file.getSize())
                        .kdtDetailFileType(file.getContentType())
                        .kdtDetailFileTime(LocalDateTime.now())
                        .build();

                detailEntity.addFile(newFile);
            }
        }

        // 저장
        kdtDetailRepository.save(detailEntity);
//        log.info("Updated Detail Entity saved: {}", detailEntity);
    }

//    @Override
//    public void updateKdtDetail(Long kdtDetailId, KDTDetailUpdateServiceRequest request) {
//        KDTDetailEntity kdtDetailEntity = kdtDetailRepository.findById(kdtDetailId)
//                .orElseThrow(() -> new IllegalArgumentException("KDTDetailEntity not found with id: " + kdtDetailId));
//
//        // 수정할 필드 업데이트
//        kdtDetailEntity.updateContent(request.getKdtDetailContent());
//
//        // 저장
//        kdtDetailRepository.save(kdtDetailEntity);
//    }

    @Override
    public KDTDetailEntity getDetailBySessionId(Long sessionId) {
        return kdtDetailRepository.findByKdtSessionEntity_KdtSessionId(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session ID에 해당하는 Detail이 없습니다."));
    }

    @Override
    public void deleteKdtDetail(Long kdtDetailId) {
        // 1. Detail Entity 조회
        KDTDetailEntity detailEntity = kdtDetailRepository.findById(kdtDetailId)
                .orElseThrow(() -> new IllegalArgumentException("Detail not found with ID: " + kdtDetailId));

        // 2. 연관된 파일 삭제
        for (KDTDetailFileEntity fileEntity : detailEntity.getFiles()) {
            // 로컬 파일 삭제
            Path filePath = Paths.get("tomcat/webapps/ROOT/WEB-INF/classes/static/images/uploads", fileEntity.getKdtDetailFileUUID());
            try {
                Files.deleteIfExists(filePath);
//                log.info("Deleted file from server: {}", filePath);
            } catch (IOException e) {
//                log.error("Failed to delete file: {}", filePath, e);
            }
        }

        // 3. Detail Entity 삭제 (Cascade에 의해 연관된 파일도 삭제)
        kdtDetailRepository.delete(detailEntity);
//        log.info("Deleted detail with ID: {}", kdtDetailId);
    }
}
