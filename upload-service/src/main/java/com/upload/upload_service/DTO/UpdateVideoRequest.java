package com.upload.upload_service.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateVideoRequest {

    @NotBlank(message = "title is required")
    @Size(max = 255, message = "title must not exceed 255 characters")
    private String title;

    @Size(max = 1000, message = "description must not exceed 1000 characters")
    private String description;

    @NotBlank(message = "status is required")
    @Pattern(
            regexp = "UPLOADED|PROCESSING|READY|FAILED",
            message = "status must be one of UPLOADED, PROCESSING, READY or FAILED")
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
