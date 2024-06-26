package com.xluo.lib_base.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    public static SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat simpleFormatter2 = new SimpleDateFormat("yyyy年MM月dd日");
    public static SimpleDateFormat simpleFormatter3 = new SimpleDateFormat("MM月dd日 HH:mm");
    public static SimpleDateFormat simpleFormatter4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 将int类型数字转换成时分秒毫秒的格式数据
     *
     * @param time long类型的数据
     * @return HH:mm:ss.SSS
     */
    public static String msecToTime(long time) {
        String timeStr = null;
        long hour = 0;
        long minute = 0;
        long second = 0;
        long millisecond = 0;
        if (time <= 0)
            return "00:00:00";
        else {
            second = time / 1000;
            minute = second / 60;
            millisecond = time % 1000;
            if (second < 60) {
                timeStr = "00:00:" + unitFormat(second);
            } else if (minute < 60) {
                second = second % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {// 数字>=3600 000的时候
                hour = minute / 60;
                minute = minute % 60;
                second = second - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(long i) {// 时分秒的格式转换
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + i;
        else
            retStr = "" + i;
        return retStr;
    }

    public static String unitFormat2(long i) {// 毫秒的格式转换
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "00" + i;
        else if (i >= 10 && i < 100) {
            retStr = "0" + i;
        } else
            retStr = "" + i;
        return retStr;
    }


    public static String dateString(Long time) {
        Date date = new Date(time);
        return simpleFormatter.format(date);
    }

    public static String dateChineseString(Long time) {
        Date date = new Date(time);
        return simpleFormatter2.format(date);
    }

    public static String getMonthAndDayTime(Long time) {
        Date date = new Date(time);
        return simpleFormatter3.format(date);
    }

    /**
     * 获取详细的事件 年月日 时分秒
     *
     * @param time
     * @return
     */
    public static String getDetailTime(Long time) {
        Date date = new Date(time);
        return simpleFormatter4.format(date);
    }

    public static long getLongTime(String time) {
        try {
            return simpleFormatter4.parse(time).getTime();
        } catch (ParseException e) {
            return 0;
        }
    }


}
