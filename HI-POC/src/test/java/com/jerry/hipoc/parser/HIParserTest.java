package com.jerry.hipoc.parser;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ism.common.exception.ISMException;
import com.ism.common.logger.DefaultLogger;
import com.ism.common.message.Message;
import com.ism.common.message.parser.IParser;
import com.ism.common.message.parser.exception.InputParseException;
import com.ism.common.util.Utility;
import com.jerry.hipoc.contants.MocomsysConstants;

public class HIParserTest {
	Message testSndMsg, testRtnMsg;
	String sndHdrStr, rtnHdrStr;
	IParser parser;

	@Before
	public void setUp() throws Exception {
		//sndHdrStr = "0000050720160726TESTGID0000000000000000100                                                                        QS                           TESTSND_IDTESTRTN_IDONLHIPOC00012016072618000000001                                                                                                                                                                                                                                                                                                                  ";
		//rtnHdrStr = "0000050720160726TESTGID0000000000000000102                                                                        RA                           TESTRTN_IDTESTRTN_IDONLHIPOC00012016072618000000911                                                                                                                                                                                                                                                                                                                  ";
		sndHdrStr="00000518 20160729180004525JERRY-TH0452500                                                           EAI                                          HIPHIPON0001 SS                                            1                                                                                                                                                                                                                                                                                                                   ";
		rtnHdrStr="00000518 20160729180004525JERRY-TH0452500                                                           EAI                                          HIPHIPON0001 AR                                            2                                                                                                                                                                                                                                                                                                                   ";
		String data = "Hello HI-POC";
		String endMsg = "@@";
		String sndMsgStr = sndHdrStr.concat(data).concat(endMsg);
		String rtnMsgStr = rtnHdrStr.concat(data).concat(endMsg);

		System.out.println(sndMsgStr.length());
		testSndMsg = new Message(sndMsgStr.getBytes(), sndMsgStr);
		testRtnMsg = new Message(rtnMsgStr.getBytes(), rtnMsgStr);
		parser = new HIParser();
	}

	@Ignore
	@Test
	public final void testAggMessages() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetAckMessage() {
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		DefaultLogger.logN(">> testGetAckMessage()                        >>");
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// given
		byte[] expectedMsgBytes = testSndMsg.getMessage();
		System.arraycopy(MocomsysConstants.RESPONSE_STRING.getBytes(), 0, expectedMsgBytes, MocomsysConstants.REQ_RES_OFFSET,
				MocomsysConstants.REQ_RES_LENGTH);
		System.arraycopy(MocomsysConstants.SUCCESS_STRING.getBytes(), 0, expectedMsgBytes, MocomsysConstants.SUCCESS_OFFSET,
				MocomsysConstants.SUCCESS_LENGTH);
		byte[] date = Utility.getFormattedDate("yyyyMMddHHmmssSSS", new Date()).getBytes();
		System.arraycopy(date, 0, expectedMsgBytes, MocomsysConstants.RTN_DATE_OFFSET, MocomsysConstants.RTN_DATE_LENGTH);
		System.out.println(new String(expectedMsgBytes));
		// when
		byte[] actualMsgBytes = parser.getAckMessage(testSndMsg).getMessage();
		System.out.println(new String(actualMsgBytes));
		// then
		assertThat("TEST", new String(actualMsgBytes), is(new String(expectedMsgBytes)));
	}

	@Test
	public final void testGetConnectorID() throws InputParseException {
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		DefaultLogger.logN(">> testGetConnectorID()                        >>");
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		int resultS = parser.getConnectorID(testSndMsg);
		DefaultLogger.logN("	result : "+resultS);
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		int resultR =parser.getConnectorID(testRtnMsg);
		DefaultLogger.logN("	result : "+resultR);
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}

	@Ignore
	@Test
	public final void testGetErrorMessage() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	@Test
	public final void testGetErrorMessageThrowable() {
		fail("Not yet implemented"); // TODO
	}
	@Ignore
	@Test
	public final void testGetErrorMessageString() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetRuleID() throws InputParseException {
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		DefaultLogger.logN(">> testGetRuleID()                        >>");
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// given
		byte[] expectedBytes = new byte[MocomsysConstants.INTFID_LENGTH];
		System.arraycopy(testSndMsg.getMessage(), MocomsysConstants.INTFID_OFFSET, expectedBytes, 0, MocomsysConstants.INTFID_LENGTH);
		String expected = new String(expectedBytes);
		// when
		String actual = parser.getRuleID(testSndMsg);
		// then
		assertThat("testGetRuleID ", actual, is(expected));
	}

	@Test
	public final void testGetGlobalID() throws InputParseException {
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		DefaultLogger.logN(">> testGetGlobalID()                        >>");
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// given
		byte[] expectedBytes = new byte[MocomsysConstants.GLOBALID_LENGTH];
		System.arraycopy(testSndMsg.getMessage(), MocomsysConstants.GLOBALID_OFFSET, expectedBytes, 0, MocomsysConstants.GLOBALID_LENGTH);
		String expected = new String(expectedBytes);
		// when
		String actual = parser.getGlobalID(testSndMsg, 0);
		// then
		assertThat("testGetGlobalID ", actual, is(expected));
	}

	/**
	 * SEND_ID
	 * 
	 * @throws InputParseException
	 */
	@Test
	public final void testGetServiceCode_SEND_ID() throws InputParseException {
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		DefaultLogger.logN(">> testGetServiceCode_SEND_ID()                        >>");
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// given
		byte[] expectedBytes = new byte[MocomsysConstants.RTN_SVC_LENGTH];
		System.arraycopy(testSndMsg.getMessage(), MocomsysConstants.RTN_SVC_OFFSET, expectedBytes, 0, MocomsysConstants.RTN_SVC_LENGTH);
		String expected = new String(expectedBytes);
		// when
		String actual = parser.getServiceCode(testSndMsg.getMessage());
		// then
		assertThat("testGetServiceCode_SEND_ID ", actual, is(expected));
	}

	/**
	 * RECV_ID
	 * 
	 * @throws InputParseException
	 */
	@Test
	public final void testGetServiceCode_RECV_ID() throws InputParseException {
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		DefaultLogger.logN(">> testGetServiceCode_RECV_ID()                        >>");
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// given
		
		byte[] expectedBytes = new byte[MocomsysConstants.RTN_SVC_LENGTH];
		System.arraycopy(testRtnMsg.getMessage(), MocomsysConstants.RTN_SVC_OFFSET, expectedBytes, 0, MocomsysConstants.RTN_SVC_LENGTH);
		String expected = new String(expectedBytes);
		// when
		String actual = parser.getServiceCode(testRtnMsg.getMessage());
		// then
		assertThat("testGetServiceCode_RECV_ID ", actual, is(expected));
	}

	@Test
	public final void testIsReturn_rtn() throws InputParseException {
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		DefaultLogger.logN(">> testIsReturn_rtn()                        >>");
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// given
		byte[] expectedBytes = new byte[MocomsysConstants.REQ_RES_LENGTH];
		System.arraycopy(testRtnMsg.getMessage(), MocomsysConstants.REQ_RES_OFFSET, expectedBytes, 0, MocomsysConstants.REQ_RES_LENGTH);
		String expectedString = new String(expectedBytes);
		System.out.printf("expectedString [%s]\n",expectedString);
		boolean expected = MocomsysConstants.RESPONSE_STRING.equalsIgnoreCase(new String(expectedString));
		// when
		boolean actual = parser.isReturn(testRtnMsg);
		// then
		assertThat("testIsReturn_rtn ", actual, is(expected));
	}

	@Test
	public final void testIsReturn_snd() throws InputParseException {
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		DefaultLogger.logN(">> testIsReturn_snd()                        >>");
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		
		// given
		boolean expected = false;
		// when
		boolean actual = parser.isReturn(testSndMsg);
		// then
		assertThat("testIsReturn_snd ", actual, is(expected));
	}

	@Test
	public final void testSetHeader() throws InputParseException {
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		DefaultLogger.logN(">> testSetHeader()                        >>");
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// given
		byte[] expected = sndHdrStr.getBytes();
		// when
		parser.setHeader(testSndMsg);
		byte[] actual = parser.getHeader();
		// then
		assertThat("testGetHeader ", actual, is(expected));
	}

	@Ignore
	@Test
	public final void testGetHealthCheckMsg() {
		fail("Not yet implemented"); // TODO
	}
	@Ignore
	@Test
	public final void testGetMessageIndex() {
		fail("Not yet implemented"); // TODO
	}
	@Ignore
	@Test
	public final void testGetMessageSequence() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetNodeIndex() throws InputParseException {
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		DefaultLogger.logN(">> testGetNodeIndex()                        >>");
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// given
		int expected = -1;
		// when
		int actual = parser.getNodeIndex(testRtnMsg);
		// then
		assertThat("testGetNodeIndex ", actual, is(expected));
	}
	@Ignore
	@Test
	public final void testGetTargetingMethod() {
		fail("Not yet implemented"); // TODO
	}
	@Ignore
	@Test
	public final void testGetTimeout() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testInUserProcess() throws ISMException {
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		DefaultLogger.logN(">> testInUserProcess()                        >>");
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		Message resultS = parser.inUserProcess(testSndMsg, true);
		DefaultLogger.logN("	result : "+ new String (resultS.getMessage()));
		DefaultLogger.logN("*************************************************");
		Message resultR =parser.inUserProcess(testRtnMsg, false);
		DefaultLogger.logN("	result : "+ new String (resultR.getMessage()));
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}

	@Test
	public final void testIsSuccess_s() throws InputParseException {
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		DefaultLogger.logN(">> testIsSuccess_s()                        >>");
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// given
		byte[] expectedBytes = new byte[MocomsysConstants.SUCCESS_LENGTH];
		System.arraycopy(testSndMsg.getMessage(), MocomsysConstants.SUCCESS_OFFSET, expectedBytes, 0, MocomsysConstants.SUCCESS_LENGTH);
		String expectedString = new String(expectedBytes);
		System.out.printf("expectedString [%s]\n",expectedString);
		boolean expected = MocomsysConstants.SUCCESS_STRING.equalsIgnoreCase(expectedString);;
		// when
		boolean actual = parser.isSuccess(testSndMsg);
		// then
		assertThat("testIsSuccess_SUCCESS ", actual, is(expected));
	}

	@Test
	public final void testIsSuccess_f() throws InputParseException {
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		DefaultLogger.logN(">> testIsSuccess_f()                        >>");
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// given
		byte[] expectedBytes = new byte[MocomsysConstants.SUCCESS_LENGTH];
		System.arraycopy(testRtnMsg.getMessage(), MocomsysConstants.SUCCESS_OFFSET, expectedBytes, 0, MocomsysConstants.SUCCESS_LENGTH);
		String expectedString = new String(expectedBytes);
		System.out.printf("expectedString [%s]\n",expectedString);
		boolean expected = MocomsysConstants.SUCCESS_STRING.equalsIgnoreCase(expectedString);
		// when
		boolean actual = parser.isSuccess(testRtnMsg);
		// then
		assertThat("testIsSuccess_FAIL ", actual, is(expected));
	}

	@Test
	public final void testIsSync_sync() throws InputParseException {
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		DefaultLogger.logN(">> testIsSync_sync()                        >>");
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// given
		byte[] expectedBytes = new byte[MocomsysConstants.SYNC_LENGTH];
		System.arraycopy(testSndMsg.getMessage(), MocomsysConstants.SYNC_OFFSET, expectedBytes, 0, MocomsysConstants.SYNC_LENGTH);
		String expectedString = new String(expectedBytes);
		System.out.printf("expectedString [%s]\n",expectedString);
		boolean expected = MocomsysConstants.SYNC_STRING.equalsIgnoreCase(new String(expectedString));
		// when
		boolean actual = parser.isSync(testSndMsg.getMessage());
		// then
		assertThat("testIsSync_sync ", actual, is(expected));
	}

	@Test
	public final void testIsSync_asyc() throws InputParseException {
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		DefaultLogger.logN(">> testIsSync_asyc()                        >>");
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// given
		byte[] expectedBytes = new byte[MocomsysConstants.SYNC_LENGTH];
		System.arraycopy(testRtnMsg.getMessage(), MocomsysConstants.SYNC_OFFSET, expectedBytes, 0, MocomsysConstants.SYNC_LENGTH);
		String expectedString = new String(expectedBytes);
		System.out.printf("expectedString [%s]\n",expectedString);
		boolean expected = MocomsysConstants.SYNC_STRING.equalsIgnoreCase(new String(expectedString));
		// when
		boolean actual = parser.isSync(testRtnMsg.getMessage());
		// then
		assertThat("testIsSync_asyc ", actual, is(expected));
	}

	@Test
	public final void testOutUserProcess() throws ISMException {
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		DefaultLogger.logN(">> testOutUserProcess()                        >>");
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		Message resultS = parser.outUserProcess(testSndMsg, true);
		DefaultLogger.logN("	result : "+ new String (resultS.getMessage()));
		DefaultLogger.logN("*************************************************");
		Message resultR =parser.outUserProcess(testRtnMsg, false);
		DefaultLogger.logN("	result : "+ new String (resultR.getMessage()));
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}

	@Test
	public final void testSetNodeIndex() throws InputParseException {
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		DefaultLogger.logN(">> testSetNodeIndex()                        >>");
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// given
		byte[] expected = testSndMsg.getMessage();
		// when
		byte[] actual = parser.setNodeIndex(testSndMsg.getMessage(), 13);
		// then
		assertThat("testSetNodeIndex ", actual, is(expected));
	}

	@Ignore
	@Test
	public final void testSetProjectName() {
		// given
	}

	@Test
	public final void testSetSyncType_a() throws InputParseException {
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		DefaultLogger.logN(">> testSetSyncType_a()      S->A               >>");
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// given
		byte[] expected = MocomsysConstants.ASYNC_STRING.getBytes();
		System.out.printf("expected Sync String [%s]\n", new String(expected));
		System.out.printf("before syncSet 0 msg [%s]\n", new String(testSndMsg.getMessage()));
		// when async !=0
		byte[] syncSetMessageBytes = parser.setSyncType(testSndMsg.getMessage(), 3);
		System.out.printf("after  syncSet 0 msg [%s]\n", new String(syncSetMessageBytes));
		byte[] actual =arrayCopy(syncSetMessageBytes, MocomsysConstants.SYNC_OFFSET, MocomsysConstants.SYNC_LENGTH);
		System.out.printf("actual Sync String [%s]\n", new String(actual));
		// then
		assertThat("testSetSyncType ", actual, is(expected));
	}
	@Test
	public final void testSetSyncType_s() throws InputParseException {
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		DefaultLogger.logN(">> testSetSyncType_s()       A->S              >>");
		DefaultLogger.logN(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// given
		
		byte[] expected = MocomsysConstants.SYNC_STRING.getBytes();
		System.out.printf("expected Sync String [%s]\n", new String(expected));
		System.out.printf("before syncSet 0 msg [%s]\n", new String(testRtnMsg.getMessage()));
		// when sync == 0
		byte[] syncSetMessageBytes = parser.setSyncType(testRtnMsg.getMessage(), 0);
		System.out.printf("after  syncSet 0 msg [%s]\n", new String(syncSetMessageBytes));
		byte[] actual =arrayCopy(syncSetMessageBytes, MocomsysConstants.SYNC_OFFSET, MocomsysConstants.SYNC_LENGTH);
		System.out.printf("actual Sync String [%s]\n", new String(actual));
		// then
		assertThat("testSetSyncType ", actual, is(expected));
	}

	@Ignore
	@Test
	public final void testSetSystemCode() {
		fail("Not yet implemented"); // TODO
	}
	@Ignore
	@Test
	public final void testValidate() {
		fail("Not yet implemented"); // TODO
	}
	
	private byte[] arrayCopy(byte[] srcMsgBytes, int srcOffset, int length) {
		
		DefaultLogger.logN("HI PARSER TEST >> arrayCopy()");
		DefaultLogger.logN("		srcMsgBytes : "+new String(srcMsgBytes));
		DefaultLogger.logN("		srcOffset : "+srcOffset);
		DefaultLogger.logN("		length : "+length);
		byte destBytes[] = new byte[length];
		System.arraycopy(srcMsgBytes, srcOffset , destBytes, 0, length);
		DefaultLogger.logN("		return : "+new String(destBytes));
		return destBytes;
	}

}
