package com.Meta_learning.course.coursecontroller;

import com.Meta_learning.course.courseentity.CourseStatus;
import com.Meta_learning.course.courseservice.CourseService;
import com.Meta_learning.user.userentity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class CourseRestController {

    private final CourseService courseService;

    @GetMapping("/api/instr/course/uploadcount")
    public ResponseEntity<Map<String, Object>> getCourseCount(@AuthenticationPrincipal UserEntity user) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long courseCount = courseService.getCourseCount(user);
            response.put("status", "success");
            response.put("data", Map.of("courseCount", courseCount));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {

            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @GetMapping("/api/admin/course/monthlycount")
    public ResponseEntity<Map<String, Object>> getMonthlyUploadCourseCount() {
        try {
            // 서비스에서 데이터를 가져옴
            List<Map<String, Object>> monthlyCourseCounts = courseService.getMonthlyUploadCourseCount();

            // JSON 구조 구성
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", monthlyCourseCounts);

            return ResponseEntity.ok(response); // 성공 응답
        } catch (Exception e) {

            // 오류 응답
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "월별 강의 업로드 수를 가져오는 중 오류가 발생했습니다.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @PostMapping("/api/admin/course/update/{status}")
    public ResponseEntity<String> updateCourseStatus(@PathVariable("status") String status, @RequestBody Map<String, Long> request) {
        Long courseId = request.get("courseId");
        if (courseId == null || courseId <= 0) {
            return ResponseEntity.badRequest().body("유효하지 않은 courseId입니다.");
        }

        try {
            CourseStatus courseStatus = CourseStatus.valueOf(status.toUpperCase());
            courseService.updateCourseStatus(courseId, courseStatus);
            return ResponseEntity.ok("상태가 성공적으로 업데이트되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("잘못된 상태 값입니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("상태 업데이트 중 오류가 발생했습니다.");
        }
    }

//    @GetMapping("/api/manager/course/uploadcount")
//    public ResponseEntity<Map<String, Object>> getManagerCourseCount(@AuthenticationPrincipal UserEntity user) {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            Long courseCount = courseService.getCourseCount(user);
//            response.put("status", "success");
//            response.put("data", Map.of("courseCount", courseCount));
//            return ResponseEntity.ok(response);
//        } catch (IllegalArgumentException e) {
//
//            response.put("status", "error");
//            response.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }

    @DeleteMapping("/api/admin/course/delete/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long courseId) {
        try {
            courseService.deleteCourseById(courseId);
            return ResponseEntity.ok().body("강의가 성공적으로 삭제되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 주문 내역이 있으면 403 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 강의를 찾을 수 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("강의 삭제 중 오류 발생");
        }
    }

    @DeleteMapping("api/admin/course/delete/courseVideo/{courseVideoId}")
    public ResponseEntity<?> deleteCourseVideo(@PathVariable Long courseVideoId){
        try {
            courseService.deleteCourseVideoById(courseVideoId);
            return ResponseEntity.ok().body("영상이 성공적으로 삭제되었습니다.");
//        } catch (IllegalStateException e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 주문 내역이 있으면 403 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 영상을 찾을 수 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("영상 삭제 중 오류 발생");
        }

    }
}
