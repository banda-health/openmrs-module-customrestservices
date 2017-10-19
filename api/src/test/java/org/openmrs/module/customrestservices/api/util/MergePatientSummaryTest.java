package org.openmrs.module.customrestservices.api.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Obs;
import org.openmrs.User;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MergePatientSummaryTest.class })
public class MergePatientSummaryTest {

	@Before
	public void before() throws Exception {
		Date mockDate = new Date(117, 3, 1, 2, 47);
		whenNew(Date.class).withNoArguments().thenReturn(mockDate);
	}

	/**
	 * Modify only the second text
	 * @throws Exception
	 */
	@Test
	public void merge_shouldTestOneTextChange() throws Exception {
		String base = "The Text";
		String text1 = "";
		String text2 = "The Text Something";

		String mergedText = MergePatientSummary.merge(base, text1, newInstance(text2));
		Assert.assertEquals("The Text Something", mergedText);
	}

	@Test
	public void merge_shouldTestDifferentNonconflictingTextChanges() throws Exception {
		String base = "The Text";
		String text1 = "Some Text";
		String text2 = "The Text Something";

		String mergedText = MergePatientSummary.merge(base, text1, newInstance(text2));
		Assert.assertEquals("Some Text Something", mergedText);

		mergedText = MergePatientSummary.merge(base, text2, newInstance(text1));
		Assert.assertEquals("Some Text Something", mergedText);
	}

	/**
	 * Test conflicting text changes
	 * @throws Exception
	 */
	@Test
	public void merge_shouldTestConflictingTextChanges() throws Exception {
		String base = "The Text";
		String text1 = "The New Text";
		String text2 = "The Old Text";

		String mergedText = MergePatientSummary.merge(base, text1, newInstance(text2));
		Assert.assertEquals(
		    "The  [Author='null' Created='01/04/2017 02:47']New=======Old  Text", mergedText);
	}

	private Obs newInstance(String value) {
		Obs obs = new Obs();
		obs.setCreator(new User());
		obs.setDateCreated(new Date());
		obs.setValueText(value);

		return obs;
	}
}
