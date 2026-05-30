package com.upload.upload_service.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.upload.upload_service.DTO.VideoDto;
import com.upload.upload_service.Entities.Video;
import com.upload.upload_service.Repositories.VideoRepository;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final Path videoStorageDirectory;

    public VideoService(
            VideoRepository videoRepository,
            @Value("${upload.storage.video-directory:uploads/videos}") String videoStorageDirectory) {
        this.videoRepository = videoRepository;
        this.videoStorageDirectory = Paths.get(videoStorageDirectory).toAbsolutePath().normalize();
    }

    public VideoDto uploadVideo(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        String storedFileName = buildStoredFileName(file.getOriginalFilename());
        Path storedFilePath = saveFile(file, storedFileName);

        Video video = new Video();
        video.setTitle(file.getOriginalFilename());
        video.setDescription("Uploaded file: " + file.getOriginalFilename());
        video.setOriginalFileName(file.getOriginalFilename());
        video.setStoragePath(storedFilePath.toString());
        video.setContentType(file.getContentType());
        video.setSizeInBytes(file.getSize());
        video.setStatus("UPLOADED");

        Video savedVideo = videoRepository.save(video);
        return toDto(savedVideo);
    }

    private Path saveFile(MultipartFile file, String storedFileName) {
        try {
            Files.createDirectories(videoStorageDirectory);
            Path targetPath = videoStorageDirectory.resolve(storedFileName).normalize();
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return targetPath;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to store uploaded video", exception);
        }
    }

    private String buildStoredFileName(String originalFileName) {
        String safeFileName = originalFileName == null ? "video" : originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        return UUID.randomUUID() + "-" + safeFileName;
    }

    private VideoDto toDto(Video video) {
        VideoDto dto = new VideoDto();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setDescription(video.getDescription());
        dto.setOriginalFileName(video.getOriginalFileName());
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
