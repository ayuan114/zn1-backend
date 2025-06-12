package com.jizy.zn1backend.common;

import com.jizy.zn1backend.config.MinioConfig;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    // 上传文件
    public String uploadFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        String contentType = file.getContentType();

        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(minioConfig.getBucketName())
                .object(fileName)
                .stream(inputStream, file.getSize(), -1)
                .contentType(contentType)
                .build()
        );
        return fileName;
    }

    // 下载文件
    public InputStream downloadFile(String fileName) throws Exception {
        return minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(minioConfig.getBucketName())
                .object(fileName)
                .build()
        );
    }

    // 检查存储桶是否存在（可选）
    public void checkBucket() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioConfig.getBucketName())
                .build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .build());
        }
    }
}