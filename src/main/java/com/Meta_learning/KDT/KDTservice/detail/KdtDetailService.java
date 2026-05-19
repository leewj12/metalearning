package com.Meta_learning.KDT.KDTservice.detail;


import com.Meta_learning.KDT.KDTentity.KDTDetailEntity.KDTDetailEntity;
import com.Meta_learning.KDT.KDTservice.request.KDTDetailCreateServiceRequest;
import com.Meta_learning.KDT.KDTservice.request.KDTDetailUpdateServiceRequest;
import com.Meta_learning.KDT.KDTservice.response.KDTDetailResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface KdtDetailService {

    KDTDetailResponse getKdtDetail(Long kdtDetailId);

    KDTDetailResponse createKdtDetail(KDTDetailCreateServiceRequest request);

    void updateKdtDetail(Long kdtDetailId, KDTDetailUpdateServiceRequest request, MultipartFile[] newFiles, List<Long> deleteFileIds) throws IOException;

    KDTDetailEntity getDetailBySessionId(Long sessionId);

    void deleteKdtDetail(Long kdtDetailId);
}


