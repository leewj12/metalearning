package com.Meta_learning.admin.adminrestcontroller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;

@RestController
public class VideoController {

    private static final long CHUNK_SIZE = 1024 * 1024; // 1MB

    /*@GetMapping(path = "/stream/video")
    public ResponseEntity<List<ResourceRegion>> streamVideo(
            @RequestHeader HttpHeaders headers,
            @RequestParam("videoUrl") String videoUrl) throws IOException {

        // S3 동영상 URL 로드
        UrlResource resource = new UrlResource(new URL(videoUrl));

        // HTTP Range 요청에 따른 ResourceRegion 생성
        List<ResourceRegion> resourceRegions = HttpRange.toResourceRegions(headers.getRange(), resource);

        // HTTP Response 생성
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(resourceRegions);
    }*/
    @GetMapping("/stream/video")
    public ResponseEntity<ResourceRegion> streamVideo(
            @RequestHeader HttpHeaders headers,
            @RequestParam("videoUrl") String videoUrl) throws IOException {

        // S3에서 동영상 URL 가져오기
        UrlResource video = new UrlResource(new URL(videoUrl));
        if (!video.exists() || !video.isReadable()) {
            throw new IllegalArgumentException("The video URL is invalid or not accessible: " + videoUrl);
        }

        // ResourceRegion 생성
        ResourceRegion region = getResourceRegion(video, headers);

        // HTTP 응답 생성
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region);
    }

    private ResourceRegion getResourceRegion(Resource video, HttpHeaders headers) throws IOException {
        try {
            long contentLength = video.contentLength();
            if (headers.getRange().isEmpty()) {
                long rangeStart = 0;
                long rangeEnd = Math.min(CHUNK_SIZE, contentLength) - 1;
                return new ResourceRegion(video, rangeStart, rangeEnd - rangeStart + 1);
            } else {
                HttpRange range = headers.getRange().get(0);
                long rangeStart = range.getRangeStart(contentLength);
                long rangeEnd = range.getRangeEnd(contentLength);
                long rangeLength = Math.min(CHUNK_SIZE, rangeEnd - rangeStart + 1);
                return new ResourceRegion(video, rangeStart, rangeLength);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error occurred while reading video content: " + e.getMessage(), e);
        }
    }
}
