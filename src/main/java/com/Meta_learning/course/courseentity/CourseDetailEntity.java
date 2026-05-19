package com.Meta_learning.course.courseentity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class CourseDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_detail_id") // 상세 정보 ID
    private Long courseDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "course_id", nullable = false)
    private CourseEntity course; // 코스 참조

//    @Column(name = "course_detail_outline", nullable = false) // 상세 목차
    @Column(name = "course_detail_outline") // 상세 목차
    private String courseDetailOutline;

    @Column(name = "course_detail_title", nullable = false) // 상세 제목
    private String courseDetailTitle;

    @Column(name = "course_detail_order", nullable = false) // 순서
    private Integer courseDetailOrder;

    @Column(name = "course_detail_content") // 상세 내용
    private String courseDetailContent;

    @CreatedDate
    @Column(name = "course_detail_created_at", updatable = false) // 생성 날짜
    private LocalDateTime courseDetailCreatedAt;

    @LastModifiedDate
    @Column(name = "course_detail_updated_at") // 수정 날짜
    private LocalDateTime courseDetailUpdatedAt;

    public void updateDetailTitle(String courseDetailTitle) {
        this.courseDetailTitle = courseDetailTitle;
    }

    public void updateCourseDetail(String courseDetailOutline, String courseDetailTitle, String courseDetailContent) {
        this.courseDetailOutline = courseDetailOutline;
        this.courseDetailTitle = courseDetailTitle;
        this.courseDetailContent = courseDetailContent;
        courseDetailUpdatedAt = LocalDateTime.now();
    }
}
