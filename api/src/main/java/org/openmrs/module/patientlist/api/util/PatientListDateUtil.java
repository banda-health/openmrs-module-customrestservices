/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.module.patientlist.api.util;

import org.openmrs.module.patientlist.api.model.PatientListRelativeDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Patient list date operations
 */
public class PatientListDateUtil {

	public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private static final int DAYS_IN_WEEK = 7;
	private static final int DAYS_IN_TWO_WEEKS = 14;
	private static final int THREE_MONTHS = 3;
	private static final int SIX_MONTHS = 6;
	private static final int NINE_MONTHS = 9;

	/**
	 * Dynamically calculate start and end days for the given time interval
	 * @param relativeDate
	 * @return
	 */
	public static String createRelativeDate(PatientListRelativeDate relativeDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int days;

		switch (relativeDate) {
			case YESTERDAY:
				calendar.add(Calendar.DATE, -1);
				Date startDate = createDate(null, -1);
				Date endDate = createDate(startDate, 1);
				return formatStartAndEndDates(startDate, endDate);
			case THIS_WEEK:
				days = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
				startDate = createDate(null, -days);
				endDate = createDate(startDate, days);
				return formatStartAndEndDates(startDate, endDate);
			case LAST_WEEK:
				days = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
				startDate = createDate(null, -days - DAYS_IN_WEEK);
				endDate = createDate(startDate, DAYS_IN_WEEK);
				return formatStartAndEndDates(startDate, endDate);
			case LAST_TWO_WEEKS:
				days = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
				startDate = createDate(null, -days - DAYS_IN_TWO_WEEKS);
				endDate = createDate(startDate, DAYS_IN_TWO_WEEKS);
				return formatStartAndEndDates(startDate, endDate);
			case THIS_MONTH:
				days = calendar.get(Calendar.DAY_OF_MONTH) - 1;
				calendar.add(Calendar.DATE, -days);
				startDate = createDate(null, -days);
				endDate = createDate(startDate, days);
				return formatStartAndEndDates(startDate, endDate);
			case LAST_MONTH:
				// get month -1
				int month = calendar.get(Calendar.MONTH) - 1;
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.set(Calendar.MONTH, month);
				startDate = calendar.getTime();
				// compute no of days in the month
				days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				// less 1 day since start date has been set to 1 (i.e end date = startDate + days)
				days--;
				endDate = createDate(startDate, days);
				return formatStartAndEndDates(startDate, endDate);
			case LAST_THREE_MONTHS:
				// get month - 3
				month = calendar.get(Calendar.MONTH) - THREE_MONTHS;
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.set(Calendar.MONTH, month);
				startDate = calendar.getTime();
				// compute no of days in the three month window
				days = calculateDaysBetweenDates(calendar, THREE_MONTHS);
				// less 1 day
				days--;
				endDate = createDate(startDate, days);
				return formatStartAndEndDates(startDate, endDate);
			case LAST_SIX_MONTHS:
				month = calendar.get(Calendar.MONTH) - SIX_MONTHS;
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.set(Calendar.MONTH, month);
				startDate = calendar.getTime();
				// compute no of days in the six month period
				days = calculateDaysBetweenDates(calendar, SIX_MONTHS);
				// less 1 day
				days--;
				endDate = createDate(startDate, days);
				return formatStartAndEndDates(startDate, endDate);
			case LAST_NINE_MONTHS:
				month = calendar.get(Calendar.MONTH) - NINE_MONTHS;
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.set(Calendar.MONTH, month);
				startDate = calendar.getTime();
				// compute no of days in the nine month period
				days = calculateDaysBetweenDates(calendar, NINE_MONTHS);
				// less 1 day
				days--;
				endDate = createDate(startDate, days);
				return formatStartAndEndDates(startDate, endDate);
			case THIS_YEAR:
				days = calendar.get(Calendar.DAY_OF_YEAR) - 1;
				calendar.add(Calendar.DATE, -days);
				startDate = calendar.getTime();
				endDate = createDate(startDate, days);
				return formatStartAndEndDates(startDate, endDate);
			case LAST_YEAR:
				calendar.add(Calendar.YEAR, -1);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.set(Calendar.MONTH, 0);
				startDate = calendar.getTime();
				days = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
				days--;
				endDate = createDate(startDate, days);
				return formatStartAndEndDates(startDate, endDate);
			default:
				throw new IllegalArgumentException("Invalid relative date " + relativeDate);
		}
	}

	private static Date createDate(Date startDate, int addDays) {
		Calendar calendar = Calendar.getInstance();
		if (startDate == null) {
			startDate = new Date();
		}

		calendar.setTime(startDate);
		calendar.add(Calendar.DATE, addDays);
		return calendar.getTime();
	}

	private static String formatStartAndEndDates(Date startDate, Date endDate) {
		return simpleDateFormat.format(startDate) + "|" + simpleDateFormat.format(endDate);
	}

	/**
	 * Calculate number of days between dates.
	 * @param calendar
	 * @param numberOfMonths
	 * @return
	 */
	private static int calculateDaysBetweenDates(Calendar calendar, int numberOfMonths) {
		int days;
		days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		for (int i = 1; i < numberOfMonths; i++) {
			// get days of next month
			calendar.add(Calendar.MONTH, 1);
			days += calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		}

		return days;
	}
}
