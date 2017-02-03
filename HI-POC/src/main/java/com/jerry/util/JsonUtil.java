package com.jerry.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonUtil {
		static Predicate<Object> isNull = obj -> obj == null;
		static Predicate<Object> isJSONObject = obj -> obj instanceof JSONObject;
		static Predicate<Object> isJSONArray = obj -> obj instanceof JSONArray;
		static Predicate<Object> isString = obj -> obj instanceof String;
		
		static Function<Object,JSONObject> toJSONObject = obj-> isJSONObject.test(obj)?(JSONObject)obj:null ;
		static Function<Object,JSONArray> toJSONArray = obj-> isJSONArray.test(obj)?(JSONArray)obj:null ;
		
		static BiFunction<JSONObject, String, Object> getValue = (sorceJSONObject, key) -> sorceJSONObject.get(key);

	public static Object getValueByJsonPath(JSONObject jsonObject, String keyPath) {
		
		return getJsonObject(jsonObject,new LinkedList<>(Arrays.asList(keyPath.split("\\."))));
	}

	private static Object getJsonObject(JSONObject jsonObject, LinkedList<String> keyList) {
		Object result=null;
		JSONObject keyObj=(JSONObject) jsonObject.get(keyList.pop());;
		//TODO: keyList의 key를 소모하면서 결과를 얻는 방법 찾기
		
		for (String key : keyList) {
			result = keyObj.get(key);
			keyObj = toJSONObject.apply(result);
			System.out.printf("key[%s], keyObj [%s], result [%s]\n",key,keyObj,result);
		}
		System.out.println("******************");
		System.out.println(result);
		System.out.println("******************");
		return result;
	}

	public static Object getValueByJsonQueryPath(JSONObject jsonObj, String queryPathWithArray, String retriveKey) {
		String[] queryPathWithArrays = queryPathWithArray.split("\\?");
		
		String[] keys =queryPathWithArrays[0].split("\\.");
		LinkedList<String> keyList = new LinkedList<>(Arrays.asList(keys));
		String queryFeild=keys[keys.length-1];
		String queruCondition = queryPathWithArrays[1];
		
		JSONObject keyObj=null;
		Object tempObj=null,result=null;
		Predicate<JSONObject> isMatched = obj-> queruCondition.equals(obj.get(queryFeild));
		
		keyObj= (JSONObject) jsonObj.get(keyList.pop());
		for (String key : keyList) {
//			System.out.println(key);
			tempObj = keyObj.get(key);
			if (isJSONObject.test(tempObj)) {
				keyObj = toJSONObject.apply(tempObj);
			}else if (isJSONArray.test(tempObj)){
				result = toJSONArray.apply(tempObj).stream().filter(isMatched).map(obj->toJSONObject.apply(obj).get(retriveKey)).findFirst().get();
			}
			
		}
		return result;
	}
	public static Object getValueByJsonQueryPath(JSONObject jsonObj, String queryPathWithArray){
		String regexPattern = "\\[(.*?)\\]";
		
		String queruCondition = RegexMatcher.REGEX_PARSE_OPERATOR.apply(queryPathWithArray, regexPattern);
		
		System.out.printf("queryPathWithArray[%s]\n",queryPathWithArray);
		System.out.printf("queruCondition[%s]\n",queruCondition);
		
		String[] queryPathWithArrays = queryPathWithArray.split(regexPattern);
		
		String[] keys =queryPathWithArrays[0].split("\\.");
		LinkedList<String> keyList = new LinkedList<>(Arrays.asList(keys));
		String queryFeild=keys[keys.length-1];
//		String queruCondition = queryPathWithArrays[1];
		String retriveKey=null;
		
		JSONObject keyObj=null;
		Object tempObj=null,result=null;
		Predicate<JSONObject> isMatched = obj-> queruCondition.equals(obj.get(queryFeild));
		keyObj= (JSONObject) jsonObj.get(keyList.pop());
		
		for (String key : keyList) {
//			System.out.println(key);
			tempObj = keyObj.get(key);
			if (isJSONObject.test(tempObj)) {
				keyObj = toJSONObject.apply(tempObj);
			}else if (isJSONArray.test(tempObj)){
				result = toJSONArray.apply(tempObj).stream().filter(isMatched).map(obj->toJSONObject.apply(obj).get(retriveKey)).findFirst().get();
			}
		}
		return result;
	}

}
