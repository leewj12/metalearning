package com.Meta_learning.KDT.KDTentity.KDTDetailEntity;


import com.Meta_learning.KDT.KDTentity.KDTDetailFileEntity.KDTDetailFileEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
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
@Table(name = "KDT_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class KDTDetailEntity { // 홍보글 상세내용

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KDT_detail_id")
    private Long kdtDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KDT_session_id", referencedColumnName = "KDT_session_id", nullable = false)
    private KDTSessionEntity kdtSessionEntity;  // 국비회차id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity userEntity;              // 국비 담당자 user_id()

    @Column(name = "KDT_detail_content", nullable = false)
    private String kdtDetailContent;            // 상세 내용

    @CreatedDate
    @Column(name = "KDT_detail_created_at", nullable = false, updatable = false)
    private LocalDateTime kdtDetailCreatedAt;   // 상세 작성일

    @LastModifiedDate
    @Column(name = "KDT_detail_updated_at")
    private LocalDateTime kdtDetailUpdatedAt;   // 상세 수정일

    public void updateContent(String content) {
        this.kdtDetailContent = content;
    }

    @OneToMany(mappedBy = "kdtDetailEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<KDTDetailFileEntity> files = new ArrayList<>();

    public void addFile(KDTDetailFileEntity fileEntity) {
        files.add(fileEntity);
        fileEntity.setKdtDetailEntity(this); // 연관 관계 설정
    }

    public void removeFile(KDTDetailFileEntity fileEntity) {
        files.remove(fileEntity);
        fileEntity.setKdtDetailEntity(null); // 연관 관계 해제
    }
//    @Builder
//    private KDTDetailEntity(KDTSessionEntity kdtSessionEntity, UserEntity userEntity, String kdtDetailContent, LocalDateTime kdtDetailCreatedAt, LocalDateTime kdtDetailUpdatedAt) {
//        this.kdtSessionEntity = kdtSessionEntity;
//        this.userEntity = userEntity;
//        this.kdtDetailContent = kdtDetailContent;
//        this.kdtDetailCreatedAt = kdtDetailCreatedAt;
//        this.kdtDetailUpdatedAt = kdtDetailUpdatedAt;
//    }
}
