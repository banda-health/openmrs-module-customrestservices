package org.openmrs.module.webservices.rest.web.controller;

import org.apache.commons.lang.StringUtils;
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
	public String get(@RequestParam(value = "conceptUuid", required = false) String uuid,
	        @RequestParam(required = false, value = "conceptId") Integer conceptId) {

		if (StringUtils.isNotEmpty(uuid)) {
			Concept concept = Context.getConceptService().getConceptByUuid(uuid);
			if (concept != null) {
				return String.valueOf(concept.getConceptId());
			}
		}

		if (conceptId != null) {
			Concept concept = Context.getConceptService().getConcept(conceptId);
			if (concept != null) {
				return concept.getName().getName();
			}
		}

		return null;
	}
}
