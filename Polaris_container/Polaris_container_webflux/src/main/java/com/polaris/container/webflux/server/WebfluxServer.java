package com.polaris.container.webflux.server;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

import com.polaris.container.config.ConfigurationHelper;
import com.polaris.container.listener.ServerListenerHelper;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.SpringUtil;
import com.polaris.core.util.StringUtil;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import reactor.netty.DisposableServer;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;

@EnableWebFlux
public class WebfluxServer {
	
	private static Logger logger = LoggerFactory.getLogger(WebfluxServer.class);
	
	private DisposableServer disposableSever;
	
	private int port = 80;
	
	/**
     * 私有构造方法
     */
    private WebfluxServer() {
    }
    
    /**
     * 获取单实例公共静态方法
     *
     * @return 单实例
     */
    public static WebfluxServer getInstance() {
        return Singletone.INSTANCE;
    }

    /**
     * 静态内部类实现单例
     */
    private static class Singletone {
        /**
         * 单实例
         */
        private static final WebfluxServer INSTANCE = new WebfluxServer();
    }
    
    /**
     * 启动服务器
     *
     * @throws Exception
     */
    public void start() {

    	//创建context
    	SpringUtil.refresh(ConfigurationHelper.getConfiguration(WebfluxServer.class));
    	
    	//通过ApplicationContext创建HttpHandler
        HttpHandler httpHandler = WebHttpHandlerBuilder.applicationContext(SpringUtil.getApplicationContext()).build();
        ReactorHttpHandlerAdapter httpHandlerAdapter = new ReactorHttpHandlerAdapter(httpHandler);
        port = Integer.parseInt(ConfClient.get(Constant.SERVER_PORT_NAME, Constant.SERVER_PORT_DEFAULT_VALUE));
        HttpServer server =
                HttpServer.create()
                		  .port(port)
                		  .forwarded(Boolean.parseBoolean(ConfClient.get("server.forwarded","true")))
                		  .compress(Boolean.parseBoolean(ConfClient.get("server.compress","true")))//压缩
                          .handle(httpHandlerAdapter);
        
        
        //ssl set
        server = secure(server);
        if (server == null) {
        	return;
        }
        
        //protocals set
        server.protocol(listProtocols());
        
        //bind server
        disposableSever = server.bindNow();
        ServerListenerHelper.started();

        //log
        logger.info("netty-webflux started on port(s) " + port + " with context path '/'");
        
        // add shutdown hook to stop server
        Runtime.getRuntime().addShutdownHook(jvmShutdownHook);

        //block
        disposableSever.onDispose().block();
    }
    
    /**
     * 停止服务服务器
     *
     * @throws Exception
     */
    public void stop() {
    	try {
        	ServerListenerHelper.stopping();
        } catch (Exception e) {
        	// ignore -- IllegalStateException means the VM is already shutting down
        }
    	
    	// remove the shutdown hook that was added when the UndertowServer was started, since it has now been stopped
        try {
            Runtime.getRuntime().removeShutdownHook(jvmShutdownHook);
        } catch (IllegalStateException e) {
            // ignore -- IllegalStateException means the VM is already shutting down
        }
    	try {
        	disposableSever.disposeNow();
        	ServerListenerHelper.stopped();
        } catch (Exception e) {
        	// ignore -- IllegalStateException means the VM is already shutting down
        }

        //log out
        logger.info("Webflux stopped on port(s) " + this.port + " with context path '/'");
    }
    
    /**
     * JVM shutdown hook to shutdown this server. Declared as a class-level variable to allow removing the shutdown hook when the
     * server is stopped normally.
     */
    private final Thread jvmShutdownHook = new Thread(new Runnable() {
        @Override
        public void run() {
            stop();
        }
    }, "Webflux-JVM-shutdown-hook");
    
    private HttpProtocol[] listProtocols() {
        boolean ssl = Boolean.parseBoolean(ConfClient.get("server.ssl.enable","false"));
        boolean http2 = Boolean.parseBoolean(ConfClient.get("server.http2.enable","false"));
		if (http2) {
			if (ssl) {
				return new HttpProtocol[] { HttpProtocol.H2, HttpProtocol.HTTP11 };
			}
			else {
				return new HttpProtocol[] { HttpProtocol.H2C, HttpProtocol.HTTP11 };
			}
		}
		return new HttpProtocol[] { HttpProtocol.HTTP11 };
	}
    
    private HttpServer secure(HttpServer server) {
        boolean ssl = Boolean.parseBoolean(ConfClient.get("server.ssl.enable","false"));
    	if (!ssl) {
    		return server;
    	}
    	try {
    		String certificate = ConfClient.get("server.certificate.file");
    		boolean isCertificate = false;
            File certificateFile = null;
            if (StringUtil.isNotEmpty(certificate)) {
            	certificateFile = new File(certificate);
            	if (certificateFile.isFile()) {
            		isCertificate = true;
            	}
            }
    		String privateKey = ConfClient.get("server.privateKey.file");
    		boolean isPrivateKey = false;
            File privateKeyFile = null;
            if (StringUtil.isNotEmpty(privateKey)) {
            	privateKeyFile = new File(privateKey);
            	if (privateKeyFile.isFile()) {
            		isPrivateKey = true;
            	}
            }
            if (!isCertificate || !isPrivateKey) {
            	SelfSignedCertificate cert = new SelfSignedCertificate();
            	certificateFile = cert.certificate();
            	privateKeyFile = cert.privateKey();
            }
    	 	SslContextBuilder sslContextBuilder =
    	 	SslContextBuilder.forServer(certificateFile, privateKeyFile);
        	server.secure(sslContextSpec -> sslContextSpec.sslContext(sslContextBuilder));
    	} catch (Exception ex) {
    		ServerListenerHelper.failure();
    		logger.info("netty-webflux start error : {}",ex);
    		return null;
    	}
    	return server;
    }

}
