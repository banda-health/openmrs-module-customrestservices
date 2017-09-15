package org.openmrs.module.webservices.rest.web.controller;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.HistogramDiff;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.impl.MutableResourceBundleMessageSource;
import org.openmrs.module.appframework.feature.FeatureToggleProperties;
import org.openmrs.module.customrestservices.web.ModuleRestConstants;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.fragment.controller.htmlform.EnterHtmlFormFragmentController;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.formatter.FormatterService;
import org.openmrs.ui.framework.fragment.FragmentActionUiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Save & Update Visit Note.
 */
@Controller
@RequestMapping("/rest/" + ModuleRestConstants.VISIT_NOTE_RESOURCE)
public class VisitNoteResourceController {
	private static final String TAG = VisitNoteResourceController.class.getSimpleName();

	private static final String NO_NEWLINE_AT_END_OF_FILE = "\\ No newline at end of file";
	private static final String ESCAPE_BACK_SLASH_CHAR = "\\";
	private static final String ESCAPE_PLUS_CHAR = "+";
	private static final String MINUS_CHAR = "-";
	private static final String EMPTY_STRING = "";
	private static final String VISIT_NOTE_2 = "visit note 2";
	private static final String VISIT_NOTE = "Visit Note";
	private static final String TEXT_OF_ENCOUNTER_NOTE = "text of encounter note";
	private static final String VOID_PATIENT_SUMMARY_MESSAGE = "void patient summary obs";
	private static final String CREATE_PATIENT_SUMMARY_MESSAGE = "create merged patient summary obs";

	private final SimpleDateFormat patientSummaryDateFormat =
	        new SimpleDateFormat("dd/MM/yyyy hh:mm");
	private final Log LOG = LogFactory.getLog(this.getClass());

	@Autowired
	private FeatureToggleProperties featureToggles;

	@Autowired
	private FormatterService formatterService;

	@Autowired
	private ObsService obsService;

	@ResponseBody
	@RequestMapping(method = RequestMethod.POST)
	public SimpleObject save(
	        @RequestParam("personId") Patient patient,
	        @RequestParam("htmlFormId") Integer htmlFormId,
	        @RequestParam(value = "obs", required = false) String obsUuid,
	        @RequestParam(value = "encounterId", required = false) Encounter encounter,
	        @RequestParam(value = "visitId", required = false) Visit visit,
	        @RequestParam(value = "createVisit", required = false) Boolean createVisit,
	        @RequestParam(value = "returnUrl", required = false) String returnUrl,
	        HttpServletRequest request) {

		boolean mergePatientSummaryInfo = false;
		Obs updatedObs = null, existingObs = null;
		if (encounter != null) {
			// check if observation exists
			updatedObs = obsService.getObsByUuid(obsUuid);
			if (updatedObs != null) {
				// retrieve existing obs if it exists
				existingObs = retrieveExistingObs(updatedObs, encounter);
				if (existingObs != null) {
					// check if obs are identical
					String existingObsUuid = existingObs.getUuid();
					if (!obsUuid.equalsIgnoreCase(existingObsUuid)
					        && !request.getParameter("w12").equalsIgnoreCase(updatedObs.getValueText())) {
						mergePatientSummaryInfo = true;
					}
				}
			}
		}

		if (mergePatientSummaryInfo) {
			return mergePatientSummaryInfo(updatedObs, existingObs, request);
		} else {
			return saveVisitNote(patient, encounter, visit, createVisit, returnUrl, request);
		}
	}

	private SimpleObject saveVisitNote(Patient patient, Encounter encounter,
	        Visit visit, Boolean createVisit,
	        String returnUrl, HttpServletRequest request) {

		HtmlForm hf = null;

		HtmlFormEntryService service = Context.getService(HtmlFormEntryService.class);
		for (HtmlForm htmlForm : service.getAllHtmlForms()) {
			if (htmlForm.getName().equalsIgnoreCase(VISIT_NOTE_2)) {
				hf = htmlForm;
				break;
			}
		}

		if (encounter != null) {
			encounter.setVoided(true);
		}

		AdtService adtService = Context.getService(AdtService.class);
		FragmentActionUiUtils uiUtils = new FragmentActionUiUtils(
		        new MutableResourceBundleMessageSource(), null, null, formatterService);
		try {
			new EnterHtmlFormFragmentController().submit(
			    null, patient, hf, encounter, visit, createVisit, returnUrl,
			    adtService, featureToggles, uiUtils, request);
		} catch (Exception ex) {
			LOG.warn(ex.getMessage());
		}

		Visit updatedVisit = Context.getVisitService().getVisitByUuid(visit.getUuid());
		Obs createdObs = null;
		for (Encounter updatedEncounter : updatedVisit.getEncounters()) {
			String encounterTypeName = updatedEncounter.getEncounterType().getName();
			if (encounterTypeName.equalsIgnoreCase(VISIT_NOTE)) {
				for (Obs obs : updatedEncounter.getAllObs()) {
					if (obs.getConcept().getName().getName().equalsIgnoreCase(TEXT_OF_ENCOUNTER_NOTE)) {
						createdObs = obs;
						break;
					}
				}
			}
		}

		return formatResults(createdObs);
	}

	/**
	 * This method compares between the current and previous obs patient summary (clinical note) merges text
	 * @param updatedObs
	 * @param existingObs
	 * @param request
	 * @return
	 */
	private SimpleObject mergePatientSummaryInfo(Obs updatedObs, Obs existingObs, HttpServletRequest request) {
		String existingPatientSummary = existingObs.getValueText();
		if (StringUtils.isEmpty(existingPatientSummary)) {
			// no need for merging
			existingObs.setVoided(true);
		} else {
			updatedObs.setValueText(
			        mergeTextAndShowConflicts(existingObs, updatedObs, request));
		}

		obsService.voidObs(existingObs, VOID_PATIENT_SUMMARY_MESSAGE);
		Obs mergedObs = Obs.newInstance(updatedObs);
		mergedObs.setVoided(false);
		Obs createdObs = obsService.saveObs(mergedObs, CREATE_PATIENT_SUMMARY_MESSAGE);

		return formatResults(createdObs);
	}

	private SimpleObject formatResults(Obs obs) {
		return SimpleObject.create(
		    "success", true,
		    "encounter", ConversionUtil.convertToRepresentation(obs.getEncounter(), Representation.FULL),
		    "w12", obs.getValueText(),
		    "observationUuid", obs.getUuid());
	}

	private Obs retrieveExistingObs(Obs updatedObs, Encounter encounter) {
		for (Obs obs : encounter.getAllObs()) {
			if (obs.getConcept().getUuid().equalsIgnoreCase(updatedObs.getConcept().getUuid())) {
				return obs;
			}
		}

		return null;
	}

	private String mergeTextAndShowConflicts(Obs existingObs, Obs updatedObs, HttpServletRequest request) {
		StringBuilder mergedText = new StringBuilder();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String existingPatientSummary = existingObs.getValueText();
		String updatedPatientSummary = request.getParameter("w12");
		try {
			RawText rawText1 = new RawText(existingPatientSummary.getBytes());
			RawText rawText2 = new RawText(updatedPatientSummary.getBytes());
			EditList diffList = new EditList();
			diffList.addAll(new HistogramDiff().diff(RawTextComparator.DEFAULT, rawText1, rawText2));
			new DiffFormatter(out).format(diffList, rawText1, rawText2);
		} catch (IOException ex) {
			LOG.error(TAG + ": error generating diffs between '" + existingPatientSummary + "' and '"
			        + updatedPatientSummary + "'");
			out = null;
		}

		String createdBy = existingObs.getCreator().getGivenName();
		String createdOn = patientSummaryDateFormat.format(existingObs.getDateCreated());
		String updatedBy = updatedObs.getCreator().getGivenName();
		String updatedOn = patientSummaryDateFormat.format(new Date());

		String[] mergedTxts = StringUtils.split(out.toString(), "\n");
		if (mergedTxts.length > 1) {
			mergedTxts[0] = "";
			mergedText.append(insertMetadata(createdBy, createdOn));
			boolean firstOccurenceNewLine = false;
			for (String text : mergedTxts) {
				// replace first occurrence of NO_NEWLINE_AT_END_OF_FILE with updated Metadata.
				if (!firstOccurenceNewLine && text.contains(NO_NEWLINE_AT_END_OF_FILE)) {
					text = text.replaceFirst(
					    NO_NEWLINE_AT_END_OF_FILE, insertMetadata(updatedBy, updatedOn));
					firstOccurenceNewLine = true;
				}

				mergedText.append(
				        StringUtils.replaceEachRepeatedly(text,
				            new String[] { NO_NEWLINE_AT_END_OF_FILE, ESCAPE_BACK_SLASH_CHAR, ESCAPE_PLUS_CHAR,
				                    MINUS_CHAR }, new String[] { EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING })
				                .trim());
			}
		}

		return mergedText.toString();
	}

	private String insertMetadata(String author, String changedOn) {
		StringBuilder metadata = new StringBuilder();
		metadata.append("\n[Author='");
		metadata.append(author);
		metadata.append("' Created='");
		metadata.append(changedOn);
		metadata.append("']\n");
		return metadata.toString();
	}
}
