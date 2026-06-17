package com.blog.upload.application;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.blog.shared.Result;
import com.blog.vo.UploadVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件上传服务
 */
@Slf4j
@Service
public class FileService {

    @Value("${upload.image-path:/data/blog/img}")
    private String imagePath;

    @Value("${upload.allowed-types:jpg,jpeg,png,gif,webp}")
    private String allowedTypes;

    @Value("${upload.max-size:5242880}")
    private long maxSize;

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(
            Arrays.asList("jpg", "jpeg", "png", "gif", "webp")
    );

    public Result<UploadVO> uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail(400, "文件不能为空");
        }

        // 类型校验
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            return Result.fail(400, "文件名不能为空");
        }
        String ext = FileUtil.extName(originalName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            return Result.fail(400, "不允许的文件类型: " + ext);
        }

        // 大小校验
        if (file.getSize() > maxSize) {
            return Result.fail(400, "文件大小超过限制: " + (maxSize / 1024 / 1024) + "MB");
        }

        try {
            // 按日期分目录
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            File dir = new File(imagePath, dateDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名
            String newName = IdUtil.fastSimpleUUID() + "." + ext;
            File dest = new File(dir, newName);
            file.transferTo(dest);

            UploadVO vo = new UploadVO();
            vo.setUrl("/img/" + dateDir + "/" + newName);
            vo.setOriginalName(originalName);
            vo.setSize(file.getSize());

            log.info("图片上传成功: {} -> {}", originalName, vo.getUrl());
            return Result.ok(vo);
        } catch (IOException e) {
            log.error("图片上传失败", e);
            return Result.fail(500, "文件上传失败: " + e.getMessage());
        }
    }
}
