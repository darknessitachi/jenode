package com.zving.framework.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DateUtil {
	public static final String Format_Date = "yyyy-MM-dd";
	public static final String Format_Time = "HH:mm:ss";
	public static final String Format_LastModified = "EEE, dd MMM yyyy HH:mm:ss";
	public static final String Format_DateTime = "yyyy-MM-dd HH:mm:ss";
	private static ThreadLocal<Formats> formats = new ThreadLocal();

	public static SimpleDateFormat getFormat(String format) {
		if (formats.get() == null) {
			formats.set(new Formats());
		}
		if (format.equals("yyyy-MM-dd")) {
			return getDefaultDateFormat();
		}
		if (format.equals("HH:mm:ss")) {
			return getDefaultTimeFormat();
		}
		if (format.equals("yyyy-MM-dd HH:mm:ss")) {
			return getDefaultDateTimeFormat();
		}
		if (format.equals("EEE, dd MMM yyyy HH:mm:ss")) {
			return getLastModifiedFormat();
		}
		if (((Formats) formats.get()).Others == null) {
			((Formats) formats.get()).Others = new HashMap();
		}
		if (!((Formats) formats.get()).Others.containsKey(format)) {
			((Formats) formats.get()).Others.put(format, new SimpleDateFormat(
					format, Locale.ENGLISH));
		}
		return (SimpleDateFormat) ((Formats) formats.get()).Others.get(format);
	}

	public static SimpleDateFormat getDefaultDateTimeFormat() {
		if (formats.get() == null) {
			formats.set(new Formats());
		}
		if (((Formats) formats.get()).DateTime == null) {
			((Formats) formats.get()).DateTime = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		}
		return ((Formats) formats.get()).DateTime;
	}

	public static SimpleDateFormat getDefaultDateFormat() {
		if (formats.get() == null) {
			formats.set(new Formats());
		}
		if (((Formats) formats.get()).DateOnly == null) {
			((Formats) formats.get()).DateOnly = new SimpleDateFormat(
					"yyyy-MM-dd", Locale.ENGLISH);
		}
		return ((Formats) formats.get()).DateOnly;
	}

	public static SimpleDateFormat getDefaultTimeFormat() {
		if (formats.get() == null) {
			formats.set(new Formats());
		}
		if (((Formats) formats.get()).TimeOnly == null) {
			((Formats) formats.get()).TimeOnly = new SimpleDateFormat(
					"HH:mm:ss", Locale.ENGLISH);
		}
		return ((Formats) formats.get()).TimeOnly;
	}

	public static SimpleDateFormat getLastModifiedFormat() {
		if (formats.get() == null) {
			formats.set(new Formats());
		}
		if (((Formats) formats.get()).LastModified == null) {
			((Formats) formats.get()).LastModified = new SimpleDateFormat(
					"EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
		}
		return ((Formats) formats.get()).LastModified;
	}

	public static String getCurrentDate() {
		return getDefaultDateFormat().format(new Date());
	}

	public static String getCurrentDate(String format) {
		return getFormat(format).format(new Date());
	}

	public static String getCurrentTime() {
		return getDefaultTimeFormat().format(new Date());
	}

	public static String getCurrentTime(String format) {
		SimpleDateFormat t = new SimpleDateFormat(format);
		return t.format(new Date());
	}

	public static String getCurrentDateTime() {
		return getDefaultDateTimeFormat().format(new Date());
	}

	public static int getDayOfWeek() {
		Calendar cal = Calendar.getInstance();
		return cal.get(7);
	}

	public static int getDayOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(7);
	}

	public static int getDayOfMonth() {
		Calendar cal = Calendar.getInstance();
		return cal.get(5);
	}

	public static int getDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(5);
	}

	public static int getMaxDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.getActualMaximum(5);
	}

	public static String getFirstDayOfMonth(String date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(parse(date));
		cal.set(5, 1);
		return getDefaultDateFormat().format(cal.getTime());
	}

	public static int getDayOfYear() {
		Calendar cal = Calendar.getInstance();
		return cal.get(6);
	}

	public static int getDayOfYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(6);
	}

	public static int getDayOfWeek(String date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(parse(date));
		return cal.get(7);
	}

	public static int getDayOfMonth(String date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(parse(date));
		return cal.get(5);
	}

	public static int getDayOfYear(String date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(parse(date));
		return cal.get(6);
	}

	public static String getCurrentDateTime(String format) {
		return getFormat(format).format(new Date());
	}

	public static String toString(Date date) {
		if (date == null) {
			return "";
		}
		return getDefaultDateFormat().format(date);
	}

	public static String toDateTimeString(Date date) {
		if (date == null) {
			return "";
		}
		return getDefaultDateTimeFormat().format(date);
	}

	public static String toString(Date date, String format) {
		if (date == null) {
			return "";
		}
		return getFormat(format).format(date);
	}

	public static String toTimeString(Date date) {
		if (date == null) {
			return "";
		}
		return getDefaultTimeFormat().format(date);
	}

	public static int compare(String date1, String date2) {
		return compare(date1, date2, "yyyy-MM-dd");
	}

	public static int compareTime(String time1, String time2) {
		return compareTime(time1, time2, "HH:mm:ss");
	}

	public static int compare(String date1, String date2, String format) {
		Date d1 = parse(date1, format);
		Date d2 = parse(date2, format);
		return d1.compareTo(d2);
	}

	public static int compareTime(String time1, String time2, String format) {
		String[] arr1 = time1.split(":");
		String[] arr2 = time2.split(":");
		if (arr1.length < 2) {
			throw new RuntimeException("Invalid time:" + time1);
		}
		if (arr2.length < 2) {
			throw new RuntimeException("Invalid time:" + time2);
		}
		int h1 = Integer.parseInt(arr1[0]);
		int m1 = Integer.parseInt(arr1[1]);
		int h2 = Integer.parseInt(arr2[0]);
		int m2 = Integer.parseInt(arr2[1]);
		int s1 = 0;
		int s2 = 0;
		if (arr1.length == 3) {
			s1 = Integer.parseInt(arr1[2]);
		}
		if (arr2.length == 3) {
			s2 = Integer.parseInt(arr2[2]);
		}
		if ((h1 < 0) || (h1 > 23) || (m1 < 0) || (m1 > 59) || (s1 < 0)
				|| (s1 > 59)) {
			throw new RuntimeException("Invalid time:" + time1);
		}
		if ((h2 < 0) || (h2 > 23) || (m2 < 0) || (m2 > 59) || (s2 < 0)
				|| (s2 > 59)) {
			throw new RuntimeException("Invalid time:" + time2);
		}
		if (h1 != h2) {
			return h1 > h2 ? 1 : -1;
		}
		if (m1 == m2) {
			if (s1 == s2) {
				return 0;
			}
			return s1 > s2 ? 1 : -1;
		}

		return m1 > m2 ? 1 : -1;
	}

	public static boolean isTime(String time) {
		String[] arr = time.split(":");
		if (arr.length < 2)
			return false;
		try {
			int h = Integer.parseInt(arr[0]);
			int m = Integer.parseInt(arr[1]);
			int s = 0;
			if (arr.length == 3) {
				s = Integer.parseInt(arr[2]);
			}
			if ((h < 0) || (h > 23) || (m < 0) || (m > 59) || (s < 0)
					|| (s > 59))
				return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean isDate(String date) {
		String[] arr = date.split("-");
		if (arr.length < 3)
			return false;
		try {
			int y = Integer.parseInt(arr[0]);
			int m = Integer.parseInt(arr[1]);
			int d = Integer.parseInt(arr[2]);
			if ((y < 0) || (m > 12) || (m < 0) || (d < 0) || (d > 31))
				return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean isDateTime(String str) {
		if (StringUtil.isEmpty(str)) {
			return false;
		}
		if (str.indexOf(" ") > 0) {
			String[] arr = str.split(" ");
			if (arr.length == 2) {
				return (isDate(arr[0])) && (isTime(arr[1]));
			}
			return false;
		}

		return isDate(str);
	}

	public static boolean isWeekend(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int t = cal.get(7);
		if ((t == 7) || (t == 1)) {
			return true;
		}
		return false;
	}

	public static boolean isWeekend(String str) {
		return isWeekend(parse(str));
	}

	public static Date parse(String str) {
		if (StringUtil.isEmpty(str))
			return null;
		try {
			return getDefaultDateFormat().parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Date parse(String str, String format) {
		if (StringUtil.isEmpty(str))
			return null;
		try {
			SimpleDateFormat t = new SimpleDateFormat(format);
			return t.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Date parseLastModified(String str) {
		if (StringUtil.isEmpty(str))
			return null;
		try {
			return getLastModifiedFormat().parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Date parseDateTime(String str) {
		if (StringUtil.isEmpty(str)) {
			return null;
		}
		if (str.length() <= 10)
			return parse(str);
		try {
			return getDefaultDateTimeFormat().parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Date parseDateTime(String str, String format) {
		if (StringUtil.isEmpty(str))
			return null;
		try {
			SimpleDateFormat t = new SimpleDateFormat(format);
			return t.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Date addMillisecond(Date date, int count) {
		return new Date(date.getTime() + 1L * count);
	}

	public static Date addSecond(Date date, int count) {
		return new Date(date.getTime() + 1000L * count);
	}

	public static Date addMinute(Date date, int count) {
		return new Date(date.getTime() + 60000L * count);
	}

	public static Date addHour(Date date, int count) {
		return new Date(date.getTime() + 3600000L * count);
	}

	public static Date addDay(Date date, int count) {
		return new Date(date.getTime() + 86400000L * count);
	}

	public static Date addWeek(Date date, int count) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(3, count);
		return c.getTime();
	}

	public static Date addMonth(Date date, int count) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(2, count);
		return c.getTime();
	}

	public static Date addYear(Date date, int count) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(1, count);
		return c.getTime();
	}

	public static String convertChineseNumber(String strDate) {
		strDate = StringUtil.replaceEx(strDate, "一十一", "11");
		strDate = StringUtil.replaceEx(strDate, "一十二", "12");
		strDate = StringUtil.replaceEx(strDate, "一十三", "13");
		strDate = StringUtil.replaceEx(strDate, "一十四", "14");
		strDate = StringUtil.replaceEx(strDate, "一十五", "15");
		strDate = StringUtil.replaceEx(strDate, "一十六", "16");
		strDate = StringUtil.replaceEx(strDate, "一十七", "17");
		strDate = StringUtil.replaceEx(strDate, "一十八", "18");
		strDate = StringUtil.replaceEx(strDate, "一十九", "19");
		strDate = StringUtil.replaceEx(strDate, "二十一", "21");
		strDate = StringUtil.replaceEx(strDate, "二十二", "22");
		strDate = StringUtil.replaceEx(strDate, "二十三", "23");
		strDate = StringUtil.replaceEx(strDate, "二十四", "24");
		strDate = StringUtil.replaceEx(strDate, "二十五", "25");
		strDate = StringUtil.replaceEx(strDate, "二十六", "26");
		strDate = StringUtil.replaceEx(strDate, "二十七", "27");
		strDate = StringUtil.replaceEx(strDate, "二十八", "28");
		strDate = StringUtil.replaceEx(strDate, "二十九", "29");
		strDate = StringUtil.replaceEx(strDate, "十一", "11");
		strDate = StringUtil.replaceEx(strDate, "十二", "12");
		strDate = StringUtil.replaceEx(strDate, "十三", "13");
		strDate = StringUtil.replaceEx(strDate, "十四", "14");
		strDate = StringUtil.replaceEx(strDate, "十五", "15");
		strDate = StringUtil.replaceEx(strDate, "十六", "16");
		strDate = StringUtil.replaceEx(strDate, "十七", "17");
		strDate = StringUtil.replaceEx(strDate, "十八", "18");
		strDate = StringUtil.replaceEx(strDate, "十九", "19");
		strDate = StringUtil.replaceEx(strDate, "十", "10");
		strDate = StringUtil.replaceEx(strDate, "二十", "20");
		strDate = StringUtil.replaceEx(strDate, "三十", "20");
		strDate = StringUtil.replaceEx(strDate, "三十一", "31");
		strDate = StringUtil.replaceEx(strDate, "零", "0");
		strDate = StringUtil.replaceEx(strDate, "○", "0");
		strDate = StringUtil.replaceEx(strDate, "一", "1");
		strDate = StringUtil.replaceEx(strDate, "二", "2");
		strDate = StringUtil.replaceEx(strDate, "三", "3");
		strDate = StringUtil.replaceEx(strDate, "四", "4");
		strDate = StringUtil.replaceEx(strDate, "五", "5");
		strDate = StringUtil.replaceEx(strDate, "六", "6");
		strDate = StringUtil.replaceEx(strDate, "七", "7");
		strDate = StringUtil.replaceEx(strDate, "八", "8");
		strDate = StringUtil.replaceEx(strDate, "九", "9");
		return strDate;
	}

	private static class Formats {
		private SimpleDateFormat DateOnly;
		private SimpleDateFormat TimeOnly;
		private SimpleDateFormat DateTime;
		private SimpleDateFormat LastModified;
		private HashMap<String, SimpleDateFormat> Others;
	}
}