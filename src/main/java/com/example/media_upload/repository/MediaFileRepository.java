package com.example.media_upload.repository;


import com.example.media_upload.model.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {
    Optional<MediaFile> findByFileName(String fileName);

}

