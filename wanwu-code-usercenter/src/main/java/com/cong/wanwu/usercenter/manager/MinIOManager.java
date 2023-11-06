package com.cong.wanwu.usercenter.manager;

import cn.dev33.satoken.stp.StpUtil;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author liuhuaicong
 * @date 2023/10/18
 */
@Component
public class MinIOManager {
    @Resource
    private MinioClient minioClient;
    /**
     * 捅名称
     */
    @Value("${minio.bucketName}")
    private String bucketName;
 
    /**
     * putObject上传文件
     *
     * @param file 文件
     * @return filePath
     */
    public String putObject(MultipartFile file) throws IOException, ServerException, InsufficientDataException, InternalException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, XmlParserException, ErrorResponseException {
        //文件名
        String originalFilename = file.getOriginalFilename();
        //文件流
        InputStream inputStream = file.getInputStream();
        //文件大小
        long size = file.getSize();
        //文件路径
        String filePath = createFilePath(originalFilename);
        //存储方法 putObject
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(filePath)
                .stream(inputStream, size, -1)
                .contentType(file.getContentType())
                .build());
        return "https://qingxin.store/img/wanwu-code-imges"+filePath;
    }
 
    /**
     * 下载文件
     *
     * @param filePath 文件路径
     */
    public void getObject(HttpServletResponse httpServletResponse, String filePath) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        String fileName = getFileName(filePath);
        InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(filePath)
                .build());
        downloadFile(httpServletResponse, inputStream, fileName);
    }
 
    /**
     * 获取文件路径
     *
     * @param originalFilename 原始文件名称
     * @return FilePath
     */
    public String createFilePath(String originalFilename) {
        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + originalFilename;
        return String.format("/%s/%s/%s",new SimpleDateFormat("yyyy/MM/dd").format(new Date()), StpUtil.getLoginId(), filename);
    }
 
    /**
     * 根据文件路径获取文件名称
     *
     * @param filePath 文件路径
     * @return 文件名
     */
    public String getFileName(String filePath) {
        String[] split = StringUtils.split(filePath, "/");
        return split[split.length - 1];
    }
 
    /**
     * 下载文件
     *
     * @param httpServletResponse httpServletResponse
     * @param inputStream         inputStream
     * @param fileName            文件名
     * @throws IOException IOException
     */
    public void downloadFile(HttpServletResponse httpServletResponse, InputStream inputStream, String fileName) throws IOException {
        //设置响应头信息，告诉前端浏览器下载文件
        httpServletResponse.setContentType("application/octet-stream;charset=UTF-8");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        //获取输出流进行写入数据
        OutputStream outputStream = httpServletResponse.getOutputStream();
        // 将输入流复制到输出流
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        // 关闭流资源
        inputStream.close();
        outputStream.close();
    }
}