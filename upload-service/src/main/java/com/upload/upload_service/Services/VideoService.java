package com.upload.upload_service.Services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Configuration;
import com.cloudinary.utils.ObjectUtils;
import com.upload.upload_service.DTO.CreateVideoRequest;
import com.upload.upload_service.DTO.UploadSignatureDto;
import com.upload.upload_service.DTO.UpdateVideoRequest;
import com.upload.upload_service.DTO.VideoDto;
import com.upload.upload_service.Entities.Video;
import com.upload.upload_service.Exceptions.VideoNotFoundException;
import com.upload.upload_service.Repositories.VideoRepository;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final Cloudinary cloudinary;
    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;

    public VideoService(
            VideoRepository videoRepository,
            Cloudinary cloudinary,
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.videoRepository = videoRepository;
        this.cloudinary = cloudinary;
        this.cloudName = cloudName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public UploadSignatureDto createUploadSignature() {
        long timestamp = System.currentTimeMillis() / 1000;
        String folder = "my-streaming-app";

        Map<String, Object> params = ObjectUtils.asMap(
                "timestamp", timestamp,
                "folder", folder);

        String signature = cloudinary.apiSignRequest(
                params,
                apiSecret,
                Configuration.DEFAULT_SIGNATURE_VERSION);

        UploadSignatureDto uploadSignatureDto = new UploadSignatureDto();
        uploadSignatureDto.setTimestamp(timestamp);
        uploadSignatureDto.setFolder(folder);
        uploadSignatureDto.setApiKey(apiKey);
        uploadSignatureDto.setCloudName(cloudName);
        uploadSignatureDto.setUploadUrl("https://api.cloudinary.com/v1_1/" + cloudName + "/video/upload");
        uploadSignatureDto.setSignature(signature);
        return uploadSignatureDto;
    }

    public VideoDto saveUploadedVideo(CreateVideoRequest request) {
        Video video = new Video();
        video.setTitle(firstNonBlank(request.getTitle(), request.getOriginalFileName()));
        video.setDescription(request.getDescription());
        video.setOriginalFileName(request.getOriginalFileName());
        video.setStoragePath(request.getStoragePath());
        video.setContentType(request.getContentType());
        video.setSizeInBytes(request.getSizeInBytes());
        video.setDurationInSeconds(request.getDurationInSeconds());
        video.setWidth(request.getWidth());
        video.setHeight(request.getHeight());
        video.setStatus("UPLOADED");

        Video savedVideo = videoRepository.save(video);
        return toDto(savedVideo);
    }

    public List<VideoDto> getVideos() {
        return videoRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public VideoDto getVideo(Long id) {
        return toDto(findVideo(id));
    }

    public VideoDto updateVideo(Long id, UpdateVideoRequest request) {
        Video video = findVideo(id);
        video.setTitle(request.getTitle());
        video.setDescription(request.getDescription());
        video.setStatus(request.getStatus());
        return toDto(videoRepository.save(video));
    }

    public void deleteVideo(Long id) {
        Video video = findVideo(id);
        videoRepository.delete(video);
    }

    private Video findVideo(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new VideoNotFoundException(id));
    }

    private String firstNonBlank(String preferred, String fallback) {
        if (preferred != null && !preferred.isBlank()) {
            return preferred;
        }
        return fallback;
    }

    private VideoDto toDto(Video video) {
        VideoDto dto = new VideoDto();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setDescription(video.getDescription());
        dto.setOriginalFileName(video.getOriginalFileName());
        dto.setStoragePath(video.getStoragePath());
        dto.setContentType(video.getContentType());
        dto.setSizeInBytes(video.getSizeInBytes());
        dto.setDurationInSeconds(video.getDurationInSeconds());
        dto.setWidth(video.getWidth());
        dto.setHeight(video.getHeight());
        dto.setStatus(video.getStatus());
        dto.setUploadedAt(video.getUploadedAt());
        return dto;
    }
}
