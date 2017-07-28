package org.openmrs.module.webservices.rest.web.search;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptSearchResult;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Search diagnosis concepts
 */
@Component
public class DiagnosisSearchHandler implements SearchHandler {
	private static final String TERM_PARAMETER = "term";
	private static final String LIMIT_PARAMETER = "limit";
	private static final int LIMIT = 100;

	private final SearchConfig searchConfig = new SearchConfig("diagnosisByTerm", RestConstants.VERSION_1 + "/concept",
	        Arrays.asList("1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*"),
	        Arrays.asList(new SearchQuery.Builder(
	                "Search diagnosis concepts.")
	                .withRequiredParameters(TERM_PARAMETER)
	                .withOptionalParameters(LIMIT_PARAMETER)
	                .build()));

	/**
	 * @see SearchHandler#getSearchConfig()
	 */
	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}

	@Override
	public PageableResult search(RequestContext context) throws ResponseException {
		PageableResult results;
		String term = context.getParameter(TERM_PARAMETER);
		int limit = LIMIT;
		String limitParam = context.getParameter(LIMIT_PARAMETER);
		if (StringUtils.isNotEmpty(limitParam) && NumberUtils.isDigits(limitParam)) {
			limit = Integer.valueOf(limitParam);
		}

		ConceptClass diagnosisClass = Context.getConceptService().getConceptClassByName("Diagnosis");

		List<ConceptClass> conceptClasses = new ArrayList<>();
		conceptClasses.add(diagnosisClass);

		List<Locale> locales = new ArrayList<>();
		locales.add(Context.getLocale());

		List<Concept> concepts = new ArrayList<>();
		if (StringUtils.isNotEmpty(term)) {
			List<ConceptSearchResult> hits = Context.getConceptService().getConcepts(term, locales,
			    false, conceptClasses, null, null, null, null, 1, limit);
			for (ConceptSearchResult hit : hits) {
				concepts.add(hit.getConcept());
			}
		}

		results = new AlreadyPaged<>(context, concepts, false);

		return results;
	}
}
