package com.jizy.zn1backend.common;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jizy.zn1backend.config.MinioConfig;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinioService {
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    // 上传文件
    public String uploadFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        String contentType = file.getContentType();

        ObjectWriteResponse objectWriteResponse = minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .object(fileName)
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(contentType)
                        .build()
        );
        String fileUrl = this.getFileUrl(fileName);
        log.info("上传文件返回地址: {}", fileUrl);
        log.info("上传文件返回地址objectWriteResponse: {}", JSONUtil.toJsonStr(objectWriteResponse));
        return minioConfig.getEndpoint()  + "/" +minioConfig.getBucketName() + "/" + fileName;
    }

    public String getFileUrl(String objectFile) {
        try {

            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(minioConfig.getBucketName())
                    .object(objectFile)
                    .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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