package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.Concept;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.ConceptResource1_9;

/**
 * Concept Resource. Add 'value' property
 */
@Resource(name = RestConstants.VERSION_1 + "/concept", order = 1, supportedClass = Concept.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*" })
public class ConceptResource12 extends ConceptResource1_9 {

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof CustomRepresentation) {
			return null;
		}

		return super.getRepresentationDescription(rep);
	}

	@PropertyGetter("value")
	public String getValue(Concept concept) {
		return "ConceptName:" + concept.getName().getId();
	}
}
