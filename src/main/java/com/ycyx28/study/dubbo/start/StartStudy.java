package com.ycyx28.study.dubbo.start;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ycyx28.study.dubbo.context.SpringContext;

public class StartStudy {
	private static Log log = LogFactory.getLog(StartStudy.class);

	private static volatile boolean running = true;

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					SpringContext.stop();
					log.info(new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date()) + " Service stopped!");
				} catch (Throwable t) {
					log.error("Service stop error:" + t);
				}
				synchronized (StartStudy.class) {
					running = false;
					StartStudy.class.notify();
				}
			}
		});

		try {
			SpringContext.start();
		} catch (RuntimeException e) {
			log.error(e.getMessage(), e);
			throw e;
		}

		log.info(new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date()) + " Service started!");

		synchronized (StartStudy.class) {
			while (running) {
				try {
					StartStudy.class.wait();
				} catch (Throwable e) {
				}
			}
		}
	}

}
