package com.example2.demo2.controller;

import com.example2.demo2.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 夏辰义
 * 2026/8/2622:23
 */

@Slf4j
@RestController
@RequestMapping("/upload")
public class FileController {

    @Value("${upload.path:${user.dir}/uploads}")
    private String uploadPath;

    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }

        try {
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalName = file.getOriginalFilename();
            String ext = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : ".jpg";
            String newName = UUID.randomUUID() + ext;

            Path targetPath = Paths.get(uploadPath, newName);
            Files.copy(file.getInputStream(), targetPath);

            String url = "http://localhost:8080/uploads/" + newName;
            log.info("文件上传成功: {}", url);

            return Result.success(url);

        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}