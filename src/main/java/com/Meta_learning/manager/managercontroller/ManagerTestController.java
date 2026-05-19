package com.Meta_learning.manager.managercontroller;

import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTDTO.KDTTestDTO.KDTTestDTO;
import com.Meta_learning.KDT.KDTDTO.KDTTestDTO.KDTTestGradingDTO;
import com.Meta_learning.KDT.KDTDTO.KDTTestDTO.KDTTestItemDTO;
import com.Meta_learning.KDT.KDTDTO.KDTTestDTO.KDTTestSubmitDTO;
import com.Meta_learning.KDT.KDTservice.KDTSessionService.KDTSessionService;
import com.Meta_learning.KDT.KDTservice.KDTTestService.KDTTestService;
import com.Meta_learning.manager.managerservice.StaffPermissionService;
import com.Meta_learning.user.userentity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ManagerTestController {

    private final StaffPermissionService staffPermissionService;
    private final KDTSessionService kdtSessionService;
    private final KDTTestService kdtTestService;

    // 시험 등록 폼 이동
    @GetMapping("/managers/KDT/{kdtSessionId}/test")
    public String getKdtTest(@PathVariable Long kdtSessionId, Model model) {

        // 세션 권한 확인
        if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
            // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 매니저만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }

        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);
        model.addAttribute("KDTSessionDTO", kdtSessionDTO);

        return "managers/KDT/test";
    }


    //시험 상세 보기
    @GetMapping("/managers/KDT/{kdtSessionId}/test/{kdtTestId}")
    public String postKdtTest(@PathVariable Long kdtSessionId, @PathVariable Long kdtTestId, Model model) {

        // 세션 권한 확인
        if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
            // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 매니저만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }

        // 작성된 시험이 없는 경우
        KDTTestDTO kdtTestDTO = kdtTestService.findKdtTestDTOTestById(kdtTestId);
        if(kdtTestDTO ==null){
            model.addAttribute("msg", "시험이 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/view/manager/KDT/" + kdtSessionId + "/test/list");  // 시험 목록으로 이동
            return "utility/message";
        }

        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);
        model.addAttribute("KDTSessionDTO", kdtSessionDTO);

        model.addAttribute("KDTTestDTO", kdtTestDTO);

        List<KDTTestItemDTO> kdtTestItemDTOS = kdtTestService.findKdtTestItemDTOByKdtTestId(kdtTestId);
        model.addAttribute("KDTTestItemDTOs", kdtTestItemDTOS);

        return "managers/KDT/testdetail";
    }

    // 시험 수정 폼 이동
    @GetMapping("/managers/KDT/{kdtSessionId}/test/update/{kdtTestId}")
    public String getKdtTestUpdate(@PathVariable Long kdtSessionId, @PathVariable Long kdtTestId, Model model) {

        // 세션 권한 확인
        if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
            // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 매니저만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }

        // 작성된 시험이 없는 경우
        KDTTestDTO kdtTestDTO = kdtTestService.findKdtTestDTOTestById(kdtTestId);
        if(kdtTestDTO ==null){
            model.addAttribute("msg", "시험이 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/view/manager/KDT/" + kdtSessionId + "/test/list");  // 시험 목록으로 이동
            return "utility/message";
        }

        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);
        model.addAttribute("KDTSessionDTO", kdtSessionDTO);


        model.addAttribute("KDTTestDTO", kdtTestDTO);

        List<KDTTestItemDTO> kdtTestItemDTOS = kdtTestService.findKdtTestItemDTOByKdtTestId(kdtTestId);
        model.addAttribute("KDTTestItemDTOs", kdtTestItemDTOS);

        return "managers/KDT/testupdate";
    }

    // 답안 상세 폼 이동
    @GetMapping("/managers/KDT/{kdtSessionId}/test/submit/detail/{kdtTestId}/{kdtPartId}")
    public String getKdtTestSubmitDetail(@PathVariable Long kdtSessionId, @PathVariable Long kdtTestId, @PathVariable Long kdtPartId,
                                         Model model, @AuthenticationPrincipal UserEntity user) {
        // 세션 권한 확인
        if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
            // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 매니저만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }

        // 작성된 시험이 없는 경우
        KDTTestDTO kdtTestDTO = kdtTestService.findKdtTestDTOTestById(kdtTestId);
        if(kdtTestDTO ==null){
            model.addAttribute("msg", "시험이 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/view/manager/KDT/" + kdtSessionId + "/test/list");  // 시험 목록으로 이동
            return "utility/message";
        }

        // 작성된 답안이 없는 경우
        List<KDTTestSubmitDTO> kdtTestSubmitDTOS = kdtTestService.findKdtTestSubmitDTOByTestIdAndPartId(kdtTestId, kdtPartId);
        if(kdtTestSubmitDTOS ==null || kdtTestSubmitDTOS.isEmpty()){
            model.addAttribute("msg", "답안이 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/view/manager/KDT/" + kdtSessionId + "/test/submit/"+ kdtTestId+"/list");  // 시험 답안 목록으로 이동
            return "utility/message";
        }

        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);
        model.addAttribute("KDTSessionDTO", kdtSessionDTO);

        model.addAttribute("KDTTestDTO", kdtTestDTO);

        List<KDTTestItemDTO> kdtTestItemDTOS = kdtTestService.findKdtTestItemDTOByKdtTestId(kdtTestId);
        model.addAttribute("KDTTestItemDTOs", kdtTestItemDTOS);

        model.addAttribute("KDTTestSubmitDTOs", kdtTestSubmitDTOS);

        List<KDTTestGradingDTO> kdtTestGradingDTOS = kdtTestService.findKdtTestGradingDTOByTestIdAndPartId(kdtTestId, kdtPartId);
        model.addAttribute("KDTTestGradingDTOs", kdtTestGradingDTOS);

        int totalScore = 0;
        for (KDTTestItemDTO item : kdtTestItemDTOS) {
            totalScore += item.getKdtTestItemScore();  // 각 항목의 점수를 합산
        }
        int studentScore = 0;
        for (KDTTestGradingDTO item : kdtTestGradingDTOS) {
            studentScore += item.getKdtTestGradingScore();  // 각 항목의 점수를 합산
        }
        model.addAttribute("totalScore", totalScore);
        model.addAttribute("studentScore", studentScore);

        return "managers/KDT/testsubmitdetail";
    }

    // 답안 수정 폼 이동
    @GetMapping("/managers/KDT/{kdtSessionId}/test/submit/update/{kdtTestId}/{kdtPartId}")
    public String getKdtTestSubmitUpdate(@PathVariable Long kdtSessionId, @PathVariable Long kdtTestId,
                                         @PathVariable Long kdtPartId, Model model){
        // 세션 권한 확인
        if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
            // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 매니저만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }

        // 작성된 시험이 없는 경우
        KDTTestDTO kdtTestDTO = kdtTestService.findKdtTestDTOTestById(kdtTestId);
        if(kdtTestDTO ==null){
            model.addAttribute("msg", "시험이 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/view/manager/KDT/" + kdtSessionId + "/test/list");  // 시험 목록으로 이동
            return "utility/message";
        }

        // 이미 작성된 답안이 없는 경우
        List<KDTTestSubmitDTO> kdtTestSubmitDTOS = kdtTestService.findKdtTestSubmitDTOByTestIdAndPartId(kdtTestId, kdtPartId);
        if(kdtTestSubmitDTOS ==null || kdtTestSubmitDTOS.isEmpty()){
            model.addAttribute("msg", "답안이 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/view/manager/KDT/" + kdtSessionId + "/test/submit/"+ kdtTestId+"/list");  // 시험 답안 목록으로 이동
            return "utility/message";
        }

        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);
        model.addAttribute("KDTSessionDTO", kdtSessionDTO);

        model.addAttribute("KDTTestDTO", kdtTestDTO);

        List<KDTTestItemDTO> kdtTestItemDTOS = kdtTestService.findKdtTestItemDTOByKdtTestId(kdtTestId);
        kdtTestItemDTOS.forEach(KDTTestItemDTO::deleteTestItemAnswer);
        model.addAttribute("KDTTestItemDTOs", kdtTestItemDTOS);


        model.addAttribute("KDTTestSubmitDTOs", kdtTestSubmitDTOS);


        return "managers/KDT/testsubmitupdate";
    }

    // 채점 수점 폼 이동
    @GetMapping("/managers/KDT/{kdtSessionId}/test/grading/update/{kdtTestId}/{kdtPartId}")
    public String getKdtTestGradingUpdate(@PathVariable Long kdtSessionId, @PathVariable Long kdtTestId, @PathVariable Long kdtPartId, Model model){

        // 세션 권한 확인
        if(!staffPermissionService.hasAccessToSession(kdtSessionId)){
            // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 매니저만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }

        // 작성된 시험이 없는 경우
        KDTTestDTO kdtTestDTO = kdtTestService.findKdtTestDTOTestById(kdtTestId);
        if(kdtTestDTO ==null){
            model.addAttribute("msg", "시험이 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/view/manager/KDT/" + kdtSessionId + "/test/list");  // 시험 목록으로 이동
            return "utility/message";
        }

        // 이미 작성된 채점이 없는 경우
        List<KDTTestGradingDTO> kdtTestGradingDTOS = kdtTestService.findKdtTestGradingDTOByTestIdAndPartId(kdtTestId, kdtPartId);
        if(kdtTestGradingDTOS ==null || kdtTestGradingDTOS.isEmpty()){
            model.addAttribute("msg", "채점이 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/view/manager/KDT/" + kdtSessionId + "/test/submit/"+ kdtTestId+"/list");  // 시험 답안 목록으로 이동
            return "utility/message";
        }

        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);
        model.addAttribute("KDTSessionDTO", kdtSessionDTO);

        model.addAttribute("KDTTestDTO", kdtTestDTO);

        List<KDTTestItemDTO> kdtTestItemDTOS = kdtTestService.findKdtTestItemDTOByKdtTestId(kdtTestId);
        model.addAttribute("KDTTestItemDTOs", kdtTestItemDTOS);

        List<KDTTestSubmitDTO> kdtTestSubmitDTOS = kdtTestService.findKdtTestSubmitDTOByTestIdAndPartId(kdtTestId, kdtPartId);
        model.addAttribute("KDTTestSubmitDTOs", kdtTestSubmitDTOS);

        model.addAttribute("KDTTestGradingDTOs", kdtTestGradingDTOS);

        int totalScore = 0;
        for (KDTTestItemDTO item : kdtTestItemDTOS) {
            totalScore += item.getKdtTestItemScore();  // 각 항목의 점수를 합산
        }
        int studentScore = 0;
        for (KDTTestGradingDTO item : kdtTestGradingDTOS) {
            studentScore += item.getKdtTestGradingScore();  // 각 항목의 점수를 합산
        }
        model.addAttribute("totalScore", totalScore);
        model.addAttribute("studentScore", studentScore);

        return "managers/KDT/testgradingupdate";
    }
}
