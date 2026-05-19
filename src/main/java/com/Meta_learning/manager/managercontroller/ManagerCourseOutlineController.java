package com.Meta_learning.manager.managercontroller;

import com.Meta_learning.KDT.KDTentity.KDTCourseOutlineEntity.KDTCourseOutlineEntity;
import com.Meta_learning.KDT.KDTentity.KDTCourseVideoEntity.KDTCourseVideoEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.KDT.KDTrepository.KDTCourseOutlineRepository.KDTCourseOutlineRepository;
import com.Meta_learning.KDT.KDTrepository.KDTCourseVideoRepository.KDTCourseVideoRepository;
import com.Meta_learning.KDT.KDTrepository.KDTSessionRepository.KDTSessionRepository;
import com.Meta_learning.KDT.KDTservice.outline.KdtCourseOutlineService;
import com.Meta_learning.KDT.KDTservice.request.KDTCourseOutlineCreateServiceRequest;
import com.Meta_learning.KDT.KDTservice.request.KDTCourseOutlineUpdateServiceRequest;
import com.Meta_learning.KDT.KDTservice.response.KDTCourseOutlineResponse;
import com.Meta_learning.KDT.KDTservice.response.KDTCourseVideoResponse;
import com.Meta_learning.KDT.KDTservice.video.KdtCourseVideoService;
import com.Meta_learning.admin.dto.request.KDTCourseOutlineCreateRequest;
import com.Meta_learning.admin.dto.request.KDTCourseOutlineUpdateRequest;
import com.Meta_learning.admin.dto.request.KDTCourseVideoCreateRequest;
import com.Meta_learning.admin.dto.request.KDTCourseVideoUpdateRequest;
import com.Meta_learning.manager.managerservice.StaffPermissionService;
import com.Meta_learning.s3.service.request.VideoUpdateRequest;
import com.Meta_learning.s3.service.request.VideoUploadRequest;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userrepository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

// 수정이 제대로 안됩니다 살려주세요
// 수정이 제대로 안됩니다 살려주세요
// 수정이 제대로 안됩니다 살려주세요
// 수정이 제대로 안됩니다 살려주세요
// 수정이 제대로 안됩니다 살려주세요

@Controller
@RequiredArgsConstructor
public class ManagerCourseOutlineController {


    @Value("${spring.servlet.multipart.max-file-size}")  // 필드 주입 방식으로 변경
    private DataSize maxFileSize;

    private final KdtCourseOutlineService kdtCourseOutlineService;
    private final KdtCourseVideoService kdtCourseVideoService;
    private final KDTSessionRepository kdtSessionRepository;
    private final KDTCourseOutlineRepository kdtCourseOutlineRepository;
    private final KDTCourseVideoRepository kdtCourseVideoRepository;
    private final UserRepository userRepository;
    private final StaffPermissionService staffPermissionService;

    @GetMapping("/managers/KDT/{kdtSessionId}/courseoutline/list")
    public String getAllCourseOutlines(@PathVariable Long kdtSessionId, @AuthenticationPrincipal UserEntity user, Model model) {

        // 매니저인지 확인하는 로직
        if (!staffPermissionService.hasAccessToSession(kdtSessionId)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }


        List<KDTCourseOutlineResponse> outlines = kdtCourseOutlineService.getKdtCourseOutlineBySessionId(kdtSessionId);


        model.addAttribute("outlines", outlines);
        return "managers/KDT/courseoutlinelist";
    }

//    @GetMapping("/list/{kdtSessionId}")
//    public String getCourseOutlinesBySessionId(@PathVariable Long kdtSessionId, Model model) {
//        List<KDTCourseOutlineResponse> outlines = kdtCourseOutlineService.getKdtCourseOutline(kdtSessionId);
//        model.addAttribute("outlines", outlines);
//        return "courseOutline/list";
//    }

    @GetMapping("/managers/KDT/{kdtSessionId}/courseoutline")
    public String createKdtCourseOutlineForm(@PathVariable Long kdtSessionId,Model model) {

        if (!staffPermissionService.hasAccessToSession(kdtSessionId)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }

        model.addAttribute("kdtSessionId", kdtSessionId);
        return "managers/KDT/courseoutline";
    }










    @PostMapping("/managers/KDT/{kdtSessionId}/courseoutline")
    public String createKdtCourseOutline(
            @PathVariable Long kdtSessionId,
            @Valid KDTCourseOutlineCreateRequest request,
            BindingResult bindingResult,
            Model model) {

        if (!staffPermissionService.hasAccessToSession(kdtSessionId)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }




        if (bindingResult.hasErrors()) {
            // 폼으로 돌아가 에러 메시지 표시
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "managers/KDT/courseoutline";
        }

        // 1. ID로 Entity 조회
        KDTSessionEntity sessionEntity = kdtSessionRepository.findById(kdtSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + kdtSessionId));

        // 여기부터 수정해야함 //
        // 요청 DTO를 Service 요청 DTO로 변환
        KDTCourseOutlineCreateServiceRequest serviceRequest = request.toServiceRequest(sessionEntity);

        // 서비스 호출하여 데이터 저장
        KDTCourseOutlineResponse savedOutline = kdtCourseOutlineService.createKdtCourseOutline(serviceRequest);


        // 리다이렉트 URL 구성
        return "redirect:/managers/KDT/" +kdtSessionId +"/courseoutline/list";


    }

    @GetMapping("/managers/KDT/{kdtSessionId}/courseoutline/update/{kdtCourseOutlineId}")
    public String updateKdtCourseOutlineForm(
            @PathVariable Long kdtSessionId,
            @PathVariable Long kdtCourseOutlineId,
            Model model) {


        if (!staffPermissionService.hasAccessToSession(kdtSessionId)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }


        KDTCourseOutlineEntity outlineEntity = kdtCourseOutlineRepository.findById(kdtCourseOutlineId)
                .orElseThrow(() -> new IllegalArgumentException("Detail not found for id: " + kdtCourseOutlineId));

        KDTCourseOutlineResponse outline = KDTCourseOutlineResponse.of(outlineEntity);

        model.addAttribute("kdtSessionId", kdtSessionId);
        model.addAttribute("outline", outline);
        return "managers/KDT/courseoutlineupdate";
    }

    @PostMapping("/managers/KDT/{kdtSessionId}/courseoutline/update/{kdtCourseOutlineId}")
    public String updateKdtCourseOutline(
            @PathVariable Long kdtSessionId,
            @PathVariable Long kdtCourseOutlineId,
            @Valid KDTCourseOutlineUpdateRequest request,
            BindingResult bindingResult,
            Model model) {

        if (!staffPermissionService.hasAccessToSession(kdtSessionId)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }



        if (bindingResult.hasErrors()) {
            // 폼으로 돌아가 에러 메시지 표시
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "managers/KDT/courseoutlineupdate";
        }

        // 1. ID로 Entity 조회
        KDTSessionEntity sessionEntity = kdtSessionRepository.findById(kdtSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + kdtSessionId));

        KDTCourseOutlineEntity outline = kdtCourseOutlineRepository.findById(kdtCourseOutlineId)
                .orElseThrow(() -> new IllegalArgumentException("Detail not found for id: " + kdtCourseOutlineId));

        KDTCourseOutlineUpdateServiceRequest serviceRequest = request.toServiceRequest(kdtCourseOutlineId, sessionEntity);

        KDTCourseOutlineResponse modifiedOutline = kdtCourseOutlineService.updateKdtCourseOutline(serviceRequest);

//        return "redirect:/manager/KDT/" + kdtSessionId + "/courseoutline/"+ kdtCourseOutlineId;
        return "redirect:/managers/KDT/" +kdtSessionId +"/courseoutline/list";
    }



    //강의영상 폴더 들어가는곳
    @GetMapping("/managers/KDT/{kdtSessionId}/courseoutline/{kdtCourseOutlineId}/coursevideo/list")
    public String getAllCourseVideo(@PathVariable Long kdtSessionId,
                                    @PathVariable Long kdtCourseOutlineId,
                                    Model model, @AuthenticationPrincipal UserEntity user) {

        if (!staffPermissionService.hasAccessToSession(kdtSessionId)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }

        List<KDTCourseVideoResponse> videos = kdtCourseVideoService.getAllKdtCourseVideoByCourseOutlineId(kdtCourseOutlineId);
        model.addAttribute("videos", videos);


        return "managers/KDT/coursevideolist";
    }






    //kdt 강의 영상 생성하는 곳
    // 강의 동영상 업로드 폼 이동
    @GetMapping("/managers/KDT/{kdtSessionId}/courseoutline/{kdtCourseOutlineId}/coursevideo")
    public String mcreateCourseVideoForm(@PathVariable Long kdtSessionId,
                                        @PathVariable Long kdtCourseOutlineId,
                                        Model model) {


        if (!staffPermissionService.hasAccessToSession(kdtSessionId)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }


        model.addAttribute("kdtSessionId", kdtSessionId);
        model.addAttribute("kdtCourseOutlineId", kdtCourseOutlineId);
        KDTCourseOutlineResponse outline = kdtCourseOutlineService.getKdtCourseOutlineByKdtCourseOutlineId(kdtCourseOutlineId);
        model.addAttribute("category", outline.getKdtCourseOutline());
        Long fileSize= maxFileSize.toMegabytes();
        model.addAttribute("maxFileSize", fileSize);

        return "managers/KDT/coursevideo";
    }

    // 강의 동영상 업로드 제출
    @PostMapping("/managers/KDT/{kdtSessionId}/courseoutline/{kdtCourseOutlineId}/coursevideo")
    public String mcreateCourseVideo(@PathVariable Long kdtSessionId,
                                    @PathVariable Long kdtCourseOutlineId,
                                    @AuthenticationPrincipal UserEntity user,
                                    @Valid @ModelAttribute KDTCourseVideoCreateRequest request,
                                    BindingResult bindingResult,
                                    Model model) {


        if (!staffPermissionService.hasAccessToSession(kdtSessionId)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }



//        log.info("데이터 확인용==========={}",request);
        Long userId = user.getUserId();
        UserEntity findUser = userRepository.findByUserId(userId);

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("kdtSessionId", kdtSessionId);
            model.addAttribute("kdtCourseOutlineId", kdtCourseOutlineId);
            return "managers/KDT/coursevideo";
        }
        // 1. ID로 Entity 조회
        KDTSessionEntity sessionEntity = kdtSessionRepository.findById(kdtSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + kdtSessionId));

        KDTCourseOutlineEntity outline = kdtCourseOutlineRepository.findById(kdtCourseOutlineId)
                .orElseThrow(() -> new IllegalArgumentException("Detail not found for id: " + kdtCourseOutlineId));


        // 2. 동영상 업로드 처리
        VideoUploadRequest serviceRequest = request.toServiceRequest(outline, findUser);

        Long savedVideoId;
        if (serviceRequest.isFileUpload()) {
            if(serviceRequest.getFile().getSize() < maxFileSize.toBytes()){
                // 파일 업로드
                kdtCourseVideoService.saveVideoFile(serviceRequest);
//                savedVideoId = s3VideoService.uploadAndSaveVideo(serviceRequest);
            }else{
                model.addAttribute("msg", "영상의 크기가 너무 큽니다. 최대 용량을 확인하세요.");
                model.addAttribute("loc", "/managers/KDT/" + kdtSessionId + "/courseoutline/" + kdtCourseOutlineId + "/coursevideo");
                return "utility/message";  // 메시지를 표시할 페이지로 리턴
            }
        } else {
            // URL 저장
            savedVideoId = kdtCourseVideoService.saveVideoUrl(serviceRequest);
        }

        model.addAttribute("msg", "강의 영상이 업로드 되었습니다!");
        model.addAttribute("loc", "/managers/KDT/" + kdtSessionId + "/courseoutline/" + kdtCourseOutlineId + "/coursevideo/list");
        return "utility/message";  // 메시지를 표시할 페이지로 리턴

    }







    //kdt 동영상 상세보기
    @GetMapping("/managers/KDT/{kdtSessionId}/courseoutline/{kdtCourseOutlineId}/coursevideo/{kdtCourseVideoId}")
    public String getCourseVideo(@PathVariable Long kdtSessionId,
                                 @PathVariable Long kdtCourseOutlineId,
                                 @PathVariable Long kdtCourseVideoId,
                                 Model model) {

        if (!staffPermissionService.hasAccessToSession(kdtSessionId)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }


        // 동영상 데이터 가져오기
        KDTCourseVideoEntity videoEntity = kdtCourseVideoRepository.findById(kdtCourseVideoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found with ID: " + kdtCourseVideoId));

        KDTCourseVideoResponse video = KDTCourseVideoResponse.of(videoEntity);

        // 모델에 데이터 추가
        model.addAttribute("video", video);
        model.addAttribute("kdtCourseOutlineId", kdtCourseOutlineId);
        model.addAttribute("kdtCourseVideoId", kdtCourseVideoId);
        return "managers/KDT/coursevideodetail";
    }


    //kdt 강의 동영상 수정 폼 이동 하는 곳
    @GetMapping("/managers/KDT/{kdtSessionId}/courseoutline/{kdtCourseOutlineId}/coursevideo/update/{kdtCourseVideoId}")
    public String updateCourseVideoForm(@PathVariable Long kdtSessionId,
                                        @PathVariable Long kdtCourseOutlineId,
                                        @PathVariable Long kdtCourseVideoId,
                                        Model model) {


        if (!staffPermissionService.hasAccessToSession(kdtSessionId)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }


        // 1. ID로 Entity 조회
        KDTSessionEntity sessionEntity = kdtSessionRepository.findById(kdtSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + kdtSessionId));

        KDTCourseOutlineEntity outlineEntity = kdtCourseOutlineRepository.findById(kdtCourseOutlineId)
                .orElseThrow(() -> new IllegalArgumentException("Detail not found for id: " + kdtCourseOutlineId));

        KDTCourseVideoEntity videoEntity = kdtCourseVideoRepository.findById(kdtCourseVideoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found with ID: " + kdtCourseVideoId));

        KDTCourseVideoResponse video = KDTCourseVideoResponse.of(videoEntity);

        model.addAttribute("kdtSessionId", kdtSessionId);
        model.addAttribute("kdtCourseOutlineId", kdtCourseOutlineId);
        model.addAttribute("video", video);

        Long fileSize= maxFileSize.toMegabytes();
        model.addAttribute("maxFileSize", fileSize);
        return "managers/KDT/coursevideoupdate";
    }

    @PostMapping("/managers/KDT/{kdtSessionId}/courseoutline/{kdtCourseOutlineId}/coursevideo/update/{kdtCourseVideoId}")
    public String mupdateCourseVideo(@PathVariable Long kdtSessionId,
                                    @PathVariable Long kdtCourseOutlineId,
                                    @PathVariable Long kdtCourseVideoId,
                                    @AuthenticationPrincipal UserEntity user,
                                    @ModelAttribute @Valid KDTCourseVideoUpdateRequest request,
                                    BindingResult bindingResult,
                                    Model model) {


        if (!staffPermissionService.hasAccessToSession(kdtSessionId)) {
            // 권한이 없을 경우 바로 리다이렉트
            model.addAttribute("msg", "매니저 권한이 필요합니다.");
            model.addAttribute("loc", "/view/manager/KDT/list");  // 리다이렉트 위치 설정
            return "utility/message";  // 리다이렉트 처리
        }


        // 에러가 있을 경우 처리
        if (bindingResult.hasErrors()) {
            // 에러가 있을 경우 다시 수정 폼으로 돌아가기
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("kdtSessionId", kdtSessionId);
            model.addAttribute("kdtCourseOutlineId", kdtCourseOutlineId);

            KDTCourseVideoEntity videoEntity = kdtCourseVideoRepository.findById(kdtCourseVideoId)
                    .orElseThrow(() -> new IllegalArgumentException("Video not found with ID: " + kdtCourseVideoId));
            KDTCourseVideoResponse video = KDTCourseVideoResponse.of(videoEntity);

            model.addAttribute("video", video);
            return "managers/KDT/coursevideoupdate";
        }

        // 1. CourseOutlineEntity와 UserEntity 가져오기
        KDTCourseOutlineEntity outlineEntity = kdtCourseOutlineRepository.findById(kdtCourseOutlineId)
                .orElseThrow(() -> new IllegalArgumentException("Course Outline not found with ID: " + kdtCourseOutlineId));

        Long userId = user.getUserId();
        UserEntity findUser = userRepository.findByUserId(userId);


        // 3. Service 요청으로 변환
        VideoUpdateRequest serviceRequest = request.toServiceRequest(outlineEntity, findUser);

        // 새로운 용량 파일의 용량 확인
        if(serviceRequest.getFile() != null && serviceRequest.getFile().getSize() >= maxFileSize.toBytes()){
            model.addAttribute("msg", "영상의 크기가 너무 큽니다. 최대 용량을 확인하세요.");
            model.addAttribute("loc", "/admin/KDT/" + kdtSessionId + "/courseoutline/" + kdtCourseOutlineId + "/coursevideo");
            return "utility/message";  // 메시지를 표시할 페이지로 리턴
        }

        // 4. 서비스 호출
        kdtCourseVideoService.updateCourseVideo(serviceRequest);

        // 5. 리스트 페이지로 리다이렉트
        return "redirect:/managers/KDT/" + kdtSessionId + "/courseoutline/" + kdtCourseOutlineId + "/coursevideo/" + kdtCourseVideoId;
    }


}
