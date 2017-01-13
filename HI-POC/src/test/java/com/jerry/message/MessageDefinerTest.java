package com.jerry.message;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jerry.util.ByteArrayUtil;
import com.jerry.util.TimeUtil;

public class MessageDefinerTest {
	byte[] inputBytes;
	private MessageDefiner definer;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void getFieldValurOrDefaulttest_default() throws Exception {
		System.out.printf("=================== %s START ===================\n", "getFieldValurOrDefaulttest");
		definer = new DefaultMessageDefiner();
		String feildName="TLG_LNGG_DST_CD";
		// given
		Object expected = getFeildValue(feildName);
		// when
		Object actual = definer.getDefaultFeildValue(feildName);
		// then
		assertThat(actual, is(expected));
	}
	@Test
	public void getFieldValurOrDefaulttest_nodefault() throws Exception {
		System.out.printf("=================== %s START ===================\n", "getFieldValurOrDefaulttest_nodefault");
		definer = new DefaultMessageDefiner();
		String feildName="RCV_SVC_CD";
		// given
		Object expected = getFeildValue(feildName);
		// when
		Object actual = definer.getDefaultFeildValue(feildName);
		// then
		assertThat(actual, is(expected));
	}
	@Test
	public void getFieldValurOrDefaulttest_default_date() throws Exception {
		System.out.printf("=================== %s START ===================\n", "getFieldValurOrDefaulttest_default_date");
		definer = new DefaultMessageDefiner();
		String feildName="TLG_LNGG_DST_CD";
		// given
		Object expected = getFeildValue(feildName);
		// when
		Object actual = definer.getDefaultFeildValue(feildName);
		// then
		assertThat(actual, is(expected));
	}

	private Object getFeildValue(String feildName) {
		byte[] feildValue;
		int length = definer.getFeildAttrValueInteager(feildName, DefaultMessageDefiner.FEILD_ATTR_NAME_LENGTH);
		String dataString = definer.getFeildAttrValueString(feildName, DefaultMessageDefiner.FEILD_ATTR_NAME_DEFAULT);
		feildValue = new byte[length];
		ByteArrayUtil.fillWhiteSpace(feildValue);
		if(dataString!=null){
			if(dataString.contains("sysdate")){
				dataString=TimeUtil.getCurrentTime((String) definer.getFeildAttrValueString(feildName,DefaultMessageDefiner.FEILD_ATTR_NAME_FORMAT));
			}
			System.arraycopy(dataString.getBytes(), 0, feildValue, 0, length);
		}
		return feildValue;
	}

}
