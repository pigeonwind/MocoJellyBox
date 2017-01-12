package com.jerry.net;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tmax.webt.WebtAttribute;
import tmax.webt.WebtBuffer;
import tmax.webt.WebtConnection;
import tmax.webt.WebtRemoteService;
import tmax.webt.io.WebtStringBuffer;

public class WebTConnectionTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void connectionTestName() throws Exception {
		
		System.out.printf("=================== %s START ===================\n", "connectionTestName");
		// given
		String remoteIp="10.10.1.127";
		int port = 8888;
		WebtConnection webtConnection = new WebtConnection(remoteIp, port);
		
		String serviceName="TOUPPER";
		WebtRemoteService service = new WebtRemoteService(serviceName, webtConnection);
		
		String testData = "test";
		WebtBuffer sndBuffer = new WebtStringBuffer();
		WebtBuffer rcvBuffer;
		
		sndBuffer.setString(testData);
		
		rcvBuffer = service.tpcall(serviceName,sndBuffer,new WebtAttribute());
		
		
		
		System.out.printf("send data[%s] recv data[%s]\n",testData,rcvBuffer.getString());
		
		webtConnection.close();
		
		// when

		// then

		fail("Not yet implements");
	}

	
}
