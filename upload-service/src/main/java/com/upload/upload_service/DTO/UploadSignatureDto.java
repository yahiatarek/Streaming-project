package com.upload.upload_service.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadSignatureDto {

    private Long timestamp;
    private String folder;

    @JsonProperty("api_key")
    private String apiKey;

    @JsonProperty("cloud_name")
    private String cloudName;

    @JsonProperty("upload_url")
    private String uploadUrl;

    private String signature;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getCloudName() {
        return cloudName;
    }

    public void setCloudName(String cloudName) {
        this.cloudName = cloudName;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
