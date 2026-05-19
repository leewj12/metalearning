package com.Meta_learning.course.coursecontroller;

import com.Meta_learning.course.coursecontroller.dto.request.CourseCreateRequest;
import com.Meta_learning.course.coursecontroller.dto.response.CourseListResponse;
import com.Meta_learning.course.coursecontroller.dto.response.CourseUpdateResponse;
import com.Meta_learning.course.coursecontroller.dto.response.CourseViewResponse;
import com.Meta_learning.course.coursecontroller.dto.update.*;
import com.Meta_learning.course.courseentity.*;
import com.Meta_learning.course.courserepository.*;
import com.Meta_learning.course.courseservice.CourseDescriptService;
import com.Meta_learning.course.courseservice.CourseDetailService;
import com.Meta_learning.course.courseservice.CourseService;
import com.Meta_learning.course.courseservice.requset.*;
import com.Meta_learning.user.userentity.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final CourseDescriptService courseDescriptService;
    private final CourseDetailService courseDetailService;
    private final InstrRepository instrRepository;
    private final CourseRepository courseRepository;
    private final CourseDescriptRepository courseDescriptRepository;
    private final CourseDetailRepository courseDetailRepository;
    private final CourseFileRepository courseFileRepository;
    private final CourseVideoRepository courseVideoRepository;

    @Value("${spring.servlet.multipart.max-file-size}")  // 필드 주입 방식으로 변경
    private DataSize maxFileSize;

    @Value("${spring.servlet.multipart.max-request-size}")  // 필드 주입 방식으로 변경
    private DataSize maxRequestSize;

//    @GetMapping("/instr/course/list/default") // 추가
//    public String getDefaultCourseList(@AuthenticationPrincipal UserEntity user, Model model) {
//        // 기본 목록 처리
//        return "instr/courseList";
//    }

    @GetMapping("/instr/course/list/approved")  //status = approval / [본인이 작성한 경우]
    public String getCourseList(@AuthenticationPrincipal UserEntity user,
                                Model model) {
        // 2. 강사 정보 확인
        // 현재 인증된 유저의 ID를 이용하여 InstrEntity(강사 정보) 조회
        InstrEntity instrEntity = instrRepository.findByUserEntityUserId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 강사 정보가 존재하지 않습니다."));


        // 2. 강사의 강의 목록 조회
        List<CourseEntity> courseEntities = courseService.getCoursesByInstructor(instrEntity);


        // 3. APPROVED 상태 필터링
        List<CourseEntity> approvedCourses = courseEntities.stream()
                .filter(course -> CourseStatus.APPROVED.equals(course.getCourseStatus()))
                .collect(Collectors.toList());

        // 4. CourseListResponse로 변환
        List<CourseListResponse> courseListResponses = approvedCourses.stream()
                .map(course -> CourseListResponse.builder()
                        .courseId(course.getCourseId())
                        .courseTitle(course.getCourseTitle())
                        .courseDescript(course.getCourseDescript())
                        .coursePrice(course.getCoursePrice())
                        .courseCategory(course.getCourseCategory())
                        .courseDifficulty(course.getCourseDifficulty())
                        .courseThumbnail(course.getCourseThumbnail())
                        .courseStatus(course.getCourseStatus())
                        .build())
                .collect(Collectors.toList());

        // 5. Model에 데이터 추가
        model.addAttribute("courseList", courseListResponses);

        // 6. 뷰 반환
        return "instr/courselist"; // courseList.html 뷰로 매핑
    }

    @GetMapping("/instr/course/{courseId}")
    public String getCourseDetail(@PathVariable Long courseId, Model model,
                                  @AuthenticationPrincipal UserEntity user) {
        // 1. 강의 기본 정보 조회
        CourseEntity courseEntity = courseService.getCourseById(courseId);
        if(!user.getUserId().equals(courseEntity.getInstr().getUserEntity().getUserId())){
            model.addAttribute("msg", "본인 강의만 조회할 수 있습니다.");
            model.addAttribute("loc", "/instr/course/list/approved/");  // 다시 강사 계정 등록 페이지로 돌아가기
            return "utility/message"; // 오류 메시지 페이지로 이동
        }

        // 2. 강의로 강의 설명 조회
        CourseDescriptEntity courseDescript = courseDescriptService.getCourseDescript(courseEntity);

        // 3. 강의설명으로 강의설명파일 조회
        List<CourseDescriptFileEntity> courseDescriptFiles = courseDescriptService.getCourseDescriptFiles(courseDescript);

        // 4. 강의로 강의 상세 정보 조회
        List<CourseDetailEntity> courseDetailEntities = courseDetailService.getCourseDetails(courseEntity);

        // 5. 강의 상세 정보로 강의 동영상 조회
        List<CourseVideoEntity> courseVideos = courseDetailService.getCourseVideos(courseDetailEntities);

        // 6. 강의 상세 정보로 강의 파일 조회
        List<CourseFileEntity> courseFiles = courseDetailService.getCourseFiles(courseDetailEntities);

        // 7. update DTO 생성
        CourseUpdateResponse updateResponse = CourseUpdateResponse.builder()
                .courseId(courseId)
                .courseDescriptId(courseDescript.getCourseDescriptId())
                .courseTitle(courseEntity.getCourseTitle())
                .courseDescript(courseEntity.getCourseDescript())
                .coursePrice(courseEntity.getCoursePrice())
                .courseCategory(courseEntity.getCourseCategory())
                .courseDifficulty(courseEntity.getCourseDifficulty())
                .courseStatus(courseEntity.getCourseStatus())
                .courseDescriptContent(courseDescript.getCourseDescriptContent()) // 설명 내용 추가
                .courseThumbnail(courseEntity.getCourseThumbnail()) // 기존 썸네일 경로
                .courseDescriptFiles(courseDescriptService.convertDescriptFilesToFileNames(courseDescriptFiles)) // 설명 파일 이름 리스트
                .courseDetails(courseDetailService.convertCourseDetailsToUpdateResponses(courseDetailEntities)) // 강의 상세 정보 리스트 변환
                .build();


        // 8. model 데이터 추가
        model.addAttribute("updateResponse", updateResponse);
        model.addAttribute("courseDifficulties", CourseDifficulty.values());
        model.addAttribute("courseId", courseId);


        // 뷰 이름 반환
        return "instr/coursedetail"; // coursedetail.html로 매핑
    }

    @GetMapping("/instr/course")
    public String createCourseForm(Model model) {
        // Enum 값을 Model에 추가
        model.addAttribute("courseDifficulties", CourseDifficulty.values());
        Long fileSize= maxFileSize.toMegabytes();
        model.addAttribute("maxFileSize", fileSize);
        Long requestSize = maxRequestSize.toMegabytes();
        model.addAttribute("maxRequestSize", requestSize);
        return "instr/course";
    }

    @PostMapping("/instr/course")
    public String createCourse(@AuthenticationPrincipal UserEntity user,
                               @Valid @ModelAttribute CourseCreateRequest request,
                               BindingResult bindingResult,
                               Model model) {

        request.setCourseStatus(CourseStatus.PENDING);
        // 1. 입력 값 유효성 검사
        if (bindingResult.hasErrors()) {
            // 유효성 검사 실패 시, 에러 메시지와 함께 입력 폼으로 다시 이동
            model.addAttribute("courseCreateRequest", request);
            model.addAttribute("courseDifficulties", CourseDifficulty.values());
            return "instr/course"; // 다시 폼 페이지로 이동
        }

        // courseDetails 리스트가 누락된 경우 기본값 설정
        if (request.getCourseDetails() == null || request.getCourseDetails().isEmpty()) {
            model.addAttribute("error", "강의 세부 정보가 누락되었습니다.");
            model.addAttribute("courseCreateRequest", request);
            model.addAttribute("courseDifficulties", CourseDifficulty.values());
            return "instr/course";
        }

//        // 각 세부 정보 확인 (로그로 출력)
//        request.getCourseDetails().forEach(detail -> {
//            log.info("CourseDetail: Outline={}, Title={}",
//                    detail.getCourseDetailOutline(),
//                    detail.getCourseDetailTitle());
//        });

        // 2. 강사 정보 검색
        // 현재 인증된 유저의 ID를 이용하여 InstrEntity(강사 정보) 조회
        InstrEntity instrEntity = instrRepository.findByUserEntityUserId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 강사 정보가 존재하지 않습니다."));

        // 3. 강의 기본 정보 생성 요청 객체 변환
        // CourseCreateRequest를 CourseCreateServiceRequest로 변환하여 서비스 계층으로 전달
        CourseCreateServiceRequest courseServiceRequest = request.toCourseServiceRequest(instrEntity);

        // 4. 강의 기본 정보 저장
        // 변환된 요청 객체를 사용하여 CourseEntity(강의 엔티티) 저장 및 반환
        CourseEntity savedCourse = courseService.saveCourse(courseServiceRequest);

        // 5. 강의 설명 정보 생성 요청 객체 변환
        // 강의 ID를 포함하는 CourseDescriptCreateServiceRequest 객체 생성
        CourseDescriptCreateServiceRequest courseDescriptServiceRequest = request.toCourseDescriptServiceRequest(savedCourse);

        // 6. 강의 설명 정보 저장
        // 강의 설명 내용을 CourseDescriptEntity에 저장
        CourseDescriptEntity savedCourseDescript = courseDescriptService.saveCourseDescript(courseDescriptServiceRequest);

        // 7. 강의 세부 정보 생성 요청 객체 변환
        // 강의 ID를 포함하는 여러 개의 CourseDetailCreateServiceRequest 객체 생성
        List<CourseDetailCreateServiceRequest> courseDetailServiceRequests = request.toCourseDetailServiceRequests(savedCourse);

        // 8. 강의 세부 정보 저장
        // 변환된 요청 객체들을 사용하여 CourseDetailEntity(강의 세부 정보)와 관련 파일 저장
        courseDetailService.saveCourseDetails(courseDetailServiceRequests);

        // 9. 강의 목록 페이지로 리다이렉트
        return "redirect:/instr/course/list/approved"; // 강의 목록 페이지로 이동
    }

    @GetMapping("/admin/course/update/course/{courseId}")
    public String updateCourseForm(@PathVariable Long courseId, Model model) {
        // 1. 강의 기본 정보 조회
        CourseEntity courseEntity = courseService.getCourseById(courseId);
        CourseUpdateResponseDTO updateResponse = CourseUpdateResponseDTO.builder()
                .courseId(courseId)
                .courseThumbnail(courseEntity.getCourseThumbnail())
                .courseTitle(courseEntity.getCourseTitle())
                .courseDescript(courseEntity.getCourseDescript())
                .courseDifficulty(courseEntity.getCourseDifficulty())
                .courseCategory(courseEntity.getCourseCategory())
                .coursePrice(courseEntity.getCoursePrice())
                .build();

        model.addAttribute("updateResponse", updateResponse);
        model.addAttribute("courseDifficulties", CourseDifficulty.values());
        model.addAttribute("courseId", courseId);
        Long fileSize= maxFileSize.toMegabytes();
        model.addAttribute("maxFileSize", fileSize);
        Long requestSize = maxRequestSize.toMegabytes();
        model.addAttribute("maxRequestSize", requestSize);

        // View로 이동
        return "admin/courseupdate";
    }

    @PostMapping("/admin/course/update/course/{courseId}")
    public String updateCourse(@PathVariable Long courseId,
                               @ModelAttribute CourseUpdateRequestDTO request,
                               @AuthenticationPrincipal UserEntity user,
                               BindingResult bindingResult,
                               Model model) {

        // 1. 강의 기본 정보 조회
        courseService.updateCourse(request);
        // 강의 정보 페이지 이동
        return "redirect:/admin/course/" + courseId;
    }

    @GetMapping("/admin/course/update/coursedescript/{courseDescriptId}")
    public String updateCourseDescriptForm(@PathVariable Long courseDescriptId, Model model) {

        CourseDescriptEntity courseDescript = courseDescriptRepository.findById(courseDescriptId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 강의 홍보글을 찾을 수 없습니다. ID: " + courseDescriptId));

        List<CourseDescriptFileEntity> courseDescriptFiles = courseDescriptService.getCourseDescriptFiles(courseDescript);
        CourseDescriptUpdateResponseDTO updateResponse = CourseDescriptUpdateResponseDTO.builder()
                .courseDescriptId(courseDescriptId)
                .courseDescriptContent(courseDescript.getCourseDescriptContent())
                .courseDescriptFiles(courseDescriptService.convertDescriptFilesToFileNames(courseDescriptFiles))
                .build();

        model.addAttribute("updateResponse", updateResponse);
        model.addAttribute("courseDescriptId", courseDescriptId);
        Long fileSize= maxFileSize.toMegabytes();
        model.addAttribute("maxFileSize", fileSize);
        Long requestSize = maxRequestSize.toMegabytes();
        model.addAttribute("maxRequestSize", requestSize);

        // View로 이동
        return "admin/coursedescriptupdate";
    }

    @PostMapping("/admin/course/update/coursedescript/{courseDescriptId}")
    public String updateCourseDescript(@PathVariable Long courseDescriptId,
                                       @ModelAttribute CourseDescriptUpdateRequestDTO request,
//                                       @RequestParam(value = "filesToDelete", required = false) List<Long> filesToDelete,
                                       @AuthenticationPrincipal UserEntity user,
                                       BindingResult bindingResult,
                                       Model model) {

        CourseDescriptEntity courseDescript = courseDescriptRepository.findById(courseDescriptId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 강의 홍보글을 찾을 수 없습니다. ID: " + courseDescriptId));
        Long courseId = courseDescript.getCourse().getCourseId();

        courseDescriptService.updateCourseDescript(request);

        // 강의 정보 페이지 이동
        return "redirect:/admin/course/" + courseId;
    }

    @GetMapping("/admin/course/update/coursefile/{courseFileId}")
    public String updateCourseFileForm(@PathVariable Long courseFileId, Model model) {

        CourseFileEntity courseFile = courseFileRepository.findById(courseFileId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 강의 자료를 찾을 수 없습니다. ID: " + courseFileId));

        CourseDetailEntity courseDetail = courseFile.getCourseDetail();

        CourseDetailUpdateResponseDTO updateResponse = CourseDetailUpdateResponseDTO.builder()
                .courseDetailId(courseDetail.getCourseDetailId())
                .courseFileId(courseFileId)
                .courseVideoId(null)
                .courseDetailTitle(courseDetail.getCourseDetailTitle())
                .courseDetailFileUUID(courseFile.getCourseFileUUID())
                .courseVideoUrl(null)
                .build();
        model.addAttribute("updateResponse", updateResponse);
        model.addAttribute("courseFileId", courseFileId);
        Long fileSize= maxFileSize.toMegabytes();
        model.addAttribute("maxFileSize", fileSize);
        Long requestSize = maxRequestSize.toMegabytes();
        model.addAttribute("maxRequestSize", requestSize);

        // View로 이동
        return "admin/coursefileupdate";
    }

    @PostMapping("/admin/course/update/coursefile/{courseFileId}")
    public String updateCourseFile(@PathVariable Long courseFileId,
                                   @ModelAttribute CourseDetailUpdateRequestDTO request,
                                   @AuthenticationPrincipal UserEntity user,
                                   BindingResult bindingResult,
                                   Model model) {

        CourseFileEntity courseFile = courseFileRepository.findById(courseFileId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 강의 자료를 찾을 수 없습니다. ID: " + courseFileId));

        CourseDetailEntity courseDetail = courseFile.getCourseDetail();

        courseDetailService.updateCourseDetailFile(request);

//        CourseDetailUpdateResponseDTO updateResponse = CourseDetailUpdateResponseDTO.builder()
//                .courseDetailId(courseDetail.getCourseDetailId())
//                .courseFileId(courseFileId)
//                .courseVideoId(null)
//                .courseDetailTitle(courseDetail.getCourseDetailTitle())
//                .courseDetailFileUUID(courseFile.getCourseFileUUID())
//                .courseVideoUrl(null)
//                .build();

        // 강의 정보 페이지 이동
        return "redirect:/admin/course/" + courseDetail.getCourse().getCourseId();
    }


    @GetMapping("/admin/course/update/coursevideo/{courseVideoId}")
    public String updateCourseVideoForm(@PathVariable Long courseVideoId, Model model) {


        CourseVideoEntity courseVideo = courseVideoRepository.findById(courseVideoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 강의 자료를 찾을 수 없습니다. ID: " + courseVideoId));

        CourseDetailEntity courseDetail = courseVideo.getCourseDetail();

        CourseDetailUpdateResponseDTO updateResponse = CourseDetailUpdateResponseDTO.builder()
                .courseDetailId(courseDetail.getCourseDetailId())
                .courseFileId(null)
                .courseVideoId(courseVideoId)
                .courseDetailTitle(courseDetail.getCourseDetailTitle())
                .courseDetailFileUUID(null)
                .courseVideoUrl(courseVideo.getCourseVideoUUID())
                .build();

        model.addAttribute("updateResponse", updateResponse);
        model.addAttribute("type", courseVideo.getCourseVideoType());
        model.addAttribute("courseVideoId", courseVideoId);

        Long fileSize= maxFileSize.toMegabytes();
        model.addAttribute("maxFileSize", fileSize);
        Long requestSize = maxRequestSize.toMegabytes();
        model.addAttribute("maxRequestSize", requestSize);
        return "admin/coursevideoupdate";
    }

    @PostMapping("/admin/course/update/coursevideo/{courseVideoId}")
    public String  updateCourseVideo (@PathVariable Long courseVideoId, @ModelAttribute CourseDetailUpdateRequestDTO request){

        CourseVideoEntity courseVideo = courseVideoRepository.findById(courseVideoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 강의 영상을 찾을 수 없습니다. ID: " + courseVideoId));

        CourseDetailEntity courseDetail = courseVideo.getCourseDetail();

        courseDetailService.updateCourseDetailVideo(request);
        // 강의 정보 페이지 이동
        return "redirect:/admin/course/" + courseDetail.getCourse().getCourseId();
    }




    @GetMapping("/admin/course/addVideo/{courseId}")
    public String addCourseVideoForm(@PathVariable Long courseId, Model model) {

        Long fileSize= maxFileSize.toMegabytes();
        model.addAttribute("maxFileSize", fileSize);
        Long requestSize = maxRequestSize.toMegabytes();
        model.addAttribute("maxRequestSize", requestSize);
        model.addAttribute("courseId", courseId);
        return "admin/addcoursevideo";
    }

    @PostMapping("/admin/course/addVideo/{courseId}")
    public String addCourseVideo(@PathVariable Long courseId, CourseDetailAddDTO request,
                                 @AuthenticationPrincipal UserEntity user) {


        CourseEntity courseEntity = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 강의를 찾을 수 없습니다. ID: " + courseId));

        // 7. 강의 세부 정보 생성 요청 객체 변환
        // 강의 ID를 포함하는 여러 개의 CourseDetailCreateServiceRequest 객체 생성
        List<CourseDetailCreateServiceRequest> courseDetailServiceRequests = request.toCourseDetailServiceRequests(courseEntity);

        // 8. 강의 세부 정보 저장
        // 변환된 요청 객체들을 사용하여 CourseDetailEntity(강의 세부 정보)와 관련 파일 저장
        courseDetailService.saveCourseDetails(courseDetailServiceRequests);


        return "redirect:/admin/course/" + courseId;
    }

//
//    @GetMapping("/admin/course/update/{courseId}")
//    public String updateCourseForm(@PathVariable Long courseId, Model model) {
//        // 1. 강의 기본 정보 조회
//        CourseEntity courseEntity = courseService.getCourseById(courseId);
//
//        // 2. 강의로 강의 설명 조회
//        CourseDescriptEntity courseDescript = courseDescriptService.getCourseDescript(courseEntity);
//
//        // 3. 강의설명으로 강의설명파일 조회
//        List<CourseDescriptFileEntity> courseDescriptFiles = courseDescriptService.getCourseDescriptFiles(courseDescript);
//
//        // 4. 강의로 강의 상세 정보 조회
//        List<CourseDetailEntity> courseDetailEntities = courseDetailService.getCourseDetails(courseEntity);
//
//        // 5. 강의 상세 정보로 강의 동영상 조회
//        List<CourseVideoEntity> courseVideos = courseDetailService.getCourseVideos(courseDetailEntities);
//
//        // 6. 강의 상세 정보로 강의 파일 조회
//        List<CourseFileEntity> courseFiles = courseDetailService.getCourseFiles(courseDetailEntities);
//
//        // 7. update DTO 생성
//        CourseUpdateResponse updateResponse = CourseUpdateResponse.builder()
//                .courseId(courseId)
//                .courseDescriptId(courseDescript.getCourseDescriptId())
//                .courseTitle(courseEntity.getCourseTitle())
//                .courseDescript(courseEntity.getCourseDescript())
//                .coursePrice(courseEntity.getCoursePrice())
//                .courseCategory(courseEntity.getCourseCategory())
//                .courseDifficulty(courseEntity.getCourseDifficulty())
//                .courseStatus(courseEntity.getCourseStatus())
//                .courseDescriptContent(courseDescript.getCourseDescriptContent()) // 설명 내용 추가
//                .courseThumbnail(courseEntity.getCourseThumbnail()) // 기존 썸네일 경로
//                .courseDescriptFiles(courseDescriptService.convertDescriptFilesToFileNames(courseDescriptFiles)) // 설명 파일 이름 리스트
//                .courseDetails(courseDetailService.convertCourseDetailsToUpdateResponses(courseDetailEntities)) // 강의 상세 정보 리스트 변환
//                .build();
//
//
//        // 8. model 데이터 추가
//        model.addAttribute("updateResponse", updateResponse);
//        model.addAttribute("courseDifficulties", CourseDifficulty.values());
//        model.addAttribute("courseId", courseId);
//
//        // View로 이동
//        return "admin/courseupdate";
//    }
//
//
//    @PostMapping("/admin/course/update/{courseId}")
//    public String updateCourse(@PathVariable Long courseId,
//                               @ModelAttribute CourseUpdateRequest request,
//                               @RequestParam(value = "filesToDelete", required = false) List<Long> filesToDelete,
//                               @AuthenticationPrincipal UserEntity user,
//                               BindingResult bindingResult,
//                               Model model) {
//        // 1. 입력 값 유효성 검사
//        if (bindingResult.hasErrors()) {
//            String redirectUrl = UriComponentsBuilder
//                    .fromPath("/admin/course/update/{courseId}")
//                    .buildAndExpand(courseId) // 경로 변수 치환
//                    .toUriString();
//            return "redirect:" + redirectUrl;
//        }
//
//        // 2. 강의 정보 확인
//        CourseEntity courseEntity = courseRepository.findById(courseId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 강의를 찾을 수 없습니다. ID: " + courseId));
//
//        // 3. 강사 정보 확인
//        InstrEntity instrEntity = courseEntity.getInstr();
//
//        // 4. `courseDetails`가 `null`이거나 비어 있을 경우 오류 메시지 추가
//        if (request.getCourseDetails() == null || request.getCourseDetails().isEmpty()) {
//            bindingResult.rejectValue("courseDetails", "empty.courseDetails", "강의 자료가 최소 1개 이상 필요합니다.");
//
//            // 기존 입력값을 유지하기 위해 모델에 추가
//            model.addAttribute("msg", "강의 자료가 최소 1개 이상 필요합니다.");
//            model.addAttribute("loc", "/admin/course/update/" + courseId);  // 다시 강사 계정 등록 페이지로 돌아가기
//            return "utility/message"; // 오류 메시지 페이지로 이동
//        }
//
//        // 5. 강의 정보 업데이트
//        CourseUpdateServiceRequest courseServiceRequest = request.toCourseServiceRequest(instrEntity);
//        courseService.updateCourse(courseServiceRequest);
//
//        // 6. 강의 설명 정보 업데이트 요청 객체 생성
//        CourseDescriptUpdateServiceRequest descriptRequest = request.toCourseDescriptServiceRequest(courseEntity,filesToDelete);
//        courseDescriptService.updateCourseDescript(descriptRequest);
//
//        // 7. 강의 세부 정보 업데이트
//        List<CourseDetailUpdateServiceRequest> courseDetailServiceRequests = request.toCourseDetailServiceRequests(courseEntity);
//        courseDetailService.updateCourseDetails(courseDetailServiceRequests);
//
//        // 8. 강의 목록으로 리다이렉트
//        return "redirect:/admin/course/" + courseId;
//    }

    @GetMapping("/admin/course")
    public String createAdminCourseForm(Model model) {
        // Enum 값을 Model에 추가
        model.addAttribute("courseDifficulties", CourseDifficulty.values());
        Long fileSize= maxFileSize.toMegabytes();
        model.addAttribute("maxFileSize", fileSize);
        Long requestSize = maxRequestSize.toMegabytes();
        model.addAttribute("maxRequestSize", requestSize);
        return "admin/course";
    }

    @PostMapping("/admin/course")
    public String createAdminCourse(@AuthenticationPrincipal UserEntity user,
                               @Valid @ModelAttribute CourseCreateRequest request,
                               BindingResult bindingResult,
                               Model model) {
        // 1. 입력 값 유효성 검사
        if (bindingResult.hasErrors()) {
            // 유효성 검사 실패 시, 에러 메시지와 함께 입력 폼으로 다시 이동
            model.addAttribute("courseCreateRequest", request);
            model.addAttribute("courseDifficulties", CourseDifficulty.values());
            return "admin/course"; // 다시 폼 페이지로 이동
        }

        // courseDetails 리스트가 누락된 경우 기본값 설정
//        if (request.getCourseDetails() == null || request.getCourseDetails().isEmpty()) {
//            model.addAttribute("error", "강의 세부 정보가 누락되었습니다.");
//            model.addAttribute("courseCreateRequest", request);
//            model.addAttribute("courseDifficulties", CourseDifficulty.values());
//            return "admin/course";
//        }


//        // 각 세부 정보 확인 (로그로 출력)
//        request.getCourseDetails().forEach(detail -> {
//            log.info("CourseDetail: Outline={}, Title={}",
//                    detail.getCourseDetailOutline(),
//                    detail.getCourseDetailTitle());
//        });

        // 2. 강사 정보 검색
        // 현재 인증된 유저의 ID를 이용하여 InstrEntity(강사 정보) 조회
        InstrEntity instrEntity = instrRepository.findByUserEntityUserId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 강사 정보가 존재하지 않습니다."));

        // 3. 강의 기본 정보 생성 요청 객체 변환
        // CourseCreateRequest를 CourseCreateServiceRequest로 변환하여 서비스 계층으로 전달
        CourseCreateServiceRequest courseServiceRequest = request.toCourseServiceRequest(instrEntity);

        // 4. 강의 기본 정보 저장
        // 변환된 요청 객체를 사용하여 CourseEntity(강의 엔티티) 저장 및 반환
        CourseEntity savedCourse = courseService.saveCourse(courseServiceRequest);

        // 5. 강의 설명 정보 생성 요청 객체 변환
        // 강의 ID를 포함하는 CourseDescriptCreateServiceRequest 객체 생성
        CourseDescriptCreateServiceRequest courseDescriptServiceRequest = request.toCourseDescriptServiceRequest(savedCourse);

        // 6. 강의 설명 정보 저장
        // 강의 설명 내용을 CourseDescriptEntity에 저장
        CourseDescriptEntity savedCourseDescript = courseDescriptService.saveCourseDescript(courseDescriptServiceRequest);

        // 7. 강의 세부 정보 생성 요청 객체 변환
        // 강의 ID를 포함하는 여러 개의 CourseDetailCreateServiceRequest 객체 생성
        List<CourseDetailCreateServiceRequest> courseDetailServiceRequests = request.toCourseDetailServiceRequests(savedCourse);

        // 8. 강의 세부 정보 저장
        // 변환된 요청 객체들을 사용하여 CourseDetailEntity(강의 세부 정보)와 관련 파일 저장
        courseDetailService.saveCourseDetails(courseDetailServiceRequests);

        // 9. 강의 목록 페이지로 리다이렉트
        return "redirect:/admin/course/list/approved"; // 강의 목록 페이지로 이동
    }

    @GetMapping("/admin/course/list/approved")  //status = approval / [본인이 작성한 경우]
    public String getAdminCourseList(@AuthenticationPrincipal UserEntity user,
//                                @PathVariable("approved") String approved,
                                Model model) {

        List<CourseEntity> approvedCourses = courseService.getApprovedCourses();

        // 4. CourseListResponse로 변환
        List<CourseListResponse> courseListResponses = approvedCourses.stream()
                .map(course -> CourseListResponse.builder()
                        .courseId(course.getCourseId())
                        .courseTitle(course.getCourseTitle())
                        .courseDescript(course.getCourseDescript())
                        .coursePrice(course.getCoursePrice())
                        .courseCategory(course.getCourseCategory())
                        .courseDifficulty(course.getCourseDifficulty())
                        .courseThumbnail(course.getCourseThumbnail())
                        .courseStatus(course.getCourseStatus())
                        .build())
                .collect(Collectors.toList());

        // 5. Model에 데이터 추가
        model.addAttribute("courseList", courseListResponses);

        // 6. 뷰 반환
        return "admin/courselist"; // courseList.html 뷰로 매핑
    }

    @GetMapping("/admin/course/{courseId}")
    public String getAdminCourseDetail(@PathVariable Long courseId, Model model) {
        // 1. 강의 기본 정보 조회
        CourseEntity courseEntity = courseService.getCourseById(courseId);

        // 2. 강의로 강의 설명 조회
        CourseDescriptEntity courseDescript = courseDescriptService.getCourseDescript(courseEntity);

        // 3. 강의설명으로 강의설명파일 조회
        List<CourseDescriptFileEntity> courseDescriptFiles = courseDescriptService.getCourseDescriptFiles(courseDescript);

        // 4. 강의로 강의 상세 정보 조회
        List<CourseDetailEntity> courseDetailEntities = courseDetailService.getCourseDetails(courseEntity);

        // 5. 강의 상세 정보로 강의 동영상 조회
        List<CourseVideoEntity> courseVideos = courseDetailService.getCourseVideos(courseDetailEntities);

        // 6. 강의 상세 정보로 강의 파일 조회
        List<CourseFileEntity> courseFiles = courseDetailService.getCourseFiles(courseDetailEntities);

        // 7. update DTO 생성
        CourseUpdateResponse updateResponse = CourseUpdateResponse.builder()
                .courseId(courseId)
                .courseDescriptId(courseDescript.getCourseDescriptId())
                .courseTitle(courseEntity.getCourseTitle())
                .courseDescript(courseEntity.getCourseDescript())
                .coursePrice(courseEntity.getCoursePrice())
                .courseCategory(courseEntity.getCourseCategory())
                .courseDifficulty(courseEntity.getCourseDifficulty())
                .courseStatus(courseEntity.getCourseStatus())
                .courseDescriptContent(courseDescript.getCourseDescriptContent()) // 설명 내용 추가
                .courseThumbnail(courseEntity.getCourseThumbnail()) // 기존 썸네일 경로
                .courseDescriptFiles(courseDescriptService.convertDescriptFilesToFileNames(courseDescriptFiles)) // 설명 파일 이름 리스트
                .courseDetails(courseDetailService.convertCourseDetailsToUpdateResponses(courseDetailEntities)) // 강의 상세 정보 리스트 변환
                .build();


        // 8. model 데이터 추가
        model.addAttribute("updateResponse", updateResponse);
        model.addAttribute("courseDifficulties", CourseDifficulty.values());
        model.addAttribute("courseId", courseId);


        // 뷰 이름 반환
        return "admin/coursedetail"; // coursedetail.html로 매핑
    }

    @GetMapping("/admin/course/list/pending")
    public String getPendingCourseList(@AuthenticationPrincipal UserEntity user,
//                                       @PathVariable("pending") String pending,
                                       Model model){

        List<CourseEntity> pendingCourses = courseService.getPendingCourses();

        // CourseListResponse로 변환
        List<CourseListResponse> courseListResponses = pendingCourses.stream()
                .map(course -> CourseListResponse.builder()
                        .courseId(course.getCourseId())
                        .courseTitle(course.getCourseTitle())
                        .courseDescript(course.getCourseDescript())
                        .coursePrice(course.getCoursePrice())
                        .courseCategory(course.getCourseCategory())
                        .courseDifficulty(course.getCourseDifficulty())
                        .courseThumbnail(course.getCourseThumbnail())
                        .courseStatus(course.getCourseStatus())
                        .build())
                .collect(Collectors.toList());

        // Model에 데이터 추가
        model.addAttribute("courseList", courseListResponses);

        return "admin/pendingcourselist";
    }

    //메니저 복붙 메니저 복붙 메니저 복붙 메니저 복붙 메니저 복붙 메니저 복붙 메니저 복붙 메니저 복붙 메니저 복붙 메니저 복붙

//    @GetMapping("/manager/course/list/{approved}")  //status = approval / [본인이 작성한 경우]
//    public String getManagerCourseList(@AuthenticationPrincipal UserEntity user,
//                                @PathVariable("approved") String approved,
//                                Model model) {
//        // 2. 강사 정보 확인
//        // 현재 인증된 유저의 ID를 이용하여 managerEntity(강사 정보) 조회
//        InstrEntity instrEntity = instrRepository.findByUserEntityUserId(user.getUserId())
//                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 강사 정보가 존재하지 않습니다."));
//
//
//        // 2. 강사의 강의 목록 조회
//        List<CourseEntity> courseEntities = courseService.getCoursesByInstructor(instrEntity);
//
//
//        // 3. APPROVED 상태 필터링
//        List<CourseEntity> approvedCourses = courseEntities.stream()
//                .filter(course -> CourseStatus.APPROVED.equals(course.getCourseStatus()))
//                .collect(Collectors.toList());
//
//        // 4. CourseListResponse로 변환
//        List<CourseListResponse> courseListResponses = approvedCourses.stream()
//                .map(course -> CourseListResponse.builder()
//                        .courseId(course.getCourseId())
//                        .courseTitle(course.getCourseTitle())
//                        .courseDescript(course.getCourseDescript())
//                        .coursePrice(course.getCoursePrice())
//                        .courseCategory(course.getCourseCategory())
//                        .courseDifficulty(course.getCourseDifficulty())
//                        .courseThumbnail(course.getCourseThumbnail())
//                        .courseStatus(course.getCourseStatus())
//                        .build())
//                .collect(Collectors.toList());
//
//        // 5. Model에 데이터 추가
//        model.addAttribute("courseList", courseListResponses);
//
//        // 6. 뷰 반환
//        return "manager/courseList"; // courseList.html 뷰로 매핑
//    }
//
//    @GetMapping("/manager/course/{courseId}")
//    public String getManagerCourseDetail(@PathVariable Long courseId, Model model) {
//        // 1. 강의 기본 정보 조회
//        CourseEntity courseEntity = courseService.getCourseById(courseId);
//
//        // 2. 강의로 강의 설명 조회
//        CourseDescriptEntity courseDescript = courseDescriptService.getCourseDescript(courseEntity);
//
//        // 3. 강의설명으로 강의설명파일 조회
//        List<CourseDescriptFileEntity> courseDescriptFiles = courseDescriptService.getCourseDescriptFiles(courseDescript);
//
//        // 4. 강의로 강의 상세 정보 조회
//        List<CourseDetailEntity> courseDetailEntities = courseDetailService.getCourseDetails(courseEntity);
//
//        // 5. 강의 상세 정보로 강의 동영상 조회
//        List<CourseVideoEntity> courseVideos = courseDetailService.getCourseVideos(courseDetailEntities);
//
//        // 6. 강의 상세 정보로 강의 파일 조회
//        List<CourseFileEntity> courseFiles = courseDetailService.getCourseFiles(courseDetailEntities);
//
//        // 7. update DTO 생성
//        CourseUpdateResponse updateResponse = CourseUpdateResponse.builder()
//                .courseId(courseId)
//                .courseDescriptId(courseDescript.getCourseDescriptId())
//                .courseTitle(courseEntity.getCourseTitle())
//                .courseDescript(courseEntity.getCourseDescript())
//                .coursePrice(courseEntity.getCoursePrice())
//                .courseCategory(courseEntity.getCourseCategory())
//                .courseDifficulty(courseEntity.getCourseDifficulty())
//                .courseStatus(courseEntity.getCourseStatus())
//                .courseDescriptContent(courseDescript.getCourseDescriptContent()) // 설명 내용 추가
//                .courseThumbnail(courseEntity.getCourseThumbnail()) // 기존 썸네일 경로
//                .courseDescriptFiles(courseDescriptService.convertDescriptFilesToFileNames(courseDescriptFiles)) // 설명 파일 이름 리스트
//                .courseDetails(courseDetailService.convertCourseDetailsToUpdateResponses(courseDetailEntities)) // 강의 상세 정보 리스트 변환
//                .build();
//
//
//        // 8. model 데이터 추가
//        model.addAttribute("updateResponse", updateResponse);
//        model.addAttribute("courseDifficulties", CourseDifficulty.values());
//        model.addAttribute("courseId", courseId);
//
//
//        // 뷰 이름 반환
//        return "manager/coursedetail"; // coursedetail.html로 매핑
//    }
//
//    @GetMapping("/manager/course")
//    public String createManagerCourseForm(Model model) {
//        // Enum 값을 Model에 추가
//        model.addAttribute("courseDifficulties", CourseDifficulty.values());
//        return "manager/course";
//    }
//
//    @PostMapping("/manager/course")
//    public String createManagerCourse(@AuthenticationPrincipal UserEntity user,
//                                      @Valid @ModelAttribute CourseCreateRequest request,
//                                      BindingResult bindingResult,
//                                      Model model) {
//        // 1. 입력 값 유효성 검사
//        if (bindingResult.hasErrors()) {
//            // 유효성 검사 실패 시, 에러 메시지와 함께 입력 폼으로 다시 이동
//            model.addAttribute("courseCreateRequest", request);
//            model.addAttribute("courseDifficulties", CourseDifficulty.values());
//            return "manager/course"; // 다시 폼 페이지로 이동
//        }
//
//        // courseDetails 리스트가 누락된 경우 기본값 설정
//        if (request.getCourseDetails() == null || request.getCourseDetails().isEmpty()) {
//            model.addAttribute("error", "강의 세부 정보가 누락되었습니다.");
//            model.addAttribute("courseCreateRequest", request);
//            model.addAttribute("courseDifficulties", CourseDifficulty.values());
//            return "manager/course";
//        }
//
////        // 각 세부 정보 확인 (로그로 출력)
////        request.getCourseDetails().forEach(detail -> {
////            log.info("CourseDetail: Outline={}, Title={}",
////                    detail.getCourseDetailOutline(),
////                    detail.getCourseDetailTitle());
////        });
//
//        // 2. 강사 정보 검색
//        // 현재 인증된 유저의 ID를 이용하여 managerEntity(강사 정보) 조회
//        InstrEntity instrEntity = instrRepository.findByUserEntityUserId(user.getUserId())
//                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 강사 정보가 존재하지 않습니다."));
//
//        // 3. 강의 기본 정보 생성 요청 객체 변환
//        // CourseCreateRequest를 CourseCreateServiceRequest로 변환하여 서비스 계층으로 전달
//        CourseCreateServiceRequest courseServiceRequest = request.toCourseServiceRequest(instrEntity);
//
//        // 4. 강의 기본 정보 저장
//        // 변환된 요청 객체를 사용하여 CourseEntity(강의 엔티티) 저장 및 반환
//        CourseEntity savedCourse = courseService.saveCourse(courseServiceRequest);
//
//        // 5. 강의 설명 정보 생성 요청 객체 변환
//        // 강의 ID를 포함하는 CourseDescriptCreateServiceRequest 객체 생성
//        CourseDescriptCreateServiceRequest courseDescriptServiceRequest = request.toCourseDescriptServiceRequest(savedCourse);
//
//        // 6. 강의 설명 정보 저장
//        // 강의 설명 내용을 CourseDescriptEntity에 저장
//        CourseDescriptEntity savedCourseDescript = courseDescriptService.saveCourseDescript(courseDescriptServiceRequest);
//
//        // 7. 강의 세부 정보 생성 요청 객체 변환
//        // 강의 ID를 포함하는 여러 개의 CourseDetailCreateServiceRequest 객체 생성
//        List<CourseDetailCreateServiceRequest> courseDetailServiceRequests = request.toCourseDetailServiceRequests(savedCourse);
//
//        // 8. 강의 세부 정보 저장
//        // 변환된 요청 객체들을 사용하여 CourseDetailEntity(강의 세부 정보)와 관련 파일 저장
//        courseDetailService.saveCourseDetails(courseDetailServiceRequests);
//
//        // 9. 강의 목록 페이지로 리다이렉트
//        return "redirect:/manager/course/list/APPROVED"; // 강의 목록 페이지로 이동
//    }

}
