package com.upload.upload_service.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.upload.upload_service.DTO.VideoDto;
import com.upload.upload_service.Entities.Video;
import com.upload.upload_service.Repositories.VideoRepository;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final Cloudinary cloudinary;

    public VideoService(
            VideoRepository videoRepository,
            Cloudinary cloudinary) {
        this.videoRepository = videoRepository;
        this.cloudinary = cloudinary;
    }

    public VideoDto uploadVideo(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        String storedFileName = buildStoredFileName(file.getOriginalFilename());
        Map<String, ?> uploadResult = uploadToCloudinary(file, storedFileName);

        Video video = new Video();
        video.setTitle(file.getOriginalFilename());
        video.setDescription("Uploaded file: " + file.getOriginalFilename());
        video.setOriginalFileName(file.getOriginalFilename());
        video.setStoragePath((String) uploadResult.get("secure_url"));
        video.setContentType(file.getContentType());
        video.setSizeInBytes(file.getSize());
        video.setDurationInSeconds(toLong(uploadResult.get("duration")));
        video.setWidth(toInteger(uploadResult.get("width")));
        video.setHeight(toInteger(uploadResult.get("height")));
        video.setStatus("UPLOADED");

        Video savedVideo = videoRepository.save(video);
        return toDto(savedVideo);
    }

    private Map<String, ?> uploadToCloudinary(MultipartFile file, String storedFileName) {
        Path temporaryFile = null;
        try {
            temporaryFile = Files.createTempFile("cloudinary-upload-", "-" + storedFileName);
            file.transferTo(temporaryFile);

            return cloudinary.uploader().uploadLarge(
                    temporaryFile.toFile(),
                    ObjectUtils.asMap(
                            "resource_type", "video",
                            "folder", "my-streaming-app",
                            "public_id", removeExtension(storedFileName),
                            "overwrite", true));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to upload video to Cloudinary", exception);
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    private String buildStoredFileName(String originalFileName) {
        String safeFileName = originalFileName == null ? "video" : originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        return UUID.randomUUID() + "-" + safeFileName;
    }

    private String removeExtension(String fileName) {
        int extensionIndex = fileName.lastIndexOf('.');
        if (extensionIndex <= 0) {
            return fileName;
        }
        return fileName.substring(0, extensionIndex);
    }

    private Long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return null;
    }

    private Integer toInteger(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        return null;
    }

    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }

        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException exception) {
            // Upload succeeded or failed already; temp-file cleanup failure should not hide that result.
        }
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
