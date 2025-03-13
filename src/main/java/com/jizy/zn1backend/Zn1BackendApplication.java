package com.jizy.zn1backend;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.jizy.zn1backend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class Zn1BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(Zn1BackendApplication.class, args);
    }

}
