package com.Meta_learning.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**", "/KDT/**", "/course/**")
                .addResourceLocations(
                        "file:tomcat/webapps/ROOT/WEB-INF/classes/static/",
                        "file:tomcat/webapps/ROOT/WEB-INF/classes/KDT/",
                        "classpath:/static/",
                        "classpath:/KDT/",
                        "classpath:/course/"
                );

//// 카페24의 public_html 경로에 업로드된 이미지를 매핑
//        registry.addResourceHandler("/static/images/course/**")
//                .addResourceLocations("classpath:/static/images/course/");



//        // /static/uploads/course/** 경로를 실제 서버 경로에 매핑
//        registry.addResourceHandler("/static/uploads/course/**")
//                .addResourceLocations("file:/home/sdcbrains/src/main/resources/static/uploads/course/");  // 실제 경로



//        // 추가 설정: /static/images/** 경로를 절대 경로와 매핑
//        registry.addResourceHandler("/KDT/course/**")
//                .addResourceLocations("file:/home/ubuntu/src/main/resources/KDT/course/");
//
//        // 추가 설정: /static/images/** 경로를 절대 경로와 매핑
//        registry.addResourceHandler("/KDT/profileimages/**")
//                .addResourceLocations("file:/home/ubuntu/src/main/resources/KDT/profileimages/");
//
//        // 추가 설정: /static/images/** 경로를 절대 경로와 매핑
//        registry.addResourceHandler("/static/images/course/**")
//                .addResourceLocations("file:/home/ubuntu/src/main/resources/static/images/course/");
//
//        // 추가 설정: /static/images/** 경로를 절대 경로와 매핑
//        registry.addResourceHandler("/static/uploads/**")
//                .addResourceLocations("file:/home/ubuntu/src/main/resources/static/uploads/");
//
    }



}
