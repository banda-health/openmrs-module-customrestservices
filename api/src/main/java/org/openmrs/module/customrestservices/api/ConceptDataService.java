package org.openmrs.module.customrestservices.api;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;

import java.util.List;

/**
 * Represents classes which provide access to {@link Concept} models.
 */
public interface ConceptDataService extends IObjectDataService<Concept> {
	List<Concept> getAllByClass(ConceptClass conceptClass, PagingInfo pagingInfo);
}
