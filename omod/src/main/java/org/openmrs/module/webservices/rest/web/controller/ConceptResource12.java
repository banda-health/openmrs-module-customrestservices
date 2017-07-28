package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.ConceptName;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.ConceptNameResource1_9;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.ConceptResource1_9;

/**
 * ConceptName Resource. Include 'tags' property
 */
@SubResource(parent = ConceptResource1_9.class, path = "name", supportedClass = ConceptName.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*" }, order = 1)
public class ConceptNameResource11 extends ConceptNameResource1_9 {

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription delegatingResourceDescription = super.getRepresentationDescription(rep);
		delegatingResourceDescription.addProperty("tags", Representation.FULL);

		return delegatingResourceDescription;
	}
}
