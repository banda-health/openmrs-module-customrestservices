/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientlist.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * REST controller to handle Patient List concepts
 */
@Controller
@RequestMapping("/rest/" + ModuleRestConstants.PATIENT_LOOKUP_DATATYPE_RESOURCE)
public class PatientListLookupDatatypeResourceController {

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public SimpleObject get(
	        @RequestParam(value = "type") String type,
	        @RequestParam(value = "uuid", required = false) String uuid,
	        @RequestParam(required = false, value = "id") Integer id) {

		SimpleObject results = new SimpleObject();

		if (StringUtils.equalsIgnoreCase(type, "concept")) {
			if (StringUtils.isNotEmpty(uuid)) {
				Concept concept = Context.getConceptService().getConceptByUuid(uuid);
				if (concept != null) {
					results.put("id", String.valueOf(concept.getConceptId()));
				}
			}

			if (id != null) {
				Concept concept = Context.getConceptService().getConcept(id);
				if (concept != null) {
					results.put("name", concept.getName().getName());
				}
			}
		} else if (StringUtils.equalsIgnoreCase(type, "location")) {
			if (StringUtils.isNotEmpty(uuid)) {
				Location location = Context.getLocationService().getLocationByUuid(uuid);
				if (location != null) {
					results.put("id", String.valueOf(location.getLocationId()));
				}
			}

			if (id != null) {
				Location location = Context.getLocationService().getLocation(id);
				if (location != null) {
					results.put("uuid", location.getUuid());
				}
			}
		}

		return results;
	}
}
