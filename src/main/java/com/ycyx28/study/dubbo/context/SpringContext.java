package com.ycyx28.study.dubbo.context;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContext {
	
	private volatile static AbstractApplicationContext applicationContext;
	private final static String XML_EXPRESSION = "classpath*:applicationContext*.xml";
    
    public synchronized static void initConfig(String regularExpression) {
		if (applicationContext == null) {
    		if(StringUtils.isEmpty(regularExpression)) {
				applicationContext = new ClassPathXmlApplicationContext(XML_EXPRESSION);
        	} else {
				applicationContext = new ClassPathXmlApplicationContext(regularExpression.split("[,\\s]+"));
        	}  
    	}    	  	
    }    
    
	public static void setAppContext(final ApplicationContext context) {
		applicationContext = (AbstractApplicationContext) context;
	}
    
    public static AbstractApplicationContext getAppContext() {
		if (applicationContext == null) {
			applicationContext = (AbstractApplicationContext) SpringContextUtil.getApplicationContext();
			if (null == applicationContext) {
				initConfig(null);
			}
    	}
		return applicationContext;
    }

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		if (applicationContext == null) {
    		initConfig(null);
    	}
		return (T) applicationContext.getBean(name);
    }
    
    public static void start() {
		if (applicationContext == null) {
    		initConfig(null);
    	}
    	
		applicationContext.start();
    }
    
    public synchronized static void stop() {
		if (applicationContext != null) {
			applicationContext.stop();
			applicationContext.close();
			applicationContext = null;
        }        
    }
       
   
}
