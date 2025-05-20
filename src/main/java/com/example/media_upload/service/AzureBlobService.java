package com.example.media_upload.service;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class AzureBlobService {

    private static final Logger log = LoggerFactory.getLogger(AzureBlobService.class);

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.container-name}")
    private String containerName;

    public String uploadFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        log.info("Uploading file '{}' to Azure Blob Storage...", fileName);

        try {
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();

            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            if (!containerClient.exists()) {
                log.info("Container '{}' does not exist. Creating...", containerName);
                containerClient.create();
            }

            BlobClient blobClient = containerClient.getBlobClient(fileName);
            blobClient.upload(file.getInputStream(), file.getSize(), true);

            String blobUrl = blobClient.getBlobUrl();
            log.info("File '{}' uploaded successfully. URL: {}", fileName, blobUrl);
            return blobUrl;

        } catch (IOException e) {
            log.error("Failed to upload file '{}'", fileName, e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public List<String> listFiles() {
        List<String> files = new ArrayList<>();
        BlobContainerClient containerClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient()
                .getBlobContainerClient(containerName);

        for (BlobItem blobItem : containerClient.listBlobs()) {
            files.add(blobItem.getName());
        }
        return files;
    }

}
