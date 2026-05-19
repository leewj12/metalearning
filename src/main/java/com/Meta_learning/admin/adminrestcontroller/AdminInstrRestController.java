package com.Meta_learning.admin.adminrestcontroller;

import com.Meta_learning.admin.dto.response.InstrCreateResponse;
import com.Meta_learning.course.courseservice.InstrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AdminInstrRestController {

    private final InstrService instrService;

    @GetMapping("/api/admin/instr")
    public ResponseEntity<List<InstrCreateResponse>> getInstrRequests() {
        List<InstrCreateResponse> instrRequests = instrService.getAllInstrRequests();
        return ResponseEntity.ok(instrRequests);
    }

    @PostMapping("/api/admin/instr/approve")
    public ResponseEntity<String> approveInstr(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        instrService.approveInstr(email);
        return ResponseEntity.ok("강사 수락 완료");
    }

    @PostMapping("/api/admin/instr/reject")
    public ResponseEntity<String> rejectInstr(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        instrService.rejectInstr(email);
        return ResponseEntity.ok("강사 거절 완료");
    }


}
