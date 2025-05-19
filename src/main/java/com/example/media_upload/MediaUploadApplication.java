package com.example.media_upload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MediaUploadApplication {

	public static void main(String[] args) {
		SpringApplication.run(MediaUploadApplication.class, args);
	}

}
