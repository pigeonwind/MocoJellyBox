package com.jerry.message;

import static java.lang.System.out;
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
		definer = new DefaultMessageDefiner();
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void getFieldValurOrDefaulttest_default() throws Exception {
		out.printf("=================== %s START ===================\n", "getFieldValurOrDefaulttest");
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
		out.printf("=================== %s START ===================\n", "getFieldValurOrDefaulttest_nodefault");
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
		out.printf("=================== %s START ===================\n", "getFieldValurOrDefaulttest_default_date");
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

	@Test
	public void getDefaultHeaderSizeTest() throws Exception {
		out.printf("========= %sTest() START =========\n", "getDefaultHeaderSize");
		// given
		Object expected = String.valueOf(512);

		// when
		Object actual = definer.getDefaultHeaderSize();
		// then
		assertThat(actual, is(expected));

	}
}
