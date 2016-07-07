package com.android.biubiu.component.util;

import android.content.Context;

import com.android.biubiu.application.BiubiuApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cc.imeetu.iu.R;

public class DateUtils {
    private static final SimpleDateFormat DATE_FORMAT_ALL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.getDefault());
    private static final SimpleDateFormat DATE_FORMAT_YEAR = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat DATE_FORMAT_MONTH = new SimpleDateFormat("MM-dd", Locale.getDefault());
    private static final SimpleDateFormat DATE_FORMAT_HOUR_MIN = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static SimpleDateFormat sf = null;

    private static String sSoon = null, sMin = null, sHour = null, sDay = null, sYesterday = null, sBeforeYesterday;

    private static void init(Context context) {
        if (sSoon == null) {
            sSoon = context.getResources().getString(R.string.date_soon);
        }
        if (sMin == null) {
            sMin = context.getResources().getString(R.string.date_min);
        }
        if (sHour == null) {
            sHour = context.getResources().getString(R.string.date_hour);
        }
        if (sDay == null) {
            sDay = context.getResources().getString(R.string.date_day);
        }
        if (sYesterday == null) {
            sYesterday = context.getResources().getString(R.string.date_yesterday);
        }
        if (sBeforeYesterday == null) {
            sBeforeYesterday = context.getResources().getString(R.string.date_before_yesterday);
        }
    }

    /* 获取系统时间 格式为："yyyy/MM/dd " */
    public static String getCurrentDate() {
        Date d = new Date();
        sf = new SimpleDateFormat("yyyy年MM月dd日");
        return sf.format(d);
    }

    /* 时间戳转换成字符窜 */
    public static String getDateToString(long time) {
        Date d = new Date(time);
        sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(d);
    }

    /* 将字符串转为时间戳 */
    public static long getStringToDate(String time) {
        sf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }


    /* 时间戳转换活动开始时间
     * */
    public static String getActivityTimeStart(long time) {
        Date d = new Date(time);
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(d);
    }

    /*
     * 时间戳转换活动结束时间
     * */
    public static String getActivityTimeStop(long time) {
        Date d = new Date(time);
        sf = new SimpleDateFormat("HH:mm:ss");
        return sf.format(d);
    }

    public static String getDateFormatInList(Context context, long time) {
        init(context);
        String result = "";
        try {
            Date date = DATE_FORMAT_ALL.parse(getActivityTimeStart(time));
            long now = System.currentTimeMillis();
            long other = date.getTime();
            long l = now - other;
            long day = l / (24 * 60 * 60 * 1000);
            long hour = (l / (60 * 60 * 1000) - day * 24);
            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            // System.out.println(""+day+"天"+hour+"小时"+min+"分"+s+"秒");
            if (day >= 7) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int other_year = cal.get(Calendar.YEAR);
                cal.setTime(new Date(now));
                int now_year = cal.get(Calendar.YEAR);
                // System.out.println(other_year + "---" + now_year);
                if (now_year > other_year) {
                    result = DATE_FORMAT_YEAR.format(date);
                } else {
                    result = DATE_FORMAT_MONTH.format(date);
                }
            } else if (day <= 6 && day > 0) {
                result = day + sDay;
            } else if (hour > 0) {
                result = hour + sHour;
            } else if (min > 0) {
                result = min + sMin;
            } else {
                result = sSoon;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getDateFormatInList2(Context context, long time) {
        init(context);
        String result = "";
        try {
            Date date = DATE_FORMAT_ALL.parse(getActivityTimeStart(time));
            Date nowDate = DATE_FORMAT_ALL.parse(getActivityTimeStart(System.currentTimeMillis()));
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int other_year = cal.get(Calendar.YEAR);
            int otherDay = cal.get(Calendar.DAY_OF_YEAR);
            cal.setTime(nowDate);
            int now_year = cal.get(Calendar.YEAR);
            int nowDay = cal.get(Calendar.DAY_OF_YEAR);
            if (now_year > other_year) {
                result = DATE_FORMAT_YEAR.format(date);
            } else {
                int day = nowDay - otherDay;
                if (day > 2) {
                    result = DATE_FORMAT_MONTH.format(date);
                } else if (day == 2) {
                    result = sBeforeYesterday + DATE_FORMAT_HOUR_MIN.format(date);
                } else if (day == 1) {
                    result = sYesterday + DATE_FORMAT_HOUR_MIN.format(date);
                } else {
                    result = DATE_FORMAT_HOUR_MIN.format(date);
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getDateFormatReceiveBiu(Context context, long time) {
        init(context);
        String result = "";
        try {
            Date date = DATE_FORMAT_ALL.parse(getActivityTimeStart(time));
            long now = System.currentTimeMillis();
            long other = date.getTime();
            long l = now - other;
            long day = l / (24 * 60 * 60 * 1000);
            long hour = (l / (60 * 60 * 1000) - day * 24);
            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            // System.out.println(""+day+"天"+hour+"小时"+min+"分"+s+"秒");
            if (day > 0) {
                result = day + "day";
            } else if (hour > 0) {
                result = hour + "h";
            } else if (min > 0) {
                result = min + "min";
            } else {
                result = sSoon;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 时间转化为显示字符串
     *
     * @param timeStamp 单位为秒
     */
    public static String getTimeStr(long timeStamp) {
        if (timeStamp == 0) return "";
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(timeStamp * 1000);
        Date currenTimeZone = inputTime.getTime();
        Calendar calendar = Calendar.getInstance();
        if (calendar.before(inputTime)) {
            //当前时间在输入时间之前
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + "-" + "MM" + "-" + "dd");
            return sdf.format(currenTimeZone);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(currenTimeZone);
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (calendar.before(inputTime)) {
            return BiubiuApplication.getInstance().getResources().getString(R.string.date_yesterday);
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            if (calendar.before(inputTime)) {
                SimpleDateFormat sdf = new SimpleDateFormat("M" + "-" + "d");
                return sdf.format(currenTimeZone);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + "-" + "MM" + "-" + "dd");
                return sdf.format(currenTimeZone);

            }

        }

    }

    /**
     * 时间转化为聊天界面显示字符串
     *
     * @param timeStamp 单位为秒
     */
    public static String getChatTimeStr(long timeStamp) {
        if (timeStamp == 0) return "";
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(timeStamp * 1000);
        Date currenTimeZone = inputTime.getTime();
        Calendar calendar = Calendar.getInstance();
        if (calendar.before(inputTime)) {
            //当前时间在输入时间之前
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + "-" + "MM" + "-" + "dd");
            return sdf.format(currenTimeZone);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(currenTimeZone);
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return BiubiuApplication.getInstance().getResources().getString(R.string.date_yesterday) + " " + sdf.format(currenTimeZone);
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            if (calendar.before(inputTime)) {
                SimpleDateFormat sdf = new SimpleDateFormat("M" + "-" + "d" + " HH:mm");
                return sdf.format(currenTimeZone);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + "-" + "MM" + "-" + "dd" + " HH:mm");
                return sdf.format(currenTimeZone);
            }

        }

    }
}
