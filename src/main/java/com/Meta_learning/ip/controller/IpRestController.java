package com.Meta_learning.ip.controller;

import com.Meta_learning.ip.service.KDTIpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class IpRestController {

    private final KDTIpService kdtIpService;

    @DeleteMapping("/api/admin/ipDelete/{id}")
    public ResponseEntity<Void> ipDelete(@PathVariable("id") Long ipId) {
        boolean isDeleted = kdtIpService.deleteId(ipId);  // IP 삭제

        if (isDeleted) {
            return ResponseEntity.ok().build();  // 삭제 성공
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();  // 삭제 실패
        }
    }
}
