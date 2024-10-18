package com.project.ecodein.controller;

import com.project.ecodein.exceptions.InvalidTypeMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.ecodein.service.AmazonS3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/amazon")
@RestController
@Slf4j
public class AmazonS3Controller {
	
	private final AmazonS3Service AMAZONS3_SERVICE;

    public AmazonS3Controller (AmazonS3Service amazonS3Service) {
		this.AMAZONS3_SERVICE = amazonS3Service;
    }

	// 이미지 업로드 process
    @PostMapping("/uploader")
	public ResponseEntity<String> uploadImage (@RequestPart("image") MultipartFile file) throws InvalidTypeMismatchException,
        IOException {
        String image = AMAZONS3_SERVICE.uploadThumbnail(file);
        return ResponseEntity.ok (image);
	}

}
