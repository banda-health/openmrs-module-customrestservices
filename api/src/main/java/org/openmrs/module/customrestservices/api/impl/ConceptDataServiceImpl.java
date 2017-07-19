package org.openmrs.module.customrestservices.api.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.module.customrestservices.api.ConceptDataService;
import org.openmrs.module.customrestservices.api.util.BasicObjectAuthorizationPrivileges;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseObjectDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.f.Action1;

import java.util.List;

public class ConceptDataServiceImpl extends BaseObjectDataServiceImpl<Concept, BasicObjectAuthorizationPrivileges>
	implements ConceptDataService {

	@Override
	public List<Concept> getAllByClass(final ConceptClass conceptClass) {
		return executeCriteria(Concept.class, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.eq("ConceptClass", conceptClass));
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
