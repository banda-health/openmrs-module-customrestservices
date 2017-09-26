package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.Provider;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.customrestservices.web.ModuleRestConstants;
import org.openmrs.module.visitdocumentsui.web.controller.VisitDocumentsController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle Patient List Imaging
 */
@Controller
@RequestMapping("/rest/" + ModuleRestConstants.PHOTOS_RESOURCE)
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
		if (provider == null) {
			Person person = Context.getPersonService().getPersonByUuid(providerUuid);
			List<Provider> providers = new ArrayList<>(
			        Context.getProviderService().getProvidersByPerson(person));
			if (!providers.isEmpty()) {
				provider = providers.get(0);
			}
		}

		SimpleObject obsObject = (SimpleObject)controller.uploadDocuments(
		    patient, visit, provider, fileCaption, instructions, request);
		Obs obs = Context.getObsService().getObsByUuid((String)obsObject.get("uuid"));

		results.put("observation", ConversionUtil.convertToRepresentation(obs, Representation.FULL));

		return results;
	}

	@RequestMapping(method = RequestMethod.GET)
	public SimpleObject downloadDocument(
	        @RequestParam("obs") String obsUuid,
	        @RequestParam(value = "view", required = false) String view,
	        HttpServletResponse response) {
		SimpleObject results = new SimpleObject();
		controller.downloadDocument(obsUuid, view, response);

		return results;
	}
}
