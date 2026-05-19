package com.Meta_learning.KDT.KDTrepository.KDTInstrEvalRepository;

import com.Meta_learning.KDT.KDTentity.KDTInstrEvalEntity.KDTInstrEvalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KDTInstrEvalRepository extends JpaRepository<KDTInstrEvalEntity,Long> {

}
