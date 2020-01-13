package com.leyou.upload.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadService {

    private static final List<String> CONTENT_TYPES = Arrays.asList("image/gif","image/png","image/jpg","image/jpeg");
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class); //打印日志
    private static final String URL = "F:/ideaWork/leyouProject/image/";

    public String uploadImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        System.out.println(originalFilename);
        //判断文件类型是否是图片格式
        /*
         *以“.” 分割，获取最后一个点后面的字符串
         * String content_type = StringUtils.substringAfterLast(originalFilename,".");
         * */
        String content_type = file.getContentType();
        if (!CONTENT_TYPES.contains(content_type)) {
            LOGGER.info("文件格式不合法 {}", originalFilename); //{}占位符
            return null;
        }
        try {
            //判断文件内容是否是图片
            BufferedImage read = ImageIO.read(file.getInputStream());
            if (read == null) {
                LOGGER.info("文件内容不合法 {}", originalFilename); //{}占位符
                return null;
            }
            //是图片，保存到服务器
            file.transferTo(new File(URL + originalFilename));
            //返回保存路径，是图片回显
            return "http://image.leyou.com/" + originalFilename;
        } catch (IOException e) {
            LOGGER.info("服务器异常 {}",originalFilename);
            e.printStackTrace();
        }
        return null;
    }

}
