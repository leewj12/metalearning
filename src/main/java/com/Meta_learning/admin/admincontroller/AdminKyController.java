package com.Meta_learning.admin.admincontroller;


import com.Meta_learning.KDT.KDTDTO.KDTConsultDTO.KDTConsultDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTDTO.KDTTrainDTO.KDTTrainDTO;
import com.Meta_learning.KDT.KDTentity.KDTConsultEntity.KDTConsultCategory;
import com.Meta_learning.KDT.KDTentity.KDTConsultEntity.KDTConsultEntity;
import com.Meta_learning.KDT.KDTentity.KDTTrainEntity.KDTTrainEntity;
import com.Meta_learning.KDT.KDTservice.KDTConsultService.KDTConsultService;
import com.Meta_learning.KDT.KDTservice.KDTSessionService.KDTSessionService;
import com.Meta_learning.KDT.KDTservice.KDTTrainService.KDTTrainService;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userservice.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AdminKyController {

    private final UserService userService;
    private final KDTTrainService kdtTrainService;
    private final KDTSessionService kdtSessionService;
    private final KDTConsultService kdtConsultService;

    // 훈련일지 작성 폼 이동
    @GetMapping("/admin/KDT/{kdtSessionId}/train")
    public String getKdtTrain(@PathVariable Long kdtSessionId, Model model) {

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

        return "admin/KDT/train";
    }

    // 훈련일지 작성
    @PostMapping("/admin/KDT/{kdtSessionId}/train")
    public String postKdtTrain(@PathVariable Long kdtSessionId,
                            KDTTrainDTO kdtTrainDTO,
                            @AuthenticationPrincipal UserEntity user,
                            Model model) {
        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);

        if(kdtSessionDTO==null){
            model.addAttribute("msg", "세션 정보가 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/");  // 이미 있는 훈련일지 상세보기로 이동
            return "utility/message";
        }

        // 국비회차와 훈련일지 날짜를 확인해서 이미 있는지 확인한다. 중복 이라면 상세보기로 보내기
        Long kdtTrainId = kdtTrainService.findKdtTrain(kdtSessionId, kdtTrainDTO.getKdtTrainDate());
        if (kdtTrainId != null) {
            model.addAttribute("msg", "같은 날의 이미 훈련일지가 존재합니다.");
            model.addAttribute("loc", "/admin/KDT/" + kdtSessionId + "/train/" + kdtTrainId);  // 이미 있는 훈련일지 상세보기로 이동
            return "utility/message";
        }

        // 중복이 아니라면 저장
        kdtTrainDTO.setUserId(user.getUserId());
        // 저장
        KDTTrainEntity kdtTrainEntity = kdtTrainService.kdtTrainSave(kdtTrainDTO);
        //return "redirect:/admin/KDT/"+kdtSessionId+"/train/"+kdtTrainEntity.getKdtTrainId();
        return "redirect:/view/admin/KDT/" + kdtSessionId + "/train/list";
    }

    // 훈련일지 상세 보기
    @GetMapping("/admin/KDT/{kdtSessionId}/train/{kdtTrainId}")
    public String getKdtTrainDetail(@PathVariable Long kdtSessionId, @PathVariable Long kdtTrainId, Model model) {
        // 작성된 훈련일지가 없는 경우
        KDTTrainDTO kdtTrainDTO = kdtTrainService.findKdtTrainById(kdtTrainId);
        if(kdtTrainDTO ==null){
            model.addAttribute("msg", "훈련일지가 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/view/admin/KDT/" + kdtSessionId + "/train/list");  // 이미 있는 훈련일지 상세보기로 이동
            return "utility/message";
        }
        model.addAttribute("KDTTrainDTO", kdtTrainDTO);

        String instrName = userService.findStaffNameByKdtStaffId(kdtTrainDTO.getKdtStaffId());
        model.addAttribute("instr", instrName);
        // 세션 정보 추가
        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);
        model.addAttribute("KDTSessionDTO", kdtSessionDTO);

        return "admin/KDT/traindetail";
    }

    //훈련일지 업데이트 폼 이동
    @GetMapping("/admin/KDT/{kdtSessionId}/train/update/{kdtTrainId}")
    public String getUpdateKdtTrain(@PathVariable Long kdtSessionId, @PathVariable Long kdtTrainId, Model model) {

        // 작성된 훈련일지가 없는 경우
        KDTTrainDTO kdtTrainDTO = kdtTrainService.findKdtTrainById(kdtTrainId);
        if(kdtTrainDTO ==null){
            model.addAttribute("msg", "훈련일지가 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "http://localhost:5173/view/admin/KDT/" + kdtSessionId + "/train/list");  // 이미 있는 훈련일지 상세보기로 이동
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

        return "admin/KDT/trainupdate";
    }

    // 훈련일지 업데이트
    @PostMapping("/admin/KDT/{kdtSessionId}/train/update/{kdtTrainId}")
    public String postUpdateKdtTrain(@PathVariable Long kdtSessionId, @PathVariable Long kdtTrainId,
                                  KDTTrainDTO kdtTrainDTO) {
        // 저장
        KDTTrainEntity kdtTrainEntity = kdtTrainService.kdtTrainSave(kdtTrainDTO);
        return "redirect:/admin/KDT/"+kdtSessionId+"/train/"+kdtTrainId;
    }

    // 상담일지 목록
    @GetMapping("/admin/KDT/{kdtSessionId}/consult/list")
    public String getConsultList(@PathVariable Long kdtSessionId,
                                 @RequestParam(value = "page", defaultValue = "1") int page,
                                 @RequestParam(value = "size", defaultValue = "10") int size,    //10
                                 @RequestParam(value = "searchCategory", required = false) String searchCategory,
                                 @RequestParam(value = "search", required = false) String search,
                                 Model model){

        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);

        if(kdtSessionDTO==null){
            model.addAttribute("msg", "세션 정보가 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/");  // 이미 있는 훈련일지 상세보기로 이동
            return "utility/message";
        }

        // 세션 정보 추가
        model.addAttribute("KDTSessionDTO", kdtSessionDTO);

        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(page -1, size);

        // 검색 조건에 맞는 데이터 조회
        Page<KDTConsultDTO> consultList=null;

        if (searchCategory != null && !searchCategory.isEmpty() && search != null && !search.isEmpty()) {
            if(searchCategory.equals("category")) {
                // 카테고리 변환 (searchCategory가 한국어일 때 해당하는 enum 값 찾기)
                KDTConsultCategory category = null;
                if (searchCategory != null && !searchCategory.isEmpty()) {
                    category = KDTConsultCategory.getCategoryByText(search);
                    consultList = kdtConsultService.searchWithPaging(kdtSessionId, searchCategory, category.name(), pageable);
                }
            }else{
                consultList = kdtConsultService.searchWithPaging(kdtSessionId, searchCategory, search, pageable);
            }
        } else {
            consultList = kdtConsultService.findAllWithPaging(kdtSessionId, pageable);
        }

        int pagingBlock = 5;
        int currentPage = consultList.getNumber()+1;
        int totalPages = consultList.getTotalPages();

        int blockStart = (currentPage - 1) / pagingBlock * pagingBlock + 1;
        int blockEnd = Math.min(blockStart + pagingBlock - 1, totalPages);

        boolean noResults = consultList.getTotalElements() == 0;

        model.addAttribute("consultList", consultList);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pagingBlock", pagingBlock);
        model.addAttribute("blockStart", blockStart);
        model.addAttribute("blockEnd", blockEnd);

        boolean hasPreviousBlock = currentPage > pagingBlock;
        boolean hasNextBlock = blockEnd < totalPages;

        model.addAttribute("hasNext", hasNextBlock);  // 다음 페이지가 있을 경우
        model.addAttribute("hasPrevious", hasPreviousBlock);  // 이전 페이지가 있을 경우

        model.addAttribute("size", size);
        model.addAttribute("noResults", noResults);
        model.addAttribute("searchCategory", searchCategory);
        model.addAttribute("search", search);

        // 페이지 그룹 계산 (5개씩 그룹화)
        int startPageGroup = (page / 5) * 5; // 현재 페이지가 속한 그룹의 첫 번째 페이지 번호
        int endPageGroup = Math.min(startPageGroup + 4, totalPages - 1); // 그룹의 마지막 페이지 번호 (총 페이지 수보다 클 수 없음)

        model.addAttribute("startPageGroup", startPageGroup);
        model.addAttribute("endPageGroup", endPageGroup);

        // 이전 그룹과 다음 그룹의 시작 페이지 계산
        int prevGroupStartPage = Math.max(0, startPageGroup - 5);
        int nextGroupStartPage = Math.min(totalPages - 1, endPageGroup + 1);

        model.addAttribute("prevGroupStartPage", prevGroupStartPage);
        model.addAttribute("nextGroupStartPage", nextGroupStartPage);


        return "admin/KDT/consultlist";
    }

    // 상담일지 작성 폼 이동
    @GetMapping("/admin/KDT/{kdtSessionId}/consult")
    public String getConsult(@PathVariable Long kdtSessionId, Model model){

        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);

        if(kdtSessionDTO==null){
            model.addAttribute("msg", "세션 정보가 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/");  // 이미 있는 훈련일지 상세보기로 이동
            return "utility/message";
        }

        // 세션에 포함된 학생 id를 가져오기
        Map<Long, String> result = kdtConsultService.findRegisteredStudent(kdtSessionId);
        // 모델에 studentMap 추가
        model.addAttribute("studentMap", result);

        // 세션 정보 추가
        model.addAttribute("KDTSessionDTO", kdtSessionDTO);

        return "admin/KDT/consult";
    }


    // 상담일지 작성
    @PostMapping("/admin/KDT/{kdtSessionId}/consult")
    public String postKdtConsult(@PathVariable Long kdtSessionId,
                               KDTConsultDTO kdtConsultDTO,
                               @AuthenticationPrincipal UserEntity user, Model model) {

        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);

        if(kdtSessionDTO==null){
            model.addAttribute("msg", "세션 정보가 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/");  // 이미 있는 훈련일지 상세보기로 이동
            return "utility/message";
        }

        // 중복 확인 생략, 작성자 정보 설정
        kdtConsultDTO.setUserId(user.getUserId());

        // 저장
        kdtConsultService.kdtConsultSave(kdtConsultDTO);
        return "redirect:/admin/KDT/"+kdtSessionId+"/consult/list";
    }

    // 상담일지 상세 보기
    @GetMapping("/admin/KDT/{kdtSessionId}/consult/{kdtConsultId}")
    public String getKdtConsultDetail(@PathVariable Long kdtSessionId, @PathVariable Long kdtConsultId, Model model) {
        // 작성된 상담일지 없는 경우
        KDTConsultDTO kdtConsultDTO = kdtConsultService.findKdtConsultById(kdtConsultId);
        if(kdtConsultDTO ==null){
            model.addAttribute("msg", "상담일지가 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/admin/KDT/" + kdtSessionId + "/consult/list");  // 이미 있는 훈련일지 상세보기로 이동
            return "utility/message";
        }

        model.addAttribute("KDTConsultDTO", kdtConsultDTO);

        String studentName = userService.findStaffNameByKdtPartId(kdtConsultDTO.getKdtPartId());
        model.addAttribute("student", studentName);
        // 세션 정보 추가
        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);
        model.addAttribute("KDTSessionDTO", kdtSessionDTO);

        return "admin/KDT/consultdetail";
    }

    // 상담일지 수정 폼 이동
    @GetMapping("/admin/KDT/{kdtSessionId}/consult/update/{kdtConsultId}")
    public String getUpdateKdtConsult(@PathVariable Long kdtSessionId, @PathVariable Long kdtConsultId, Model model) {

        // 작성된 상담일지 없는 경우
        KDTConsultDTO kdtConsultDTO = kdtConsultService.findKdtConsultById(kdtConsultId);
        if(kdtConsultDTO ==null){
            model.addAttribute("msg", "상담일지가 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/admin/KDT/" + kdtSessionId + "/consult/list");  // 이미 있는 훈련일지 상세보기로 이동
            return "utility/message";
        }

        model.addAttribute("KDTConsultDTO", kdtConsultDTO);

        model.addAttribute("student", userService.findStaffNameByKdtPartId(kdtConsultDTO.getKdtPartId()));

        // 세션에 포함된 학생 id를 가져오기
        Map<Long, String> result = kdtConsultService.findRegisteredStudent(kdtSessionId);
        // 모델에 studentMap 추가
        model.addAttribute("studentMap", result);

        // 세션 정보 추가
        KDTSessionDTO kdtSessionDTO = kdtSessionService.findKdtSessionDTOById(kdtSessionId);
        model.addAttribute("KDTSessionDTO", kdtSessionDTO);

        return "admin/KDT/consultupdate";
    }

    // 상담일지 수정 하기
    @PostMapping("/admin/KDT/{kdtSessionId}/consult/update/{kdtConsultId}")
    public String postUpdateKdtTrain(@PathVariable Long kdtSessionId, @PathVariable Long kdtConsultId,
                                     KDTConsultDTO kdtConsultDTO, Model model) {

        // 작성된 상담일지 없는 경우
        KDTConsultDTO kdtConsult = kdtConsultService.findKdtConsultById(kdtConsultId);
        if(kdtConsult ==null){
            model.addAttribute("msg", "상담일지가 삭제됐거나 존재하지 않습니다.");
            model.addAttribute("loc", "/admin/KDT/" + kdtSessionId + "/consult/list");  // 이미 있는 훈련일지 상세보기로 이동
            return "utility/message";
        }
        // 저장
        KDTConsultEntity kdtConsultEntity = kdtConsultService.kdtConsultSave(kdtConsultDTO);
        return "redirect:/admin/KDT/"+kdtSessionId+"/consult/"+kdtConsultId;
    }
}
