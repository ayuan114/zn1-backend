package com.jizy.zn1backend.controller;

import com.jizy.zn1backend.common.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final MinioService minioService;

/*    // 文件上传
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        try {
            log.info("上传文件: {}", file.getOriginalFilename());
            System.out.println("上传文件: " + file.getOriginalFilename());

            return "文件上传成功! 文件名: " + minioService.uploadFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            return "上传失败: " + e.getMessage();
        }
    }

    // 文件下载
    @GetMapping("/download/{fileName}")
    public ResponseEntity<InputStreamResource> download(@PathVariable String fileName) {
        try {
            InputStream inputStream = minioService.downloadFile(fileName);
            InputStreamResource resource = new InputStreamResource(inputStream);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }*/
}