package com.Meta_learning.KDT.KDTservice.video;


import com.Meta_learning.KDT.KDTservice.response.KDTCourseVideoResponse;
import com.Meta_learning.s3.service.request.VideoUpdateRequest;
import com.Meta_learning.s3.service.request.VideoUploadRequest;

import java.util.List;

public interface KdtCourseVideoService {


    List<KDTCourseVideoResponse> getAllKdtCourseVideoByCourseOutlineId(Long kdtCourseOutlineId);

    Long saveVideoUrl(VideoUploadRequest serviceRequest);

    void saveVideoFile(VideoUploadRequest serviceRequest);

    void deleteKdtCourseVideo(Long kdtCourseVideoId);

    void updateCourseVideo(VideoUpdateRequest serviceRequest);
//
//    KDTCourseOutlineResponse createKdtCourseOutline(KDTCourseOutlineCreateServiceRequest request);
//
//    KDTCourseOutlineResponse updateKdtCourseOutline(KDTCourseOutlineUpdateServiceRequest request);
//
//    void deleteKdtCourseOutline(Long id);
}
