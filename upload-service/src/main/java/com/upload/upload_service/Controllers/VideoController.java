package com.upload.upload_service.Controllers;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upload.upload_service.DTO.CreateVideoRequest;
import com.upload.upload_service.DTO.UploadSignatureDto;
import com.upload.upload_service.DTO.UpdateVideoRequest;
import com.upload.upload_service.DTO.VideoDto;
import com.upload.upload_service.Services.VideoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/videos")
@Tag(name = "Videos", description = "Upload metadata and video lifecycle operations")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Store metadata for an uploaded video")
    @ApiResponse(responseCode = "201", description = "Video metadata created")
    @ApiResponse(responseCode = "400", description = "Request validation failed")
    public ResponseEntity<VideoDto> uploadVideo(
            @Valid @RequestBody CreateVideoRequest request) {
        VideoDto createdVideo = videoService.saveUploadedVideo(request);
        return ResponseEntity
                .created(URI.create("/videos/" + createdVideo.getId()))
                .body(createdVideo);
    }

    @GetMapping
    @Operation(summary = "List all videos")
    public List<VideoDto> getVideos() {
        return videoService.getVideos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a video by id")
    @ApiResponse(responseCode = "404", description = "Video not found")
    public VideoDto getVideo(@PathVariable Long id) {
        return videoService.getVideo(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update editable video metadata")
    @ApiResponse(responseCode = "404", description = "Video not found")
    public VideoDto updateVideo(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVideoRequest request) {
        return videoService.updateVideo(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a video")
    @ApiResponse(responseCode = "204", description = "Video deleted")
    @ApiResponse(responseCode = "404", description = "Video not found")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/upload-signature")
    @Operation(summary = "Create a signed Cloudinary upload request")
    public UploadSignatureDto createUploadSignature() {
        return videoService.createUploadSignature();
    }
}
