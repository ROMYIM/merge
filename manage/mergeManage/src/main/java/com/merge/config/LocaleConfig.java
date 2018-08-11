package com.merge.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.merge.interceptor.LoginHandlerInterceptor;


@Configuration
@EnableAutoConfiguration
@ComponentScan
public class LocaleConfig extends WebMvcConfigurerAdapter {

    @SuppressWarnings("unused")
    private ApplicationContext applicationContext;
    
    public LocaleConfig(){
        super();
    }
    
    @Bean
    public ResourceBundleMessageSource messageSource(){
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages","category","channel","merge","agreement","language");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
   
    @Bean  
    public LocaleResolver localeResolver() {  
        SessionLocaleResolver slr = new SessionLocaleResolver();  
        //slr.setDefaultLocale(Locale.US);
        slr.setDefaultLocale(Locale.CHINA);
        return slr;  
    }  

    @Bean  
    public LocaleChangeInterceptor localeChangeInterceptor() {  
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();  
        lci.setParamName("lang");  
        return lci;  
    }
    
    
    /** 
     * 初始化RequestMappingHandlerAdapter，并加载Http的Json转换器 
     * @return  RequestMappingHandlerAdapter 对象 
     */  
    @Bean(name="requestMappingHandlerAdapter")   
    public HandlerAdapter initRequestMappingHandlerAdapter() {  
        //创建RequestMappingHandlerAdapter适配器  
        RequestMappingHandlerAdapter rmhd = new RequestMappingHandlerAdapter();  
        // HTTP JSON转换器  
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();  
        //MappingJackson2HttpMessageConverter接收JSON类型消息的转换  
        MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;  
        List<MediaType> mediaTypes = new ArrayList<MediaType>();  
        mediaTypes.add(mediaType);  
        //加入转换器的支持类型  
        jsonConverter.setSupportedMediaTypes(mediaTypes);  
        //往适配器加入json转换器  
        rmhd.getMessageConverters().add(jsonConverter);  
        return rmhd;  
    }  
  

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/").addResourceLocations("/**");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);  
    }

    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截规则：除了 /,login,code，其他都拦截判断      
        registry.addInterceptor(new LoginHandlerInterceptor())
        .addPathPatterns("/**")
        .excludePathPatterns("/")
        .excludePathPatterns("/login")
        .excludePathPatterns("/code");
        super.addInterceptors(registry);
        registry.addInterceptor(localeChangeInterceptor()); 
    }

    
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
         this.applicationContext = applicationContext;
    }
}
