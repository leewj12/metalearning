package com.Meta_learning.student.studentpermissionservice;

import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionEvalDTO.KDTSessionEvalDTO;

import java.util.List;

public interface StudentService {


    List<KDTSessionDTO> getSessionsByUserId(Long userId);

    int saveCourseReview(KDTSessionEvalDTO kdtSessionEvalDTO, Long userId);

}
