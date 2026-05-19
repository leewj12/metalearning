package com.Meta_learning.KDT.KDTentity.KDTBoardEntity;

import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.user.userentity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity // 이 클래스는 JPA 엔티티 클래스로 매핑됨을 나타냄
@Table(name = "KDT_board") // KDT_board 테이블에 매핑됨
@Builder // 빌더 패턴을 사용하여 객체 생성 가능
@Getter // 모든 필드에 대해 getter 메서드 자동 생성
@EntityListeners(AuditingEntityListener.class) // 엔티티 변경 사항을 자동으로 기록하기 위해 사용
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 생성, 접근 제한자를 'protected'로 설정하여 외부에서 직접 생성하지 않도록 함
@AllArgsConstructor // 모든 필드를 포함하는 생성자 생성
public class KDTBoardEntity {

    @Id // 이 필드는 테이블의 기본 키임을 나타냄
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    @Column(name = "KDT_board_id") // 테이블 컬럼명 지정
    private Long kdtBoardId; // 게시판 ID

    @ManyToOne // 다대일 관계 설정
    @JoinColumn(name = "KDT_session_id", nullable = false) // KDT_session_id 컬럼을 외래 키로 사용
    private KDTSessionEntity kdtSessionEntity; // 게시판과 세션 간의 연관 관계

    @ManyToOne // 다대일 관계 설정
    @JoinColumn(name = "user_id", nullable = false) // user_id 컬럼을 외래 키로 사용
    private UserEntity userEntity; // 게시판 작성자(유저)와의 연관 관계

    @Column(name = "KDT_board_title", nullable = false) // 게시판 제목 컬럼
    private String kdtBoardTitle; // 게시판 제목

    @Column(name = "KDT_board_content", nullable = false) // 게시판 내용 컬럼
    private String kdtBoardContent; // 게시판 내용

    @CreatedDate
    @Column(name = "KDT_board_created_at",updatable = false) // 게시판 생성일 컬럼, 수정 불가
    private LocalDateTime kdtBoardCreatedAt; // 게시판 생성 일자 (기본값으로 현재 시각 설정)

    @LastModifiedDate
    @Column(name = "KDT_board_updated_at") // 게시판 수정일 컬럼
    private LocalDateTime kdtBoardUpdatedAt; // 게시판 수정 일자

    @Column(name = "KDT_board_view", nullable = false) // 게시판 조회수 컬럼
    private Long kdtBoardViewCount; // 게시판 조회수

    @Enumerated(EnumType.STRING) // 열거형을 문자열로 저장
    @Column(name = "KDT_board_category", nullable = false) // 게시판 카테고리 컬럼
    private KDTBoardCategory kdtBoardCategory;  // 수정된 필드명 사용

    @Column(name = "KDT_board_hidden", nullable = false) // 게시판 숨김 여부 컬럼
    private Boolean kdtBoardHidden; // 게시판이 숨겨져 있는지 여부

    @Column(name = "KDT_board_answer", nullable = false) // 게시판 답변 여부 컬럼
    private Boolean kdtBoardAnswerCompleted; // 게시판에 답변이 완료되었는지 여부

    // 파일과의 연관 관계 처리
    @OneToMany(mappedBy = "kdtBoardEntity", cascade = CascadeType.ALL, orphanRemoval = true) // Board 엔티티와 관련된 파일 목록
    private List<KDTBoardFileEntity> files; // 게시판과 연관된 파일들만 삭제되도록 설정

    public void incrementViewCount() {
        this.kdtBoardViewCount++;
    }

}
