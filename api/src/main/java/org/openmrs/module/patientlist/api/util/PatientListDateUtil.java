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

	private static Calendar calendar = Calendar.getInstance();
	private static final int DAYS_IN_WEEK = 7;
	private static final int DAYS_IN_TWO_WEEKS = 14;
	private static final int DAYS_IN_MONTH = 30;
	private static final int DAYS_IN_THREE_MONTHS = 90;
	private static final int DAYS_IN_SIX_MONTHS = 180;
	private static final int DAYS_IN_NINE_MONTHS = 270;
	private static final int DAYS_IN_YEAR = 365;

	/**
	 * Dynamically calculate start and end days for the given time interval
	 * @param relativeDate
	 * @return
	 */
	public static String createRelativeDate(PatientListRelativeDate relativeDate) {
		Date startDate;
		Date endDate;
		int days;
		int interval;
		switch (relativeDate) {
			case YESTERDAY:
				calendar.add(Calendar.DATE, -1);

				startDate = calendar.getTime();
				calendar.add(Calendar.DATE, 1);

				endDate = calendar.getTime();
				return formatDates(startDate, endDate);
			case THIS_WEEK:
				interval = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
				calendar.add(Calendar.DATE, -interval);

				startDate = calendar.getTime();
				calendar.add(Calendar.DATE, interval);

				endDate = calendar.getTime();
				return formatDates(startDate, endDate);
			case LAST_WEEK:
				days = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
				calendar.add(Calendar.DATE, -days - DAYS_IN_WEEK);

				startDate = calendar.getTime();
				calendar.add(Calendar.DATE, DAYS_IN_WEEK);

				endDate = calendar.getTime();
				return formatDates(startDate, endDate);
			case LAST_TWO_WEEKS:
				days = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
				calendar.add(Calendar.DATE, -days - DAYS_IN_TWO_WEEKS);

				startDate = calendar.getTime();
				calendar.add(Calendar.DATE, DAYS_IN_TWO_WEEKS);

				endDate = calendar.getTime();
				return formatDates(startDate, endDate);
			case THIS_MONTH:
				interval = calendar.get(Calendar.DAY_OF_MONTH) - 1;
				calendar.add(Calendar.DATE, -interval);

				startDate = calendar.getTime();
				calendar.add(Calendar.DATE, interval);

				endDate = calendar.getTime();
				return formatDates(startDate, endDate);
			case LAST_MONTH:
				days = calendar.get(Calendar.DAY_OF_MONTH) - 1;
				calendar.add(Calendar.DAY_OF_MONTH, -days - DAYS_IN_MONTH);

				startDate = calendar.getTime();
				calendar.add(Calendar.DATE, DAYS_IN_MONTH);

				endDate = calendar.getTime();
				return formatDates(startDate, endDate);
			case LAST_THREE_MONTHS:
				days = calendar.get(Calendar.DAY_OF_MONTH) - 1;
				calendar.add(Calendar.DAY_OF_MONTH, -days - DAYS_IN_THREE_MONTHS);

				startDate = calendar.getTime();
				calendar.add(Calendar.DATE, DAYS_IN_THREE_MONTHS);

				endDate = calendar.getTime();
				return formatDates(startDate, endDate);
			case LAST_SIX_MONTHS:
				days = calendar.get(Calendar.DAY_OF_MONTH) - 1;
				calendar.add(Calendar.DAY_OF_MONTH, -days - DAYS_IN_SIX_MONTHS);

				startDate = calendar.getTime();
				calendar.add(Calendar.DATE, DAYS_IN_SIX_MONTHS);

				endDate = calendar.getTime();
				return formatDates(startDate, endDate);
			case LAST_NINE_MONTHS:
				days = calendar.get(Calendar.DAY_OF_MONTH) - 1;
				calendar.add(Calendar.DAY_OF_MONTH, -days - DAYS_IN_NINE_MONTHS);

				startDate = calendar.getTime();
				calendar.add(Calendar.DATE, DAYS_IN_NINE_MONTHS);

				endDate = calendar.getTime();
				return formatDates(startDate, endDate);
			case THIS_YEAR:
				interval = calendar.get(Calendar.DAY_OF_YEAR) - 1;
				calendar.add(Calendar.DATE, -interval);

				startDate = calendar.getTime();
				calendar.add(Calendar.DATE, interval);

				endDate = calendar.getTime();
				return formatDates(startDate, endDate);
			case LAST_YEAR:
				days = calendar.get(Calendar.DAY_OF_YEAR) - 1;

				calendar.add(Calendar.DAY_OF_YEAR, -days - DAYS_IN_YEAR);
				startDate = calendar.getTime();
				calendar.add(Calendar.DATE, DAYS_IN_YEAR);

				endDate = calendar.getTime();
				return formatDates(startDate, endDate);
			default:
				throw new IllegalArgumentException("Invalid relative date " + relativeDate);
		}
	}

	private static String formatDates(Date startDate, Date endDate) {
		return simpleDateFormat.format(startDate) + "|" + simpleDateFormat.format(endDate);
	}
}
