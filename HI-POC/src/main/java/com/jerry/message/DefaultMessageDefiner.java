package com.jerry.message;


import java.io.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.json.simple.*;
import org.json.simple.parser.*;

import com.jerry.context.*;
import com.jerry.util.*;

import jeus.util.logging.SystemOutRecord;

public class DefaultMessageDefiner implements MessageDefiner {
	public static final String FEILD_ATTR_NAME_LENGTH = "length";
	public static final String FEILD_ATTR_NAME_OFFSET = "offset";
	public static final String FEILD_ATTR_NAME_DEFAULT = "default";
	public static final String FEILD_ATTR_NAME_FORMAT = "format";
	public static final String FEILD_ATTR_NAME_DOMAIN = "domain";
	public static final String BODY_ATTR_NAME_OFFSET = "message.body.offset";
	public static final String BODY_ATTR_NAME_ENDOFDATA = "message.body.feild.endofdata";
	private static final Object FEILD_ATTR_NAME_NAME = "name";
	private Map<String, JSONObject> messageDefineMap;
	private JSONObject messageObject,bodyFeild;
	private JSONArray headerFeildArray;
	
	Function<String,Map<String,Object>> getObject;
	BiFunction<Map<String, Object>,String , String> getValue;
	Function<String,Integer> getFeildLength;
	Function<String, Integer> toInteger;
	Function<String,Integer> getFeildOffset;
	Function<String,String> getFeildDefault,getFeildDefaultCurrentTime;
	Function<String,byte[]> whiteSpaceByteArraySupplier;
	Predicate<String> isNotNull;
	Predicate<String> isContainSysdateString;
	public DefaultMessageDefiner() throws FileNotFoundException, IOException, ParseException{
		this.messageDefineMap = new HashMap<String, JSONObject>();
		messageDefineLookup();
		initMessageDefineMap();
		initFunction();
	}
	
	private void initFunction() {
		getObject= feildName-> messageDefineMap.get(feildName);
		getValue = (map,attrName)->(String) map.get(attrName);
		toInteger = intString-> Integer.parseInt(intString);
		getFeildLength = getObject.andThen( map->getValue.apply(map, FEILD_ATTR_NAME_LENGTH) ).andThen(toInteger);
		getFeildOffset = getObject.andThen( map->getValue.apply(map, FEILD_ATTR_NAME_OFFSET) ).andThen(toInteger);
		getFeildDefault = getObject.andThen( map->(String) getValue.apply(map, FEILD_ATTR_NAME_DEFAULT));
		getFeildDefaultCurrentTime = getObject.andThen( map->(String) getValue.apply(map, FEILD_ATTR_NAME_FORMAT)).andThen(TimeUtil::getCurrentTime);
		
		whiteSpaceByteArraySupplier = getFeildLength.andThen(byte[]::new).andThen(ByteArrayUtil::fillWhiteSpaceReturn);
		isNotNull = (obj)->obj!=null;
		isContainSysdateString = str->str.contains("sysdate");
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
		{
			JSONParser parser = new JSONParser();
			String messageDefinePath = ApplicationContext.loadContext().getValue("messageDefine.path");
			messageObject = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(messageDefinePath)));
			// header
			headerFeildArray = (JSONArray) JsonUtil.getValueByJsonPath(messageObject, "message.header.field");
			//body
			bodyFeild =  (JSONObject) JsonUtil.getValueByJsonPath(messageObject, "message.body.field");
			
		}
		System.out.println("---------------------");
		System.out.println("messageDefineLookup()");
		System.out.println("**** HEADER *****");
		System.out.println(headerFeildArray);
		System.out.println("**** DATA *****");
		System.out.println(bodyFeild);
		System.out.println("---------------------");
	}
	
	private void initMessageDefineMap() {
		Consumer<JSONObject> putAtMessageDefineMap =(headerFeildObject)->messageDefineMap.put((String) headerFeildObject.get(FEILD_ATTR_NAME_NAME), headerFeildObject);
		headerFeildArray.forEach(putAtMessageDefineMap);
		putAtMessageDefineMap.accept(bodyFeild);
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
	public byte[] getDefaultFeildValue(String name) {
		byte[] container = whiteSpaceByteArraySupplier.apply(name);
		String defaultString=getFeildDefault.apply(name);
		if(isNotNull.test(defaultString)) {
			System.arraycopy(isContainSysdateString.test(defaultString)?getFeildDefaultCurrentTime.apply(name).getBytes():defaultString.getBytes() , 0, container, 0, container.length);
		};
		return container;
	}


}