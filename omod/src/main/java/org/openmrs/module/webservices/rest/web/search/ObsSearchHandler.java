package org.openmrs.module.webservices.rest.web.search;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.EncounterService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Search obs by encounter and concept
 */
@Component
public class ObsSearchHandler implements SearchHandler {
	private static final String ENCOUNTER_PARAM = "encounter";
	private static final String CONCEPT_PARAM = "concept";

	@Autowired
	@Qualifier("encounterService")
	private EncounterService encounterService;

	private final SearchConfig searchConfig = new SearchConfig("byEncounter", RestConstants.VERSION_1 + "/obs",
	        Arrays.asList("1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*"),
	        new SearchQuery.Builder(
	                "Search obs by encounter.")
	                .withRequiredParameters(ENCOUNTER_PARAM, CONCEPT_PARAM)
	                .build());

	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}

	@Override
	public PageableResult search(final RequestContext context) throws ResponseException {
		PageableResult result = null;
		Encounter encounter;
		String encounterUuid = context.getParameter(ENCOUNTER_PARAM);
		String conceptUuid = context.getParameter(CONCEPT_PARAM);

		if (StringUtils.isNotEmpty(encounterUuid)) {
			encounter = encounterService.getEncounterByUuid(encounterUuid);
			if (encounter != null) {
				for (final Obs obs : encounter.getAllObs(false)) {
					if (obs.getConcept().getUuid().equalsIgnoreCase(conceptUuid)) {
						result = new PageableResult() {
							@Override
							public SimpleObject toSimpleObject(Converter<?> preferredConverter) throws ResponseException {
								return (SimpleObject)ConversionUtil.convertToRepresentation(
								    obs, context.getRepresentation());
							}
						};
						break;
					}
				}
			}
		}

		if (result == null) {
			return new EmptySearchResult();
		} else {
			return result;
		}
	}
}
