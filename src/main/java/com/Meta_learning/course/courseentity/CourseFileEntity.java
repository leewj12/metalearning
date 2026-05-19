package com.Meta_learning.course.courseentity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_file")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class CourseFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_file_id") // 파일 ID
    private Long courseFileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_detail_id", referencedColumnName = "course_detail_id", nullable = false)
    private CourseDetailEntity courseDetail; // 코스 상세 참조

    @Column(name = "course_file_name", nullable = false) // 파일 이름
    private String courseFileName;

    @Column(name = "course_file_UUID", nullable = false) // 파일 UUID
    private String courseFileUUID;

    @Column(name = "course_file_size", nullable = false) // 파일 크기
    private Long courseFileSize;

    @Column(name = "course_file_type", nullable = false) // 파일 타입
    private String courseFileType;

    @CreatedDate
    @Column(name = "course_file_created_at", updatable = false) // 파일 생성 시간
    private LocalDateTime courseFileCreatedAt;

    public void updateCourseFile(String courseFileName, String courseFileUUID, Long courseFileSize, String courseFileType) {
        this.courseFileName = courseFileName;
        this.courseFileUUID = courseFileUUID;
        this.courseFileSize = courseFileSize;
        this.courseFileType = courseFileType;
    }
}
