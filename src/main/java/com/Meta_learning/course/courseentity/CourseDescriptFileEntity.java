package com.Meta_learning.course.courseentity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_descript_file")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class CourseDescriptFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_descript_file_id") // 파일 ID
    private Long courseDescriptFileId;

    @Column(name = "course_descript_file_name", nullable = false) // 파일 이름
    private String courseDescriptFileName;

    @Column(name = "course_descript_file_UUID", nullable = false) // 파일 UUID
    private String courseDescriptFileUUID;

    @Column(name = "course_descript_file_size", nullable = false) // 파일 크기
    private Long courseDescriptFileSize;

    @Column(name = "course_descript_file_type", nullable = false) // 파일 타입
    private String courseDescriptFileType;

    @CreatedDate
    @Column(name = "course_descript_file_created_at", updatable = false) // 파일 업로드 시간
    private LocalDateTime courseDescriptFileCreatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_descript_id", referencedColumnName = "course_descript_id", nullable = false)
    private CourseDescriptEntity courseDescript; // 코스 상세 설명 참조
}
