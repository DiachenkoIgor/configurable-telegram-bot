package com.incuube.agent.config;

import com.incuube.agent.controllers.interceptors.PhoneValidationInterceptor;
import com.incuube.agent.controllers.interceptors.SizeValidationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private PhoneValidationInterceptor phoneValidationInterceptor;
    private SizeValidationInterceptor sizeValidationInterceptor;

    @Autowired
    public WebConfig(PhoneValidationInterceptor phoneValidationInterceptor, SizeValidationInterceptor sizeValidationInterceptor) {
        this.phoneValidationInterceptor = phoneValidationInterceptor;
        this.sizeValidationInterceptor = sizeValidationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(phoneValidationInterceptor).addPathPatterns("/rcs/**");
        registry.addInterceptor(sizeValidationInterceptor).addPathPatterns("/rcs/**");

    }
}
