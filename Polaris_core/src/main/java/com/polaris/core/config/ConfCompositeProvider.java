package com.polaris.core.config;

import com.polaris.core.config.value.AutoUpdateConfigChangeListener;
import com.polaris.core.util.SpringUtil;


public class ConfCompositeProvider extends ConfHandlerProvider {

	/**
     * 单实例
     */
    public static final ConfCompositeProvider INSTANCE = new ConfCompositeProvider();
    private static final ConfEndPointProvider INSTANCE_ENDPOINT = new ConfEndPointProvider();
    private ConfCompositeProvider() {}
    
    @Override
    public void init() {
    	super.init();
    	INSTANCE_ENDPOINT.init();
    }

	public String get(Config config,String key) {
		return config.get(key);
	}
    
	@Override
	protected void put(Config config, String key, String value) {
		super.put(config, key, value);
		INSTANCE_ENDPOINT.filter(key, value);//接入点的过滤
	}
	
	@Override
	protected void listenForPut(Config config, String key, String value) {
		super.listenForPut(config, key, value);
		SpringUtil.getBean(AutoUpdateConfigChangeListener.class).onChange(config.get());//监听配置
	}
}
