package com.upload.upload_service.Controllers;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upload.upload_service.DTO.CreateVideoRequest;
import com.upload.upload_service.DTO.UploadSignatureDto;
import com.upload.upload_service.DTO.VideoDto;
import com.upload.upload_service.Services.VideoService;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VideoDto> uploadVideo(
            @RequestBody CreateVideoRequest request) {
        VideoDto createdVideo = videoService.saveUploadedVideo(request);
        return ResponseEntity
                .created(URI.create("/videos/" + createdVideo.getId()))
                .body(createdVideo);
    }

    @GetMapping("/upload-signature")
    public UploadSignatureDto createUploadSignature() {
        return videoService.createUploadSignature();
    }
}
