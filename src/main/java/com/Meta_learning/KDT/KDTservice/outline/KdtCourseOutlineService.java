package com.Meta_learning.KDT.KDTservice.outline;


import com.Meta_learning.KDT.KDTservice.request.KDTCourseOutlineCreateServiceRequest;
import com.Meta_learning.KDT.KDTservice.request.KDTCourseOutlineUpdateServiceRequest;
import com.Meta_learning.KDT.KDTservice.response.KDTCourseOutlineResponse;

import java.util.List;

public interface KdtCourseOutlineService {

    List<KDTCourseOutlineResponse> getKdtCourseOutlineBySessionId(Long kdtSessionId);

    KDTCourseOutlineResponse createKdtCourseOutline(KDTCourseOutlineCreateServiceRequest request);

    KDTCourseOutlineResponse updateKdtCourseOutline(KDTCourseOutlineUpdateServiceRequest request);

    KDTCourseOutlineResponse getKdtCourseOutlineByKdtCourseOutlineId(Long kdtCourseOutlineId);



    void deleteKdtCourseOutline(Long id);
}
