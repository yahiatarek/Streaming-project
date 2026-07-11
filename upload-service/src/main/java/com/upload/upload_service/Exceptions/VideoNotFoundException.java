package com.upload.upload_service.Exceptions;

public class VideoNotFoundException extends RuntimeException {

    public VideoNotFoundException(Long id) {
        super("Video with id " + id + " was not found");
    }
}
