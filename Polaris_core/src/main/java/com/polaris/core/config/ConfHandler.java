package com.polaris.core.config;

public interface ConfHandler {
	default void addListener(String fileName, String group, ConfListener listener) {
	}
	default String getConfig(String fileName, String group) {
		return null;
	}
}