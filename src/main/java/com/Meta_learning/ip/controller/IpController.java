package com.Meta_learning.ip;

import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.ip.dto.requestDTO.KDTIpCreateDTO;
import com.Meta_learning.ip.dto.responseDTO.KDTIpViewDTO;
import com.Meta_learning.ip.service.KDTIpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
public class IpController {

    private final KDTIpService kdtIpService;


    @GetMapping("/admin/iplist/{sessionId}")
    public String adminIpList(@PathVariable("sessionId") Long sessionId, Model model){

        List<KDTIpViewDTO> kdtIpViewDTOS = kdtIpService.ipviewall(sessionId);
        model.addAttribute("kdtIpViewDTOS",kdtIpViewDTOS);

        return "admin/KDT/iplist";
    }





    @GetMapping("/admin/ipCreate/{sessionId}")
    public String adminIpCreate(@PathVariable("sessionId") String sessionId, Model model) {
        // sessionId를 Model에 추가
        model.addAttribute("sessionId", sessionId);

        // ipcreate 페이지로 전달
        return "admin/KDT/ipcreate";
    }

    @PostMapping("/admin/ipCreate/{sessionId}")
    public String createIp(@PathVariable("sessionId") Long sessionId, KDTIpCreateDTO ipCreateDTO, Model model) {
        log.info("받은 IP 주소 =============={}", ipCreateDTO);
        log.info("세션 ID =============={}", sessionId);

        boolean kdtIpSave = kdtIpService.ipsave(ipCreateDTO);

        if (kdtIpSave) {
            // 성공 시 메시지 추가
            model.addAttribute("msg", "IP 등록이 완료되었습니다!");
            model.addAttribute("loc", "/view/admin/KDT/list");  // 목록으로 리디렉션
        } else {
            // 실패 시 메시지 추가
            model.addAttribute("msg", "IP 등록이 실패했습니다.");
            model.addAttribute("loc", "/admin/KDT/course/update/" + sessionId);  // 수정 페이지로 리디렉션
        }

        // 결과 메시지 페이지로 이동
        return "utility/message";
    }





}

//
//    @GetMapping("/testip")
//    public String testip(HttpServletRequest request) {
//        String clientIP = getClientIP(request);
//        request.setAttribute("clientIP", clientIP);  // IP 주소를 뷰에 전달
//
//        return "main/testip";
//    }
//
//    private String getClientIP(HttpServletRequest request) {
//        String ip = request.getHeader("X-Forwarded-For");
//        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("Proxy-Client-IP");
//        }
//        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("WL-Proxy-Client-IP");
//        }
//        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getRemoteAddr();
//        }
//        return ip;
//    }
//}

// @GetMapping("/api/testip")  // AJAX로 호출될 API
//    @ResponseBody  // JSON 형태로 반환
//    public String getClientIPApi(HttpServletRequest request) {
//        String clientIP = getClientIP(request);
//        return "{\"clientIP\":\"" + clientIP + "\"}";  // IP를 JSON 형식으로 반환
//    }
//<!DOCTYPE html>
//<html lang="en">
//<head>
//  <meta charset="UTF-8">
//  <meta name="viewport" content="width=device-width, initial-scale=1.0">
//  <title>클라이언트 IP 주소</title>
//  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script> <!-- jQuery 사용 -->
//</head>
//<body>
//  <h1>클라이언트 IP 확인</h1>
//
//  <!-- 클라이언트 IP 표시 -->
//  <p id="client-ip">Loading...</p>
//
//  <!-- AJAX를 통해 서버에서 클라이언트 IP를 가져옴 -->
//  <script>
//    $(document).ready(function() {
//      // 페이지 로드 시, AJAX를 통해 /api/testip 요청
//      $.ajax({
//        url: '/api/testip',  // 서버에서 클라이언트 IP를 받을 API
//        method: 'GET',
//        success: function(response) {
//          // 서버로부터 받은 JSON 응답에서 clientIP 값을 가져와서 HTML에 표시
//          $('#client-ip').text('클라이언트 IP 주소: ' + response.clientIP);
//        },
//        error: function(xhr, status, error) {
//          console.error('AJAX 요청 실패:', error);
//          $('#client-ip').text('IP를 가져오는 데 실패했습니다.');
//        }
//      });
//    });
//  </script>
//</body>
//</html>
