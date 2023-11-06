package com.cong.wanwu.usercenter.controller;

import cn.hutool.core.io.FileUtil;
import com.cong.wanwu.common.common.BaseResponse;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.usercenter.manager.MinIOManager;
import com.cong.wanwu.common.common.ResultUtils;
import com.cong.wanwu.usercenter.constant.FileConstant;
import com.cong.wanwu.common.exception.BusinessException;
import com.cong.wanwu.usercenter.manager.CosManager;
import com.cong.wanwu.usercenter.model.dto.file.UploadFileRequest;
import com.cong.wanwu.common.model.entity.User;
import com.cong.wanwu.usercenter.model.enums.FileUploadBizEnum;
import com.cong.wanwu.usercenter.service.UserService;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件接口
 *
 *  @author 聪
 *  
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    @Resource
    private MinIOManager minioManager;
    /**
     * 文件上传
     *
     * @param multipartFile
     * @param uploadFileRequest
 
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
            UploadFileRequest uploadFileRequest) {
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        validFile(multipartFile, fileUploadBizEnum);
        User loginUser = userService.getLoginUser();
        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            // 返回可访问地址
            return ResultUtils.success(FileConstant.COS_HOST + filepath);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 校验文件
     *
     * @param multipartFile
     * @param fileUploadBizEnum 业务类型
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 1024 * 1024L;
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
        }
    }


    @PostMapping("/minio/upload")
    public BaseResponse<String> upload(@RequestPart MultipartFile file) {
        String filePath;
        try {
            filePath = minioManager.putObject(file);
        } catch (Exception e) {
           throw new BusinessException(ErrorCode.SYSTEM_ERROR,"上传失败");
        }
        return ResultUtils.success(filePath);
    }

    @GetMapping("/minio/download")
    public void download(HttpServletResponse response, @RequestParam(value = "filepath") String filepath) throws IOException {
        try {
            minioManager.getObject(response, filepath);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("下载失败", e);
            response.reset();
        }
    }
}
