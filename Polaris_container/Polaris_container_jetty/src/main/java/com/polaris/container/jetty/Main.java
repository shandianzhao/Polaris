package com.polaris.container.jetty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import com.polaris.container.Server;
import com.polaris.container.ServerOrder;
import com.polaris.container.jetty.server.JettyServer;

/**
 * 入口启动类
 */
@Order(ServerOrder.JETTY)
public class Main implements Server{

	private static Logger logger = LoggerFactory.getLogger(Main.class);
    /**
     * 服务启动
     *
     */
	@Override
	public void start() {
		//启动jetty
    	logger.info("jetty启动！");
        JettyServer server = JettyServer.getInstance();
        server.start();
	}
	
	
	/**
     * servlet上下文
     *
     */
	@Override
	public Object getContext() {
		JettyServer server = JettyServer.getInstance();
		return server.getServletContex();
    }
}