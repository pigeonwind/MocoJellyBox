package com.jerry.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
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
		static BiFunction<JSONObject, String, Object> getValue = (sorceJSONObject, key) -> sorceJSONObject.get(key);

	public static Object getValueByJsonPath(JSONObject jsonObject, String keyPath) {
		return getJsonObject(jsonObject,Arrays.asList(keyPath.split("\\.")), obj-> isJSONObject.test(obj)?(JSONObject)obj:null );
	}

	private static Object getJsonObject(JSONObject jsonObject, List<String> keyList,Filter<JSONObject> filter) {
		JSONObject keyObj=null;
		Object result=null;

		//TODO: keyList의 key를 소모하면서 결과를 얻는 방법 찾기


		for (String key : keyList) {
			if(isNull.test(keyObj)){
				keyObj=getJSONObject(jsonObject,key);
			}else {
				result = keyObj.get(key);
				keyObj = filter.isJSONObject(result);
			}
			System.out.printf("key[%s], keyObj [%s], result [%s]\n",key,keyObj,result);
		}
		System.out.println("******************");
		System.out.println(result);
		System.out.println("******************");
		return result;
	}

	private static JSONObject getJSONObject(JSONObject jsonObject, String key) {
		return (JSONObject) jsonObject.get(key);
	}


	public static Object getValueByJsonPath(JSONObject jsonObj, String queryPathWithArray, String retriveKey) {
		String[] queryPathWithArrays = queryPathWithArray.split("\\?");
		
		String[] keys =queryPathWithArrays[0].split("\\.");
		String queryFeild=keys[keys.length-1];
		String queruCondition = queryPathWithArrays[1];
		
		JSONObject keyObj=null;
		Object tempObj=null,result=null;
		String queryFeildCandidate;
		
		for (String key : keys) {
			System.out.println(key);
			if(isNull.test(keyObj)){
				keyObj=(JSONObject) jsonObj.get(key);
			}else {
				tempObj = keyObj.get(key);
				if (isJSONObject.test(tempObj)) {
					keyObj = (JSONObject) tempObj;
				}else if (isJSONArray.test(tempObj)){
					JSONArray array= (JSONArray) tempObj;
					//Todo : reduce 작업하기
//					array.stream().reduce()

					Iterator<?> jsonIter =array.iterator();
					while (jsonIter.hasNext()) {
						JSONObject jsonObject = (JSONObject) jsonIter.next();
						queryFeildCandidate = (String) jsonObject.get(queryFeild);
						if(queruCondition.equals(queryFeildCandidate)){
							result = jsonObject.get(retriveKey);
						}
					}
				}
			}
		}
		return result;
	}

}
