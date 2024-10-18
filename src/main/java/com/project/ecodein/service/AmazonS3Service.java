package com.project.ecodein.service;

import com.project.ecodein.exceptions.InvalidTypeMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.utils.IoUtils;

import java.io.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Component
public class AmazonS3Service {

    private final S3Client S3_CLIENT;

    // 개발기간만 가용됨.
    private final String folder = "devlop/";

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // controller로 부터 호출 -> 검증 method 실행 -> bucket 저장
    public String getThumbnail (String path) {
//        return s3Client.getUrl(bucketName, folder+path).toString();

        GetUrlRequest getUrlRequest = GetUrlRequest.builder().bucket(bucketName).key(folder.concat(path)).build();

        return S3_CLIENT.utilities().getUrl(getUrlRequest).toString();
    }

    public String uploadThumbnail(MultipartFile file) throws IOException, InvalidTypeMismatchException {
        if (file.isEmpty()) {
            throw new FileNotFoundException("[uploadThumbnail] File is empty.");
        }

        return this.uploadImage(file);
    }

    private String uploadImage(MultipartFile file) throws IOException, InvalidTypeMismatchException {
        this.validateImageFileTypeExtension(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            return this.uploadImageToS3(file);
        } catch (IOException e) {
            throw new IOException("S3 Uploading Error", e);
        }
    }

    // 기능 메서드 : 파일 타입 체크(이미지 필터링)
    private void validateImageFileTypeExtension(String originalFilename) throws FileNotFoundException, InvalidTypeMismatchException {
        int dotIndex = originalFilename.lastIndexOf(".");
        final List<String> EXTENSION_LIST = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (dotIndex == -1) {
            throw new FileNotFoundException("File is not a valid image file.");
        }

        String extension = originalFilename.substring(dotIndex + 1).toLowerCase();

        if (!EXTENSION_LIST.contains(extension)) {
            // 타입 체크 시, 에러 처리를 위한 Exception 생성
            throw new InvalidTypeMismatchException();
        }
    }

    // bucket 서버에 파일을 업로드하는 메서드
    private String uploadImageToS3(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 파일명 관리를 위해 UUID 사용
        String uploadTargetFile =
            UUID.randomUUID().toString().substring(0, 5) + "-" + UUID.randomUUID().toString().substring(0, 10) + "-" + UUID.randomUUID().toString().substring(0, 5) + extension;

        // 파일 업로드를 위한 I/S 생성
        InputStream inputStream = file.getInputStream();

        // Metadata 생성(기존 ObjectMetadata Descraded로 Map<> 대체)
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", extension);
        metadata.put("Content-Length", String.valueOf(file.getSize()));

        // PutObjectRequest 빌드
        PutObjectRequest putRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(uploadTargetFile)
            .metadata(metadata)  // 메타데이터 추가
            .build();

        // S3에 객체 업로드
        S3_CLIENT.putObject(putRequest, RequestBody.fromInputStream(inputStream, file.getSize()));

        inputStream.close();

        // 저장된 객체 링크 반환
        return S3_CLIENT.utilities().getUrl(GetUrlRequest.builder().bucket(bucketName).key(uploadTargetFile).build()).toString();
    }






}
