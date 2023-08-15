package org.tevid.todo_list.utils;

import java.io.IOException;
import java.nio.file.Path;

import org.tevid.todo_list.log.LoggingService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import lombok.extern.java.Log;

@Log
public class ConfigService {

	private static ConfigService instance;

	public static ConfigService getInstance() {
		if (instance == null)
			instance = new ConfigService();
		return instance;
	}

	private Config config;
	private PathService pathService = PathService.getInstance();
	private LoggingService loggingService = LoggingService.getInstance();

	private ConfigService() {
		if (load() == false) {
			this.config = new Config();
			config.setPort(8080);
			config.setActiveProfile(null);
			save();
		}

	}

	public String getActiveProfile() {
		if (config == null)
			return null;
		return config.getActiveProfile();
	}

	public void setActiveProfile(String profileName) {
		if (config == null)
			return;
		config.setActiveProfile(profileName);
		save();
	}

	public int getPort() {
		if (config == null)
			return 8080;
		return config.getPort();
	}

	public void setPort(int port) {
		if (config == null)
			return;

		config.setPort(port);
		save();
	}
	
	public int getNextkey() {
		if(config == null)
			return NativeKeyEvent.VC_F1;
		return config.getNextKey();
	}
	
	public void setNextKey(int keyCode) {
		if(config == null)
			return;
		config.setNextKey(keyCode);
		save();
	}
	
	public int getPreviousKey() {
		if(config == null)
			return NativeKeyEvent.VC_F2;
		return config.getPreviousKey();
	}
	
	public void setPreviousKey(int keyCode) {
		if(config == null)
			return;
		config.setPreviousKey(keyCode);
		save();
	}	

	private boolean save() {
		try {
			Path configPath = pathService.getRootDir().resolve("config.json");
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(configPath.toFile(), config);

		} catch (IOException e) {
			log.severe("Error saving config.json. " + e.toString());
			loggingService.logWarning("Error saving config.json");
			return false;
		}
		return true;
	}

	private boolean load() {

		try {
			Path configPath = pathService.getRootDir().resolve("config.json");
			if (configPath.toFile().exists() == false)
				return false;

			ObjectMapper mapper = new ObjectMapper();
			this.config = mapper.readValue(configPath.toFile(), Config.class);

		} catch (IOException e) {
			log.severe("Error saving config.json. " + e.toString());
			loggingService.logWarning("Error loading config.json");
			return false;
		}
		return true;
	}
}
