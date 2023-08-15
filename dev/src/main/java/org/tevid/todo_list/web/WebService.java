package org.tevid.todo_list.web;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.tevid.todo_list.log.LoggingService;
import org.tevid.todo_list.utils.ConfigService;

public class WebService {

	private static WebService instance = null;	
	
	public static WebService getInstance() {
		if(instance == null)
			instance = new WebService();
		return instance;
	}
	
	
	private ConfigurableApplicationContext webContext;
	private ConfigService config = ConfigService.getInstance();
	
	private WebService() {
		
	}
	
	public void start() {
		if(webContext == null) {
			SpringApplication sp = new SpringApplication(PoeSpringApplication.class);
			sp.setDefaultProperties(Collections.singletonMap("server.port", config.getPort()));
			this.webContext = sp.run();
		}			
	}
	
	public void stop() {
		if(webContext != null)
			webContext.stop();
		webContext = null;
		
	}
	
	public boolean hasStarted() {
		return webContext != null;
	}
	
	public void open() {
		URI uri;
		try {
			uri = new URI("http://localhost:" + config.getPort());
			Desktop.getDesktop().browse(uri);
		} catch (URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
