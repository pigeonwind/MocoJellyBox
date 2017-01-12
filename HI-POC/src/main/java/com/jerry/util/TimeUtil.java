package com.jerry.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

	public static String getCurrentTime(String timeFormat) {
		long time= System.currentTimeMillis();
		SimpleDateFormat dayTime = new SimpleDateFormat(timeFormat);
		return dayTime.format(new Date(time));
	}

}
