package org.openmrs.module.webservices.rest.web.controller;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptSearchResult;
import org.openmrs.ConceptSource;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.impl.MutableResourceBundleMessageSource;
import org.openmrs.module.customrestservices.web.ModuleRestConstants;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.concept.EmrConceptService;
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Search diagnoses
 */
@Controller
@RequestMapping("/rest/" + ModuleRestConstants.DIAGNOSES_RESOURCE)
public class DiagnosesResourceController {

	@Autowired
	private EmrApiProperties emrApiProperties;
	@Autowired
	private FormatterService formatterService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject search(@RequestParam("term") String query,
	        @RequestParam(value = "limit", required = false) Integer limit) {
		SimpleObject results = new SimpleObject();
		EmrConceptService emrConceptService = Context.getService(EmrConceptService.class);

		FragmentActionUiUtils uiUtils = new FragmentActionUiUtils(
		        new MutableResourceBundleMessageSource(), null, null, formatterService);
		try {
			Collection<Concept> diagnosisSets = emrApiProperties.getDiagnosisSets();
			List<ConceptSource> sources = emrApiProperties.getConceptSourcesForDiagnosisSearch();
			List<ConceptSearchResult> hits = emrConceptService.conceptSearch(query, Context.getLocale(),
			    null, diagnosisSets, sources, limit);
			List values = new LinkedList();
			for (ConceptSearchResult hit : hits) {
				values.add(simplify(hit, uiUtils));
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
