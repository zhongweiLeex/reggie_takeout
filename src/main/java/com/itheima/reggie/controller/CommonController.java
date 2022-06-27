package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * description 处理文件上传下载
 *
 * @author Administrator
 * @date 2022/6/21-8:56
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /***
     * description: 文件上传
     * @param file 此处必须声明为 MultipartFile类的 file 名称必须和前端的From_Data中的参数名称保持一致
     * @return com.itheima.reggie.common.R<java.lang.String> 文件名称
     * @throws
     * @author zhongweileex
     * @date: 2022/6/21 - 9:02
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){//此处必须声明为 MultipartFile类的 file 名称必须和前端的From_Data中的参数名称保持一致
        //file是个临时文件，需要转存到指定位置，否则本次请求结束后， 将会删除
        log.info(file.toString());

        String originalFilename = file.getOriginalFilename();
        //文件名称后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID生成文件名称，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在 , 如果不存在则创建此文件夹
        if(!dir.exists()){
            dir.mkdirs();
        }

        try{
            //将临时文件转存到指定地址
            file.transferTo(new File(basePath + "/" + fileName));
        }catch (IOException e){
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /***
     * description: 文件下载 <br> 向浏览器中输出 服务器中的 fileName的文件
     * @param name 下载文件的名称
     * @param response 响应浏览器
     * @return void
     * @throws
     * @author zhongweileex
     * @date: 2022/6/21 - 9:54
     */

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){//此处的name 参数 必须和前端路径中的的name一样
        try(//输入流 ， 通过输入流读取文件内容 将磁盘中的文件读取到 内存中
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + "/" + name));
            //输出流， 通过输出流向浏览器中写出 文件内容， 在浏览器中展示图片
            ServletOutputStream outputStream = response.getOutputStream()) {

            response.setContentType("image/jpeg");//设置响应格式

            int len = 0;
            byte[] bytes = new byte[1024];
            while((len = fileInputStream.read(bytes))!=-1){//==-1 表示读完了， 如果！=-1 表示 没有读完  向byte数组中输出值
                //向outputStream中输出图片字节数据，outputStream 在 response中 ， 所以就是向 给 浏览器的响应中输出数据
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
