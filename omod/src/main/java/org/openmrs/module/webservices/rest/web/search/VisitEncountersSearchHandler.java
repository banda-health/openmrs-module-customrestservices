package org.openmrs.module.webservices.rest.web.search;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Returns encounters that occurred as part of a visit.
 */
@Component
public class VisitEncountersSearchHandler implements SearchHandler {
	private static final String VISIT_PARAMETER = "visit";

	@Autowired
	VisitService visitService;

	@Autowired
	EncounterService encounterService;

	private final SearchConfig searchConfig = new SearchConfig("byVisit", RestConstants.VERSION_1 + "/encounter",
	        Arrays.asList("1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*"),
	        new SearchQuery.Builder(
	                "Search to find the encounters that occurred as part of specified visit (by uuid).")
	                .withRequiredParameters(VISIT_PARAMETER)
	                .build());

	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}

	@Override
	public PageableResult search(RequestContext context) throws ResponseException {
		PageableResult result = null;

		String visitUuid = context.getParameter(VISIT_PARAMETER);

		if (StringUtils.isNotEmpty(visitUuid)) {
			Visit visit = visitService.getVisitByUuid(visitUuid);
			if (visit != null) {
				List<Encounter> encounters = encounterService.getEncountersByVisit(visit, false);

				result = new AlreadyPaged<>(context, encounters, false);
			}
		}

		if (result == null) {
			return new EmptySearchResult();
		} else {
			return result;
		}
	}
}
