package com.polaris.core.util;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.EncryptUtil.Type;

public abstract class SystemCallUtil {

	private static Map<String, String> encryptMap = new ConcurrentHashMap<>();
	public static String value() {
	    return value(ConfClient.get());
	}
	public static String value(Properties properties) {
		String key = properties.getProperty(Constant.SYSTEM_CALL_ENCRYPT_KEY,Constant.SYSTEM_CALL_ENCRYPT_KEY_DEFAULT);
    	String startwith = properties.getProperty(Constant.SYSTEM_CALL_START_WITH,Constant.SYSTEM_CALL_START_WITH_DEFAULT);
    	String strChars = properties.getProperty(Constant.SYSTEM_CALL_ENCRYPT_VALUE,Constant.SYSTEM_CALL_ENCRYPT_VALUE_DEFAULT);
    	String systemKey = key + startwith + strChars;
    	String value = getValue(systemKey);
    	if (StringUtil.isNotEmpty(value)) {
    		return value;
    	}
		return putValue(systemKey, startwith, strChars, key);
	}
	
	public static String key() {
		return key(ConfClient.get());
	}
	public static String key(Properties properties) {
        return properties.getProperty(Constant.SYSTEM_CALL_HEADER_KEY, Constant.SYSTEM_CALL_HEADER_KEY_DEFAULT);
    }
	
	public static boolean verify(String sourceValue) {
	    return verify(ConfClient.get(),sourceValue);
	}
	public static boolean verify(Properties properties, String sourceValue) {
		if (StringUtil.isEmpty(sourceValue)) {
			return false;
		}
		String key = properties.getProperty(Constant.SYSTEM_CALL_ENCRYPT_KEY,Constant.SYSTEM_CALL_ENCRYPT_KEY_DEFAULT);
    	String startwith = properties.getProperty(Constant.SYSTEM_CALL_START_WITH,Constant.SYSTEM_CALL_START_WITH_DEFAULT);
    	String strChars = properties.getProperty(Constant.SYSTEM_CALL_ENCRYPT_VALUE,Constant.SYSTEM_CALL_ENCRYPT_VALUE_DEFAULT);
    	String systemKey = key + startwith + strChars;
    	String value = getValue(systemKey);
    	if (StringUtil.isEmpty(value)) {
    		value = putValue(systemKey, startwith, strChars, key);
    	}
		return sourceValue.equals(value);
	}
	
	private synchronized static String putValue(String systemKey, String startwith, String strChars, String encryptKey) {
		encryptMap.clear();
		String value = EncryptUtil.getEncryptValue(startwith, strChars, EncryptUtil.getInstance(encryptKey,Type.AES));
    	encryptMap.put(systemKey, value);
    	return value;
	}
	
	private static String getValue(String systemKey) {
		return encryptMap.get(systemKey);
	}
}
