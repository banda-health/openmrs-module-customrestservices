package org.openmrs.module.patientlist.api.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.module.patientlist.api.model.PatientListRelativeDate;
import org.openmrs.module.patientlist.api.util.PatientListDateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PatientListDateUtil.class })
public class PatientListDateUtilTest {

	@Test
	public void createRelativeDatesYesterday() throws Exception {
		Date mockDate = new Date(116, 3, 1);
		whenNew(Date.class).withNoArguments().thenReturn(mockDate);

		String formattedDates = PatientListDateUtil.createRelativeDate(PatientListRelativeDate.YESTERDAY);
		Assert.assertEquals(formattedDates, "2016-03-31|2016-04-01");
	}

	@Test
	public void createRelativeDatesThisWeek() throws Exception {
		Date mockDate = new Date(116, 2, 31);
		whenNew(Date.class).withNoArguments().thenReturn(mockDate);

		String formattedDates = PatientListDateUtil.createRelativeDate(PatientListRelativeDate.THIS_WEEK);
		Assert.assertEquals(formattedDates, "2016-03-27|2016-03-31");
	}

	@Test
	public void createRelativeDatesLastWeek() throws Exception {
		Date mockDate = new Date(116, 4, 5);
		whenNew(Date.class).withNoArguments().thenReturn(mockDate);

		String formattedDates = PatientListDateUtil.createRelativeDate(PatientListRelativeDate.LAST_WEEK);
		Assert.assertEquals(formattedDates, "2016-04-24|2016-05-01");
	}

	@Test
	public void createRelativeDatesLastTwoWeeks() throws Exception {
		Date mockDate = new Date(116, 4, 5);
		whenNew(Date.class).withNoArguments().thenReturn(mockDate);

		String formattedDates = PatientListDateUtil.createRelativeDate(PatientListRelativeDate.LAST_TWO_WEEKS);
		Assert.assertEquals(formattedDates, "2016-04-17|2016-05-01");
	}

	@Test
	public void createRelativeDatesThisMonth() throws Exception {
		Date mockDate = new Date(115, 11, 3);
		whenNew(Date.class).withNoArguments().thenReturn(mockDate);

		String formattedDates = PatientListDateUtil.createRelativeDate(PatientListRelativeDate.THIS_MONTH);
		Assert.assertEquals(formattedDates, "2015-12-01|2015-12-03");
	}

	@Test
	public void createRelativeDatesLastMonth() throws Exception {
		Date mockDate = new Date(116, 4, 3);
		whenNew(Date.class).withNoArguments().thenReturn(mockDate);

		String formattedDates = PatientListDateUtil.createRelativeDate(PatientListRelativeDate.LAST_MONTH);
		Assert.assertEquals(formattedDates, "2016-04-01|2016-04-30");
	}

	@Test
	public void createRelativeDatesLastThreeMonths() throws Exception {
		Date mockDate = new Date(116, 4, 3);
		whenNew(Date.class).withNoArguments().thenReturn(mockDate);

		String formattedDates = PatientListDateUtil.createRelativeDate(PatientListRelativeDate.LAST_THREE_MONTHS);
		Assert.assertEquals(formattedDates, "2016-02-01|2016-04-30");
	}

	@Test
	public void createRelativeDatesLastSixMonths() throws Exception {
		Date mockDate = new Date(116, 4, 3);
		whenNew(Date.class).withNoArguments().thenReturn(mockDate);

		String formattedDates = PatientListDateUtil.createRelativeDate(PatientListRelativeDate.LAST_SIX_MONTHS);
		Assert.assertEquals(formattedDates, "2015-11-01|2016-04-30");
	}

	@Test
	public void createRelativeDatesLastNineMonths() throws Exception {
		Date mockDate = new Date(116, 4, 3);
		whenNew(Date.class).withNoArguments().thenReturn(mockDate);

		String formattedDates = PatientListDateUtil.createRelativeDate(PatientListRelativeDate.LAST_NINE_MONTHS);
		Assert.assertEquals(formattedDates, "2015-08-01|2016-04-30");
	}

	@Test
	public void createRelativeDatesThisYear() throws Exception {
		Date mockDate = new Date(116, 5, 9);
		whenNew(Date.class).withNoArguments().thenReturn(mockDate);

		String formattedDates = PatientListDateUtil.createRelativeDate(PatientListRelativeDate.THIS_YEAR);
		Assert.assertEquals(formattedDates, "2016-01-01|2016-06-09");
	}

	@Test
	public void createRelativeDatesLastYear() throws Exception {
		Date mockDate = new Date(117, 11, 30);
		whenNew(Date.class).withNoArguments().thenReturn(mockDate);

		String formattedDates = PatientListDateUtil.createRelativeDate(PatientListRelativeDate.LAST_YEAR);
		Assert.assertEquals(formattedDates, "2016-01-01|2016-12-31");
	}
}
