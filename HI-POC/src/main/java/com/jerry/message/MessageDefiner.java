package com.jerry.message;

import java.util.Iterator;

public interface MessageDefiner {
	int getMessageDefineInt(String messageDefineName);
	Iterator<String> feildNameIterator();
	String getFeildAttrValueString(String feildName, String feildAttr);
	int getFeildAttrValueInteager(String feildName, String key);
	boolean isValidFeild(String feildName, Object value);
	Iterator<String> getFeildAttrNameIter(String feildName);
	byte[] getDefaultFeildValue(String name);

}