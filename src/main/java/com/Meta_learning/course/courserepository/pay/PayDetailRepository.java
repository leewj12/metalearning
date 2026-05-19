package com.Meta_learning.course.courserepository.pay;

import com.Meta_learning.course.courseentity.pay.PayDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayDetailRepository extends JpaRepository<PayDetailEntity, Long> {
}
