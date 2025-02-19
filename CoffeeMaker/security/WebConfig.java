package edu.ncsu.csc.CoffeeMaker.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers ( final ViewControllerRegistry registry ) {
        registry.addViewController( "/customelogin" ).setViewName( "customelogin" );
    }
}
