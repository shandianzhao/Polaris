package com.polaris.container.springboot.server;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.polaris.container.servlet.filter.RequestFirstFilter;

@Configuration
public class SpringbootConfiguration { 

	@Bean
    public FilterRegistrationBean<Filter> registerFilter() {
        FilterRegistrationBean<Filter> filter = new FilterRegistrationBean<>();
        filter.setFilter(new RequestFirstFilter());
        filter.addUrlPatterns("/*");
        return filter;
    }
}