package com.jerry.message;

import java.util.Iterator;

public interface MessageDefiner {
	int getMessageDefineInt(String messageDefineName);
	Iterator<String> getFeildNameIterator();
	String getFeildAttrValueString(String feildName, String feildAttr);
	int getFeildAttrValueInteager(String feildName, String key);
	Iterator<String> getFeildAttrNameIter(String feildName);
	byte[] getDefaultFeildValue(String name);

	int getDefaultHeaderSize();

	int getDefaultBodySize();

	int getBodyOffset();
}