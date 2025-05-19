package com.example.media_upload.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String blobUrl;
    private String status;
    private LocalDateTime uploadTime;

    public MediaFile() {}

    public MediaFile(String fileName, String blobUrl, String status, LocalDateTime uploadTime) {
        this.fileName = fileName;
        this.blobUrl = blobUrl;
        this.status = status;
        this.uploadTime = uploadTime;
    }

    // Getters and setters
}
