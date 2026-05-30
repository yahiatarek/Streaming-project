package com.upload.upload_service.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upload.upload_service.Entities.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
