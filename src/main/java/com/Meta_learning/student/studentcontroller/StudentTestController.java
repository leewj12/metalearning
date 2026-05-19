package com.Meta_learning.student.studentcontroller;

import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTDTO.KDTTestDTO.*;
import com.Meta_learning.KDT.KDTservice.KDTPartservice.KDTPartservice;
import com.Meta_learning.KDT.KDTservice.KDTSessionService.KDTSessionService;
import com.Meta_learning.KDT.KDTservice.KDTTestService.KDTTestService;
import com.Meta_learning.student.studentpermissionservice.StudentPermissionService;
import com.Meta_learning.student.studentpermissionservice.StudentService;
import com.Meta_learning.user.userentity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class StudentTestController {

    private final KDTSessionService kdtSessionService;
    private final KDTTestService kdtTestService;
    private final KDTPartservice kdtPartservice;
    private final StudentPermissionService studentPermissionService;
    private final StudentService studentService;

    // 답안 작성 폼 이동
    @GetMapping("/student/KDT/{kdtSessionId}/test/submit/{kdtTestId}")
    public String getKdtTestSubmit(@PathVariable Long kdtSessionId, @PathVariable Long kdtTestId, Model model,
                                   @AuthenticationPrincipal UserEntity user) {

        // 세션 권한 확인
        if(!studentPermissionService.hasAccessToSession(kdtSessionId)){
            // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 학생만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }

        Long sessionid = kdtSessionId;  // URL에서 받은 sid를 sessionid로 설정
        Long userId = user.getUserId();  // UserEntity에서 'userId'를 가져오기
        List<KDTSessionDTO> sessions = studentService.getSessionsByUserId(userId);  // 서비스에서 유저 ID로 세션 정보 가져오기
        // 첫 번째 세션의 ID를 가져오기
        Long sessionId = null;
        if (!sessions.isEmpty()) {
            sessionId = sessions.get(0).getKdtSessionId();
        }
        model.addAttribute("sessionId", sessionId );  // 세션 정보를 모델에 추가
        model.addAttribute("hasSessions", !sessions.isEmpty());  // 세션이 있으면 true, 없으면 false로 설정

        // 이미 작성한 폼이 있을 경우 작성한 답안으로 이동
        Long partid = kdtPartservice.findPartIdBySessionIdAndUserId(kdtSessionId, user.getUserId());
        List<KDTTestSubmitDTO> kdtTestSubmitDTOS = kdtTestService.findKdtTestSubmitDTOByTestIdAndPartId(kdtTestId, partid);

        KDTTestDTO kdtTestDTO = kdtTestService.findKdtTestDTOTestById(kdtTestId);
        if(kdtTestDTO ==null){
            model.addAttribute("msg", "시험이 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/student/KDT/" + kdtSessionId +"/test/list");  // 회차 입력하는 곳으로 이동
            return "utility/message";
        }

        if(kdtTestSubmitDTOS !=null && !kdtTestSubmitDTOS.isEmpty()){
            model.addAttribute("msg", "이미 제출한 답안이 있습니다.");
            model.addAttribute("loc", "/student/KDT/"+kdtSessionId+"/test/submit/detail/"+kdtTestId);  // 회차 입력하는 곳으로 이동
            return "utility/message";
        }

        // 접근 가능 시간 제한
        // 시작 시간 이전
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = kdtTestDTO.getKdtTestStartDate();
        LocalDateTime endTime = kdtTestDTO.getKdtTestEndDate();

        // 시작 시간 이전 혹은 종료 시간 이후인 경우
        if (now.isBefore(startTime) || now.isAfter(endTime)){
            model.addAttribute("msg", "제출 가능한 시간이 아닙니다");
            model.addAttribute("loc", "/student/KDT/" + kdtSessionId +"/test/list");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";
        }

        model.addAttribute("KDTTestDTO", kdtTestDTO);

        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);
        model.addAttribute("KDTSessionDTO", kdtSessionDTO);

        List<KDTTestItemDTO> kdtTestItemDTOS = kdtTestService.findKdtTestItemDTOByKdtTestId(kdtTestId);
        kdtTestItemDTOS.forEach(KDTTestItemDTO::deleteTestItemAnswer);
        model.addAttribute("KDTTestItemDTOs", kdtTestItemDTOS);

        return "student/KDT/testsubmit";

    }

    // 답안 상세 폼 이동
    @GetMapping("/student/KDT/{kdtSessionId}/test/submit/detail/{kdtTestId}")
    public String getKdtTestSubmitDetail(@PathVariable Long kdtSessionId, @PathVariable Long kdtTestId, Model model,
                                         @AuthenticationPrincipal UserEntity user) {
        // 세션 권한 확인
        if(!studentPermissionService.hasAccessToSession(kdtSessionId)){
            // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 학생만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }

        Long sessionid = kdtSessionId;  // URL에서 받은 sid를 sessionid로 설정
        Long userId = user.getUserId();  // UserEntity에서 'userId'를 가져오기
        List<KDTSessionDTO> sessions = studentService.getSessionsByUserId(userId);  // 서비스에서 유저 ID로 세션 정보 가져오기
        // 첫 번째 세션의 ID를 가져오기
        Long sessionId = null;
        if (!sessions.isEmpty()) {
            sessionId = sessions.get(0).getKdtSessionId();
        }
        model.addAttribute("sessionId", sessionId );  // 세션 정보를 모델에 추가
        model.addAttribute("hasSessions", !sessions.isEmpty());  // 세션이 있으면 true, 없으면 false로 설정

        KDTTestDTO kdtTestDTO = kdtTestService.findKdtTestDTOTestById(kdtTestId);
        if(kdtTestDTO ==null){
            model.addAttribute("msg", "시험이 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/student/KDT/" + kdtSessionId +"/test/list");  // 회차 입력하는 곳으로 이동
            return "utility/message";
        }

        Long partId = kdtPartservice.findPartIdBySessionIdAndUserId(kdtSessionId, user.getUserId());

        List<KDTTestSubmitDTO> kdtTestSubmitDTOS = kdtTestService.findKdtTestSubmitDTOByTestIdAndPartId(kdtTestId, partId);
        model.addAttribute("KDTTestSubmitDTOs", kdtTestSubmitDTOS);

        if (kdtTestSubmitDTOS == null || kdtTestSubmitDTOS.isEmpty()){
            model.addAttribute("msg", "제출한 답안이 삭제 됐거나 존재하지 않습니다");
            model.addAttribute("loc", "/student/KDT/" + kdtSessionId +"/test/list");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }

        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);
        model.addAttribute("KDTSessionDTO", kdtSessionDTO);

        model.addAttribute("KDTTestDTO", kdtTestDTO);

        List<KDTTestItemDTO> kdtTestItemDTOS = kdtTestService.findKdtTestItemDTOByKdtTestId(kdtTestId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = kdtTestDTO.getKdtTestEndDate();

        // 시작 시간 이전 혹은 종료 시간 이후인 경우
        if (now.isAfter(endTime)){
            List<KDTTestGradingDTO> kdtTestGradingDTOS = kdtTestService.findKdtTestGradingDTOByTestIdAndPartId(kdtTestId, partId);

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
        }else{
            kdtTestItemDTOS.forEach(KDTTestItemDTO::deleteTestItemAnswer);
        }
        model.addAttribute("KDTTestItemDTOs", kdtTestItemDTOS);

        return "student/KDT/testsubmitdetail";
    }


    // 답안 수정 폼 이동
    @GetMapping("/student/KDT/{kdtSessionId}/test/submit/update/{kdtTestId}")
    public String getKdtTestSubmitUpdate(@PathVariable Long kdtSessionId, @PathVariable Long kdtTestId,
                                         @AuthenticationPrincipal UserEntity user, Model model){
        // 세션 권한 확인
        if(!studentPermissionService.hasAccessToSession(kdtSessionId)){
            // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 학생만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }

        Long sessionid = kdtSessionId;  // URL에서 받은 sid를 sessionid로 설정
        Long userId = user.getUserId();  // UserEntity에서 'userId'를 가져오기
        List<KDTSessionDTO> sessions = studentService.getSessionsByUserId(userId);  // 서비스에서 유저 ID로 세션 정보 가져오기
        // 첫 번째 세션의 ID를 가져오기
        Long sessionId = null;
        if (!sessions.isEmpty()) {
            sessionId = sessions.get(0).getKdtSessionId();
        }
        model.addAttribute("sessionId", sessionId );  // 세션 정보를 모델에 추가
        model.addAttribute("hasSessions", !sessions.isEmpty());  // 세션이 있으면 true, 없으면 false로 설정

        KDTTestDTO kdtTestDTO = kdtTestService.findKdtTestDTOTestById(kdtTestId);
        if(kdtTestDTO ==null){
            model.addAttribute("msg", "시험이 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/student/KDT/" + kdtSessionId +"/test/list");  // 회차 입력하는 곳으로 이동
            return "utility/message";
        }

        // 접근 가능 시간 제한
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = kdtTestDTO.getKdtTestStartDate();
        LocalDateTime endTime = kdtTestDTO.getKdtTestEndDate();

        // 시작 시간 이전 혹은 종료 시간 이후인 경우
        if (now.isBefore(startTime) || now.isAfter(endTime)){
            model.addAttribute("msg", "제출 가능한 시간이 아닙니다");
            model.addAttribute("loc", "/student/KDT/" + kdtSessionId +"/test/list");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";
        }
        Long partId = kdtPartservice.findPartIdBySessionIdAndUserId(kdtSessionId, user.getUserId());

        List<KDTTestSubmitDTO> kdtTestSubmitDTOS = kdtTestService.findKdtTestSubmitDTOByTestIdAndPartId(kdtTestId, partId);
        model.addAttribute("KDTTestSubmitDTOs", kdtTestSubmitDTOS);

        if (kdtTestSubmitDTOS == null || kdtTestSubmitDTOS.isEmpty()){
            model.addAttribute("msg", "제출한 답안이 삭제 됐거나 존재하지 않습니다");
            model.addAttribute("loc", "/student/KDT/" + kdtSessionId +"/test/list");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }

        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);
        model.addAttribute("KDTSessionDTO", kdtSessionDTO);

        model.addAttribute("KDTTestDTO", kdtTestDTO);

        List<KDTTestItemDTO> kdtTestItemDTOS = kdtTestService.findKdtTestItemDTOByKdtTestId(kdtTestId);
        kdtTestItemDTOS.forEach(KDTTestItemDTO::deleteTestItemAnswer);
        model.addAttribute("KDTTestItemDTOs", kdtTestItemDTOS);

        model.addAttribute("KDTTestSubmitDTOs", kdtTestSubmitDTOS);

        return "student/KDT/testsubmitupdate";
    }



    // 시험 목록으로 가는 코드
    @GetMapping("/student/KDT/{kdtSessionId}/test/list")
    public String getKdtTestList(@PathVariable Long kdtSessionId,
                                 @AuthenticationPrincipal UserEntity user,
                                 Model model) {
        // 세션 권한 확인
        if(!studentPermissionService.hasAccessToSession(kdtSessionId)){
            // 권한이 없을 경우 에러 메시지와 리다이렉트 위치를 전달
            model.addAttribute("msg", "해당 국비 프로그램에 참여하는 학생만 접근 가능합니다.");
            model.addAttribute("loc", "/");  // 경로 설정 (홈페이지 등으로 리다이렉트)
            return "utility/message";  // 에러 메시지를 출력할 뷰 (예: message.html)
        }

        Long partId = kdtPartservice.findPartIdBySessionIdAndUserId(kdtSessionId, user.getUserId());
        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);
        List<KDTTestStudentListDTO> kdtTestStudentListDTOs = kdtTestService.findKdtTestStudentListBySessionId(kdtSessionId, partId);

        model.addAttribute("kdtTestStudentListDTOs", kdtTestStudentListDTOs);
        model.addAttribute("KDTSessionDTO", kdtSessionDTO);




        Long sessionid = kdtSessionId;  // URL에서 받은 sid를 sessionid로 설정
        Long userId = user.getUserId();  // UserEntity에서 'userId'를 가져오기
        List<KDTSessionDTO> sessions = studentService.getSessionsByUserId(userId);  // 서비스에서 유저 ID로 세션 정보 가져오기
        // 첫 번째 세션의 ID를 가져오기
        Long sessionId = null;
        if (!sessions.isEmpty()) {
            sessionId = sessions.get(0).getKdtSessionId();
        }
        model.addAttribute("sessionId", sessionId );  // 세션 정보를 모델에 추가
        model.addAttribute("hasSessions", !sessions.isEmpty());  // 세션이 있으면 true, 없으면 false로 설정








        return "student/KDT/testlist"; // 200 OK 응답

    }
}
