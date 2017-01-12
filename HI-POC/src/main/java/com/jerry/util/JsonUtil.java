package com.jerry.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonUtil {

	public static Object getValueByJsonPath(JSONObject jsonObject, String keyPath) {
		return getJsonObject(jsonObject,Arrays.asList(keyPath.split("\\.")), new Filter<JSONObject>() {
			@Override
			public JSONObject isJSONObject(Object object) {
				JSONObject result=null;;
				if (object instanceof JSONObject) {
					result = (JSONObject) object;
				}
				return result;
			}
		});
	}
	
	private static Object getJsonObject(JSONObject jsonObject, List<String> list,Filter<JSONObject> filter) {
		JSONObject keyObj=null;
		Object result=null;
		for (String key : list) {
			if(isNull(keyObj)){
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

	private static boolean isNull(JSONObject keyObj) {
		return keyObj==null;
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
			if(keyObj==null){
				keyObj=(JSONObject) jsonObj.get(key);
			}else {
				tempObj = keyObj.get(key);
				if (tempObj instanceof JSONObject) {
					keyObj = (JSONObject) tempObj;
				}else if (tempObj instanceof JSONArray){
					JSONArray array= (JSONArray) tempObj;
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
	
	public static Object getValue(JSONObject jsonObject, String key){
		return jsonObject.get(key);
	}

}
