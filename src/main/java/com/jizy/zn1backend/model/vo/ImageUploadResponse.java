package com.jizy.zn1backend.model.vo;

import lombok.Data;

// 图片上传响应
@Data
    public class ImageUploadResponse {
        private String imageId;
        private String url;
        private String placeholder; // {{IMAGE:img_id}}
    }