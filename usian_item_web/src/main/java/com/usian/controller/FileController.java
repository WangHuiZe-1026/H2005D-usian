package com.usian.controller;

import com.usian.utils.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("file")
public class FileController {

    @RequestMapping("upload")
    public Result upload(MultipartFile file){
        if(file!=null && file.getSize()>0){
            String filename = file.getOriginalFilename();
            filename = filename.substring(filename.lastIndexOf("."));
            filename = UUID.randomUUID()+filename;
            File imgfile = new File("D:\\img\\" + filename);
            try {
                file.transferTo(imgfile);
                return Result.ok("http://image.usian.com/"+filename);
            } catch (IOException e) {
                e.printStackTrace();
                return Result.error("上传图片失败");
            }
        }
        return Result.error("上传图片失败");
    }
}
