package com.lsm.utils;

import static org.apache.commons.lang3.time.DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT;
import static org.apache.commons.lang3.time.DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DurationFormatUtils;

public class DateUtil {

	public static Date toDate(long f){
		return new Date(f);
	}
	
	public static Date toDate(LocalDateTime localDate) {
		ZonedDateTime zoned = localDate.atZone(ZoneOffset.systemDefault());
		return Date.from(zoned.toInstant());
	}

	public static LocalDateTime toLocalDate(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}

	public static Date plus(Date date,long time,TimeUnit timeUnit) {
		return new Date( date.getTime()+(TimeUnit.MILLISECONDS.convert(time,timeUnit)));
	}
	public static Date plusHours(Date date,int hours) {
		return plus(date,hours,TimeUnit.HOURS);
	}
	public static Date plusDays(Date date,int days) {
		return plus(date,days,TimeUnit.DAYS);
	}
	public static Date plusMinutes(Date date,int min) {
		return plus(date,min,TimeUnit.MINUTES);
	}
	public static String dateFormat(LocalDateTime date) {
		if (date != null) {
			ZonedDateTime zoned = date.atZone(ZoneOffset.systemDefault());
			Date.from(zoned.toInstant());
			return ISO_8601_EXTENDED_DATETIME_FORMAT.format(Date.from(zoned.toInstant()));
		}
		return null;
	}

	public static String duration(long startTime) {
		return DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - startTime);
	}

	public static String dateFormatWithtimeZone(Date date) {
		return date != null ? ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.format(date) : null;
	}

	public static String dateFormat(Date date) {
		return date != null ? ISO_8601_EXTENDED_DATETIME_FORMAT.format(date) : null;
	}

	public static String durationShortFormat(Date date) {
		return date != null ? DurationFormatUtils.formatDurationHMS(date.getTime()) : null;
	}

	public static LocalDateTime parse(String dt) {
		//if (dt instanceof String) {
		try {
			 return LocalDateTime.parse((String)dt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		}catch (Exception e) {
		}
		return null;
	}
}
