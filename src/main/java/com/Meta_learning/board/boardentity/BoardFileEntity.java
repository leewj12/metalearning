package com.Meta_learning.board.boardentity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_file") // board_file 테이블에 매핑되는 엔티티 클래스
@Builder // 빌더 패턴을 사용하여 객체를 생성할 수 있도록 설정
@Getter // getter 메서드 자동 생성
@EntityListeners(AuditingEntityListener.class) // 엔티티 변경 사항을 자동으로 기록하기 위해 사용
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 생성, 접근 제한자로 보호
@AllArgsConstructor // 모든 필드를 포함하는 생성자 생성
public class BoardFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 생성 방식: Identity (자동 증가)
    @Column(name = "file_id") // 해당 컬럼이 테이블에서 'file_id'임을 명시
    private Long fileId; // 파일의 고유 ID

    @ManyToOne(fetch = FetchType.LAZY) // 여러 파일이 하나의 게시글에 속함
    @JoinColumn(name = "board_id", referencedColumnName = "board_id") // 'board_id' 컬럼을 기준으로 게시글과 연결
    private BoardEntity boardEntity; // 연관된 게시글 엔티티

    @Column(name = "board_file_name", nullable = false) // 'board_file_name' 컬럼은 필수로 입력되어야 함
    private String fileName; // 파일의 실제 이름

    @Column(name = "board_file_UUID", nullable = false) // 'board_file_UUID' 컬럼은 필수로 입력되어야 함
    private String fileUUID; // 파일을 고유하게 식별할 수 있는 UUID

    @Column(name = "board_file_size", nullable = false) // 'board_file_size' 컬럼은 필수로 입력되어야 함
    private Long fileSize; // 파일의 크기 (바이트 단위)

    @Column(name = "board_file_type", nullable = false) // 'board_file_type' 컬럼은 필수로 입력되어야 함
    private String fileType; // 파일의 MIME 타입 (예: image/jpeg, application/pdf 등)

    @CreatedDate
    @Column(name = "board_file_time") // 기본값으로 현재 시간 설정
    private LocalDateTime fileTime; // 파일이 업로드된 시간
}
