//package com.polaris.demo.configurer;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.multipart.MultipartResolver;
//import org.springframework.web.multipart.commons.CommonsMultipartResolver;
//import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//
//@Configuration
//@EnableWebMvc 
//public class WebConfig extends WebMvcConfigurerAdapter {
//	
//	@Value("${uploadFilePath}")
//	private String uploadFilePath;
//	
//	@Value("${uploadImgPath}")
//	private String uploadImgPath;
//	
//	@Bean(name = "multipartResolver")
//    public MultipartResolver multipartResolver() {
//        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
//        multipartResolver.setDefaultEncoding("UTF-8");
//        multipartResolver.setMaxUploadSize(1024*1024*1024);//1G
//        multipartResolver.setMaxInMemorySize(40960);
//        return multipartResolver;
//    }
//
//	/**
//       * 
//       * Description：FreeMarker视图配置
//       * @return 
//       */
//	@Bean
//    public FreeMarkerConfigurer freeMarkerConfigurer() {
//		FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
//		freeMarkerConfigurer.setTemplateLoaderPath("classpath:template/");
//		freeMarkerConfigurer.setDefaultEncoding("UTF-8");
//	    return freeMarkerConfigurer;
//   }
//    @Bean
//    public FreeMarkerViewResolver freeMarkerViewResolver() {
//	    FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
//	    viewResolver.setPrefix("/template/");
//	    viewResolver.setSuffix(".ftl");
//	    viewResolver.setCache(true);
//	    viewResolver.setContentType("text/html;charset=UTF-8");
//	    viewResolver.setRequestContextAttribute("requestContext");
//	    viewResolver.setOrder(0);
//	    return viewResolver;
//   }
//	
//	@Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//	    
//		//支持静态资源访问
//		registry.addResourceHandler("/**")
//        	.addResourceLocations("classpath:/resources/")
//        	.addResourceLocations("classpath:/static/")
//        	.addResourceLocations("classpath:/public/");
//		
//		//静态文件映射
//        registry.addResourceHandler("/resource/img/**")
//        	.addResourceLocations("file:"+uploadImgPath+File.separator);
//        registry.addResourceHandler("/resource/file/**")
//        	.addResourceLocations("file:"+uploadFilePath+File.separator);
//        super.addResourceHandlers(registry);
//    }
//
//	 @Override
//	 public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
//		 configurer.enable();
//	 }
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//    	
//    	//string utf-8
//        converters.add(new StringHttpMessageConverter(Charset.defaultCharset()));
//        
//        //pojo utf-8
//    	List<MediaType> list = new ArrayList<MediaType>();
//    	list.add(MediaType.APPLICATION_JSON_UTF8);
//    	MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
//    	mappingJackson2HttpMessageConverter.setSupportedMediaTypes(list);
//        converters.add(mappingJackson2HttpMessageConverter);
//    }
////
//}
