package com.jizy.zn1backend.common;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class MinioStorageService {

    private final MinioClient minioClient;
    private final String bucketName;

    // 图片格式到MIME类型的映射
    private static final Map<String, String> IMAGE_MIME_TYPES = new HashMap<>();

    static {
        IMAGE_MIME_TYPES.put("jpg", "image/jpeg");
        IMAGE_MIME_TYPES.put("jpeg", "image/jpeg");
        IMAGE_MIME_TYPES.put("png", "image/png");
        IMAGE_MIME_TYPES.put("gif", "image/gif");
        IMAGE_MIME_TYPES.put("bmp", "image/bmp");
        IMAGE_MIME_TYPES.put("webp", "image/webp");
        IMAGE_MIME_TYPES.put("svg", "image/svg+xml");
    }

    public MinioStorageService(
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            @Value("${minio.bucket-name}") String bucketName)
            throws MinioException, InvalidKeyException, NoSuchAlgorithmException, IOException {

        this.bucketName = bucketName;

        // 创建MinioClient实例
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        // 确保存储桶存在
        initializeBucket();
    }

    /**
     * 初始化存储桶
     */
    private void initializeBucket() throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());

            // 设置存储桶策略（可选）
            // setBucketPolicy();
        }
    }

    /**
     * 保存图片到MinIO（自动生成唯一文件名）
     *
     * @param imageData 图片字节数据
     * @return 存储的对象路径（格式：bucketName/objectPath）
     */
    public String saveToMinioStorage(byte[] imageData) {
        try {
            // 检测图片格式
            String format = detectImageFormat(imageData);

            // 生成唯一文件名
            String fileName = generateUniqueFileName(format);

            // 上传到MinIO
            return uploadToMinio(imageData, fileName, format);
        } catch (Exception e) {
            throw new StorageException("Failed to save image to MinIO", e);
        }
    }

    /**
     * 保存图片到MinIO（指定文件名）
     *
     * @param imageData 图片字节数据
     * @param fileName  文件名（可包含路径）
     * @return 存储的对象路径（格式：bucketName/objectPath）
     */
    public String saveToMinioStorage(byte[] imageData, String fileName) {
        try {
            // 从文件名中提取格式
            String format = FilenameUtils.getExtension(fileName).toLowerCase();
            if (format.isEmpty()) {
                // 如果无法从文件名获取格式，则检测图片格式
                format = detectImageFormat(imageData);
                fileName = fileName + "." + format;
            }
            checkBucket();
            // 上传到MinIO
            return uploadToMinio(imageData, fileName, format);
        } catch (Exception e) {
            throw new StorageException("Failed to save image to MinIO", e);
        }
    }

    /**
     * 保存图片到MinIO（指定文件名）
     *
     * @param imageData 图片字节数据
     * @param fileName  文件名（可包含路径）
     * @return 存储的对象路径（格式：bucketName/objectPath）
     */
    public String saveToMinioStorageFile(byte[] imageData, String fileName) {
        try {
            // 从文件名中提取格式
            String format = FilenameUtils.getExtension(fileName).toLowerCase();
            if (format.isEmpty()) {
                // 如果无法从文件名获取格式，则检测图片格式
                format = detectImageFormat(imageData);
                fileName = fileName + "." + format;
            }

            // 上传到MinIO
            return uploadToMinio(imageData, fileName, format);
        } catch (Exception e) {
            throw new StorageException("Failed to save image to MinIO", e);
        }
    }

    /**
     * 保存图片到MinIO（按文章ID组织目录）
     *
     * @param imageData 图片字节数据
     * @param articleId 文章ID
     * @return 存储的对象路径（格式：bucketName/objectPath）
     */
    public String saveToMinioStorageForArticle(byte[] imageData, Long articleId) {
        try {
            // 检测图片格式
            String format = detectImageFormat(imageData);

            // 生成存储路径
            String fileName = String.format("articles/%d/%s.%s",
                    articleId,
                    generateUniqueFileName(""),
                    format);

            // 上传到MinIO
            return uploadToMinio(imageData, fileName, format);
        } catch (Exception e) {
            throw new StorageException("Failed to save image to MinIO for article", e);
        }
    }

    /**
     * @param objectFile 对象文件
     */
    public String getFileUrl(String objectFile) {
        try {

            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectFile)
                    .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 实际执行MinIO上传操作
     *
     * @param imageData  图片字节数据
     * @param objectName 存储对象名称
     * @param format     图片格式
     * @return 存储的对象路径（格式：bucketName/objectPath）
     */
    private String uploadToMinio(byte[] imageData, String objectName, String format)
            throws MinioException, InvalidKeyException, NoSuchAlgorithmException, IOException {

        // 获取内容类型
        String contentType = getContentTypeForFormat(format);

        try (InputStream inputStream = new ByteArrayInputStream(imageData)) {
            // 执行上传
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, imageData.length, -1)
                            .contentType(contentType)
                            .build()
            );
            // 返回完整存储路径
            log.info("上传图片的minio成功，图片路径为：{}", bucketName + "/" + objectName);
            return objectName;
        } catch (Exception e) {
            log.error("上传图片的minio报错，报错原因：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 检测图片格式
     *
     * @param imageData 图片字节数据
     * @return 图片格式（如 "jpg", "png"）
     */
    private String detectImageFormat(byte[] imageData) {
        if (imageData.length < 4) {
            throw new IllegalArgumentException("Image data too short");
        }

        // 检查常见的图片文件魔数
        if (isJpeg(imageData)) {
            return "jpg";
        } else if (isPng(imageData)) {
            return "png";
        } else if (isGif(imageData)) {
            return "gif";
        } else if (isBmp(imageData)) {
            return "bmp";
        } else if (isWebp(imageData)) {
            return "webp";
        } else if (isSvg(imageData)) {
            return "svg";
        }

        throw new IllegalArgumentException("Unsupported image format");
    }

    private boolean isJpeg(byte[] data) {
        return data[0] == (byte) 0xFF && data[1] == (byte) 0xD8;
    }

    private boolean isPng(byte[] data) {
        return data[0] == (byte) 0x89 && data[1] == (byte) 0x50 &&
                data[2] == (byte) 0x4E && data[3] == (byte) 0x47;
    }

    private boolean isGif(byte[] data) {
        return data[0] == (byte) 0x47 && data[1] == (byte) 0x49 &&
                data[2] == (byte) 0x46;
    }

    private boolean isBmp(byte[] data) {
        return data[0] == (byte) 0x42 && data[1] == (byte) 0x4D;
    }

    private boolean isWebp(byte[] data) {
        return data[0] == (byte) 0x52 && data[1] == (byte) 0x49 &&
                data[2] == (byte) 0x46 && data[3] == (byte) 0x46 &&
                data[8] == (byte) 0x57 && data[9] == (byte) 0x45 &&
                data[10] == (byte) 0x42 && data[11] == (byte) 0x50;
    }

    private boolean isSvg(byte[] data) {
        // SVG是文本格式，检查开头是否是XML声明或<svg>
        String start = new String(data, 0, Math.min(data.length, 100)).trim();
        return start.startsWith("<?xml") || start.startsWith("<svg");
    }

    /**
     * 根据图片格式获取内容类型
     *
     * @param format 图片格式
     * @return MIME类型
     */
    private String getContentTypeForFormat(String format) {
        String contentType = IMAGE_MIME_TYPES.get(format.toLowerCase());
        if (contentType == null) {
            // 默认使用jpeg
            return "image/jpeg";
        }
        return contentType;
    }

    /**
     * 生成唯一文件名（不含扩展名）
     *
     * @return UUID格式的唯一标识符
     */
    private String generateUniqueFileName() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成唯一文件名（带扩展名）
     *
     * @param extension 文件扩展名
     * @return 格式为 "UUID.扩展名" 的文件名
     */
    private String generateUniqueFileName(String extension) {
        if (extension == null || extension.isEmpty()) {
            return UUID.randomUUID().toString();
        }
        return UUID.randomUUID().toString() + "." + extension;
    }

    /**
     * 自定义存储异常
     */
    public static class StorageException extends RuntimeException {
        public StorageException(String message) {
            super(message);
        }

        public StorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // 下载文件
    public InputStream downloadFile(String fileName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }

    // 检查存储桶是否存在（可选）
    public void checkBucket() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        }
    }
}