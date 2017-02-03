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
	private JSONParser parser;
	private JSONObject jsonObj;
	@Before
	public void setUp() throws Exception {
		String messageDefinePath = ApplicationContext.loadContext().getValue("messageDefine.path");
		parser = new JSONParser();
		jsonObj = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(messageDefinePath)));
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public final void getValueByJsonPathTest_query() throws FileNotFoundException, IOException, ParseException {
		System.out.printf("=================== %s START ===================\n", "getValueByJsonPathTest_query");
		String expected ="8";
		String actual = (String) JsonUtil.getValueByJsonQueryPath(jsonObj, "message.header.field.name?STND_TLG_LEN","length");
		assertThat(actual, is(expected));
	}
	@Test
	public final void getValueByJsonPathTest_query2() throws FileNotFoundException, IOException, ParseException {
		System.out.printf("=================== %s START ===================\n", "getValueByJsonPathTest_query2");
		String expected ="8";
		String actual = (String) JsonUtil.getValueByJsonQueryPath(jsonObj, "message.header.field.[name=STND_TLG_LEN].length");
		assertThat(actual, is(expected));
	}
	@Test
	public void getValueByJsonPathTest_String() throws Exception {
		System.out.printf("=================== %s START ===================", "getValueByJsonPathTest");
		// given
		String path = "message.body.name";
		Object expected="Body";
		// when
		Object actual=JsonUtil.getValueByJsonPath(jsonObj, path);
		// then
		assertThat(actual, is(expected));
	}
}
