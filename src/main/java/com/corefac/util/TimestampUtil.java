package com.corefac.util;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by john on 2017/6/19.
 * 获取格式化时间的工具类
 */
public class TimestampUtil implements Serializable {
    private static final long serialVersionUID = 5836254631785464360L;

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date getDatetime() {
        return getCalendar().getTime();
    }

    public static Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"), Locale.SIMPLIFIED_CHINESE);

        return calendar;
    }

    // 返回可以增加秒数的Date
    public static Date getDatetime(int second) {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.SECOND, second);

        return calendar.getTime();
    }

    // 返回指定日期增加秒数的Date
    public static Date getDatetime(Date date, int second) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, second);

        return calendar.getTime();
    }

    // 返回指定日期的Date
    public static Date getDatetime(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = getCalendar();
        if(year > 0) calendar.set(Calendar.YEAR, year);
        month -= 1; // 日历的月份从0开始计算
        if(month > 0) calendar.set(Calendar.MONTH, month);
        if(day > 0) calendar.set(Calendar.DAY_OF_MONTH, day);
        if(hour >= 0) calendar.set(Calendar.HOUR_OF_DAY, hour);
        if(minute >= 0) calendar.set(Calendar.MINUTE, minute);
        if(second >= 0) calendar.set(Calendar.SECOND, second);

        return calendar.getTime();
    }

    public static String getDateDay( int day) {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.DAY_OF_WEEK, day);
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String getDateMouth(int mouth) {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.MONTH, mouth);
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String getDateYear(int year) {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.YEAR, year);
        return simpleDateFormat.format(calendar.getTime());
    }

    // 返回今天零点的Date
    public static Date getZeroDatetime() {
        Calendar calendar = getCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTime();
    }

    public static String getDatetimeString() {
        return simpleDateFormat.format(getDatetime());
    }

    public static String getDatetimeString(String format) {
        return new SimpleDateFormat(format).format(getDatetime());
    }

    public static String formatDatetime(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static String formatDatetime(Date date) {
        return simpleDateFormat.format(date);
    }

    public static String formatDatetime(Calendar calendar, String format) {
        return formatDatetime(calendar.getTime(), format);
    }

    public static String formatDatetime(Calendar calendar) {
        return formatDatetime(calendar.getTime());
    }

    public static String formatDatetime() {
        return formatDatetime(getDatetime());
    }

    public static Date parseDatetime(String datetime, String format) {
        try {
            return new SimpleDateFormat(format).parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date parseDatetime(String datetime) {
        try {
            return simpleDateFormat.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
