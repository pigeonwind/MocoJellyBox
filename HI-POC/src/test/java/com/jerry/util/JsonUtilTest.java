package com.jerry.util;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.hamcrest.CoreMatchers.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jerry.context.ApplicationContext;

public class JsonUtilTest {
	JSONObject jsonObj;
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public final void getValueByJsonPathTest_query() throws FileNotFoundException, IOException, ParseException {
		System.out.printf("=================== %s START ===================\n", "getValueByJsonPathTest_query");
		String messageDefinePath = ApplicationContext.loadContext().getValue("messageDefine.path");
		JSONParser parser = new JSONParser();
		JSONObject jsonObj=(JSONObject) parser.parse(new InputStreamReader(new FileInputStream(messageDefinePath)));
		
		String expected ="8";
		String actual = (String) JsonUtil.getValueByJsonPath(jsonObj, "message.header.field.name?STND_TLG_LEN","length");
		assertThat(actual, is(expected));
	}
}
