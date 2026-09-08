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

    @Value("${file.base-url:http://localhost:8080}")
    private String baseUrl;

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

            //目标路径
            Path targetPath = Paths.get(uploadPath, newName);
            //路径安全效验
            //将基础目录转化为标准化绝对路径
            Path baseDir = Paths.get(uploadPath).toAbsolutePath().normalize();
            //将最终文件转化为标准化绝对路径
            Path fullPath = targetPath.toAbsolutePath().normalize();
            //判断目标路径是否以基础路径为前缀
            if(!fullPath.startsWith(baseDir)){
                log.error("检测到非法路径穿越攻击！目标路径：{}",fullPath);
                return Result.error("上传失败：非法的文件存储路径");
            }

            Files.copy(file.getInputStream(), targetPath);


            String url = baseUrl + "/uploads/" + newName;
            log.info("文件上传成功: {}", url);

            return Result.success(url);

        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}