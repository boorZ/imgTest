package com.example.demo.controller;

import com.example.demo.controller.extend.AdPictureURL;
import com.example.demo.controller.extend.SFTPUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * @author zhoulin
 * @date 2018/11/28
 */
@RestController
public class InmInfoController {

    @ResponseBody
    @GetMapping("img/hell")
    public String hell() {
        return "hell";
    }

    @Value("${file.upload.url}")
    private String fileUploadUrl;

    @Value("${file.upload.username}")
    private String fileUploadUsername;

    @Value("${file.upload.password}")
    private String fileUploadPassword;

    @Value("${file.upload.destination}")
    private String fileUploadDestination;

    @ResponseBody
    @PostMapping("/upload")
    public AdPictureURL create(@RequestParam("file") List<MultipartFile> list) {
        // wangEditor推荐返回的数据格式
        AdPictureURL returnAd = new AdPictureURL();
        // 数组，返回若干图片的线上地址
        List<String> data;
        // 错误代码，0 表示没有错误
        int errno = 0;
        SFTPUtils sftp;
        if (list.size() == 0) {
            errno = 1;
        }
        sftp = new SFTPUtils(fileUploadUrl, fileUploadUsername, fileUploadPassword);
        data = new ArrayList<>();
        for (MultipartFile file : list) {
            // 通过SFTP连接服务器
            sftp.connect();
            String filename = file.getOriginalFilename();
            // get the suffix name
            String prefix=filename.substring(Objects.requireNonNull(filename).lastIndexOf(".")+1);
            // new name of picture
            filename = UUID.randomUUID().toString() + "." + prefix;
            try {
                // 通过SFTP来保存图片
                sftp.uploadFileByEdit(fileUploadDestination + "/", filename, file.getInputStream());
                data.add("//"+fileUploadUrl+"/"+filename);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 关闭连接
                sftp.disconnect();
            }
        }
        returnAd.setData(data);
        returnAd.setErrno(errno);
        return returnAd;
    }

}
