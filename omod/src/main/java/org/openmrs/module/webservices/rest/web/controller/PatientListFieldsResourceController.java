package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.module.patientlist.api.util.PatientInformation;
import org.openmrs.module.patientlist.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * REST controller for Patient List Fields
 */
@Controller
@RequestMapping("/rest/" + ModuleRestConstants.PATIENT_LIST_FIELDS_RESOURCE)
public class PatientListFieldsResourceController {

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public SimpleObject get() {
		SimpleObject results = new SimpleObject();
		results.put("fields", PatientInformation.getInstance().getFields());
		return results;
	}
}
