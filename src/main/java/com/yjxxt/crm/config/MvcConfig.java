package com.yjxxt.crm.config;

import com.yjxxt.crm.interceptors.NoLoginInterceptors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器生效的配置类
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Bean
    public NoLoginInterceptors noLoginInterceptors(){
        return new NoLoginInterceptors();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 需要一个实现HandlerInterceptor接口的拦截器实例，这里使用的是NoLoginInterceptor
        registry.addInterceptor(noLoginInterceptors())
                //拦截器拦截的路径
                .addPathPatterns("/**")
                //不需要拦截的过滤规则
                .excludePathPatterns("/index","/user/login","/css/**","/images/**","/js/**","/lib/**");
    }
}
