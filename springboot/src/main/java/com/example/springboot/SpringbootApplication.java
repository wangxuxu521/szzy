package com.example.springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

@SpringBootApplication
@MapperScan("com.example.springboot.mapper")

public class SpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootApplication.class, args);
    }

    @Bean
    public CommandLineRunner initUploadDirectory() {
        return args -> {
            // 创建资源上传目录
            String uploadDir = "uploads/resources/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    System.out.println("上传目录创建成功：" + directory.getAbsolutePath());
                } else {
                    System.err.println("上传目录创建失败：" + directory.getAbsolutePath());
                }
            } else {
                System.out.println("上传目录已存在：" + directory.getAbsolutePath());
            }
        };
    }

}
