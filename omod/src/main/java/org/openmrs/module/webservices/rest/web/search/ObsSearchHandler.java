package org.openmrs.module.webservices.rest.web.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Obs search by concept, and visit
 */
@Component
public class ObsSearchHandler implements SearchHandler {
	protected final Log LOG = LogFactory.getLog(getClass());
	private static final String CONCEPT_PARAMETER = "conceptList";
	private static final String VISIT_PARAMETER = "visit";

	@Autowired
	@Qualifier("obsService")
	private ObsService obsService;

	@Autowired
	@Qualifier("conceptService")
	private ConceptService conceptService;

	@Autowired
	@Qualifier("visitService")
	private VisitService visitService;

	private final SearchConfig searchConfig = new SearchConfig("obsByConceptListVisit", RestConstants.VERSION_1 + "/obs",
	        Arrays.asList("1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*"),
	        new SearchQuery.Builder(
	                "Search obs by conceptS and visit.")
	                .withRequiredParameters(CONCEPT_PARAMETER, VISIT_PARAMETER)
	                .build());

	/**
	 * @see SearchHandler#getSearchConfig()
	 */
	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}

	@Override
	public PageableResult search(RequestContext requestContext) throws ResponseException {
		String conceptListStr = requestContext.getRequest().getParameter(CONCEPT_PARAMETER);
		String visitUuid = requestContext.getRequest().getParameter(VISIT_PARAMETER);
		List<Concept> conceptList = parseConceptList(conceptListStr);
		if (visitUuid != null) {
			Visit visit = visitService.getVisitByUuid(visitUuid);
			if (visit == null) {
				LOG.warn("Visit \"" + visitUuid + "\" was not found. Returning empty set.");
				return new EmptySearchResult();
			}

			Patient patient = visit.getPatient();

			List<Obs> obsList = new ArrayList<>();
			if (patient != null & conceptList.size() != 0) {
				for (Concept concept : conceptList) {
					List<Obs> observations = obsService.getObservationsByPersonAndConcept(patient, concept);
					for (Obs obs : observations) {
						if (obs.getEncounter().getVisit().getUuid().equalsIgnoreCase(visitUuid)) {
							obsList.add(obs);
						}
					}
				}
			}

			if (obsList.size() != 0) {
				// Sorting obs by descending obsDatetime
				Collections.sort(obsList, new Comparator<Obs>() {
					public int compare(Obs obs1, Obs obs2) {
						return obs2.getObsDatetime().compareTo(obs1.getObsDatetime());
					}
				});

				return new NeedsPaging<>(obsList, requestContext);
			}
		}

		return new EmptySearchResult();
	}

	/**
	 * Returns a {@link List} of {@link Concept} from a provided {@link String} of comma separated concepts. Each concept of
	 * the list can be provided either as UUID or as Concept Mapping
	 * @param conceptListStr
	 * @return a list of concepts
	 */
	protected List<Concept> parseConceptList(String conceptListStr) {
		List<String> conceptUuidsList = Arrays.asList(conceptListStr.split("\\s*,\\s*"));
		List<Concept> conceptList = new ArrayList<>();
		if (conceptUuidsList != null) {
			for (String conceptStr : conceptUuidsList) {
				Concept concept;

				// See if the concept is a mapping or a uuid
				List<String> conceptMapping = Arrays.asList(conceptStr.split(":"));
				if (conceptMapping.size() > 1) {
					// it is a mapping
					concept = conceptService.getConceptByMapping(conceptMapping.get(1), conceptMapping.get(0));
				} else {
					// it is a uuid
					concept = conceptService.getConceptByUuid(conceptStr);
				}

				if (concept == null) {
					LOG.warn("Concept \"" + conceptStr + "\" was not found. Ignoring it.");
				} else {
					conceptList.add(concept);
				}
			}
		}
		return conceptList;
	}
}
