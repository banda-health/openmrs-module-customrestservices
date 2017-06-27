package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.customrestservices.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;

/**
 * Edit visit functionality
 */
@Controller
@RequestMapping("/rest/" + ModuleRestConstants.VISIT_EDIT_RESOURCE)
public class VisitEditResourceController {

	@ResponseBody
	@RequestMapping(method = RequestMethod.POST,
	        consumes = MediaType.APPLICATION_JSON_VALUE)
	public SimpleObject post(
	        @RequestParam(value = "visit") Visit existingVisit,
	        @RequestParam(value = "visitType") VisitType visitType,
	        @RequestBody List<LinkedHashMap<String, String>> attributes) {
		SimpleObject results = new SimpleObject();

		existingVisit.setVisitType(visitType);

		List<VisitAttribute> attrs = new ArrayList<VisitAttribute>();
		for (LinkedHashMap<String, String> attribute : attributes) {
			VisitAttribute visitAttribute = new VisitAttribute();
			for (Map.Entry<String, String> set : attribute.entrySet()) {
				if (set.getKey().equalsIgnoreCase("attributeType")) {
					VisitAttributeType type =
					        Context.getVisitService().getVisitAttributeTypeByUuid(set.getValue());
					visitAttribute.setAttributeType(type);
				} else {
					visitAttribute.setValue(set.getValue());
				}
			}

			attrs.add(visitAttribute);
		}

		for (VisitAttribute vatt : attrs) {
			VisitAttribute existingAttr = searchAttributeByType(existingVisit, vatt.getAttributeType().getUuid());
			if (existingAttr != null) {
				if (existingAttr.getValueReference().equalsIgnoreCase(vatt.getValue().toString())) {
					continue;
				} else {
					existingAttr.setVoided(true);
					existingVisit.setAttribute(existingAttr);
				}
			}

			existingVisit.setAttribute(vatt);
		}

		existingVisit = Context.getVisitService().saveVisit(existingVisit);
		results.put("results", "successful updated visit - " + existingVisit.getUuid());

		return results;
	}

	private VisitAttribute searchAttributeByType(Visit visit, String typeUuid) {
		for (VisitAttribute attribute : visit.getAttributes()) {
			if (attribute.getAttributeType().getUuid().equalsIgnoreCase(typeUuid)) {
				return attribute;
			}
		}
		return null;
	}
}
