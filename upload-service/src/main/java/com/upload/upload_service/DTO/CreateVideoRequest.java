package com.upload.upload_service.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class CreateVideoRequest {

    @Size(max = 255, message = "title must not exceed 255 characters")
    private String title;

    @Size(max = 1000, message = "description must not exceed 1000 characters")
    private String description;

    @NotBlank(message = "originalFileName is required")
    private String originalFileName;

    @NotBlank(message = "storagePath is required")
    private String storagePath;

    @NotBlank(message = "contentType is required")
    private String contentType;

    @NotNull(message = "sizeInBytes is required")
    @Positive(message = "sizeInBytes must be greater than zero")
    private Long sizeInBytes;

    @PositiveOrZero(message = "durationInSeconds must not be negative")
    private Long durationInSeconds;

    @Positive(message = "width must be greater than zero")
    private Integer width;

    @Positive(message = "height must be greater than zero")
    private Integer height;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(Long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public Long getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(Long durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
}
