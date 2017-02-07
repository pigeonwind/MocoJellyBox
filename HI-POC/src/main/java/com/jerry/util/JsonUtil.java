package com.jerry.util;

import java.util.*;
import java.util.function.*;

import org.json.simple.*;

public class JsonUtil {
	private static Predicate<Object> isJSONObject = obj -> obj instanceof JSONObject;
	private static Predicate<Object> isJSONArray = obj -> obj instanceof JSONArray;

	private static Function<Object, JSONObject> toJSONObject = obj -> isJSONObject.test(obj) ? (JSONObject) obj : null;
	private static Function<Object, JSONArray> toJSONArray = obj -> isJSONArray.test(obj) ? (JSONArray) obj : null;

	private static BiFunction<String, String, String> bf = RegexMatcher.REGEX_PARSE_OPERATOR.andThen(result -> result.replaceAll("\\[@", "").replace("]", ""));
	private static BiFunction<String, String, String> getQueryConditionKeyFunc = bf.andThen(str -> str.split("=")[0]);
	private static BiFunction<String, String, String> getQueryConditionValueFunc = bf.andThen(str -> str.split("=")[1]);
	private static String regexPattern = "\\[(.*?)\\]";

	public static Object getValueByJsonQueryPath(JSONObject jsonObj, String queryPathWithArray) {
		String[] keys = queryPathWithArray.split("\\.");
		LinkedList<String> keyList = new LinkedList<>(Arrays.asList(keys));
		Object result = jsonObj.get(keyList.pop());
		for (String key : keyList) {
			result = retrive(result, extractKey(key), queryPathWithArray);
		}
		return result;
	}
	
	private static Object retrive(Object result, String key, String queryPathWithArray) {
		String queruConditionKey,queruConditionValue;
		if (isJSONObject.test(result)) {
			result = toJSONObject.apply(result).get(key);// Áõ°¡
		} else if (isJSONArray.test(result)) {
			queruConditionKey = getQueryConditionKeyFunc.apply(queryPathWithArray, regexPattern);
			queruConditionValue = getQueryConditionValueFunc.apply(queryPathWithArray, regexPattern);
			result = (JSONObject) toJSONArray.apply(result).stream().filter(obj -> queruConditionValue.equals(((JSONObject) obj).get(queruConditionKey))).findFirst().get();
		}
		return result;
	}

	private static String extractKey(String key) {
		Predicate<String> isQueryKey = str -> str.contains("[@");
		if (isQueryKey.test(key)) {
			key = getQueryConditionValueFunc.apply(key, regexPattern);
		}
		return key;
	}

}
