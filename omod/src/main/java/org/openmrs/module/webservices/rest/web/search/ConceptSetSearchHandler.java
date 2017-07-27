package org.openmrs.module.webservices.rest.web.search;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Returns concepts that are a member of the specified concept set.
 */
@Component
public class ConceptSetSearchHandler implements SearchHandler {
	private static final String SET_PARAMETER = "set";
	private static final String INCLUDE_SET_CONCEPTS_PARAMETER = "includeSetConcepts";

	@Autowired
	@Qualifier("conceptService")
	ConceptService conceptService;

	private final SearchConfig searchConfig = new SearchConfig("bySet", RestConstants.VERSION_1 + "/concept",
	        Arrays.asList("1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*"),
	        new SearchQuery.Builder(
	                "Search to find concepts which are members of the specified concept set (by uuid.")
	                .withRequiredParameters(SET_PARAMETER)
	                .withOptionalParameters(INCLUDE_SET_CONCEPTS_PARAMETER)
	                .build());

	/**
	 * @see SearchHandler#getSearchConfig()
	 */
	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}

	/**
	 * @see SearchHandler#search(RequestContext)
	 */
	@Override
	public PageableResult search(RequestContext context) throws ResponseException {
		PageableResult result = null;

		String conceptSetUuid = context.getParameter(SET_PARAMETER);

		boolean includeSets = true;
		String includeSetsString = context.getParameter(INCLUDE_SET_CONCEPTS_PARAMETER);
		if (StringUtils.isNotEmpty(includeSetsString)) {
			try {
				includeSets = Boolean.parseBoolean(includeSetsString);
			} catch (Exception ex) {
				includeSets = true;
			}
		}

		if (StringUtils.isNotEmpty(conceptSetUuid)) {
			ConceptService conceptService = Context.getConceptService();
			Concept conceptSet = conceptService.getConceptByUuid(conceptSetUuid);
			if (conceptSet != null) {
				List<Concept> concepts = conceptService.getConceptsByConceptSet(conceptSet);

				if (!includeSets) {
					List<Concept> filteredConcepts = new ArrayList<>();
					for (Concept concept : concepts) {
						if (!concept.isSet()) {
							filteredConcepts.add(concept);
						}
					}

					concepts = filteredConcepts;
				}

				result = new AlreadyPaged<>(context, concepts, false);
			}
		}

		if (result == null) {
			return new EmptySearchResult();
		} else {
			return result;
		}
	}

}
