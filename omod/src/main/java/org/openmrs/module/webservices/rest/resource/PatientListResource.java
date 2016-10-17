package org.openmrs.module.webservices.rest.resource;

import org.openmrs.annotation.Handler;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.patientlist.api.IPatientListService;
import org.openmrs.module.patientlist.api.model.PatientList;
import org.openmrs.module.patientlist.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

/**
 * REST resource representing a {@link PatientList}
 */
@Resource(name = ModuleRestConstants.PATIENT_LIST_RESOURCE, supportedClass = PatientList.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*" })
@Handler(supports = { PatientList.class }, order = 0)
public class PatientListResource extends BaseRestMetadataResource<PatientList> {

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		description.addProperty("displayTemplate", Representation.DEFAULT);
		description.addProperty("dateCreated", Representation.DEFAULT);

		return description;
	}

	@Override
	public PatientList newDelegate() {
		return new PatientList();
	}

	@Override
	public Class<? extends IMetadataDataService<PatientList>> getServiceClass() {
		return IPatientListService.class;
	}

	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = super.getCreatableProperties();
		description.addProperty("patientListConditions", Representation.REF);

		return description;
	}
}
