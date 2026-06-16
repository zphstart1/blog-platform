package com.blog.service;

import com.blog.common.ErrorCode;
import com.blog.common.Ret;
import com.blog.config.CurrentUser;
import com.blog.config.UploadProperties;
import com.blog.vo.UploadVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传服务 — 图片上传到本地磁盘
 * 路径规范: /data/blog/img/{yyyy}/{MM}/{dd}/{uuid}.{ext}
 * 白名单校验: jpg/jpeg/png/gif/webp
 * 大小限制: 5MB
 */
@Slf4j
@Service
public class FileService {

    private final UploadProperties uploadProperties;

    public FileService(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    /**
     * 上传图片 — 白名单校验 + UUID 重命名 + 按日期分目录
     */
    public Ret<UploadVO> uploadImage(MultipartFile file) {
        CurrentUser currentUser = CurrentUser.get();
        if (currentUser == null) {
            return Ret.unauthorized("未登录");
        }

        // 文件为空检查
        if (file == null || file.isEmpty()) {
            return Ret.badRequest("请选择要上传的文件");
        }

        // 文件大小检查
        if (file.getSize() > uploadProperties.getMaxSize()) {
            return Ret.badRequest("文件大小不能超过5MB");
        }

        // 文件类型白名单检查
        String originalName = file.getOriginalFilename();
        String ext = getFileExtension(originalName);
        if (!isAllowedType(ext)) {
            return Ret.badRequest("不支持的文件类型，仅允许 jpg/png/gif/webp");
        }

        // 按日期分目录 + UUID重命名
        LocalDate today = LocalDate.now();
        String datePath = today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String newFileName = UUID.randomUUID().toString() + "." + ext;

        Path targetDir = Paths.get(uploadProperties.getImagePath(), datePath);
        Path targetFile = targetDir.resolve(newFileName);

        try {
            // 创建目录（如果不存在）
            Files.createDirectories(targetDir);
            // 写入文件
            file.transferTo(targetFile.toFile());
        } catch (IOException e) {
            log.error("文件写入失败: {}", e.getMessage(), e);
            return Ret.fail("文件上传失败");
        }

        // 返回相对路径 URL
        String url = "/img/" + datePath + "/" + newFileName;
        log.info("图片上传成功: userId={}, url={}, size={}", currentUser.getId(), url, file.getSize());

        UploadVO vo = new UploadVO();
        vo.setUrl(url);
        vo.setOriginalName(originalName);
        vo.setSize(file.getSize());
        return Ret.ok("上传成功", vo);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 白名单校验
     */
    private boolean isAllowedType(String ext) {
        String allowedTypes = uploadProperties.getAllowedTypes();
        return allowedTypes.contains(ext);
    }
}