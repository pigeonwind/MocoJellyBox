package com.jerry.hipoc.parser;

import java.util.Date;
import java.util.HashMap;

import com.ism.common.exception.ErrorCode;
import com.ism.common.exception.ErrorMessage;
import com.ism.common.exception.ISMException;
import com.ism.common.logger.DefaultLogger;
import com.ism.common.message.Message;
import com.ism.common.message.parser.IParser;
import com.ism.common.message.parser.exception.InputParseException;
import com.ism.common.util.Utility;
import com.ism.rule.entity.constant.Constants;
import com.jerry.hipoc.contants.MocomsysConstants;

public class HIParser implements IParser, MocomsysConstants {
	  protected byte[] inData = null;
	  protected byte[] header = null;

	  protected String systemCode = null;
	  protected String projectName = null;

	  private int systemNode = 0;
	  private int instanceNode = 0;

	  private static String HOST = null;
	  private static String IP = null;
	  private static String MAC = null;
	  private static Object _lock = new Object();
	  private static final int DUMP_MAX_TIME = 600;
	  private static final int DUMP_CHECK_TIME = 30;
	  private static final int TIMEOUT_DUMP_COUNT = 10;
	  private static long lastCreated = 0L;

	  private static Object _dumpLock = new Object();

	  private static HashMap<String, String> timeoutCnt = new HashMap();

	@Override
	public Message aggMessages(Message msg, Message[] msgArr) {
		DefaultLogger.logN("DefaultParser.aggMessages() ");
		if(msgArr == null) {
			return msg;
		}
		if(msgArr != null && msgArr.length == 1) {
			return msgArr[0];
		}
		else {
			// TODO : agg 관련 있을 시 구현할 것..
			return msgArr[0];
		}

	}

	@Override
	public Message getAckMessage(Message msg) {
		byte[] inputMsgBytes = msg.getMessage();
		DefaultLogger.logN("DefaultParser.getAckMessage()");
		// 요청 응답 코드
		System.arraycopy(RESPONSE_STRING.getBytes(), 0, inputMsgBytes, REQ_RES_OFFSET, REQ_RES_LENGTH);
		// 성공 실패 유무
		System.arraycopy(SUCCESS_STRING.getBytes(), 0, inputMsgBytes, SUCCESS_OFFSET, SUCCESS_LENGTH);
		
		// 응답 시간
		byte[] date = Utility.getFormattedDate("yyyyMMddHHmmssSSS", new Date()).getBytes();
		System.arraycopy(date, 0, inputMsgBytes, RTN_DATE_OFFSET, RTN_DATE_LENGTH);
		
		// 출력전문유형코드 : 1 , 전문연속일련번호 : 000
		System.arraycopy("1000".getBytes(), 0, inputMsgBytes, 205, 4);
		String ackMessage = new String(inputMsgBytes);
		DefaultLogger.logN("DefaultParser.getAckMessage() - [" + ackMessage + "]");
		return new Message(inputMsgBytes,ackMessage );
	}

	@Override
	public int getConnectorID(Message msg) throws InputParseException {
		DefaultLogger.logN("DefaultParser.getConnectorID()");
		byte[] node = new byte[NODE_LENGTH];
		byte[] data = msg.getMessage();
		System.arraycopy(data, NODE_OFFSET, node, 0, NODE_LENGTH);
		//System.out.println("XX : " + new String(node));
		int iNode = -1;
		try {
			iNode = Integer.parseInt(new String(node));
			systemNode		= (int)(iNode / 100);
			instanceNode	= (int)(iNode % 100);
			// system node 또는 instanceNode 중 하나라도 0 이면 설정이 않되어 있는 것으로 인식.
			if(systemNode == 0 || instanceNode == 0) {
				return -1;
			}
		} catch (Exception e) { // NODE DATA 에 세팅이 않되어 있을 경우 -1 반환.
			systemNode		= 0;
			instanceNode	= 0;
			return -1;
		}
		// TODO : 우선 시스템 Node 로 반환하게 설정 함. instanceNode 가 어떻게 되는지 파악 되어야 수정가능 할 것 같음.
		int res = systemNode -1;
		DefaultLogger.logN("DefaultParser.getConnectorID() return : " + res);

		return res;
	}

	@Override
	public byte[] getErrorMessage() {
		DefaultLogger.logN("DefaultParser.getErrorMessage()");
		return getErrorMessage("EAI ERROR");
	}

	@Override
	public byte[] getErrorMessage(Throwable err) {
		DefaultLogger.logN("DefaultParser.getErrorMessage(Throwable)");
		
		// HEADER
		byte[] header		= getHeader();
		
		// 성공여부 및 응답코드여부
		System.arraycopy(FAIL_STRING.getBytes(), 0, header, SUCCESS_OFFSET, SUCCESS_LENGTH);
		System.arraycopy(RESPONSE_STRING.getBytes(), 0, header, REQ_RES_OFFSET, REQ_RES_LENGTH);
		
		// 응답 시간
		byte[] date = Utility.getFormattedDate("yyyyMMddHHmmssSSS", new Date()).getBytes();
		System.arraycopy(date, 0, header, RTN_DATE_OFFSET, RTN_DATE_LENGTH);
		// 출력전문유형코드 : 1 , 전문연속일련번호 : 000
		System.arraycopy("1000".getBytes(), 0, header, 205, 4);
		
		//**********************************************************************
		// DATA
		// DATA 부분은 EM : 오류(2)  + 오류데이터 길이(8) + 오류 데이터(654) 로 이루어진다.
		//**********************************************************************
		byte[] errMessage = new byte[664];
		for(int i=0;i<errMessage.length;i++)
			errMessage[i] = 0x20;
		
		String getErrMessage1 = null;
		String getErrMessage2 = null;
		try {
			getErrMessage2 = getEAIErrorMessage(err);		
			System.arraycopy("01".getBytes(), 0, errMessage, 552, 2);									// 부가메시지반복횟수
			System.arraycopy(getErrMessage2.getBytes(), 0, errMessage, 564, getErrMessage2.getBytes().length);	// 부가메시지내용
		} catch(Exception e) {
			getErrMessage1 = "EAI1000003EAI 내부장애입니다. 센타 해당계로 연락 바랍니다 --거래결과 확인 바랍니다.";
			getErrMessage2 = getEAIErrorMessage(err);
			System.arraycopy("1".getBytes(), 0, errMessage, 241, 1);									// 주메시지반복횟수
			System.arraycopy(getErrMessage1.getBytes(), 0, errMessage, 242, getErrMessage1.getBytes().length);	// 메시지코드+주메세지내용
			System.arraycopy("01".getBytes(), 0, errMessage, 552, 2);									// 부가메시지반복횟수
			System.arraycopy(getErrMessage2.getBytes(), 0, errMessage, 564, getErrMessage2.getBytes().length);	// 부가메시지내용			
		}

		System.arraycopy("EM".getBytes(), 0, errMessage, 0, 2);	// 오류전문코드
		System.arraycopy(Utility.createLengthAsString(errMessage.length-10, 8).getBytes(), 0, errMessage, 2, 8);	// 오류데이터부길이
		
		
		// END_STRING
		byte[] endString	= END_STRING.getBytes();		
		
		byte[] tmp1 = add(header, errMessage);
		byte[] tmp2 = add(tmp1, endString);
		
		// 시스템헤더  오류발생 시스템코드, 오류코드설정 
		System.arraycopy("EAI".getBytes(), 0, tmp2, 209, 3);
		System.arraycopy(getErrMessage1.getBytes(), 0, tmp2, 212, 10);
				
		// 길이 변환.
		byte[] len			= null;
		if(ISTOTAL_LENGTH) {
			len = Utility.createLengthAsByte(tmp2.length, LENGTH_LENGTH);
		}
		else {
			len = Utility.createLengthAsByte(tmp2.length - (LENGTH_OFFSET + LENGTH_LENGTH), LENGTH_LENGTH);
		}
		System.arraycopy(len, 0, tmp2, 0, len.length);
		DefaultLogger.logN("==> 변환 전문[" + new String(tmp2) + "]\n");
		
		return tmp2;
	}
	private String getEAIErrorMessage(Throwable t) {
		String rtnValue = "EAI ERROR";
		int error_int = ErrorCode.FISS_COMMON;
		if ( t instanceof ISMException ){
			error_int = ((ISMException)t).getErrorCode();
		}
		else {
			if ( t != null ) {
				ISMException fe = getISMException(t);
                if ( fe != null ) {
                    error_int = fe.getErrorCode();
                }
                else {
                    try {
                        fe = (ISMException)t;
                        error_int = fe.getErrorCode();
                        DefaultLogger.logE(fe.getMessage());
                    } catch(Exception e) {
                        DefaultLogger.logE("*** NOT ISMException");
                        DefaultLogger.logE(e.getMessage(), e);
                    }
                }
            }
		}
		
		try {			
			rtnValue = ErrorMessage.getMessage(error_int, ((ISMException)t).getParams());
            DefaultLogger.logE("*** Error Message [" + rtnValue + "]\n");
        } catch( Exception e ) {
            DefaultLogger.logE("failed to get error message.", e );
        }
		return rtnValue;
	}
	private ISMException getISMException(Throwable t) {
		if (t == null)
			return null;
		Throwable o = t;
		while (t != null) {
			o = t.getCause();
			if (o == null)
				return null;
			if (o instanceof ISMException)
				return (ISMException) o;
			t = o;
		}
		return null;
	}
	protected byte[] add(byte[] data1, byte[] data2) {
		byte rtnValue[] = new byte[data1.length + data2.length];
		int offset = 0;
		System.arraycopy(data1, 0, rtnValue, offset, data1.length);
		offset += data1.length;
		System.arraycopy(data2, 0, rtnValue, offset, data2.length);
		return rtnValue;
	}
	private String getEAIErrorCode(Throwable t) {
		String rtnValue = "EAI ERROR";
		int error_int = ErrorCode.FISS_COMMON;
		if ( t instanceof ISMException ){
			error_int = ((ISMException)t).getErrorCode();
		}		
		return fillZero(String.valueOf(error_int),5);
	}
	private String fillZero(String unit, int length) {
		String res = unit;
		if(res == null) {
			res = "";
		}
		while(res.getBytes().length < length) {
			res = "0" + res;
		}
		return res;
	}

	@Override
	public byte[] getErrorMessage(String errMsgStr) {
		DefaultLogger.logN("DefaultParser.getErrorMessage(String)");
		return getErrorMessage(new Throwable(errMsgStr));
	}

	/**
	 * parse msg to string
	 */

	@Override
	public String getRuleID(Message msg) throws InputParseException {
		DefaultLogger.logN(">> getRuleID ()");
		DefaultLogger.logN(msg.getIntegrationServiceId());
		String ruleId = parseMsg(msg.getMessage(), INTFID_OFFSET, INTFID_LENGTH);
		DefaultLogger.logN("	getRuleID : ".concat(ruleId));
		return ruleId;
	}

	@Override
	public String getGlobalID(Message msg, int mode) throws InputParseException {
		DefaultLogger.logN("DefaultParser.getGlobalID() " + mode);
		if(mode == 0) {
			DefaultLogger.logN("DefaultParser Global Id Check Mode : false");
		}
		else if(mode == 1) {
			DefaultLogger.logN("DefaultParser Global Id Check Mode : true");
		}
		else {
			DefaultLogger.logN("DefaultParser Global Id Check Mode : known mode - " + mode);
		}
		String globalId;
		try {
			globalId = parseMsg(msg.getMessage(), GLOBALID_OFFSET, GLOBALID_LENGTH);
			DefaultLogger.logN("DefaultParser parse Global ID [" + globalId + "]");
		} catch (Exception e) {
			throw new InputParseException(ErrorCode.ONL_GID_COMMON);
		}
		return globalId;
	}

	@Override
	public String getServiceCode(byte[] msgByte) throws InputParseException {
		DefaultLogger.logN(">> getServiceCode ()");
		String serviceCode;

		if (isReturn(msgByte)) {
			serviceCode = parseMsg(msgByte, RTN_SVC_OFFSET, RTN_SVC_LENGTH);
		} else {
			serviceCode = parseMsg(msgByte, SND_SVC_OFFSET, SND_SVC_LENGTH);
		}

		DefaultLogger.logN("	serviceCode : ".concat(serviceCode));
		return serviceCode;
	}

	private String parseMsg(byte[] srcMsgBytes, int srcOffset, int length) {
		DefaultLogger.logN(">> parseMsg()");
		DefaultLogger.logN("	srcMsgBytes : " + new String(srcMsgBytes));
		DefaultLogger.logN("	srcOffset : " + srcOffset);
		DefaultLogger.logN("	length : " + length);

		byte[] destBytes = arrayCopy(srcMsgBytes, srcOffset, length);
		// System.arraycopy(srcMsgBytes, srcOffset-1, destBytes, 0, length);
		DefaultLogger.logN("	destBytes length : " + destBytes.length);
		return new String(destBytes);
	}

	private byte[] arrayCopy(byte[] srcMsgBytes, int srcOffset, int length) {
		DefaultLogger.logN("	>> arrayCopy()");
		DefaultLogger.logN("		srcMsgBytes : " + new String(srcMsgBytes));
		DefaultLogger.logN("		srcOffset : " + srcOffset);
		DefaultLogger.logN("		length : " + length);
		byte destBytes[] = new byte[length];
		System.arraycopy(srcMsgBytes, srcOffset, destBytes, 0, length);
		DefaultLogger.logN("		return : " + new String(destBytes));
		return destBytes;
	}

	/**
	 * parse msg to boolean
	 */

	@Override
	public boolean isReturn(Message msg) throws InputParseException {
		return isReturn(msg.getMessage());
	}

	private boolean isReturn(byte[] data) {
		DefaultLogger.logN(">> isReturn()");
		boolean result;
		if (RESPONSE_STRING.equalsIgnoreCase(parseMsg(data, REQ_RES_OFFSET, REQ_RES_LENGTH))) {
			DefaultLogger.logN("	isReturn : TRUE");
			result = true;
		} else {
			DefaultLogger.logN("	isReturn: FALSE");
			result = false;
		}
		return result;
	}

	/**
	 * 
	 */

	@Override
	public byte[] getHeader() {
		// TODO Auto-generated method stub
		return header;
	}

	@Override
	public byte[] getHealthCheckMsg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMessageIndex(Message msg) throws InputParseException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMessageSequence(Message msg) throws InputParseException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNodeIndex(Message msg) throws InputParseException {
		DefaultLogger.logN("DefaultParser.getNodeIndex()");
		byte[] data = msg.getMessage();
		int index = 0;
		byte[] node = new byte[2];
		System.arraycopy(data, NODE_OFFSET, node, 0, 2);
		try{
			index = Integer.parseInt(new String(node));			
			if(index <= 0) {
				index = -1;
			}
		}
		catch (NumberFormatException e)
		{
			index = -1;
		}
		
		DefaultLogger.logN("DefaultParser.getNodeIndex() " + index);  
		return index;
	}

	@Override
	public int getTargetingMethod(Message msg) throws InputParseException {
		DefaultLogger.logN("DefaultParser.getTargetingMethod() - LB_BY_RULE");
		return Constants.LB_BY_RULE;
	}

	@Override
	public int getTimeout(byte[] data) throws InputParseException {
		DefaultLogger.logN("DefaultParser.getTimeout()");
		if(data[TIMEOUT_USE_OFFSET] != '2') {
			DefaultLogger.logN("DefaultParser.getTimeout() - INTERFACE SETTING.");
			return -1;
		}
		return -1;
	}

	@Override
	public Message inUserProcess(Message msg, boolean isRequest) throws ISMException {
		byte[] data = msg.getMessage();
		DefaultLogger.logN("DefaultParser.inUserProcess [" + isRequest + "][" + new String(data) + "]");
		
		// 전송 시스템 코드 셋
		System.arraycopy("EAI".getBytes(), 0, data, TRS_SYS_CD_OFFSET, TRS_SYS_CD_LENGTH);
		
		try { // sequence 증가.
			byte[] bSeq = new byte[SEQUENCE_LENGTH];
			
			System.arraycopy(data, SEQUENCE_OFFSET, bSeq, 0, SEQUENCE_LENGTH);
			int seq = Integer.parseInt(new String(bSeq)) + 1; // sequence 증가.
			if(seq > 99) {
				seq = 99;
			}
			if(seq < 10)
				bSeq = ( "0" + seq ).getBytes();
			else
				bSeq = ( seq + "" ).getBytes();
			System.arraycopy(bSeq, 0, data, SEQUENCE_OFFSET, SEQUENCE_LENGTH);
		} catch(Exception e) {
			DefaultLogger.logE("Sequence Number Get Set Fail..", e);
		}
		msg.setMessage(data);
		return msg;
	}

	@Override
	public boolean isSuccess(Message msg) throws InputParseException {
		DefaultLogger.logN(">> isSuccess ()");
		byte[] msgByte = msg.getMessage();
		byte[] successCode = arrayCopy(msgByte, SUCCESS_OFFSET, SUCCESS_LENGTH);
		DefaultLogger.logN("["+new String(successCode)+"]");
		if (SUCCESS_STRING.equals(new String(successCode))) {
			DefaultLogger.logN("	SUCCESS");
			return true;
		}
		DefaultLogger.logN("	FAILURE");
		return false;
	}

	@Override
	public boolean isSync(byte[] msgByte) throws InputParseException {
		DefaultLogger.logN(">> isSync ()");
		byte[] isSync = arrayCopy(msgByte, SYNC_OFFSET, SYNC_LENGTH);
		if (SYNC_STRING.equalsIgnoreCase(new String(isSync))) {
			DefaultLogger.logN("	Sync");
			return true;
		}
		DefaultLogger.logN("	ASync");
		return false;
	}

	@Override
	public Message outUserProcess(Message msg, boolean isRequest) throws ISMException {
	    byte[] data = msg.getMessage();
	    DefaultLogger.logN("DefaultParser.outUserProcess [" + isRequest + "][" + new String(data) + "]");
	    if (!isRequest) {
	      System.arraycopy("US".getBytes(), 0, data, 225, 2);
	    }
	    msg.setMessage(data);
	    return msg;
	}

	@Override
	public void setHeader(Message msg) throws InputParseException {
		DefaultLogger.logN("DefaultParser.setHeader()");
		this.inData = msg.getMessage();
		this.header = new byte[HEADER_LENGTH];
		System.arraycopy(inData, 0, this.header, 0, HEADER_LENGTH);	
	}

	@Override
	public byte[] setNodeIndex(byte[] msg, int nodeIndex) throws InputParseException {
		DefaultLogger.logN("DefaultParser.setNodeIndex() " + nodeIndex);		
		int index = nodeIndex;
		if(index <= 0) {
			index = 0;
		}
		byte[] node = Utility.createLengthAsByte(index, NODE_LENGTH);
		System.arraycopy(node, 0, msg, NODE_OFFSET, NODE_LENGTH);
		DefaultLogger.logN("DefaultParser.setNodeIndex() : current node " + index);
		return msg;
	}

	@Override
	public void setProjectName(String projectName) {
		DefaultLogger.logN("DefaultParser.setProjectName()");
		this.projectName = projectName;

	}

	@Override
	public byte[] setSyncType(byte[] msg, int isSyncIntCode) throws InputParseException {
		DefaultLogger.logN(">> setSyncType() ");
		String syncAsyncStr;
		if (isSyncIntCode == 0) {
			syncAsyncStr = SYNC_STRING;
		} else {
			syncAsyncStr = ASYNC_STRING;
		}
		System.arraycopy(syncAsyncStr.getBytes(), 0, msg , SYNC_OFFSET, SYNC_LENGTH);
		return msg;
	}

	@Override
	public void setSystemCode(String systemCode) {
		DefaultLogger.logN("DefaultParser.setSystemCode()");
		this.systemCode = systemCode; 	

	}
	
	@Override
	public void validate(Message msg, boolean arg1) throws InputParseException {
		byte[] data = msg.getMessage();
	}
	

}
