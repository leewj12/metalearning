package com.Meta_learning.admin.adminrestcontroller;


import com.Meta_learning.KDT.KDTentity.KDTCourseOutlineEntity.KDTCourseOutlineEntity;
import com.Meta_learning.KDT.KDTentity.KDTCourseVideoEntity.KDTCourseVideoEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.KDT.KDTrepository.KDTCourseOutlineRepository.KDTCourseOutlineRepository;
import com.Meta_learning.KDT.KDTrepository.KDTCourseVideoRepository.KDTCourseVideoRepository;
import com.Meta_learning.KDT.KDTrepository.KDTSessionRepository.KDTSessionRepository;
import com.Meta_learning.KDT.KDTservice.outline.KdtCourseOutlineService;
import com.Meta_learning.KDT.KDTservice.video.KdtCourseVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

public class AdminCourseOutlineRestController {

    private final KdtCourseOutlineService kdtCourseOutlineService;
    private final KDTSessionRepository kdtSessionRepository;
    private final KDTCourseOutlineRepository kdtCourseOutlineRepository;
    private final KDTCourseVideoRepository kdtCourseVideoRepository;
    private final KdtCourseVideoService kdtCourseVideoService;

    @DeleteMapping("/api/admin/KDT/{kdtSessionId}/courseoutline/delete/{kdtCourseOutlineId}")
    public ResponseEntity<String> deleteKdtCourseOutline(
            @PathVariable Long kdtSessionId,
            @PathVariable Long kdtCourseOutlineId
    ){
        // 1. ID로 Entity 조회
        KDTSessionEntity sessionEntity = kdtSessionRepository.findById(kdtSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + kdtSessionId));

        KDTCourseOutlineEntity outline = kdtCourseOutlineRepository.findById(kdtCourseOutlineId)
                .orElseThrow(() -> new IllegalArgumentException("Detail not found for id: " + kdtCourseOutlineId));

        // 2. 해당 목차에 연결된 동영상이 있는지 확인
        boolean hasVideos = kdtCourseVideoRepository.existsByKdtCourseOutlineEntity_KdtCourseOutlineId(kdtCourseOutlineId);

        // 3. 동영상이 있으면 에러를 반환
        if (hasVideos) {
//            throw new IllegalArgumentException("Cannot delete outline because there are associated videos.");
            return ResponseEntity.badRequest().body("강의 폴더 안에 파일이 있다면, 폴더를 삭제할 수 없습니다.");
        }
        kdtCourseOutlineService.deleteKdtCourseOutline(kdtCourseOutlineId);
        return ResponseEntity.ok("삭제되었습니다.");
    }

    @DeleteMapping("/api/admin/KDT/{kdtSessionId}/courseoutline/{kdtCourseOutlineId}/coursevideo/delete/{kdtCourseVideoId}")
    public void deleteKdtCourse(
            @PathVariable Long kdtSessionId,
            @PathVariable Long kdtCourseOutlineId,
            @PathVariable Long kdtCourseVideoId
    ){
        // 1. ID로 Entity 조회
        KDTSessionEntity sessionEntity = kdtSessionRepository.findById(kdtSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + kdtSessionId));

        KDTCourseOutlineEntity outline = kdtCourseOutlineRepository.findById(kdtCourseOutlineId)
                .orElseThrow(() -> new IllegalArgumentException("Detail not found for id: " + kdtCourseOutlineId));

        KDTCourseVideoEntity videoEntity = kdtCourseVideoRepository.findById(kdtCourseVideoId)
                .orElseThrow(() -> new IllegalArgumentException("Detail not found for id: " + kdtCourseVideoId));

        kdtCourseVideoService.deleteKdtCourseVideo(kdtCourseVideoId);
        // 이전 S3용 삭제 주석처리
//        s3VideoService.deleteKdtCourseVideoWithDB(kdtCourseVideoId);
    }


}
