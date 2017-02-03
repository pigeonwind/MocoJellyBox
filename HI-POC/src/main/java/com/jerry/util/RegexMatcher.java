package com.jerry.util;

import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatcher {
	public static final BiFunction<String, String,String> REGEX_PARSE_OPERATOR = (line,columnRegex) -> {
		Matcher matcher = Pattern.compile(columnRegex).matcher(line);
		String result;
		if (matcher.find()) {
			int beginOffset = matcher.start();
			int endOffset = matcher.end();
			result = line.substring(beginOffset, endOffset);
		} else {
			result = "null";
		}
		System.out.printf("line[%s]\ncolumnRegex[%s]\n",line,columnRegex);
		return result;
	};
}
