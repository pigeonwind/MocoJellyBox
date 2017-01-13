package com.jerry.message;

import java.io.*;
import java.util.*;
import java.util.function.Function;

import com.jerry.util.*;

public class Message {
	private Map<String, Object> dataMap;
	private MessageDefiner messageDefiner;
	private byte[] intputData, outputData, defalutMessageHeaderBytes;
	private Function<String, Integer> getFeildLength, getFeildOffset;
	private void init() throws FileNotFoundException, IOException {
		this.dataMap = new HashMap<String, Object>();
		getFeildLength = name -> messageDefiner.getFeildAttrValueInteager(name,
				DefaultMessageDefiner.FEILD_ATTR_NAME_LENGTH);
		getFeildOffset = name -> messageDefiner.getFeildAttrValueInteager(name,
				DefaultMessageDefiner.FEILD_ATTR_NAME_OFFSET);
		initDefaultMessageHeader();
	}

	public Message(MessageDefiner definer) throws FileNotFoundException, IOException {
		messageDefiner = definer;
		init();
		makeDefaultMessage();
	}

	private void initDefaultMessageHeader() {
		int messageHeaderLength = messageDefiner.getMessageDefineInt("message.header.length");
		defalutMessageHeaderBytes = new byte[messageHeaderLength];
		ByteArrayUtil.fillWhiteSpace(defalutMessageHeaderBytes);
	}

	public int getMessageFeildValueInteger(String feildName) {
		System.out.println(dataMap);
		return ByteArrayUtil.parseInt((byte[]) dataMap.get(feildName));
	}

	public String getMessageFeildValueString(String feildName) {
		return ByteArrayUtil.parseString((byte[]) dataMap.get(feildName));
	}

	public Message parse(byte[] inputData) throws FileNotFoundException, IOException {
		setIntPutData(inputData);
		messageDefiner.feildNameIterator().forEachRemaining(this::setInputMessageHeader);
		setOutputData(inputData);
		return this;
	}

	private void setIntPutData(byte[] inputData) {
		this.intputData = inputData;
	}

	private void setInputMessageHeader(String feildName) {

		int feildLength = messageDefiner.getFeildAttrValueInteager(feildName,
				DefaultMessageDefiner.FEILD_ATTR_NAME_LENGTH);
		int feildOffset = messageDefiner.getFeildAttrValueInteager(feildName,
				DefaultMessageDefiner.FEILD_ATTR_NAME_OFFSET);
		byte[] feildDataBytes = new byte[feildLength];
		System.arraycopy(intputData, feildOffset, feildDataBytes, 0, feildLength);
		dataMap.put(feildName, feildDataBytes);
		System.out.printf("input : %s[%s]\n", feildName, new String(feildDataBytes));
	}

	public Message makeDefaultMessage() {
		messageDefiner.feildNameIterator().forEachRemaining(name -> {
			setDefaultMessageAtDefaultMessageHeaderBytes(name);
			putDefaultMessageAtDataMap(name);
		});
		setOutputData(defalutMessageHeaderBytes);
		return this;
	}

	private void putDefaultMessageAtDataMap(String feildName) {
		String data = messageDefiner.getFeildAttrValueString(feildName, DefaultMessageDefiner.FEILD_ATTR_NAME_DEFAULT);
		dataMap.put(feildName, data == null ? null : data.getBytes());
	}

	private void setOutputData(byte[] data) {
		this.outputData = data;
	}

	private void setDefaultMessageAtDefaultMessageHeaderBytes(String feildName) {
		int feildLength = getFeildLength.apply(feildName);
		byte[] dataBytes = getDefaultHeaderDataBytes(feildName, feildLength,
				DefaultMessageDefiner.FEILD_ATTR_NAME_DEFAULT);
		System.arraycopy(dataBytes, 0, defalutMessageHeaderBytes, getFeildOffset.apply(feildName),
				feildLength);
		System.out.printf("default : %s[%s]\n", feildName, new String(dataBytes));
	}

	private byte[] getDefaultHeaderDataBytes(String feildName, int feildLength, String string) {
		byte[] dataBytes = new byte[feildLength];
		String feildDefaultValue = (String) messageDefiner.getFeildAttrValueString(feildName,
				DefaultMessageDefiner.FEILD_ATTR_NAME_DEFAULT);
		ByteArrayUtil.fillWhiteSpace(dataBytes);
		if (feildDefaultValue != null) {
			if (feildDefaultValue.contains("sysdate")) {
				feildDefaultValue = TimeUtil.getCurrentTime((String) messageDefiner.getFeildAttrValueString(feildName,
						DefaultMessageDefiner.FEILD_ATTR_NAME_FORMAT));
			}
			dataBytes = feildDefaultValue.getBytes();
		}
		return dataBytes;
	}

	public byte[] getBytes() {
		return outputData;
	}

	public void setMessageFeildValue(String feildName, Object feildValue) {
		if (messageDefiner.isValidFeild(feildName, feildValue)) {
			dataMap.put(feildName, feildValue);
		} else {
			new RuntimeException(feildName.concat(" is not valid "));
		}
	}

	public byte[] getMessageFeildValue(String feildName) {
		byte[] result = (byte[]) dataMap.get(feildName);
		System.out.printf("%s [%s]\n", feildName, new String(result));
		return result;
	}

	public void setMessageBodyData(byte[] bodyDataBytes) {

	}

	public byte[] getMessageBodyData() {
		return null;
	}

}
