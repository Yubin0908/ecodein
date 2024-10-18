package com.project.ecodein.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AmazonS3ServiceTest {

    private static final Logger log = LoggerFactory.getLogger(AmazonS3ServiceTest.class);

    @Mock
    private AmazonS3Service amazonS3Service;

    @DisplayName("S3 BUCKET 데이터 로드 테스트")
    @Test
    public void findImage() {
        String fileName = "logo.png";
        String url = "https://eco-de-in-bucket.s3.amazonaws.com/devlop/" + fileName;
        when(amazonS3Service.getThumbnail(fileName)).thenReturn(url);

        String imageUrl = amazonS3Service.getThumbnail(fileName);

        log.info("Retrieved image URL: {}", imageUrl);

        Assertions.assertNotNull(imageUrl);
        Assertions.assertEquals(url, imageUrl);

    }

}
