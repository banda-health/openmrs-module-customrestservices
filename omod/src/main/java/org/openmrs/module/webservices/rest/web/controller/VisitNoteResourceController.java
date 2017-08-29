package org.openmrs.module.webservices.rest.web.controller;

import com.sksamuel.diffpatch.DiffMatchPatch;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import java.util.LinkedList;

/**
 * Save & Update Visit Note.
 */
@Controller
@RequestMapping("/rest/" + ModuleRestConstants.VISIT_NOTE_RESOURCE)
public class VisitNoteResourceController {

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
	        @RequestParam("obs") String obsUuid,
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
					if (!obsUuid.equalsIgnoreCase(existingObsUuid)) {
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

	private SimpleObject mergePatientSummaryInfo(Obs updatedObs, Obs existingObs, HttpServletRequest request) {
		String existingPatientSummary = existingObs.getValueText();
		if (existingPatientSummary.equalsIgnoreCase("")) {
			// no need for merging
			existingObs.setVoided(true);
		} else {
			String updatedPatientSummary = request.getParameter("w12");
			DiffMatchPatch matchPatch = new DiffMatchPatch();
			LinkedList<DiffMatchPatch.Diff> diffs = matchPatch.diff_main(
			    existingPatientSummary, updatedPatientSummary);
			StringBuilder mergedText = new StringBuilder();
			for (DiffMatchPatch.Diff diff : diffs) {
				switch (diff.operation) {
					case EQUAL:
						mergedText.append(diff.text);
						break;
					case DELETE:
						mergedText.append("[" + diff.text + "]");
						break;
					case INSERT:
						mergedText.append(diff.text);
						break;
					default:
						break;
				}
			}

			updatedObs.setValueText(mergedText.toString());
		}

		obsService.voidObs(existingObs, "void patient summary obs");
		Obs mergedObs = Obs.newInstance(updatedObs);
		mergedObs.setVoided(false);
		obsService.saveObs(mergedObs, "create merged patient summary obs");

		return SimpleObject.create(
		    "success", true,
		    "encounterId", mergedObs.getEncounter().getUuid(),
		    "w12", mergedObs.getValueText());
	}

	private SimpleObject saveVisitNote(Patient patient, Encounter encounter,
	        Visit visit, Boolean createVisit,
	        String returnUrl, HttpServletRequest request) {

		SimpleObject result = new SimpleObject();

		HtmlForm hf = null;

		HtmlFormEntryService service = Context.getService(HtmlFormEntryService.class);
		for (HtmlForm htmlForm : service.getAllHtmlForms()) {
			if (htmlForm.getName().equalsIgnoreCase("visit note 2")) {
				hf = htmlForm;
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
		for (Encounter updatedEncounter : updatedVisit.getEncounters()) {
			String encounterTypeName = updatedEncounter.getEncounterType().getName();
			if (encounterTypeName.equalsIgnoreCase("Visit Note")) {
				result = SimpleObject.create("success", true, "encounterId", updatedEncounter.getUuid());
				break;
			}
		}

		return result;
	}

	private Obs retrieveExistingObs(Obs updatedObs, Encounter encounter) {
		for (Obs obs : encounter.getAllObs()) {
			if (obs.getConcept().getUuid().equalsIgnoreCase(updatedObs.getConcept().getUuid())) {
				return obs;
			}
		}

		return null;
	}
}
