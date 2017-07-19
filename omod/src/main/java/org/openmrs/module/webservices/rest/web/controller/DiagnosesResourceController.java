package org.openmrs.module.webservices.rest.web.controller;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptSearchResult;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.impl.MutableResourceBundleMessageSource;
import org.openmrs.module.customrestservices.api.ConceptDataService;
import org.openmrs.module.customrestservices.api.impl.ConceptDataServiceImpl;
import org.openmrs.module.customrestservices.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.formatter.FormatterService;
import org.openmrs.ui.framework.fragment.FragmentActionUiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Search diagnoses
 */
@Controller
@RequestMapping("/rest/" + ModuleRestConstants.DIAGNOSES_RESOURCE)
public class DiagnosesResourceController {

	@Autowired
	private FormatterService formatterService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject search(@RequestParam("term") String query,
	        @RequestParam(value = "limit", required = false) Integer limit) {
		SimpleObject results = new SimpleObject();

		FragmentActionUiUtils uiUtils = new FragmentActionUiUtils(
		        new MutableResourceBundleMessageSource(), null, null, formatterService);
		try {
			ConceptClass diagnosisClass = Context.getConceptService().getConceptClassByName("Diagnosis");

			List<ConceptClass> conceptClasses = new ArrayList<ConceptClass>();
			conceptClasses.add(diagnosisClass);

			List<Locale> locales = new ArrayList<Locale>();
			locales.add(Context.getLocale());

			List<org.openmrs.ui.framework.SimpleObject > values;
			if (StringUtils.isNotEmpty(query)) {
				List<ConceptSearchResult> hits = Context.getConceptService().getConcepts(query, locales,
						false, conceptClasses, null, null, null, null, 1, limit);
				values = new ArrayList<>(hits.size());
				for (ConceptSearchResult hit : hits) {
					values.add(simplify(hit, uiUtils));
				}
			} else {
				ConceptDataService dataService = Context.getService(ConceptDataService.class);
				List<Concept> concepts = dataService.getAllByClass(diagnosisClass);

				values = new ArrayList<>(concepts.size());
				for (Concept concept : concepts) {
					values.add(simplify(new ConceptSearchResult("", concept, concept.getName()), uiUtils));
				}
			}

			results.put("results", values);
		} catch (Exception ex) {
			results.put("error", ex.getMessage());
		}

		return results;
	}

	private org.openmrs.ui.framework.SimpleObject simplify(ConceptSearchResult result, UiUtils ui)
	        throws Exception {
		org.openmrs.ui.framework.SimpleObject simple =
		        org.openmrs.ui.framework.SimpleObject.fromObject(
		            result, ui,
		            new String[] {
		                    "conceptName.uuid",
		                    "conceptName.conceptNameType", "conceptName.name",
		                    "concept.uuid",
		                    "concept.conceptMappings.conceptMapType",
		                    "concept.conceptMappings.conceptReferenceTerm.code",
		                    "concept.conceptMappings.conceptReferenceTerm.name",
		                    "concept.conceptMappings.conceptReferenceTerm.conceptSource.name" });
		PropertyUtils.setProperty(simple, "value", "ConceptName:" + result.getConceptName().getId());
		return simple;
	}
}
