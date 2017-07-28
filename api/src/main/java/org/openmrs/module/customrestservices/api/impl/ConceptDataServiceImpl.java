package org.openmrs.module.customrestservices.api.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptSet;
import org.openmrs.module.customrestservices.api.ConceptDataService;
import org.openmrs.module.customrestservices.api.util.BasicObjectAuthorizationPrivileges;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseObjectDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.f.Action1;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of data access service for {@link Concept} models.
 */
public class ConceptDataServiceImpl extends BaseObjectDataServiceImpl<Concept, BasicObjectAuthorizationPrivileges>
        implements ConceptDataService {
	@Override
	public List<Concept> getAllByClass(final ConceptClass conceptClass, PagingInfo pagingInfo) {
		return executeCriteria(Concept.class, pagingInfo, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.eq("conceptClass", conceptClass));
			}
		});
	}

	@Override
	protected BasicObjectAuthorizationPrivileges getPrivileges() {
		return new BasicObjectAuthorizationPrivileges();
	}

	@Override
	protected void validate(Concept concept) {

	}
}
