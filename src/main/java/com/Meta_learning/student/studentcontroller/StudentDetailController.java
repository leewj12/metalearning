package com.Meta_learning.student.studentcontroller;

import com.Meta_learning.KDT.KDTentity.KDTDetailEntity.KDTDetailEntity;
import com.Meta_learning.KDT.KDTentity.KDTDetailFileEntity.KDTDetailFileEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.KDT.KDTrepository.KDTDetailRepository.KDTDetailRepository;
import com.Meta_learning.KDT.KDTrepository.KDTSessionRepository.KDTSessionRepository;
import com.Meta_learning.KDT.KDTservice.detail.KdtDetailService;
import com.Meta_learning.KDT.KDTservice.request.KDTDetailCreateServiceRequest;
import com.Meta_learning.KDT.KDTservice.response.KDTDetailResponse;
import com.Meta_learning.admin.dto.request.KDTDetailCreateRequest;
import com.Meta_learning.admin.dto.request.KDTDetailUpdateRequest;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userrepository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class StudentDetailController {

    private final KdtDetailService kdtDetailService;
    private final UserRepository userRepository;
    private final KDTSessionRepository kdtSessionRepository;
    private final KDTDetailRepository kdtDetailRepository;

    // 국비 상세 조회
    @GetMapping("/student/KDT/{kdtSessionId}/detail/detail")
    public String getKdtDetail(@PathVariable Long kdtSessionId, Model model) {

//        KDTDetailResponse kdtDetail = kdtDetailService.getKdtDetail(kdtDetailId);
//        model.addAttribute("kdtDetail", kdtDetail);
        Optional<KDTDetailEntity> optionalDetail = kdtDetailRepository.findByKdtSessionEntity_KdtSessionId(kdtSessionId);

        if (optionalDetail.isPresent()) {
            KDTDetailResponse kdtDetailResponse = KDTDetailResponse.of(optionalDetail.get());
            model.addAttribute("detail", kdtDetailResponse);
        } else {
            model.addAttribute("detail", null);
        }

        model.addAttribute("kdtSessionId", kdtSessionId);
        return "student/KDT/detaildetail";
    }

    // 상세 생성 폼
    @GetMapping("/student/KDT/{kdtSessionId}/detail")
    public String createKdtDetailForm(@PathVariable Long kdtSessionId, Model model) {
        Optional<KDTDetailEntity> optionalDetail = kdtDetailRepository.findByKdtSessionEntity_KdtSessionId(kdtSessionId);

        if (optionalDetail.isPresent()) {
            KDTDetailResponse kdtDetailResponse = KDTDetailResponse.of(optionalDetail.get());
            model.addAttribute("detail", kdtDetailResponse);
        } else {
            model.addAttribute("detail", null);
        }

        model.addAttribute("kdtSessionId", kdtSessionId);
        return "student/KDT/detail";
    }

    // 상세 생성 처리
    @PostMapping("/student/KDT/{kdtSessionId}/detail")
    public String createKdtDetail(
            @PathVariable Long kdtSessionId,
            @Valid KDTDetailCreateRequest request,
            BindingResult bindingResult,
            @RequestParam("files") MultipartFile[] files,
            Model model) throws IOException {

        if (bindingResult.hasErrors()) {

            // 폼으로 돌아가 에러 메시지 표시
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "student/KDT/detail";
        }


        // 1. ID로 Entity 조회
        UserEntity userEntity = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));

        KDTSessionEntity sessionEntity = kdtSessionRepository.findById(kdtSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + kdtSessionId));

        // 파일 저장 로직
        List<KDTDetailFileEntity> fileEntities = new ArrayList<>();
        String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/static/images/uploads";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        StringBuilder fileNames = new StringBuilder();
        for (MultipartFile file : files) {
            String originalFileName = file.getOriginalFilename();
            String uuidFileName = UUID.randomUUID() + "_" + originalFileName;
            Path filePath = uploadPath.resolve(uuidFileName);
            Files.write(filePath, file.getBytes());

            fileEntities.add(new KDTDetailFileEntity(
                    null,
                    null, // kdtDetailEntity는 저장 후 연관관계 설정
                    originalFileName,
                    uuidFileName,
                    file.getSize(),
                    file.getContentType(),
                    LocalDateTime.now()
            ));
        }

        // 요청 DTO를 Service 요청 DTO로 변환
        KDTDetailCreateServiceRequest serviceRequest = request.toServiceRequest(userEntity, sessionEntity, fileEntities);

        // 서비스 호출하여 데이터 저장
        KDTDetailResponse savedDetail = kdtDetailService.createKdtDetail(serviceRequest);


        // 리다이렉트 URL 구성
        return "redirect:/student/KDT/" +kdtSessionId +"/detail/detail";
    }


    // 국비 상세 수정 폼 이동
    @GetMapping("/student/KDT/{kdtSessionId}/detail/update")
    public String updateKdtDetailForm(@PathVariable Long kdtSessionId, Model model) {
        Optional<KDTDetailEntity> optionalDetail = kdtDetailRepository.findByKdtSessionEntity_KdtSessionId(kdtSessionId);

        if (optionalDetail.isEmpty()) {
            throw new IllegalArgumentException("Detail not found for session ID: " + kdtSessionId);
        }

        KDTDetailEntity kdtDetailEntity = optionalDetail.get();
        Long kdtDetailId = kdtDetailEntity.getKdtDetailId(); // kdtDetailId 추출
        KDTDetailResponse kdtDetail = kdtDetailService.getKdtDetail(kdtDetailId);

        model.addAttribute("kdtSessionId", kdtSessionId);
        model.addAttribute("kdtDetail", kdtDetail);
        return "student/KDT/detailupdate";
    }

    // 국비 상세 수정 처리
    @PostMapping("/student/KDT/{kdtSessionId}/detail/update")
    public String updateKdtDetail(
            @PathVariable Long kdtSessionId,
            @Valid KDTDetailUpdateRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "deleteFiles", required = false) List<Long> deleteFileIds,
            Model model) throws IOException {

        if (bindingResult.hasErrors()) {

            // 모델에 현재 요청 데이터 추가
            model.addAttribute("kdtDetailUpdateRequest", request);
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("kdtSessionId", kdtSessionId);

            // 기존 데이터를 다시 모델에 추가
            Long kdtDetailId = request.getKdtDetailId();
            KDTDetailResponse kdtDetail = kdtDetailService.getKdtDetail(kdtDetailId);
            model.addAttribute("kdtDetail", kdtDetail);

            return "student/KDT/detailupdate";
        }

//        // kdtSessionId를 기준으로 KDTDetailEntity 조회
//        Optional<KDTDetailEntity> optionalDetail = kdtDetailRepository.findByKdtSessionEntity_KdtSessionId(kdtSessionId);
//
//        if (optionalDetail.isEmpty()) {
//            throw new IllegalArgumentException("Detail not found for session ID: " + kdtSessionId);
//        }
//
//        KDTDetailEntity kdtDetailEntity = optionalDetail.get();
//        Long kdtDetailId = kdtDetailEntity.getKdtDetailId(); // kdtDetailId 추출
//
//        log.info("Session ID: {}", kdtSessionId);
//        log.info("Detail ID: {}", kdtDetailId);
//        log.info("Update Request: {}", request);
//        log.info("Files to upload: {}", files != null ? files.length : "No files uploaded");
//        log.info("Files to delete: {}", deleteFileIds);



        // 수정 서비스 호출
//        kdtDetailService.updateKdtDetail(kdtDetailId, request.toServiceRequest(), files, deleteFileIds);

//        log.info("Successfully updated KDT detail with ID: {}", kdtDetailId);
        KDTDetailEntity detail = kdtDetailService.getDetailBySessionId(kdtSessionId);
        request.setKdtDetailId(detail.getKdtDetailId());
        kdtDetailService.updateKdtDetail(request.getKdtDetailId(), request.toServiceRequest(), files, deleteFileIds);
        return "redirect:/student/KDT/" + kdtSessionId + "/detail/detail";
    }


}
