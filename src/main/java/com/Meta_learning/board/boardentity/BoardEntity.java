package com.Meta_learning.board.boardentity;

import com.Meta_learning.user.userentity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board") // 게시판
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BoardEntity {

    // 기본 키 설정: board_id 컬럼을 엔티티의 기본 키로 사용
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 생성 방식: Identity (자동 증가)
    @Column(name = "board_id") // 해당 컬럼이 테이블에서 'board_id'임을 명시
    private Long boardId;

    // 유저와의 관계 설정: 여러 게시글은 하나의 유저에 속함
    @ManyToOne(fetch = FetchType.LAZY) // 여러 게시글이 하나의 유저에 속함
    @JoinColumn(name = "user_id", referencedColumnName = "user_id") // 'user_id' 컬럼을 기준으로 유저와 연결
    private UserEntity userEntity; // 연관된 유저 엔티티

    // 게시글 제목: 게시글의 제목을 저장
    @Column(name = "board_title", nullable = false) // 'board_title' 컬럼은 필수로 입력되어야 함
    private String boardTitle;

    // 게시글 내용: 게시글의 상세 내용을 저장
    @Column(name = "board_content", nullable = false) // 'board_content' 컬럼은 필수로 입력되어야 함
    private String boardContent;

    // 게시글 생성일: 게시글 작성 시 자동으로 현재 시간이 들어가도록 설정
    @CreatedDate
    @Column(name = "board_created_at", updatable = false) // 기본값으로 현재 시간 설정
    private LocalDateTime boardCreatedAt;

    // 게시글 수정일: 게시글 수정 시 수정 시간이 들어가며, 처음에는 null일 수 있음
    @LastModifiedDate
    @Column(name = "board_updated_at") // 수정일은 선택 사항
    private LocalDateTime boardUpdatedAt;

    // 게시글 조회수: 게시글이 조회된 횟수를 저장
    @Column(name = "board_view") // 조회수 컬럼
    private Long boardView;

    // 게시글 카테고리: 게시글의 유형을 나타내는 값 (예: 공지사항, QnA, 수강생 후기 등)
    @Enumerated(EnumType.STRING) // 열거형을 문자열로 저장
    @Column(name = "board_category", nullable = false) // 'board_category' 컬럼은 필수로 입력되어야 함
    private BoardCategory boardCategory; // 열거형 타입으로 카테고리 저장

    // 게시글 숨김 여부: 게시글이 숨겨졌는지 여부를 나타냄
    @Column(name = "board_hidden", nullable = false) // 'board_hidden' 컬럼은 필수로 입력되어야 함
    private Boolean boardHidden;

    // QnA 게시판에서 답변 여부: 게시글에 답변이 완료되었는지 여부를 나타냄
    @Column(name = "board_answer", nullable = false) // 'board_answer' 컬럼은 필수로 입력되어야 함
    private Boolean boardAnswer;

    // 수정된 부분: CascadeType.REMOVE, orphanRemoval=true 추가
    @Builder.Default
    @OneToMany(mappedBy = "boardEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BoardFileEntity> boardFiles = new ArrayList<>();

}
