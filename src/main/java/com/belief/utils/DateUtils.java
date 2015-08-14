package com.belief.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

	public static String getCurrentTime() {
		return dateFormat.format(new Date());
	}
}
