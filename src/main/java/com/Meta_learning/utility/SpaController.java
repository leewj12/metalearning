package com.Meta_learning.utility;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SpaController {

    @GetMapping(value = {
        "/view/admin/**",
        "/view/manager/**",
        "/view/instr/**",
        "/view/student/**",
        "/view/dashboard"
    })
    @ResponseBody
    public ResponseEntity<Resource> spa() {
        Resource resource = new ClassPathResource("static/view/index.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }
}
