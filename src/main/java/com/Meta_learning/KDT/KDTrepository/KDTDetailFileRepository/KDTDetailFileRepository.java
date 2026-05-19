package com.Meta_learning.KDT.KDTrepository.KDTDetailFileRepository;


import com.Meta_learning.KDT.KDTentity.KDTDetailEntity.KDTDetailEntity;
import com.Meta_learning.KDT.KDTentity.KDTDetailFileEntity.KDTDetailFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KDTDetailFileRepository extends JpaRepository<KDTDetailFileEntity,Long> {
    List<KDTDetailFileEntity> findByKdtDetailEntity(KDTDetailEntity kdtDetailEntity);
}
