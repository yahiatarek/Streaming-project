package com.upload.upload_service.Controllers;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.upload.upload_service.DTO.VideoDto;
import com.upload.upload_service.Services.VideoService;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VideoDto> uploadVideo(
            @RequestParam("file") MultipartFile file) {
        VideoDto createdVideo = videoService.uploadVideo(file);
        return ResponseEntity
                .created(URI.create("/videos/" + createdVideo.getId()))
                .body(createdVideo);
    }
}
