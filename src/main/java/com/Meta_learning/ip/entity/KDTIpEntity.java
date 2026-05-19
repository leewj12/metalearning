package com.Meta_learning.ip.entity;

import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "KDT_ip")
public class KDTIpEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kdt_ip_id")
    private Long kdtIpId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kdt_session_id", referencedColumnName = "kdt_session_id", nullable = false)
    private KDTSessionEntity kdtSessionEntity;


    @Column(name = "kdt_ip_address", nullable = false)
    private String kdtIpAddress;

    @CreatedDate
    @Column(name = "kdt_ip_created_at", updatable = false)
    private LocalDateTime kdtIpCreatedAt;

    @LastModifiedDate
    @Column(name = "kdt_ip_updated_at")
    private LocalDateTime kdtIpUpdatedAt;
}
