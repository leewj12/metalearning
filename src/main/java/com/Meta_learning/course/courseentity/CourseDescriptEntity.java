package com.Meta_learning.course.courseentity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_descript")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class CourseDescriptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_descript_id") // 코스 상세 설명 ID
    private Long courseDescriptId;

    // @Column(name = "course_descript_content", nullable = false) // 상세 설명 내용
    @Column(name = "course_descript_content") // 상세 설명 내용
    private String courseDescriptContent;

    @CreatedDate
    @Column(name = "course_descript_created_at", updatable = false) // 생성 날짜
    private LocalDateTime courseDescriptCreatedAt;

    @LastModifiedDate
    @Column(name = "course_descript_updated_at") // 수정 날짜
    private LocalDateTime courseDescriptUpdatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "course_id", nullable = false)
    private CourseEntity course; // 코스 참조

    public void updateCourseDescriptContent(String courseDescriptContent) {
        this.courseDescriptContent = courseDescriptContent;
    }
}
