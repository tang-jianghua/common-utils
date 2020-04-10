package com.jh.utils;

import org.springframework.context.ApplicationContext;

public class ApplicationContextKeeper {
	private static ApplicationContext appCtx = null;

	public static ApplicationContext getAppCtx() {
		return appCtx;
	}

	public static void init(ApplicationContext ctxVal) {
		appCtx = ctxVal;
	}

	public static Object getBean(String beanName) {
		return appCtx.getBean(beanName);
	}

}
