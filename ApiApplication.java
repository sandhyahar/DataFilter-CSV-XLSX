package com.API;

import javax.servlet.ServletContext;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ServletContextAware;

@SpringBootApplication
@RestController
public class ApiApplication extends SpringBootServletInitializer implements ServletContextAware {
	private ServletContext scontext;

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ApiApplication.class);

	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		// TODO Auto-generated method stub
		this.setScontext(servletContext);

	}

	public ServletContext getScontext() {
		return scontext;
	}

	public void setScontext(ServletContext scontext) {
		this.scontext = scontext;
	}

}
