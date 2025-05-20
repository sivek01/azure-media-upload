package com.example.media_upload.service;

import com.azure.storage.blob.*;
import com.example.media_upload.model.MediaFile;
import com.example.media_upload.repository.MediaFileRepository;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ProcessingService {

    private static final Logger log = LoggerFactory.getLogger(ProcessingService.class);

    private final MediaFileRepository repository;

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.container-name}")
    private String containerName;

    public ProcessingService(MediaFileRepository repository) {
        this.repository = repository;
    }

    @Async
    public void processMedia(MediaFile mediaFile) {
        String fileName = mediaFile.getFileName();
        String resizedFileName = "resized-" + fileName;

        log.info("Processing image: {}", fileName);

        // Define temp file paths
        String tempDir = System.getProperty("java.io.tmpdir");
        String inputPath = tempDir + File.separator + fileName;
        String outputPath = tempDir + File.separator + resizedFileName;

        try {
            // 1. Connect to Azure Blob
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();

            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            BlobClient originalBlob = containerClient.getBlobClient(fileName);

            // 2. Download the original file
            log.info("Downloading original file from blob: {}", fileName);
            originalBlob.downloadToFile(inputPath, true);

            // 3. Resize image using Thumbnailator
            log.info("Resizing image to 300x300");
            Thumbnails.of(new File(inputPath))
                    .size(300, 300)
                    .toFile(new File(outputPath));

            // 4. Upload resized image
            BlobClient resizedBlob = containerClient.getBlobClient(resizedFileName);
            resizedBlob.uploadFromFile(outputPath, true);
            log.info("Uploaded resized image as: {}", resizedFileName);

            // 5. Update database status
            mediaFile.setStatus("PROCESSED");
            repository.save(mediaFile);
            log.info("Processing completed for: {}", fileName);

        } catch (Exception e) {
            mediaFile.setStatus("FAILED");
            repository.save(mediaFile);
            log.error("Image processing failed for: {}", fileName, e);
        } finally {
            // 6. Cleanup
            new File(inputPath).delete();
            new File(outputPath).delete();
        }
    }
}
