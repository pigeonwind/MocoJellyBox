package com.jerry.message;

import java.io.*;
import java.util.*;

import com.jerry.util.*;

public class Message {
	private Map<String, Object> dataMap;
	private MessageDefiner messageDefiner;
	private byte[] intputData;
	private byte[] outputData;
	private byte[] defalutMessageHeaderBytes;
	
	private void init() throws FileNotFoundException, IOException {
		this.dataMap = new HashMap<String, Object>();
		initDefaultMessageHeader();
	}

	public Message(MessageDefiner definer) throws FileNotFoundException, IOException{
		this.messageDefiner = definer;
		init();
		makeDefaultMessage();
	}

	private void initDefaultMessageHeader() {
		int messageHeaderLength= messageDefiner.getMessageDefineInt("message.header.length");
		defalutMessageHeaderBytes = new byte[messageHeaderLength];
		ByteArrayUtil.fillWhiteSpace(defalutMessageHeaderBytes);
	}
	public int getMessageFeildValueInteger(String feildName) {
		return ByteArrayUtil.parseInt((byte[])dataMap.get(feildName));
	}
	public String getMessageFeildValueString(String feildName){
		return ByteArrayUtil.parseString((byte[])dataMap.get(feildName));
	}
	public Message parse(byte[] inputData) throws FileNotFoundException, IOException{
		this.intputData=inputData;
		Iterator<String> messageFeildNames = messageDefiner.feildNameIterator();
		while (messageFeildNames.hasNext()) {
			setInputMessageHeader(messageFeildNames.next());
		}
		String bodyDataName =messageDefiner.getBodyFeildName();
		
		setOutputData(inputData);
		return this;
	}
	private void setInputMessageHeader(String feildName) {
		int feildLength = messageDefiner.getFeildAttrValueInteager(feildName,DefultMessageDefiner.FEILD_ATTR_NAME_LENGTH);
		int feildOffset = messageDefiner.getFeildAttrValueInteager(feildName,DefultMessageDefiner.FEILD_ATTR_NAME_OFFSET);
		byte[] feildDataBytes = new byte[feildLength];
		System.arraycopy(intputData, feildOffset, feildDataBytes, 0, feildLength);
		dataMap.put(feildName, feildDataBytes);
		System.out.printf("input : %s[%s]\n", feildName, new String(feildDataBytes));
	}
	public Message makeDefaultMessage(){
		Iterator<String> messageFeildNames = messageDefiner.feildNameIterator();
		while (messageFeildNames.hasNext()) {
			setDefaultMessage(messageFeildNames.next());
		}
		setOutputData(defalutMessageHeaderBytes);
		return this;
	}
	
	private void setOutputData(byte[] data) {
		this.outputData=data;
	}

	private void setDefaultMessage(String feildName) {
		int feildLength = messageDefiner.getFeildAttrValueInteager(feildName,DefultMessageDefiner.FEILD_ATTR_NAME_LENGTH);
		int feildOffset = messageDefiner.getFeildAttrValueInteager(feildName,DefultMessageDefiner.FEILD_ATTR_NAME_OFFSET);
		byte[] dataBytes =  getDefaultHeaderDataBytes(feildName,feildLength,DefultMessageDefiner.FEILD_ATTR_NAME_DEFAULT);
		System.arraycopy(dataBytes, 0, defalutMessageHeaderBytes, feildOffset, feildLength);
		System.out.printf("default : %s[%s]\n", feildName, new String(dataBytes));
	}
	private byte[] getDefaultHeaderDataBytes(String feildName,int feildLength ,String string) {
		byte[] dataBytes = new byte[feildLength];
		String feildDefaultValue = (String) messageDefiner.getFeildAttrValueString(feildName,DefultMessageDefiner.FEILD_ATTR_NAME_DEFAULT);
		ByteArrayUtil.fillWhiteSpace(dataBytes);
		if (feildDefaultValue != null) {
			if (feildDefaultValue.contains("sysdate")) {
				feildDefaultValue = TimeUtil.getCurrentTime((String) messageDefiner.getFeildAttrValueString(feildName,DefultMessageDefiner.FEILD_ATTR_NAME_FORMAT));
			} 
			dataBytes = feildDefaultValue.getBytes();
		}
		return dataBytes;
	}
	public byte[] getBytes() {
		return outputData;
	}

	public void setMessageFeildValue(String feildName, Object feildValue) {
		if(messageDefiner.isValidFeild(feildName, feildValue)){
			dataMap.put(feildName, feildValue);
		}else{
			new RuntimeException(feildName.concat(" is not valid "));
		}
	}
	public byte[] getMessageFeildValue(String feildName) {
		byte[] result =(byte[]) dataMap.get(feildName);
		System.out.printf("%s [%s]\n",feildName, new String(result));
		return result;
	}
	public void setMessageBodyData(byte[] bodyDataBytes) {
		
		
	}
	public byte[] getMessageBodyData() {
		return null;
	}
}
