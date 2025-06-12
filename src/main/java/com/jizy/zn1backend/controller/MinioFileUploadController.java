package com.jizy.zn1backend.controller;

import com.jizy.zn1backend.common.ResultUtils;
import com.jizy.zn1backend.utils.MinioUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

/**
 * @Description minio文件上传控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/api")
public class MinioFileUploadController {
    @Autowired
    private MinioUtils minioUtils;

    /**
     * @param file     文件
     * @param fileName 文件名称
     * @Description 上传文件
     */
    @GetMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, String fileName) {

        minioUtils.upload(file, fileName);
        return fileName;
    }

    /**
     * @param fileName 文件名称
     * @Description dowload文件
     */
    @GetMapping("/dowload")
    public ResponseEntity dowloadFile(@RequestParam("fileName") String fileName) {
        return minioUtils.download(fileName);
    }

    /**
     * @param fileName 文件名称
     * @Description 得到文件url
     */
    @GetMapping("/getUrl")
    public String getFileUrl(@RequestParam("fileName") String fileName){
        HashMap map=new HashMap();
        map.put("FileUrl",minioUtils.getFileUrl(fileName));
        return fileName;
    }
}
