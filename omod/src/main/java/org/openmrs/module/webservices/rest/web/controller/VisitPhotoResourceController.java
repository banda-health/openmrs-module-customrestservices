package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientlist.web.ModuleRestConstants;
import org.openmrs.module.visitdocumentsui.web.controller.VisitDocumentsController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Handle Patient List Imaging
 */
@Controller
@RequestMapping("/rest/" + ModuleRestConstants.PATIENT_LIST_PHOTOS_RESOURCE)
public class VisitPhotoResourceController {
	@Autowired
	private VisitDocumentsController controller;

	@ResponseBody
	@RequestMapping(method = RequestMethod.POST)
	public SimpleObject upload(@RequestParam(value = "patient") String patientUuid,
	        @RequestParam(value = "visit") String visitUuid,
	        @RequestParam(value = "provider") String providerUuid,
	        @RequestParam(value = "fileCaption") String fileCaption,
	        @RequestParam(value = "instructions", required = false) String instructions,
	        MultipartHttpServletRequest request) {
		SimpleObject results = new SimpleObject();

		Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);

		Visit visit = Context.getVisitService().getVisitByUuid(visitUuid);

		Provider provider = Context.getProviderService().getProviderByUuid(providerUuid);

		results.put("results", controller.uploadDocuments(
		    patient, visit, provider, fileCaption, instructions, request));

		return results;
	}
}
