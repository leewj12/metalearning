package com.Meta_learning.course.courseentity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Entity
@Table(name = "course")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class CourseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id") // 코스 ID
    private Long courseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instr_id", referencedColumnName = "instr_id", nullable = false)
    private InstrEntity instr; // 강사 ID

    @Column(name = "course_thumbnail") // 코스 썸네일
    private String courseThumbnail;

    @Column(name = "course_title", nullable = false) // 코스 제목
    private String courseTitle;

    //@Column(name = "course_descript", nullable = false) // 코스 설명
    @Column(name = "course_descript") // 코스 한줄 설명
    private String courseDescript;

    @Column(name = "course_price", nullable = false) // 코스 가격
    private Long coursePrice;

    @CreatedDate
    @Column(name = "course_created_at", updatable = false) // 생성 날짜
    private LocalDateTime courseCreatedAt;

    @LastModifiedDate
    @Column(name = "course_updated_at") // 수정 날짜
    private LocalDateTime courseUpdatedAt;

    @Column(name = "course_curriculum") // 코스 커리큘럼
    private String courseCurriculum;

    //@Column(name = "course_category", nullable = false) // 코스 카테고리
    @Column(name = "course_category") // 코스 카테고리
    private String courseCategory;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "course_difficulty", nullable = false) // 코스 난이도
    private CourseDifficulty courseDifficulty = CourseDifficulty.BEGINNER; // 기본값: 입문

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "course_status", nullable = false) // 코스 상태
    private CourseStatus courseStatus = CourseStatus.PENDING; // 기본값: 대기

    // 업데이트 메서드
    public void update(String courseThumbnail, String courseTitle, String courseDescript,
                       Long coursePrice, String courseCurriculum, String courseCategory,
                       CourseDifficulty courseDifficulty, CourseStatus courseStatus) {
        if (courseThumbnail != null) {
            this.courseThumbnail = courseThumbnail;
        }
        if (courseTitle != null) {
            this.courseTitle = courseTitle;
        }
        if (courseDescript != null) {
            this.courseDescript = courseDescript;
        }
        if (coursePrice != null) {
            this.coursePrice = coursePrice;
        }
        if (courseCurriculum != null) {
            this.courseCurriculum = courseCurriculum;
        }
        if (courseCategory != null) {
            this.courseCategory = courseCategory;
        }
        if (courseDifficulty != null) {
            this.courseDifficulty = courseDifficulty;
        }
        if (courseStatus != null) {
            this.courseStatus = courseStatus;
        }
        this.courseUpdatedAt = LocalDateTime.now(); // 항상 현재 시간으로 수정일 갱신
    }

    public void updateCourseStatus(CourseStatus courseStatus) {
        this.courseStatus = courseStatus; // 상태 변경
        this.courseUpdatedAt = LocalDateTime.now(); // 수정 시간 갱신
    }
}