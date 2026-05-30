package com.upload.upload_service.Seeders;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.upload.upload_service.Entities.Video;
import com.upload.upload_service.Repositories.VideoRepository;

@Component
public class VideoDataSeeder implements CommandLineRunner {

    private final VideoRepository videoRepository;

    public VideoDataSeeder(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Override
    public void run(String... args) {
        if (videoRepository.count() > 0) {
            return;
        }

        videoRepository.saveAll(List.of(
                createVideo(
                        "Spring Boot Upload Demo",
                        "A sample video showing the upload-service flow.",
                        "spring-boot-upload-demo.mp4",
                        "/uploads/videos/spring-boot-upload-demo.mp4",
                        "video/mp4",
                        48_500_000L,
                        420L,
                        1920,
                        1080,
                        "UPLOADED"),
                createVideo(
                        "Config Server Walkthrough",
                        "A sample uploaded recording for config server setup.",
                        "config-server-walkthrough.mp4",
                        "/uploads/videos/config-server-walkthrough.mp4",
                        "video/mp4",
                        72_300_000L,
                        615L,
                        1920,
                        1080,
                        "PROCESSING"),
                createVideo(
                        "Streaming Preview Clip",
                        "A short clip used to test listing and metadata endpoints.",
                        "streaming-preview-clip.webm",
                        "/uploads/videos/streaming-preview-clip.webm",
                        "video/webm",
                        15_800_000L,
                        95L,
                        1280,
                        720,
                        "READY")));
    }

    private Video createVideo(
            String title,
            String description,
            String originalFileName,
            String storagePath,
            String contentType,
            Long sizeInBytes,
            Long durationInSeconds,
            Integer width,
            Integer height,
            String status) {
        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setOriginalFileName(originalFileName);
        video.setStoragePath(storagePath);
        video.setContentType(contentType);
        video.setSizeInBytes(sizeInBytes);
        video.setDurationInSeconds(durationInSeconds);
        video.setWidth(width);
        video.setHeight(height);
        video.setStatus(status);
        return video;
    }
}
