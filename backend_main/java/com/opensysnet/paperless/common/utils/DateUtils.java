package com.opensysnet.paperless.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {
    private static Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public final static String DATE_FORMAT             = "yyyy-MM-dd";
    public final static String TIME_FORMAT             = "HH:mm:ss";
    public final static String DATE_TIME_FORMAT        = DATE_FORMAT + " " + TIME_FORMAT;
    public final static String DATE_TIME_MS_FORMAT     = DATE_FORMAT + " " + TIME_FORMAT + ".SSS";
    public final static String DATE_TIME_ISO_FORMAT    = DATE_FORMAT + "'T'" + TIME_FORMAT + "Z";

    public final static int    TYPE_SECOND             = Calendar.SECOND;
    public final static int    TYPE_MINUTE             = Calendar.MINUTE;
    public final static int    TYPE_HOUR               = Calendar.HOUR;
    public final static int    TYPE_DATE               = Calendar.DATE;
    public final static int    TYPE_MONTH              = Calendar.MONTH;
    public final static int    TYPE_YEAR               = Calendar.YEAR;

    public static final int getTimeValue(Date date, int type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if ( type == DateUtils.TYPE_YEAR ) {
            return calendar.get(Calendar.YEAR);
        }
        else if ( type == DateUtils.TYPE_MONTH ) {
            return calendar.get(Calendar.MONTH);
        }
        else if ( type == DateUtils.TYPE_DATE ) {
            return calendar.get(Calendar.DATE);
        }
        else if ( type == DateUtils.TYPE_HOUR ) {
            return calendar.get(Calendar.HOUR_OF_DAY);
        }
        else if ( type == DateUtils.TYPE_MINUTE ) {
            return calendar.get(Calendar.MINUTE);
        }
        else if ( type == DateUtils.TYPE_SECOND ) {
            return calendar.get(Calendar.SECOND);
        }
        else {
            return 0;
        }
    }

    public static final Date addTime(Date date, int type, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(type, amount);
        return calendar.getTime();
    }

    public static String getDatetimeAsString(Date date, String format, TimeZone timeZone)
    {
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        if ( timeZone != null ) sdf.setTimeZone(timeZone);
        final String dateString = sdf.format(date);

        return dateString;
    }

    public static Date getStringDateToDate(String StrDate) {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);

        try{
            dateToReturn = dateFormat.parse(StrDate);
        } catch (ParseException e)  {
            logger.error("ParseException", e);
        }

        return dateToReturn;
    }

    public static Date getStringDateToDate(String StrDate,String format) {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        try{
            dateToReturn = dateFormat.parse(StrDate);
        } catch (ParseException e)  {
            logger.error("ParseException", e);
        }

        return dateToReturn;
    }


    public static String getStringDateFormatNow(String format){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        Date currentDate = cal.getTime();
        DateFormat df = new SimpleDateFormat(format);
        df.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return df.format(currentDate);
    }

    public static Date getDateNow(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        return cal.getTime();
    }

    public static ZonedDateTime getZoneDateTimeFromDate(Date date) {
        ZonedDateTime d = ZonedDateTime.ofInstant(date.toInstant(),
                ZoneId.systemDefault());

        return d;
    }

}
