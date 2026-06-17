package com.blog.upload.interfaces;

import com.blog.shared.Result;
import com.blog.upload.application.FileService;
import com.blog.vo.UploadVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传接口 — DDD interfaces 层（通用域）
 */
@Slf4j
@Api(tags = "文件上传接口")
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final FileService fileService;

    @ApiOperation("上传图片")
    @PostMapping("/image")
    public Result<UploadVO> uploadImage(@RequestParam("file") MultipartFile file) {
        return fileService.uploadImage(file);
    }
}
