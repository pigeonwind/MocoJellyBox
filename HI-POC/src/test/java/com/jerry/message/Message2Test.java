package com.jerry.message;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Message2Test {
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	// TODO defaultMessageTest
	@Test
	public void getDefaultMessageTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "getDefaultMessageTest");
		MessageDefiner definer= new DefaultMessageDefiner();
		HashMap<String, byte[]> defaultMessage = new HashMap<>();
		Function<String,byte[]> getDefaultFeildValue=definer::getDefaultFeildValue;
		Consumer<String> action =feildName-> defaultMessage.put(feildName, getDefaultFeildValue.apply(feildName));
		definer.feildNameIterator().forEachRemaining(action);
		byte[] messageBytes = new byte[512];





		Message2 msg = new Message2(definer);
		// given
		Object expected=defaultMessage;
		// when
		Object actual=msg.getDefaultMessage();

		// then
		assertThat(actual, is(expected));
	}
}
