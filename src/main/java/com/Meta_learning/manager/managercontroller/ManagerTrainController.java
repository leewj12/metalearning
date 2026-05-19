package com.Meta_learning.manager.managercontroller;

import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTDTO.KDTTrainDTO.KDTTrainDTO;
import com.Meta_learning.KDT.KDTentity.KDTTrainEntity.KDTTrainEntity;
import com.Meta_learning.KDT.KDTservice.KDTSessionService.KDTSessionService;
import com.Meta_learning.KDT.KDTservice.KDTTrainService.KDTTrainService;
import com.Meta_learning.manager.managerservice.StaffPermissionService;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userservice.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ManagerTrainController {

    private final UserService userService;
    private final KDTTrainService kdtTrainService;
    private final KDTSessionService kdtSessionService;
    private final StaffPermissionService staffPermissionService;

    // 훈련일지 작성 폼 이동
    @GetMapping("/managers/KDT/{kdtSessionId}/train")
    public String getKdtTrain(@PathVariable Long kdtSessionId, Model model) {

        // 세션 권한 확인
        if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
            // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 매니저만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }
        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);

        if(kdtSessionDTO==null){
            model.addAttribute("msg", "세션 정보가 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/");  // 이미 있는 훈련일지 상세보기로 이동
            return "utility/message";
        }

        // 세션에 포함된 강사 id를 가져오기
        Map<Long, String> result = kdtTrainService.findRegisteredInstr(kdtSessionId);
        // 모델에 userMap을 추가
        model.addAttribute("instrMap", result);
        // 세션 정보 추가

        model.addAttribute("KDTSessionDTO", kdtSessionDTO);

        return "managers/KDT/train";
    }

    // 훈련일지 작성
    @PostMapping("/managers/KDT/{kdtSessionId}/train")
    public String postKdtTrain(@PathVariable Long kdtSessionId,
                               KDTTrainDTO kdtTrainDTO,
                               @AuthenticationPrincipal UserEntity user,
                               Model model) {
        // 세션 권한 확인
        if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
            // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 매니저만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }

        // 국비회차와 훈련일지 날짜를 확인해서 이미 있는지 확인한다. 중복 이라면 상세보기로 보내기
        Long kdtTrainId = kdtTrainService.findKdtTrain(kdtSessionId, kdtTrainDTO.getKdtTrainDate());
        if (kdtTrainId != null) {
            model.addAttribute("msg", "같은 날의 이미 훈련일지가 존재합니다.");
            model.addAttribute("loc", "/managers/KDT/" + kdtSessionId + "/train/" + kdtTrainId);  // 이미 있는 훈련일지 상세보기로 이동
            return "utility/message";
        }

        // 중복이 아니라면 저장
        kdtTrainDTO.setUserId(user.getUserId());
        // 저장
        KDTTrainEntity kdtTrainEntity = kdtTrainService.kdtTrainSave(kdtTrainDTO);
        //return "redirect:/manager/KDT/"+kdtSessionId+"/train/"+kdtTrainEntity.getKdtTrainId();
        return "redirect:/view/manager/KDT/" + kdtSessionId + "/train/list";
    }


    // 훈련일지 상세 보기
    @GetMapping("/managers/KDT/{kdtSessionId}/train/{kdtTrainId}")
    public String getKdtTrainDetail(@PathVariable Long kdtSessionId, @PathVariable Long kdtTrainId, Model model) {

        // 세션 권한 확인
        if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
            // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 매니저만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }

        // 작성된 훈련일지가 없는 경우
        KDTTrainDTO kdtTrainDTO = kdtTrainService.findKdtTrainById(kdtTrainId);
        if(kdtTrainDTO ==null){
            model.addAttribute("msg", "훈련일지가 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/view/manager/KDT/" + kdtSessionId + "/train/list");  // 이미 있는 훈련일지 상세보기로 이동
            return "utility/message";
        }
        model.addAttribute("KDTTrainDTO", kdtTrainDTO);

        String instrName = userService.findStaffNameByKdtStaffId(kdtTrainDTO.getKdtStaffId());
        model.addAttribute("instr", instrName);
        // 세션 정보 추가
        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);
        model.addAttribute("KDTSessionDTO", kdtSessionDTO);

        return "managers/KDT/traindetail";
    }

    //훈련일지 업데이트 폼 이동
    @GetMapping("/managers/KDT/{kdtSessionId}/train/update/{kdtTrainId}")
    public String getUpdateKdtTrain(@PathVariable Long kdtSessionId, @PathVariable Long kdtTrainId, Model model) {

        // 세션 권한 확인
        if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
            // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 매니저만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }

        // 작성된 훈련일지가 없는 경우
        KDTTrainDTO kdtTrainDTO = kdtTrainService.findKdtTrainById(kdtTrainId);
        if(kdtTrainDTO ==null){
            model.addAttribute("msg", "훈련일지가 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/view/manager/KDT/" + kdtSessionId + "/train/list");  // 이미 있는 훈련일지 상세보기로 이동
            return "utility/message";
        }

        // 해당하는TrainId의 정보 가져오기
        model.addAttribute("KDTTrainDTO", kdtTrainDTO);
        model.addAttribute("instr", userService.findStaffNameByKdtStaffId(kdtTrainDTO.getKdtStaffId()));

        // 세션에 포함된 강사 id를 가져오기
        Map<Long, String> result = kdtTrainService.findRegisteredInstr(kdtSessionId);
        // 모델에 userMap을 추가
        model.addAttribute("instrMap", result);

        // 세션 정보 추가
        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);
        model.addAttribute("KDTSessionDTO", kdtSessionDTO);

        return "managers/KDT/trainupdate";
    }

    // 훈련일지 업데이트
    @PostMapping("/managers/KDT/{kdtSessionId}/train/update/{kdtTrainId}")
    public String postUpdateKdtTrain(@PathVariable Long kdtSessionId, @PathVariable Long kdtTrainId,
                                     KDTTrainDTO kdtTrainDTO, Model model) {

        // 세션 권한 확인
        if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
            // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 매니저만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }

        // 작성된 훈련일지가 없는 경우
        KDTTrainDTO kdtTrain = kdtTrainService.findKdtTrainById(kdtTrainId);
        if(kdtTrain ==null){
            model.addAttribute("msg", "훈련일지가 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/view/manager/KDT/" + kdtSessionId + "/train/list");  // 이미 있는 훈련일지 상세보기로 이동
            return "utility/message";
        }

        // 저장
        KDTTrainEntity kdtTrainEntity = kdtTrainService.kdtTrainSave(kdtTrainDTO);
        return "redirect:/managers/KDT/"+kdtSessionId+"/train/"+kdtTrainId;
    }


}
