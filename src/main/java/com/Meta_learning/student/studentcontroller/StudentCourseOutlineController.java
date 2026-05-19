package com.Meta_learning.student.studentcontroller;


import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
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
import com.Meta_learning.s3.service.request.VideoUpdateRequest;
import com.Meta_learning.s3.service.request.VideoUploadRequest;
import com.Meta_learning.student.studentpermissionservice.StudentPermissionService;
import com.Meta_learning.student.studentpermissionservice.StudentService;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userrepository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StudentCourseOutlineController {

    private final KdtCourseOutlineService kdtCourseOutlineService;
    private final KdtCourseVideoService kdtCourseVideoService;
    private final KDTSessionRepository kdtSessionRepository;
    private final KDTCourseOutlineRepository kdtCourseOutlineRepository;
    private final KDTCourseVideoRepository kdtCourseVideoRepository;
    private final UserRepository userRepository;
    private final StudentPermissionService studentPermissionService;
    private final StudentService studentService;

    @GetMapping("/student/KDT/{kdtSessionId}/courseoutline/list")
    public String getAllCourseOutlines(@PathVariable Long kdtSessionId,Model model,  @AuthenticationPrincipal UserEntity user) {



        if(!studentPermissionService.hasAccessToSession(kdtSessionId)){
            // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 학생만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }

        Long userId = user.getUserId();

        // 유저 ID로 세션 목록을 가져옴
        List<KDTSessionDTO> sessions = studentService.getSessionsByUserId(userId);

        // 첫 번째 세션의 ID를 가져오기 (세션이 있을 경우)
        Long sessionId = sessions.isEmpty() ? null : sessions.get(0).getKdtSessionId();

        // 모델에 필요한 속성 추가
        model.addAttribute("sessionId", sessionId);  // 첫 번째 세션 ID
        model.addAttribute("hasSessions", !sessions.isEmpty());  // 세션이 있으면 true



        List<KDTCourseOutlineResponse> outlines = kdtCourseOutlineService.getKdtCourseOutlineBySessionId(kdtSessionId);
        model.addAttribute("outlines", outlines);


        return "student/KDT/courseoutlinelist";
    }





//    @GetMapping("/list/{kdtSessionId}")
//    public String getCourseOutlinesBySessionId(@PathVariable Long kdtSessionId, Model model) {
//        List<KDTCourseOutlineResponse> outlines = kdtCourseOutlineService.getKdtCourseOutline(kdtSessionId);
//        model.addAttribute("outlines", outlines);
//        return "courseOutline/list";
//    }

//    @GetMapping("/student/KDT/{kdtSessionId}/courseoutline")
//    public String createKdtCourseOutlineForm(@PathVariable Long kdtSessionId,Model model) {
//        model.addAttribute("kdtSessionId", kdtSessionId);
//        return "student/KDT/courseoutline";
//    }
//
//    @PostMapping("/student/KDT/{kdtSessionId}/courseoutline")
//    public String createKdtCourseOutline(
//            @PathVariable Long kdtSessionId,
//            @Valid KDTCourseOutlineCreateRequest request,
//            BindingResult bindingResult,
//            Model model) {
//
//        if (bindingResult.hasErrors()) {
//            // 폼으로 돌아가 에러 메시지 표시
//            model.addAttribute("errors", bindingResult.getAllErrors());
//            return "student/KDT/courseoutline";
//        }
//
//        // 1. ID로 Entity 조회
//        KDTSessionEntity sessionEntity = kdtSessionRepository.findById(kdtSessionId)
//                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + kdtSessionId));
//
//        // 여기부터 수정해야함 //
//        // 요청 DTO를 Service 요청 DTO로 변환
//        KDTCourseOutlineCreateServiceRequest serviceRequest = request.toServiceRequest(sessionEntity);
//
//        // 서비스 호출하여 데이터 저장
//        KDTCourseOutlineResponse savedOutline = kdtCourseOutlineService.createKdtCourseOutline(serviceRequest);
//
//
//        // 리다이렉트 URL 구성
//        return "redirect:/student/KDT/" +kdtSessionId +"/courseoutline/list";
//
//
//    }

//    @GetMapping("/student/KDT/{kdtSessionId}/courseoutline/update/{kdtCourseOutlineId}")
//    public String updateKdtCourseOutlineForm(
//            @PathVariable Long kdtSessionId,
//            @PathVariable Long kdtCourseOutlineId,
//            Model model) {
//
//        KDTCourseOutlineEntity outlineEntity = kdtCourseOutlineRepository.findById(kdtCourseOutlineId)
//                .orElseThrow(() -> new IllegalArgumentException("Detail not found for id: " + kdtCourseOutlineId));
//
//        KDTCourseOutlineResponse outline = KDTCourseOutlineResponse.of(outlineEntity);
//
//        model.addAttribute("kdtSessionId", kdtSessionId);
//        model.addAttribute("outline", outline);
//        return "student/KDT/courseoutlineupdate";
//    }
//
//    @PostMapping("/student/KDT/{kdtSessionId}/courseoutline/update/{kdtCourseOutlineId}")
//    public String updateKdtCourseOutline(
//            @PathVariable Long kdtSessionId,
//            @PathVariable Long kdtCourseOutlineId,
//            @Valid KDTCourseOutlineUpdateRequest request,
//            BindingResult bindingResult,
//            Model model) {
//
//        if (bindingResult.hasErrors()) {
//            // 폼으로 돌아가 에러 메시지 표시
//            model.addAttribute("errors", bindingResult.getAllErrors());
//            return "student/KDT/courseoutlineupdate";
//        }
//
//        // 1. ID로 Entity 조회
//        KDTSessionEntity sessionEntity = kdtSessionRepository.findById(kdtSessionId)
//                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + kdtSessionId));
//
//        KDTCourseOutlineEntity outline = kdtCourseOutlineRepository.findById(kdtCourseOutlineId)
//                .orElseThrow(() -> new IllegalArgumentException("Detail not found for id: " + kdtCourseOutlineId));
//
//        KDTCourseOutlineUpdateServiceRequest serviceRequest = request.toServiceRequest(kdtCourseOutlineId, sessionEntity);
//
//        KDTCourseOutlineResponse modifiedOutline = kdtCourseOutlineService.updateKdtCourseOutline(serviceRequest);
//
////        return "redirect:/student/KDT/" + kdtSessionId + "/courseoutline/"+ kdtCourseOutlineId;
//        return "redirect:/student/KDT/" +kdtSessionId +"/courseoutline/list";
//    }

    @GetMapping("/student/KDT/{kdtSessionId}/courseoutline/{kdtCourseOutlineId}/coursevideo/list")
    public String getAllCourseVideo(@PathVariable Long kdtSessionId,
                                    @PathVariable Long kdtCourseOutlineId,
                                    @AuthenticationPrincipal UserEntity user,
                                    Model model) {
        List<KDTCourseVideoResponse> videos = kdtCourseVideoService.getAllKdtCourseVideoByCourseOutlineId(kdtCourseOutlineId);
        model.addAttribute("videos", videos);

        Long userId = user.getUserId();
        // 유저 ID로 세션 목록을 가져옴
        List<KDTSessionDTO> sessions = studentService.getSessionsByUserId(userId);

        // 첫 번째 세션의 ID를 가져오기 (세션이 있을 경우)
        Long sessionId = sessions.isEmpty() ? null : sessions.get(0).getKdtSessionId();

        // 모델에 필요한 속성 추가
        model.addAttribute("sessionId", sessionId);  // 첫 번째 세션 ID
        model.addAttribute("hasSessions", !sessions.isEmpty());  // 세션이 있으면 true

        return "student/KDT/coursevideolist";
    }

//    @GetMapping("/student/KDT/{kdtSessionId}/courseoutline/{kdtCourseOutlineId}/coursevideo")
//    public String createCourseVideoForm(@PathVariable Long kdtSessionId,
//                                    @PathVariable Long kdtCourseOutlineId,
//                                    Model model) {
//        model.addAttribute("kdtSessionId", kdtSessionId);
//        model.addAttribute("kdtCourseOutlineId", kdtCourseOutlineId);
//        return "student/KDT/coursevideo";
//    }
//
//    @PostMapping("/student/KDT/{kdtSessionId}/courseoutline/{kdtCourseOutlineId}/coursevideo")
//    public String createCourseVideo(@PathVariable Long kdtSessionId,
//                                       @PathVariable Long kdtCourseOutlineId,
//                                       @AuthenticationPrincipal UserEntity user,
//                                       @Valid @ModelAttribute KDTCourseVideoCreateRequest request,
//                                       BindingResult bindingResult,
//                                        Model model) {
//
//        Long userId = user.getUserId();
//        UserEntity findUser = userRepository.findByUserId(userId);
//
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("errors", bindingResult.getAllErrors());
//            model.addAttribute("kdtSessionId", kdtSessionId);
//            model.addAttribute("kdtCourseOutlineId", kdtCourseOutlineId);
//            return "student/KDT/coursevideo";
//        }
//        // 1. ID로 Entity 조회
//        KDTSessionEntity sessionEntity = kdtSessionRepository.findById(kdtSessionId)
//                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + kdtSessionId));
//
//        KDTCourseOutlineEntity outline = kdtCourseOutlineRepository.findById(kdtCourseOutlineId)
//                .orElseThrow(() -> new IllegalArgumentException("Detail not found for id: " + kdtCourseOutlineId));
//
//        // 2. 동영상 업로드 처리
//        VideoUploadRequest serviceRequest = request.toServiceRequest(outline, findUser);
//
////        Long savedVideoId = s3VideoService.uploadAndSaveVideo(serviceRequest);
//
//
//        // 3. 리다이렉트
//        return "redirect:/student/KDT/" + kdtSessionId + "/courseoutline/" + kdtCourseOutlineId + "/coursevideo/list";
//    }

    @GetMapping("/student/KDT/{kdtSessionId}/courseoutline/{kdtCourseOutlineId}/coursevideo/{kdtCourseVideoId}")
    public String getCourseVideo(@PathVariable Long kdtSessionId,
                                           @PathVariable Long kdtCourseOutlineId,
                                           @PathVariable Long kdtCourseVideoId,
                                            @AuthenticationPrincipal UserEntity user,
                                           Model model) {
        // 동영상 데이터 가져오기
        KDTCourseVideoEntity videoEntity = kdtCourseVideoRepository.findById(kdtCourseVideoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found with ID: " + kdtCourseVideoId));

        KDTCourseVideoResponse video = KDTCourseVideoResponse.of(videoEntity);

        // 모델에 데이터 추가
        model.addAttribute("video", video);
        model.addAttribute("kdtCourseOutlineId", kdtCourseOutlineId);
        model.addAttribute("kdtCourseVideoId", kdtCourseVideoId);

        Long userId = user.getUserId();
        // 유저 ID로 세션 목록을 가져옴
        List<KDTSessionDTO> sessions = studentService.getSessionsByUserId(userId);

        // 첫 번째 세션의 ID를 가져오기 (세션이 있을 경우)
        Long sessionId = sessions.isEmpty() ? null : sessions.get(0).getKdtSessionId();

        // 모델에 필요한 속성 추가
        model.addAttribute("sessionId", sessionId);  // 첫 번째 세션 ID
        model.addAttribute("hasSessions", !sessions.isEmpty());  // 세션이 있으면 true

        return "student/KDT/coursevideodetail";
    }

//    @GetMapping("/student/KDT/{kdtSessionId}/courseoutline/{kdtCourseOutlineId}/coursevideo/update/{kdtCourseVideoId}")
//    public String updateCourseVideoForm(@PathVariable Long kdtSessionId,
//                                        @PathVariable Long kdtCourseOutlineId,
//                                        @PathVariable Long kdtCourseVideoId,
//                                        Model model) {
//        // 1. ID로 Entity 조회
//        KDTSessionEntity sessionEntity = kdtSessionRepository.findById(kdtSessionId)
//                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + kdtSessionId));
//
//        KDTCourseOutlineEntity outlineEntity = kdtCourseOutlineRepository.findById(kdtCourseOutlineId)
//                .orElseThrow(() -> new IllegalArgumentException("Detail not found for id: " + kdtCourseOutlineId));
//
//        KDTCourseVideoEntity videoEntity = kdtCourseVideoRepository.findById(kdtCourseVideoId)
//                .orElseThrow(() -> new IllegalArgumentException("Video not found with ID: " + kdtCourseVideoId));
//
//        KDTCourseVideoResponse video = KDTCourseVideoResponse.of(videoEntity);
//
//        model.addAttribute("kdtSessionId", kdtSessionId);
//        model.addAttribute("kdtCourseOutlineId", kdtCourseOutlineId);
//        model.addAttribute("video", video);
//        return "student/KDT/coursevideoupdate";
//    }
//
//    @PostMapping("/student/KDT/{kdtSessionId}/courseoutline/{kdtCourseOutlineId}/coursevideo/update/{kdtCourseVideoId}")
//    public String updateCourseVideo(@PathVariable Long kdtSessionId,
//                                    @PathVariable Long kdtCourseOutlineId,
//                                    @PathVariable Long kdtCourseVideoId,
//                                    @AuthenticationPrincipal UserEntity user,
//                                    @ModelAttribute @Valid KDTCourseVideoUpdateRequest request,
//                                    BindingResult bindingResult,
//                                    Model model) {
//
//        // 에러가 있을 경우 처리
//        if (bindingResult.hasErrors()) {
//            // 에러가 있을 경우 다시 수정 폼으로 돌아가기
//            model.addAttribute("errors", bindingResult.getAllErrors());
//            model.addAttribute("kdtSessionId", kdtSessionId);
//            model.addAttribute("kdtCourseOutlineId", kdtCourseOutlineId);
//
//            KDTCourseVideoEntity videoEntity = kdtCourseVideoRepository.findById(kdtCourseVideoId)
//                    .orElseThrow(() -> new IllegalArgumentException("Video not found with ID: " + kdtCourseVideoId));
//            KDTCourseVideoResponse video = KDTCourseVideoResponse.of(videoEntity);
//
//            model.addAttribute("video", video);
//            return "student/KDT/coursevideoupdate";
//        }
//
//        // 1. CourseOutlineEntity와 UserEntity 가져오기
//        KDTCourseOutlineEntity outlineEntity = kdtCourseOutlineRepository.findById(kdtCourseOutlineId)
//                .orElseThrow(() -> new IllegalArgumentException("Course Outline not found with ID: " + kdtCourseOutlineId));
//
//        Long userId = user.getUserId();
//        UserEntity findUser = userRepository.findByUserId(userId);
//
//        // 2. 파일이 비어 있는 경우 기존 파일 정보를 유지
//        if (request.getFile() == null || request.getFile().isEmpty()) {
//            KDTCourseVideoEntity existingVideo = kdtCourseVideoRepository.findById(kdtCourseVideoId)
//                    .orElseThrow(() -> new IllegalArgumentException("Video not found with ID: " + kdtCourseVideoId));
//
//            request = KDTCourseVideoUpdateRequest.builder()
//                    .kdtCourseVideoId(kdtCourseVideoId)
//                    .kdtCourseOutlineId(kdtCourseOutlineId)
//                    .category(request.getCategory())
//                    .title(request.getTitle())
//                    .file(null) // 파일 업로드 없음
//                    .build();
//        }
//
//        // 3. Service 요청으로 변환
//        VideoUpdateRequest serviceRequest = request.toServiceRequest(outlineEntity, findUser);
//
//        // 4. 서비스 호출
////        s3VideoService.updateCourseVideo(serviceRequest);
//
//        // 5. 리스트 페이지로 리다이렉트
//        return "redirect:/student/KDT/" + kdtSessionId + "/courseoutline/" + kdtCourseOutlineId + "/coursevideo/" + kdtCourseVideoId;
//    }


}
