package com.Meta_learning.course.courseentity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_video")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class CourseVideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_video_id") // 동영상 ID
    private Long courseVideoId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_detail_id", referencedColumnName = "course_detail_id", nullable = false)
    private CourseDetailEntity courseDetail; // 코스 상세 참조

    @Column(name = "course_video_file", nullable = false) // 동영상 파일 이름
    private String courseVideoFile;

    @Column(name = "course_video_UUID", nullable = false) // 동영상 UUID 또는 URL 링크
    private String courseVideoUUID;

    @Column(name = "course_video_size", nullable = false) // 동영상 파일 크기
    private Long courseVideoSize;

    @Column(name = "course_video_type", nullable = false) // 동영상 파일 타입
    private String courseVideoType;

    @Column(name = "course_video_playtime", nullable = false) // 동영상 재생 시간 (초 단위)
    private Long courseVideoPlaytime;

    @CreatedDate
    @Column(name = "course_video_created_at", updatable = false) // 동영상 업로드 시간
    private LocalDateTime courseVideoCreatedAt;

    public void updateCourseVideo(String courseVideoFile, String courseVideoUUID, Long courseVideoSize, String courseVideoType) {
        this.courseVideoFile = courseVideoFile;
        this.courseVideoUUID = courseVideoUUID;
        this.courseVideoSize = courseVideoSize;
        this.courseVideoType = courseVideoType;
    }
}
