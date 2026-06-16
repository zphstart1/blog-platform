package com.blog.controller;

import com.blog.common.Ret;
import com.blog.service.FileService;
import com.blog.vo.UploadVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传接口 — 图片上传（管理端，需要鉴权）
 */
@Slf4j
@Api(tags = "文件上传接口")
@RestController
@RequestMapping("/upload")
public class UploadController {

    private final FileService fileService;

    public UploadController(FileService fileService) {
        this.fileService = fileService;
    }

    @ApiOperation("上传图片")
    @PostMapping("/image")
    public Ret<UploadVO> uploadImage(@RequestParam("file") MultipartFile file) {
        return fileService.uploadImage(file);
    }
}