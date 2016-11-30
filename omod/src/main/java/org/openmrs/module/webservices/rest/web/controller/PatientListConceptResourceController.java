package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientlist.web.ModuleRestConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * REST controller to handle Patient List concepts
 */
@Controller
@RequestMapping("/rest/" + ModuleRestConstants.PATIENT_LIST_GET_CONCEPT_RESOURCE)
public class PatientListConceptResourceController {

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public int get(@RequestParam("conceptUuid") String uuid) {
		int conceptId = 0;

		Concept concept = Context.getConceptService().getConceptByUuid(uuid);
		if (concept != null) {
			conceptId = concept.getConceptId();
		}

		return conceptId;
	}
}
