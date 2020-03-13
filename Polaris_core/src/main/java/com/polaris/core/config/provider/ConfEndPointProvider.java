package com.polaris.core.config.provider;

import java.util.ServiceLoader;

import com.polaris.core.config.ConfEndPoint;
import com.polaris.core.config.Config.Opt;

public class ConfEndPointProvider {
    protected final ServiceLoader<ConfEndPoint> endPointLoader = ServiceLoader.load(ConfEndPoint.class);
    private ConfEndPointProvider() {}
    public static ConfEndPointProvider INSTANCE = new ConfEndPointProvider();
    public void init(ConfCompositeProvider composite) {
    	for (ConfEndPoint confEndPoint : endPointLoader) {
	    	confEndPoint.init();
        }
    }
    public void onChange (String key, String value, Opt opt) {
    	for (ConfEndPoint confEndPoint : endPointLoader) {
	    	confEndPoint.onChange(key, value, opt);
        }
    }
    public void onComplete() {
    	for (ConfEndPoint confEndPoint : endPointLoader) {
	    	confEndPoint.onComplete();
        }
    }
}
