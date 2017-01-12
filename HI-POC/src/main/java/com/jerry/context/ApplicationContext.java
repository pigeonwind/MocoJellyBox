package com.jerry.context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.jerry.util.JsonUtil;

public class ApplicationContext{
	private JSONObject configJson;
	private ApplicationContext() {
		JSONParser parser = new JSONParser();
		try {
			configJson = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(getApplicationHome()+"/config/config.json")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	private String getApplicationHome() {
		String appHome=System.getenv("app.home");
		if(appHome==null){
			appHome=System.getProperty("user.dir");
		}
		System.out.printf("AppHome[%s]\n",appHome);
		return appHome;
	}
	private static class Singletone{
		private static final ApplicationContext instance = new ApplicationContext();
	}
	public static ApplicationContext loadContext() {
		System.out.println("load context !!");
		return Singletone.instance;
	}
	public String getValue(String keyPath) {
		return (String) JsonUtil.getValueByJsonPath(configJson,keyPath);
	}
	
}
