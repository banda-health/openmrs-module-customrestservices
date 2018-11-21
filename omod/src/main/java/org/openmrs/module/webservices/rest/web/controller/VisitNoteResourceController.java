package org.openmrs.module.webservices.rest.web.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.impl.MutableResourceBundleMessageSource;
import org.openmrs.module.appframework.feature.FeatureToggleProperties;
import org.openmrs.module.customrestservices.api.util.MergePatientSummary;
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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Save & Update Visit Note.
 */
@Controller
@RequestMapping("/rest/" + ModuleRestConstants.VISIT_NOTE_RESOURCE)
public class VisitNoteResourceController {
	private static final String TAG = VisitNoteResourceController.class.getSimpleName();

	private static final String VISIT_NOTE_2 = "visit note 2";
	private static final String VISIT_NOTE = "Visit Note";
	private static final String TEXT_OF_ENCOUNTER_NOTE = "text of encounter note";
	private static final String VOID_PATIENT_SUMMARY_MESSAGE = "void patient summary obs";
	private static final String CREATE_PATIENT_SUMMARY_MESSAGE = "create merged patient summary obs";

	private final Log LOG = LogFactory.getLog(this.getClass());

	@Autowired
	private FeatureToggleProperties featureToggles;

	@Autowired
	private FormatterService formatterService;

	@Autowired
	private ObsService obsService;

	@Autowired
	private EncounterService encounterService;

	@ResponseBody
	@RequestMapping(method = RequestMethod.POST,
	        produces = MediaType.APPLICATION_JSON_VALUE,
	        consumes = MediaType.APPLICATION_JSON_VALUE)
	public SimpleObject save(
	        @RequestParam("personId") Patient patient,
	        @RequestParam(value = "obs", required = false) String obsUuid,
	        @RequestParam(value = "encounterId", required = false) String encounterUuid,
	        @RequestParam(value = "visitId", required = false) Visit visit,
	        @RequestParam(value = "createVisit", required = false) Boolean createVisit,
	        @RequestParam(value = "returnUrl", required = false) String returnUrl,
	        @RequestParam(value = "basePatientSummary", required = false) String basePatientSummary,
	        HttpServletRequest request) {

		boolean mergePatientSummaryInfo = false;
		Obs updatedObs = null;
		Obs existingObs = null;
		String updatedPatientSummary = request.getParameter("w12");

		Encounter encounter = checkEncounterExists(encounterUuid, visit);

		if (encounter != null) {
			// check if observation exists
			updatedObs = obsService.getObsByUuid(obsUuid);
			if (updatedObs != null) {
				// retrieve base, existing obs if they exists
				existingObs = retrieveExistingObs(updatedObs, encounter);
				if (existingObs != null) {
					// check if obs are identical
					String existingObsUuid = existingObs.getUuid();
					if (!obsUuid.equalsIgnoreCase(existingObsUuid)
					        && !updatedPatientSummary.equalsIgnoreCase(updatedObs.getValueText())) {
						mergePatientSummaryInfo = true;
					}
				}
			}
		}

		if (mergePatientSummaryInfo) {
			return mergePatientSummaryInfo(
			    updatedObs, basePatientSummary, existingObs, updatedPatientSummary);
		} else {
			return saveVisitNote(patient, encounter, visit, createVisit, returnUrl, request);
		}
	}

	/**
	 * Save a visit note encounter
	 * @param patient
	 * @param encounter
	 * @param visit
	 * @param createVisit
	 * @param returnUrl
	 * @param request
	 * @return
	 */
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
	 * @param basePatientSummary
	 * @param existingObs
	 * @param updatedPatientSummary
	 * @return
	 */
	private SimpleObject mergePatientSummaryInfo(Obs updatedObs, String basePatientSummary,
	        Obs existingObs, String updatedPatientSummary) {
		String existingPatientSummary = existingObs.getValueText();
		if (StringUtils.isEmpty(existingPatientSummary)) {
			// no need for merging
			existingObs.setVoided(true);
		} else {
			//updatedObs.setValueText(
			//        MergePatientSummary.merge(basePatientSummary, updatedPatientSummary, existingObs));
			StringBuilder updatedNote = new StringBuilder(basePatientSummary);
			updatedNote.append("-------------------------------");
			updatedNote.append(updatedPatientSummary);
			updatedObs.setValueText(updatedNote.toString());
		}

		obsService.voidObs(existingObs, VOID_PATIENT_SUMMARY_MESSAGE);
		Obs mergedObs = Obs.newInstance(updatedObs);
		mergedObs.setVoided(false);
		Obs createdObs = obsService.saveObs(mergedObs, CREATE_PATIENT_SUMMARY_MESSAGE);

		return formatResults(createdObs);
	}

	private Encounter checkEncounterExists(String uuid, Visit visit) {
		Encounter encounter = null;
		// check if encounter exists
		if (StringUtils.isNotEmpty(uuid)) {
			encounter = encounterService.getEncounterByUuid(uuid);
		}

		// This check is important since a new encounter could have been created right before this request
		if (encounter == null) {
			List<Encounter> encounters = encounterService.getEncountersByVisit(visit, false);
			for (Encounter enc : encounters) {
				if (enc.getEncounterType().getName().equalsIgnoreCase(VISIT_NOTE)) {
					encounter = enc;
					break;
				}
			}
		}

		return encounter;
	}

	private Obs retrieveExistingObs(Obs updatedObs, Encounter encounter) {
		Obs existingObs = null;
		for (Obs obs : encounter.getAllObs()) {
			if (obs.getConcept().getUuid().equalsIgnoreCase(updatedObs.getConcept().getUuid())) {
				existingObs = obs;
			}
		}

		return existingObs;
	}

	private SimpleObject formatResults(Obs obs) {
		return SimpleObject.create(
		    "success", true,
		    "encounter", ConversionUtil.convertToRepresentation(obs.getEncounter(), Representation.FULL),
		    "w12", obs.getValueText(),
		    "observation", ConversionUtil.convertToRepresentation(obs, Representation.FULL));
	}
}
