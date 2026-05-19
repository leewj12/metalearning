package com.Meta_learning.KDT.KDTentity.KDTBoardEntity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity // 이 클래스는 JPA 엔티티로 매핑되며 데이터베이스 테이블에 해당 클래스가 대응됨을 나타냄
@Table(name = "kdt_file") // 해당 엔티티가 매핑될 테이블 이름을 소문자로 설정
@Builder // 빌더 패턴을 사용하여 객체를 생성할 수 있도록 설정
@Getter // 모든 필드에 대해 자동으로 getter 메서드를 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 생성, 접근 제한자를 'protected'로 설정하여 외부에서 직접 객체를 생성할 수 없게 설정
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자 생성
public class KDTBoardFileEntity { // 'KdtBoardFileEntity' 클래스는 게시판 파일 엔티티를 나타냄

    @Id // 이 필드는 테이블의 기본 키임을 나타냄
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키가 자동으로 증가하도록 설정
    @Column(name = "kdt_file_id") // 테이블의 'kdt_file_id' 컬럼에 매핑
    private Long kdtFileId; // 파일의 고유 ID

    @ManyToOne // 다대일 관계 설정 (하나의 게시판 파일에 여러 파일이 포함될 수 있음)
    @JoinColumn(name = "kdt_board_id", nullable = false) // 외래 키로 'kdt_board_id' 컬럼을 사용하여 게시판과 연결
    private KDTBoardEntity kdtBoardEntity; // 해당 파일이 속한 게시판 엔티티

    @Column(name = "kdt_file_name", nullable = false) // 파일 이름을 저장하는 컬럼
    private String kdtFileName; // 파일의 이름

    @Column(name = "kdt_file_uuid", nullable = false) // 파일의 고유 UUID를 저장하는 컬럼 (파일 중복 방지)
    private String kdtFileUUID; // 파일의 UUID (중복 방지 및 고유 식별자)

    @Column(name = "kdt_file_size", nullable = false) // 파일 크기를 저장하는 컬럼
    private Long kdtFileSize; // 파일의 크기 (바이트 단위)

    @Column(name = "kdt_file_type", nullable = false) // 파일의 유형(파일 형식)을 저장하는 컬럼
    private String kdtFileType; // 파일의 타입 (예: image/jpeg, application/pdf 등)

    @CreatedDate
    @Column(name = "kdt_file_time", nullable = false) // 파일 업로드 시간을 저장하는 컬럼
    private LocalDateTime kdtFileTime; // 파일이 업로드된 시간 (기본값으로 현재 시간으로 설정)
}
