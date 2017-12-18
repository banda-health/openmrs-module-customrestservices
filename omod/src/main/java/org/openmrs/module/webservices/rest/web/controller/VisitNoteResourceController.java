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
import org.openmrs.module.customrestservices.CustomRestConstants;
import org.openmrs.module.customrestservices.api.util.MergePatientSummary;
import org.openmrs.module.customrestservices.web.ModuleRestConstants;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.fragment.controller.htmlform.EnterHtmlFormFragmentController;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
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
import java.util.Set;

/**
 * Save & Update Visit Note.
 */
@Controller
@RequestMapping("/rest/" + ModuleRestConstants.VISIT_NOTE_RESOURCE)
public class VisitNoteResourceController {
	private static final String TAG = VisitNoteResourceController.class.getSimpleName();

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
		Obs requestObs = null;
		Obs existingObs = null;
		String updatedPatientSummary = request.getParameter("w12");

		Encounter encounter = checkEncounterExists(encounterUuid, visit);

		/**
		 * Here we try to establish if the clinical note/patient summary requires a merge.
		 * We compare the request observation with the existing one contained in the database.
		 * If the request observation is voided, then we compare request patient summary text to the one contained
		 * in the database, and make sure they don't match.
		 * Lastly, we make sure that the conflicting changes are coming from different users.
		 *
         */
		if (encounter != null) {
			// check if observation exists
			requestObs = obsService.getObsByUuid(obsUuid);
			if (requestObs != null && requestObs.getVoided()) {
				// retrieve base, existing obs if they exists
				existingObs = retrieveExistingObs(requestObs, encounter);
				if (existingObs != null) {
					// check if obs are identical
					String existingObsUuid = existingObs.getUuid();
					if (!obsUuid.equalsIgnoreCase(existingObsUuid)
							&& !updatedPatientSummary.equalsIgnoreCase(requestObs.getValueText())
					        && !requestObs.getCreator().getUuid().equalsIgnoreCase(existingObs.getCreator().getUuid())) {
						mergePatientSummaryInfo = true;
					}
				}
			}
		}

		if (mergePatientSummaryInfo) {
			return mergePatientSummaryInfo(
			    requestObs, basePatientSummary, existingObs, updatedPatientSummary);
		} else {
			return saveVisitNote(patient, encounter, visit, createVisit, returnUrl, request);
		}
	}

	/**
	 * Save a visit note encounter. This method calls {@link EnterHtmlFormFragmentController}
	 * which is what the web htmlform uses.
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
			if (htmlForm.getName().equalsIgnoreCase(CustomRestConstants.VISIT_NOTE_2)) {
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
			LOG.warn(TAG + ":" + ex.getMessage());
		}

		Visit updatedVisit = Context.getVisitService().getVisitByUuid(visit.getUuid());
		Obs patientSummaryObs = null;
		Encounter visitNoteEncounter = null;
		for (Encounter updatedEncounter : updatedVisit.getEncounters()) {
			String encounterTypeName = updatedEncounter.getEncounterType().getName();
			if (encounterTypeName.equalsIgnoreCase(CustomRestConstants.VISIT_NOTE)) {
				visitNoteEncounter = updatedEncounter;
				for (Obs obs : visitNoteEncounter.getAllObs()) {
					if (obs.getConcept().getName().getName()
					        .equalsIgnoreCase(CustomRestConstants.TEXT_OF_ENCOUNTER_NOTE)) {
						patientSummaryObs = obs;
						break;
					}
				}
			}
		}

		return formatResults(visitNoteEncounter, patientSummaryObs);
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
			updatedObs.setValueText(
			        MergePatientSummary.merge(basePatientSummary, updatedPatientSummary, existingObs));
		}

		obsService.voidObs(existingObs, CustomRestConstants.VOID_PATIENT_SUMMARY_MESSAGE);
		Obs mergedObs = Obs.newInstance(updatedObs);
		mergedObs.setVoided(false);
		Obs patientSummaryObs = obsService.saveObs(mergedObs, CustomRestConstants.CREATE_PATIENT_SUMMARY_MESSAGE);

		return formatResults(patientSummaryObs.getEncounter(), patientSummaryObs);
	}

	/**
	 * Retrieve the 'visit note' encounter
	 * @param uuid
	 * @param visit
     * @return
     */
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
				if (enc.getEncounterType().getName().equalsIgnoreCase(CustomRestConstants.VISIT_NOTE)) {
					encounter = enc;
					break;
				}
			}
		}

		return encounter;
	}

	/**
	 * Retrieve a non-voided 'Text of encounter note' observation in the given encounter object
	 * @param updatedObs
	 * @param encounter
     * @return
     */
	private Obs retrieveExistingObs(Obs updatedObs, Encounter encounter) {
		Obs existingObs = null;
		for (Obs obs : encounter.getAllObs()) {
			if (obs.getConcept().getUuid().equalsIgnoreCase(updatedObs.getConcept().getUuid())
			        && !obs.getVoided()) {
				existingObs = obs;
			}
		}

		return existingObs;
	}

	private SimpleObject formatResults(Encounter encounter, Obs obs) {
		SimpleObject result = SimpleObject.create("success", true);
		if (encounter != null) {
			result.put("encounter", ConversionUtil.convertToRepresentation(encounter, Representation.FULL));
		}

		if (obs != null) {
			result.put("w12", obs.getValueText());
			result.put("observation", ConversionUtil.convertToRepresentation(obs, Representation.FULL));
		}

		return result;
	}
}
