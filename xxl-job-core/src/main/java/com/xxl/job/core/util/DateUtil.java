package com.xxl.job.core.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * date util
 *
 * @author xuxueli 2018-08-19 01:24:11
 */
public class DateUtil {

	// ---------------------- format parse ----------------------
	private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

	static final String DATE_FORMAT = "yyyy-MM-dd";
	static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	static FastDateFormat getDateFormat(String pattern) {
		return FastDateFormat.getInstance(pattern);
	}

	/**
	 * format datetime. like "yyyy-MM-dd"
	 */
	public static String formatDate(Date date) {
		return format(date, DATE_FORMAT);
	}

	/**
	 * format date. like "yyyy-MM-dd HH:mm:ss"
	 */
	public static String formatDateTime(Date date) {
		return format(date, DATETIME_FORMAT);
	}

	/**
	 * format date
	 */
	public static String format(Date date, String patten) {
		return getDateFormat(patten).format(date);
	}

	/**
	 * parse date string, like "yyyy-MM-dd HH:mm:s"
	 */
	public static Date parseDate(String dateString) {
		return parse(dateString, DATE_FORMAT);
	}

	/**
	 * parse datetime string, like "yyyy-MM-dd HH:mm:ss"
	 */
	public static Date parseDateTime(String dateString) {
		return parse(dateString, DATETIME_FORMAT);
	}

	/**
	 * parse date
	 */
	public static Date parse(String dateString, String pattern) {
		try {
			return getDateFormat(pattern).parse(dateString);
		} catch (Exception e) {
			logger.warn("parse date error, dateString = {}, pattern={}; errorMsg = {}", dateString, pattern, e.getMessage());
			return null;
		}
	}

	// ---------------------- add date ----------------------

	public static Date addYears(final Date date, final int amount) {
		return add(date, Calendar.YEAR, amount);
	}

	public static Date addMonths(final Date date, final int amount) {
		return add(date, Calendar.MONTH, amount);
	}

	public static Date addDays(final Date date, final int amount) {
		return add(date, Calendar.DAY_OF_MONTH, amount);
	}

	public static Date addHours(final Date date, final int amount) {
		return add(date, Calendar.HOUR_OF_DAY, amount);
	}

	public static Date addMinutes(final Date date, final int amount) {
		return add(date, Calendar.MINUTE, amount);
	}

	private static Date add(final Date date, final int calendarField, final int amount) {
		if (date == null) {
			return null;
		}
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(calendarField, amount);
		return c.getTime();
	}

}
