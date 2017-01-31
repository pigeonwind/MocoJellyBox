package com.jerry.message;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.parser.ParseException;
import org.junit.*;

import com.jerry.net.SyncClient;
import com.jerry.util.TimeUtil;

public class MessageTest {
	byte[] dummySyncMsgByte;
	MessageDefiner definer;
	Message dummyMessage;
	@Before
	public void setUp() throws Exception {
		dummySyncMsgByte = SyncClient.getDummyMessage();
		definer = new DefaultMessageDefiner();
		dummyMessage =  new Message(definer).parse(dummySyncMsgByte);
	}
	@After
	public void tearDown() throws Exception {
	}
	@Test
	public final void a_buildMsgByByteDataTest() throws FileNotFoundException, IOException, ParseException {
		System.out.printf("=================== %s START ===================\n", "buildMsgByByteDataTest");
		Message msg = new Message(new DefaultMessageDefiner()).parse(dummySyncMsgByte);
		byte[] lengthBytes = new byte[8];
		System.arraycopy(dummySyncMsgByte, 0, lengthBytes, 0, 8);
		int expectMsgSize = Integer.parseInt(new String(lengthBytes));
		int actualMsgSize = msg.getMessageFeildValueInteger("STND_TLG_LEN");
		assertThat(actualMsgSize, is(expectMsgSize));
	}
	@Ignore
	@Test
	public void b_isValidMessageTest_STRING() throws Exception {
		
		System.out.printf("=================== %s START ===================\n", "isValidMessageTest_STRING");
		// given
		boolean expected=true;
		String feildAttrName,feildAttrValue;
		Iterator<String> feildNameIterator,feildAttrIter;
		feildNameIterator=definer.feildNameIterator();
		while (feildNameIterator.hasNext()) {
			String feildName = (String) feildNameIterator.next();
			feildAttrIter = definer.getFeildAttrNameIter(feildName);
			int length=0;
			String defalutValueString=null;
			String format=null;
			while (feildAttrIter.hasNext()) {
				feildAttrName = (String) feildAttrIter.next();
				feildAttrValue = definer.getFeildAttrValueString(feildName, feildAttrName);
				if("length".equals(feildAttrName)){
					length= Integer.parseInt(feildAttrValue);
				}
				if("default".equals(feildAttrName)){
					defalutValueString = feildAttrValue;
				}
				if("format".equals(feildAttrName)){
					format=feildAttrValue;
				}
			}
			System.out.println("--------------------------------------");
			byte[] data = new byte[length];
			if(defalutValueString!=null){
				if(defalutValueString.contains("sysdate")){
					defalutValueString=TimeUtil.getCurrentTime(format);
				}
				data=defalutValueString.getBytes();
			}
			// when
//			boolean actual=definer.isValidFeild(feildName, data);
			// then
//			assertThat(actual, is(expected));
		}
	}
	@Test
	public void c_builDefaultMessageHeaderTest() throws Exception {
		
		System.out.printf("=================== %s START ===================\n", "builDefaultMessageHeaderTest");
		Message actualMessage = new Message(definer);
		Iterator<String> feildNameIter = definer.feildNameIterator();
		String feildName;
		Object expectObj,actualObj;
		while(feildNameIter.hasNext()){
			feildName = feildNameIter.next();
			// given
			System.out.println(feildName);
			System.out.printf("expect : ");
			expectObj =dummyMessage.getMessageFeildValue(feildName);
			// when
//			actualMessage.setMessageFeildValue(feildName,expectObj);
			System.out.printf("actual : ");
			actualObj = actualMessage.getMessageFeildValue(feildName);
			System.out.println("-");
			// then
			assertThat(actualObj, is(expectObj));
		}
	}
	@Test
	public void d_setBodyDataTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "setBodyDataTest");
		// given
		Object expected = dummyMessage.getMessageBodyData();
		System.out.println("========== start message build ==============");
		Message msg = MessageBuilder.build();
		// when
		Object actual =msg.getMessageBodyData();
		// then
		assertThat(actual, is(expected));
	}
}
