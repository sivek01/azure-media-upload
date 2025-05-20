package com.example.media_upload.controller;

import com.example.media_upload.model.MediaFile;
import com.example.media_upload.repository.MediaFileRepository;
import com.example.media_upload.service.AzureBlobService;
import com.example.media_upload.service.ProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private static final Logger log = LoggerFactory.getLogger(MediaController.class);

    @Autowired
    private AzureBlobService azureBlobService;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private ProcessingService processingService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        log.info("Received file for upload: {}", originalFilename);

        try {
            String blobUrl = azureBlobService.uploadFile(file);

            MediaFile mediaFile = new MediaFile(originalFilename, blobUrl, "PENDING", LocalDateTime.now());
            mediaFileRepository.save(mediaFile);

            log.info("File metadata saved to database: {}", originalFilename);

            processingService.processMedia(mediaFile);

            return ResponseEntity.ok("File uploaded successfully. Processing started.");
        } catch (Exception e) {
            log.error("Error occurred while uploading file: {}", originalFilename, e);
            return ResponseEntity.status(500).body("Upload failed due to server error.");
        }
    }

    @GetMapping("/status/{fileName}")
    public ResponseEntity<String> getStatus(@PathVariable String fileName) {
        return mediaFileRepository.findByFileName(fileName)
                .map(file -> ResponseEntity.ok("Status: " + file.getStatus()))
                .orElse(ResponseEntity.status(404).body("File not found"));
    }

    @GetMapping("/list")
    public List<String> listBlobs() {
        return azureBlobService.listFiles();
    }


}
