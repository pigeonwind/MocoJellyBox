package com.jerry.message;


import java.io.*;
import java.util.*;

import org.json.simple.*;
import org.json.simple.parser.*;

import com.jerry.context.*;
import com.jerry.util.*;

public class DefultMessageDefiner implements MessageDefiner {
	public static final String FEILD_ATTR_NAME_LENGTH = "length";
	public static final String FEILD_ATTR_NAME_OFFSET = "offset";
	public static final String FEILD_ATTR_NAME_DEFAULT = "default";
	public static final String FEILD_ATTR_NAME_FORMAT = "format";
	public static final String FEILD_ATTR_NAME_DOMAIN = "domain";
	public static final String BODY_ATTR_NAME_OFFSET = "message.body.offset";
	public static final String BODY_ATTR_NAME_ENDOFDATA = "message.body.feild.endofdata";
	private Map<String, JSONObject> messageDefineMap;
	private JSONObject messageObject,dataBodyObject;
	private JSONArray headerFeildArray;
	public DefultMessageDefiner() throws FileNotFoundException, IOException, ParseException{
		this.messageDefineMap = new HashMap<String, JSONObject>();
		messageDefineLookup();
		initMessageDefineMap();
//		System.out.println(messageDefineMap);
	}
	
	/* (non-Javadoc)
	 * @see com.jerry.message.MessageDefiner#getMessageDefineInt(java.lang.String)
	 */
	@Override
	public int getMessageDefineInt(String messageDefineName) {
		return Integer.parseInt((String) JsonUtil.getValueByJsonPath(messageObject, messageDefineName));
	}

	/**
	 * lookup message define
	 */
	private void messageDefineLookup() throws FileNotFoundException, IOException, ParseException {
		headerFeildArray = null;
		dataBodyObject = null;
		{
			// header
			JSONParser parser = new JSONParser();
			String messageDefinePath = ApplicationContext.loadContext().getValue("messageDefine.path");
			messageObject = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(messageDefinePath)));
			headerFeildArray = (JSONArray) JsonUtil.getValueByJsonPath(messageObject, "message.header.field");
			//body
			dataBodyObject=  (JSONObject)JsonUtil.getValueByJsonPath(messageObject, "message.body");
		}
		System.out.println("---------------------");
		System.out.println("messageDefineLookup()");
		System.out.println("**** HEADER *****");
		System.out.println(headerFeildArray);
		System.out.println("**** DATA *****");
		System.out.println(dataBodyObject);
		System.out.println("---------------------");
	}
	
	private void initMessageDefineMap() {
		Iterator<?> headerFeilds = headerFeildArray.iterator();
		JSONObject headerFeild;
		while (headerFeilds.hasNext()) {
			headerFeild = (JSONObject) headerFeilds.next();
			messageDefineMap.put((String) headerFeild.get("name"), headerFeild);
		}
//		messageDefineMap.put(BODY_ATTR_NAME_OFFSET,(JSONObject) JsonUtil.getValueByJsonPath(messageObject, BODY_ATTR_NAME_OFFSET));
//		messageDefineMap.put(BODY_ATTR_NAME_ENDOFDATA,(JSONObject) JsonUtil.getValueByJsonPath(messageObject, BODY_ATTR_NAME_ENDOFDATA));
	}

	/* (non-Javadoc)
	 * @see com.jerry.message.MessageDefiner#keyIterator()
	 */
	@Override
	public Iterator<String> feildNameIterator() {
		return messageDefineMap.keySet().iterator();
	}

	@Override
	public String getFeildAttrValueString(String feildName, String feildAttr) {
		return (String) messageDefineMap.get(feildName).get(feildAttr);
	}

	@Override
	public int getFeildAttrValueInteager(String feildName, String feildAttr) {
		return Integer.parseInt(getFeildAttrValueString(feildName,feildAttr));
	}

	@Override
	public boolean isValidFeild(String feildName, Object value) {
		byte[] valueBytes = (byte[])value;
		return isValidLength(feildName,valueBytes)&isValidDomain(feildName,valueBytes);
	}

	private boolean isValidDomain(String feildName, byte[] valueBytes) {
		boolean result=false;
		String valueString = new String(valueBytes);
		String domainSetString=getFeildAttrValueString(feildName,FEILD_ATTR_NAME_DOMAIN);
		String offsetString=getFeildAttrValueString(feildName,FEILD_ATTR_NAME_OFFSET);
		if(domainSetString==null){ // domain attr is not exsist
			result=true;
		}else{
			result=domainSetString.contains(valueString);
		}
		System.out.printf("%s[%s] | domain : %s | offset: %s | isValid : %s\n",feildName,valueString,domainSetString,offsetString,result?"true":"false");
		return result;
	}

	private boolean isValidLength(String feildName, byte[] value) {
		return getFeildAttrValueInteager(feildName,"length")==value.length?true:false;
	}


	@Override
	public Iterator<String> getFeildAttrNameIter(String feildName) {
		return messageDefineMap.get(feildName).keySet().iterator();
	}

	@Override
	public String getBodyFeildName() {
		return null;
	}

}
