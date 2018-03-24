package com.ycyx28.study.dubbo.service;

import com.ycyx28.study.api.IHelloWorld;
import com.ycyx28.study.api.dto.Hello;
import com.ycyx28.study.api.response.ServiceResponse;

public class HelloDubboService implements IHelloWorld{

	@Override
	public ServiceResponse sayHello(Hello hello) {
		System.out.println(hello);
		ServiceResponse response = new ServiceResponse();
		response.setRetMsg("hello dubbo world");
		return response;
	}

}
