package com.hyphenate.easeui.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EasyDateUtils {
	// 常用的格式
	public static final String DateFormat_Only_Date = "MM-dd";
	public static final String DateFormat_Date = "yyyy-MM-dd";
	public static final String DateFormat_Time = "HH:mm";
	public static final String DateFormat_DateTime = "MM-dd HH:mm";
	public static final String DateFormat_YearTime = "yyyy-MM-dd HH:mm";
	// 一分钟
	public static final long Time_Of_Minute = 60 * 1000;
	// 一小时
	public static final long Time_Of_Hour = 60 * Time_Of_Minute;
	// 一天
	public static final long Time_Of_Day = 24 * Time_Of_Hour;
	public static String getFormattedTimeInterval(long time) {
		Date date = new Date(time);
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		//Calendar.HOUR获取的是12小时制
		int hour = cal.get(Calendar.HOUR_OF_DAY);// 小时
		int minute = cal.get(Calendar.MINUTE);// 分
		int second = cal.get(Calendar.SECOND);// 秒
		Calendar calMsg = Calendar.getInstance();
		calMsg.setTime(date);
		long todayZero = System.currentTimeMillis() - hour * Time_Of_Hour
				- minute * Time_Of_Minute - second * 1000;
		if ((todayZero - time) < 0 ) {
			if((todayZero - time)> (-24* Time_Of_Hour)){
				return format(time, DateFormat_Time);
			}else{
				return "";
			}
		}
		if (todayZero - time < (24* Time_Of_Hour)) {
			return "昨天 " + format(time, DateFormat_Time);
		}
		return format(time, DateFormat_DateTime);
	}
	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String format(long time, String format) {
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		return sdf.format(date);
	}
}
