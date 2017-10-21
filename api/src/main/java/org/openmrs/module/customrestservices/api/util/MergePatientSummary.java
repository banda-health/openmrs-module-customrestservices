package org.openmrs.module.customrestservices.api.util;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.merge.MergeAlgorithm;
import org.eclipse.jgit.merge.MergeFormatter;
import org.eclipse.jgit.merge.MergeResult;

import org.openmrs.Obs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * Merge texts and resolve conflicts
 */
public class MergePatientSummary {
	private static final String TAG = MergePatientSummary.class.getSimpleName();
	private static final Log LOG = LogFactory.getLog(MergePatientSummary.class);
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm");

	/**
	 * Merge two texts against a base text
	 * @param basePatientSummary
	 * @param updatedPatientSummary
	 * @param existingObs
	 * @return mergedText
	 */
	public static String merge(String basePatientSummary, String updatedPatientSummary, Obs existingObs) {
		String existingPatientSummary = existingObs.getValueText();

		if (StringUtils.isEmpty(updatedPatientSummary)) {
			return existingPatientSummary;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MergeResult<RawText> mergeResult;
		String createdBy = existingObs.getCreator().getGivenName();
		String createdOn = DATE_FORMAT.format(existingObs.getDateCreated());

		try {
			// initialize the merge algorithm to be used
			MergeAlgorithm mergeAlgorithm = new MergeAlgorithm(
			        DiffAlgorithm.getAlgorithm(DiffAlgorithm.SupportedAlgorithm.MYERS));

			// perform merge
			mergeResult = mergeAlgorithm.merge(
			    RawTextComparator.DEFAULT, constructRawText(basePatientSummary),
			    constructRawText(updatedPatientSummary), constructRawText(existingPatientSummary));

			MergeFormatter formatter = new MergeFormatter();
			formatter.formatMerge(out, mergeResult, Arrays.asList(
			        new String[] { "", insertMetadata(createdBy, createdOn), "" }),
			    Constants.CHARACTER_ENCODING);
			String mergedText = out.toString();
			mergedText = mergedText
			        .replace("\n", "")
			        .replaceAll("<<<<<<<", "")
			        .replaceAll(">>>>>>>", "[End]")
			        .replaceAll("=======", "[With]")
			        .trim();
			return mergedText;
		} catch (IOException ex) {
			LOG.error(TAG + ": error merging '" + existingPatientSummary + "' and '"
			        + updatedPatientSummary + "'");
		}

		return "";
	}

	private static RawText constructRawText(String text) {
		return new RawText(Constants.encode(formatString(text)));
	}

	/**
	 * Put every character in a new line. This is the only way the merge will work
	 * @param text
	 * @return
	 */
	private static String formatString(String text) {
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			output.append(text.charAt(i));
			if (i < text.length() - 1) {
				output.append('\n');
			}
		}

		return output.toString();
	}

	/**
	 * Format conflicting text metadata
	 * @param author
	 * @param changedOn
	 * @return
	 */
	private static String insertMetadata(String author, String changedOn) {
		StringBuilder metadata = new StringBuilder();
		metadata.append("\n[Last updated='");
		metadata.append(changedOn);
		metadata.append(" by='");
		metadata.append(author);
		metadata.append("']\n");
		return metadata.toString();
	}
}
