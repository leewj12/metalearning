package com.Meta_learning.main.maincontroller;

import com.Meta_learning.KDT.KDTDTO.KDTAppConsultDTO.KDTAppConsultDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionCategoryDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionDTO;
import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTSessionViewDTO;
import com.Meta_learning.KDT.KDTentity.KDTDetailEntity.KDTDetailEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.KDT.KDTrepository.KDTDetailRepository.KDTDetailRepository;
import com.Meta_learning.KDT.KDTrepository.KDTSessionRepository.KDTSessionRepository;
import com.Meta_learning.KDT.KDTservice.KDTService.KDTService;
import com.Meta_learning.KDT.KDTservice.response.KDTDetailResponse;
import com.Meta_learning.board.boardDTO.BoardDTO;
import com.Meta_learning.board.boardDTO.BoardFileDTO;
import com.Meta_learning.board.boardDTO.BoardTitleDTO;
import com.Meta_learning.board.boardservice.BoardService;
import com.Meta_learning.course.coursecontroller.dto.response.CourseListResponse;
import com.Meta_learning.course.coursecontroller.dto.response.CourseUpdateResponse;
import com.Meta_learning.course.coursecontroller.dto.response.CourseViewResponse;
import com.Meta_learning.course.courseentity.*;
import com.Meta_learning.course.courseentity.order.OrderEntity;
import com.Meta_learning.course.courserepository.CourseVideoRepository;
import com.Meta_learning.course.courseservice.CourseDescriptService;
import com.Meta_learning.course.courseservice.CourseDetailService;
import com.Meta_learning.course.courseservice.CourseService;
import com.Meta_learning.course.courseservice.order.OrderService;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userservice.UserBuyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final CourseService courseService;
    private final CourseDescriptService courseDescriptService;
    private final CourseDetailService courseDetailService;
    private final KDTService kdtService;
    private final BoardService boardService;
    private final OrderService orderService;
    private final UserBuyService userBuyService;
    private final CourseVideoRepository courseVideoRepository;
    private final KDTDetailRepository kdtDetailRepository;
    private final KDTSessionRepository kdtSessionRepository;

    @GetMapping("/")
    public String home() {
        return "home/home";
    }


    //국비과정 목록 페이징처리
    @GetMapping("/main/KDT-curriculum")
    public String KDTcurriculum(Model model,
                                @RequestParam(defaultValue = "1") int page,  // 페이지 번호, 기본값 1
                                @RequestParam(defaultValue = "10") int size,  // 페이지 크기, 기본값 10
                                @RequestParam(defaultValue = "") String searchName,  // 검색어
                                @RequestParam(defaultValue = "") String searchCategory) {  // 카테고리 검색

        // Pageable 설정: 페이지 번호가 0부터 시작하므로 1에서 -1을 해줍니다.
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);  // 페이지 번호가 1일 때 0부터 시작하도록 보정

        // 페이징과 검색을 포함한 서비스 호출
        Page<KDTSessionViewDTO> sessionPage;

        // 검색어가 있을 경우 필터링을 하도록 처리
        if (!searchName.isEmpty() || !searchCategory.isEmpty()) {
            sessionPage = kdtService.getSessionsAllview(searchName, searchCategory, pageable);
        } else {
            sessionPage = kdtService.getSessionsAllview("", "", pageable); // 검색어가 없을 경우 전체 조회
        }





        // 페이징 블록 관련 로직 (페이징 블록 계산)
        int pagingBlock = 5;
        int currentPage = sessionPage.getNumber() + 1;  // 1-based 페이지 번호로 변경
        int totalPages = sessionPage.getTotalPages();
        long totalElements = sessionPage.getTotalElements();

        // 페이징 블록 시작과 끝 페이지 계산
        int blockStart = (currentPage - 1) / pagingBlock * pagingBlock + 1;
        int blockEnd = Math.min(blockStart + pagingBlock - 1, totalPages);

        // 검색 결과가 없을 경우
        boolean noResults = totalElements == 0;

        // 세션 목록을 처리하면서 D-Day 계산
        List<KDTSessionViewDTO> sessionList = sessionPage.getContent();

        // D-Day 계산
        // D-Day 계산
        for (KDTSessionViewDTO session : sessionList) {
            // 시작일시 만들기
            LocalDateTime startDateTime = LocalDateTime.of(session.getKdtSessionStartDate(), session.getKdtSessionStartTime());
            LocalDateTime now = LocalDateTime.now();

            // 날짜만 비교하도록 수정
            LocalDate startDate = startDateTime.toLocalDate();
            LocalDate today = now.toLocalDate();

            // D-Day 계산
            String dDay = "";
            if (startDate.isAfter(today)) {
                // 시작일이 아직 오지 않은 경우
                long daysBetween = java.time.Duration.between(now, startDateTime).toDays();
                dDay = "D-" + (daysBetween);  // 시작일까지 남은 일수, D-0을 피하기 위해 1 추가
            } else if (startDate.isEqual(today)) {
                // 오늘 시작하는 경우
                dDay = "D-Day 수강 시작";
            } else {
                // 이미 종료된 경우
                dDay = "모집 마감";
            }

            // D-Day 값을 session 객체에 추가
            session.setKdtSessionDday(dDay); // D-Day 값을 세션에 추가
        }

        //카테고리 db에서 꺼내와서 보내서 목록 만들어주는 거임
        List<KDTSessionCategoryDTO> kdtSessionCategorys= kdtService.getDistinctCategories();

        model.addAttribute("kdtSessionCategorys",kdtSessionCategorys);

        // 모델에 데이터를 추가
        model.addAttribute("sessionLists", sessionList);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pagingBlock", pagingBlock);
        model.addAttribute("blockStart", blockStart);
        model.addAttribute("blockEnd", blockEnd);

        boolean hasPreviousBlock = currentPage > pagingBlock;
        boolean hasNextBlock = blockEnd < totalPages;

        model.addAttribute("hasNext", hasNextBlock);  // 다음 페이지가 있을 경우
        model.addAttribute("hasPrevious", hasPreviousBlock);  // 이전 페이지가 있을 경우
        model.addAttribute("searchName", searchName);  // Pass searchName to the model
        model.addAttribute("noResults", noResults);
        model.addAttribute("searchCategory", searchCategory);  // Pass searchCategory to the model
        model.addAttribute("totalElements", totalElements);

        // KDT-curriculum 페이지로 데이터를 전달
        return "main/KDT-curriculum";
    }


    // 비회원 국비신청 상담 작성하는 곳
    @GetMapping("/main/KDT-app-consult/{kdtSessionId}")
    public String getviewConsultation(@PathVariable Long kdtSessionId, Model model) {
        // kdtSessionId를 사용하여 세션 정보 조회

        KDTSessionDTO kdtSession = kdtService.getSessionsBySessId(kdtSessionId);

        model.addAttribute("kdtSession", kdtSession);
        return "main/KDT-app-consultation"; // 뷰 이름 반환
    }

    // 비회원 국비신청 상담 작성 데이터 받는 곳
    @PostMapping("/main/KDT-app-consultation")
    public String submitConsultation(KDTAppConsultDTO kdtAppConsultDTO, Model model) {
        // 비회원이 제출한 상담 내용이 올바르게 들어오는지 확인하는 로그

        // 상담 저장 서비스 호출, 저장 여부 결과를 boolean 변수에 저장
        boolean consultationsave = kdtService.consultationsave(kdtAppConsultDTO);

        // 상담 저장 성공 시 메시지 설정
        if (consultationsave) {
            // 상담 신청이 성공적으로 저장되었을 경우
            model.addAttribute("msg", "상담 신청이 완료되었습니다");
        } else {
            // 상담 저장에 실패한 경우
            model.addAttribute("msg", "상담 신청에 실패했습니다. 다시 시도해주세요.");
        }

        // 상담 완료 후 리디렉션할 URL을 설정
        model.addAttribute("loc", "/main/KDT-curriculum");  // 회원가입 페이지로 리디렉션 (예시)

        // 메시지를 출력하는 뷰를 반환
        return "utility/message";
    }


    @GetMapping("/main/notice")
    public String notice() {
        return "main/notice";
    }


    @GetMapping("/main/about-metalearning")
    public String aboutMetalearning() {
        return "main/about-metalearning";
    }

    @GetMapping("/main/way-to-come")
    public String wayToCome() {
        return "main/way-to-come";
    }

    @GetMapping("/main/KDT-info")
    public String kdtInfo() {
        return "main/KDT-info";
    }

    @GetMapping("/main/course")
    public String courseList(@AuthenticationPrincipal UserEntity user, Model model) {
        // 로그인 여부 확인
        boolean isAuthenticated = user != null;
        String userRole = (isAuthenticated) ? user.getUserRole() : "GUEST"; // 비로그인 시 GUEST 처리

        model.addAttribute("isAuthenticated", isAuthenticated);

        // 승인된 전체 강의 조회
        List<CourseEntity> approvedCourses = courseService.getApprovedCourses();

        // CourseListResponse로 변환
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

        // 주문한 강의 ID 조회
        List<Long> purchasedCourseIds = new ArrayList<>();
        if (isAuthenticated) {
            List<OrderEntity> orders = orderService.findOrdersByUser(user);
            purchasedCourseIds = orders.stream()
                    .flatMap(order -> order.getOrderDetails().stream())
                    .map(orderDetail -> orderDetail.getCourse().getCourseId())
                    .collect(Collectors.toList());
        }
        model.addAttribute("purchasedCourseIds", purchasedCourseIds);
        model.addAttribute("userRole", userRole); // 사용자 역할 추가

        // Model에 데이터 추가
        model.addAttribute("courseList", courseListResponses);




        // 뷰 반환
        return "main/courselist"; // courselist.html로 매핑
    }

    @GetMapping("/main/course/{courseId}")
    public String courseDetail(@AuthenticationPrincipal UserEntity user, @PathVariable Long courseId, Model model) {

        Long userId = (user != null) ? user.getUserId() : null;
        String userRole = (user != null) ? user.getUserRole() : ""; // 사용자 역할 확인

        // 비회원이라면 구매 여부를 체크하지 않고 false로 설정
        boolean hasPurchased = false;
        if (userId != null) {
            // "COMPLETED" 상태로 구매했는지 확인
            hasPurchased = userBuyService.hasUserPurchasedCourse(userId, courseId);
        }

        // 관리자(ADMIN)인 경우 강의 페이지로 바로 이동
        boolean isAdmin = "ADMIN".equals(userRole);

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
                .courseDescriptId(courseDescript.getCourseDescriptId())     // 설명 id
                .courseTitle(courseEntity.getCourseTitle())                 // 제목
                .courseDescript(courseEntity.getCourseDescript())           // 강의 한줄 설명
                .coursePrice(courseEntity.getCoursePrice())                 // 가격
                .courseCategory(courseEntity.getCourseCategory())           // 카테고리
                .courseDifficulty(courseEntity.getCourseDifficulty())       // 난이도
                .courseStatus(courseEntity.getCourseStatus())               // 상태
                .courseDescriptContent(courseDescript.getCourseDescriptContent()) // 설명 내용 추가
                .courseThumbnail(courseEntity.getCourseThumbnail()) // 기존 썸네일 경로
                .courseDescriptFiles(courseDescriptService.convertDescriptFilesToFileNames(courseDescriptFiles)) // 설명 파일 이름 리스트
                .courseDetails(courseDetailService.convertCourseDetailsToUpdateResponses(courseDetailEntities)) // 강의 상세 정보 리스트 변환
                .build();

        // 8. model 데이터 추가
        model.addAttribute("updateResponse", updateResponse);
        model.addAttribute("courseDifficulties", CourseDifficulty.values());    //난이도
        model.addAttribute("courseId", courseId);

        // 관리자는 구매 여부와 관계없이 강의 페이지로 이동
        if (isAdmin) {
            return "main/coursebuy";
        }

        // 비회원도 페이지에 접근할 수 있게 하기
        if (!hasPurchased) {
            return "main/coursedetail"; // 구매하지 않았으면 강의 상세 페이지로
        } else {
            return "main/coursebuy"; // 구매했다면 "coursebuy" 페이지로
        }
    }


    // 강의 상세보기
    @GetMapping("/main/course/detail/{courseId}/{courseDetailId}")
    public String getCourseDetail(@PathVariable("courseId") Long courseId,
                                  @PathVariable("courseDetailId") Long courseDetailId,
                                  @AuthenticationPrincipal UserEntity user, Model model) {


        // 현재 로그인된 사용자의 정보 가져오기
        Long userId = (user != null) ? user.getUserId() : null;
        String userRole = (user != null) ? user.getUserRole() : ""; // 사용자 역할 확인


        CourseViewResponse courseViewResponse = courseService.getCourseViewByDetailId(courseDetailId);

        model.addAttribute("courseViewResponse",courseViewResponse);
        model.addAttribute("courseId",courseId);

        // 비회원이라면 구매 여부를 체크하지 않고 false로 설정
        boolean hasPurchased = false;
        if (userId != null) {
            // 사용자가 해당 강의를 구매했는지 확인
            hasPurchased = userBuyService.hasUserPurchasedCourse(userId, courseId);
        }

        // 사용자가 관리자(ADMIN)인지 확인
        boolean isAdmin = "ADMIN".equals(userRole);

        // 관리자일 경우 강의 구매 페이지로 이동
        if (isAdmin) {
            // 관리자는 강의 페이지와 관계없이 강의 구매 페이지로 이동
            model.addAttribute("courseId", courseId); // courseId 추가
            return "main/coursebuydetail"; // 관리자용 페이지
        }

        // 구매하지 않은 사용자에게는 상세 정보를 보여주고, 구매한 사용자에게는 구매 페이지로 리디렉션
        if (!hasPurchased) {
            return "redirect:/main/course"; // 구매하지 않았으면 강의 목록 페이지로 리디렉트
        } else {
            return "main/coursebuydetail"; // 구매한 경우 강의 상세보기 페이지로
        }
    }


    // 관리자 강의 상세보기
    @GetMapping("/admin/course/detail/{courseId}/{courseDetailId}")
    public String getAdminCourseDetail(@PathVariable("courseId") Long courseId,
                                       @PathVariable("courseDetailId") Long courseDetailId,
                                       @AuthenticationPrincipal UserEntity user, Model model) {

        // 현재 로그인된 사용자의 정보 가져오기
        Long userId = (user != null) ? user.getUserId() : null;
        String userRole = (user != null) ? user.getUserRole() : ""; // 사용자 역할 확인

        // courseDetailId를 사용해 강의 정보 조회
        CourseViewResponse courseViewResponse = courseService.getCourseViewByDetailId(courseDetailId);

        model.addAttribute("courseViewResponse", courseViewResponse);
        model.addAttribute("courseId", courseId);

        // 사용자가 관리자(ADMIN)인지 확인
        boolean isAdmin = "ADMIN".equals(userRole);

        // 관리자일 경우 강의 구매 페이지로 이동
        if (isAdmin) {
            // 관리자는 강의 페이지와 관계없이 강의 구매 페이지로 이동
            model.addAttribute("courseId", courseId); // courseId 추가
            return "admin/courseviewdetail"; // 관리자용 페이지
        }

        // 관리자 외의 사용자가 접근했을 때는 다른 페이지로 리다이렉트하거나 에러 페이지로 이동
        // 예시로 일반 사용자 페이지로 리다이렉트하는 경우:
        model.addAttribute("message", "관리자만 접근할 수 있습니다.");
        return "redirect:/user/course/detail/" + courseId;  // 일반 사용자용 페이지로 리다이렉트 (예시)
    }







    //취업후기 보는 곳
    @GetMapping("/main/employment-review")
    public String employmentReview(
            @RequestParam(defaultValue = "1") int page, // 기본 페이지 번호: 1
            @RequestParam(defaultValue = "12") int size, // 기본 페이지 크기: 5
            Model model) {

        // Pageable 설정: 페이지 번호가 0부터 시작하도록 변환
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);

        // 서비스에서 페이징된 데이터 가져오기
        Page<BoardTitleDTO> boardPage = boardService.getPagedBoardTitleReview(pageable);

        // 페이징 블록 설정
        int pagingBlock = 5; // 한 번에 보여줄 페이지 블록 수
        int currentPage = boardPage.getNumber() + 1; // 1-based 페이지 번호
        int totalPages = boardPage.getTotalPages();
        long totalElements = boardPage.getTotalElements();

        // 페이징 블록 시작과 끝 페이지 계산
        int blockStart = (currentPage - 1) / pagingBlock * pagingBlock + 1;
        int blockEnd = Math.min(blockStart + pagingBlock - 1, totalPages);

        // 검색 결과 여부
        boolean noResults = totalElements == 0;

        // 랜덤 이미지 목록
        List<String> imageUrls = List.of(
                "/static/images/image1.jpg",
                "/static/images/image2.jpg",
                "/static/images/image3.jpg",
                "/static/images/image4.jpg"
        );

        // 모델에 데이터 추가
        model.addAttribute("boardviewList", boardPage.getContent()); // 현재 페이지의 게시글 리스트
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pagingBlock", pagingBlock);
        model.addAttribute("blockStart", blockStart);
        model.addAttribute("blockEnd", blockEnd);
        boolean hasPreviousBlock = currentPage > pagingBlock;
        boolean hasNextBlock = blockEnd < totalPages;

        model.addAttribute("hasNext", hasNextBlock);  // 다음 페이지가 있을 경우
        model.addAttribute("hasPrevious", hasPreviousBlock);  // 이전 페이지가 있을 경우
        model.addAttribute("noResults", noResults);
        model.addAttribute("noResults", noResults);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("imageUrls", imageUrls); // 랜덤 이미지 URL 리스트

//        log.info("Board List: {}", boardPage.getContent());

        return "main/employment-review"; // Thymeleaf 템플릿 반환
    }



//    //자료실 자료 올리는 메서드임 boarddowlindtest
//    @GetMapping("/main/employment-review")
//    public String employmentReview(Model model) {
//        List<BoardviewDTO> boardviewList = boardService.boardReview();  // 리뷰 게시글 목록 가져오기
//        model.addAttribute("boardviewList", boardviewList);  // 모델에 담아 화면으로 전달
//
//        log.info("보드내용 오는지---------------------------{}",boardviewList);
//
//        return "/main/employment-review";  // Thymeleaf 템플릿
//    }

//    //취업자 후기 쓰는 곳 이넘타임 가져와서 카테고리 뿌려주는 곳
//    @GetMapping("/user/board/employreview")
//    public String showBoardForm(Model model) {
//        model.addAttribute("categories", BoardCategory.values());
//        return "/main/employreviewboard";
//    }

    //취업자 후기  작성 하는 곳
    @GetMapping("/user/board/employreview")
    public String showBoardForm() {
        return "main/employreviewboard";
    }



    @PostMapping("/user/board/employreview")
    public String postEmployreviewWrite(@AuthenticationPrincipal UserEntity user,
                                        @ModelAttribute BoardDTO boardDTO,
                                        @ModelAttribute BoardFileDTO boardFileDTO,
                                        @RequestParam("file") MultipartFile[] files) {
        // 로그인한 사용자 정보에서 userId를 가져옵니다.
        Long userId = user.getUserId();

        // BoardDTO에 userId를 설정합니다.
        boardDTO.setUserId(userId);

        // 파일 목록을 담을 리스트
        List<BoardFileDTO> fileList = new ArrayList<>();

        // 파일 정보 처리: BoardFileDTO에 파일 정보 설정
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    BoardFileDTO fileDTO = new BoardFileDTO();
                    fileDTO.setFileName(file.getOriginalFilename());
                    fileDTO.setFileUUID(UUID.randomUUID().toString()); // UUID 생성
                    fileDTO.setFileSize(file.getSize());
                    fileDTO.setFileType(file.getContentType());

                    // 파일을 리스트에 추가
                    fileList.add(fileDTO);
                }
            }
        }

//        log.info("테스트용 ============================================{}", boardDTO);

        // 서비스를 호출하여 게시글 작성
        boardService.writeEmployReview(boardDTO, fileList, files); // fileList를 BoardService에 전달

        // 게시글 작성 후 리다이렉트할 페이지 (예: 작성된 게시글 페이지로 리다이렉트)
        return "redirect:/main/employment-review";  // 예시로 게시글 리스트로 리다이렉트
    }

    @GetMapping("/logout")
    public String logoutmethod(){
        return "main/logout";
    }

    //홍보게시글
    @GetMapping("/main/KDT/{kdtSessionId}/detail/detail")
    public String getKdtDetail(@PathVariable Long kdtSessionId, Model model) {

//        KDTDetailResponse kdtDetail = kdtDetailService.getKdtDetail(kdtDetailId);
//        model.addAttribute("kdtDetail", kdtDetail);
        Optional<KDTSessionEntity> optionalKDTSession = kdtSessionRepository.findById(kdtSessionId);
        Optional<KDTDetailEntity> optionalDetail = kdtDetailRepository.findByKdtSessionEntity_KdtSessionId(kdtSessionId);

        if(optionalKDTSession.isPresent()){
            KDTSessionDTO kdtSession = kdtService.getSessionsBySessId(kdtSessionId);
            model.addAttribute("kdtSession", kdtSession);
        }
        if (optionalDetail.isPresent()) {
            KDTDetailResponse kdtDetailResponse = KDTDetailResponse.of(optionalDetail.get());
            model.addAttribute("detail", kdtDetailResponse);
        } else {

            model.addAttribute("detail", null);
        }

        model.addAttribute("kdtSessionId", kdtSessionId);
        return "main/KDT-detaildetail";
    }

}



