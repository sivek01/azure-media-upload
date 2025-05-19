package com.example.media_upload.service;

import com.example.media_upload.model.MediaFile;
import com.example.media_upload.repository.MediaFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ProcessingService {

    private static final Logger log = LoggerFactory.getLogger(ProcessingService.class);

    private final MediaFileRepository repository;

    public ProcessingService(MediaFileRepository repository) {
        this.repository = repository;
    }

    @Async
    public void processMedia(MediaFile mediaFile) {
        String fileName = mediaFile.getFileName();
        log.info("Starting async processing for file: {}", fileName);

        try {
            // Simulate processing (e.g., resizing, metadata extraction)
            TimeUnit.SECONDS.sleep(3);

            mediaFile.setStatus("PROCESSED");
            repository.save(mediaFile);
            log.info("Processing completed for file: {}", fileName);

        } catch (InterruptedException e) {
            mediaFile.setStatus("FAILED");
            repository.save(mediaFile);
            log.error("Processing failed for file: {}", fileName, e);
        }
    }
}
