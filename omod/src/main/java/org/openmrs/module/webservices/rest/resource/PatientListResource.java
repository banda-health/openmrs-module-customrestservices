package org.openmrs.module.webservices.rest.resource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.patientlist.api.IPatientListService;
import org.openmrs.module.patientlist.api.model.PatientList;
import org.openmrs.module.patientlist.api.model.PatientListCondition;
import org.openmrs.module.patientlist.api.model.PatientListOrder;
import org.openmrs.module.patientlist.api.util.PatientListTemplate;
import org.openmrs.module.patientlist.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * REST resource representing a {@link PatientList}
 */
@Resource(name = ModuleRestConstants.PATIENT_LIST_RESOURCE, supportedClass = PatientList.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class PatientListResource extends BaseRestMetadataResource<PatientList> {

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		description.addProperty("headerTemplate");
		description.addProperty("bodyTemplate");
		description.addProperty("dateCreated");
		description.addProperty("patientListConditions", Representation.DEFAULT);
		description.addProperty("ordering", Representation.DEFAULT);

		return description;
	}

	@PropertySetter("patientListConditions")
	public void setPatientListConditions(PatientList instance, List<PatientListCondition> patientListConditions) {
		if (instance.getPatientListConditions() == null) {
			instance.setPatientListConditions(new ArrayList<PatientListCondition>(patientListConditions.size()));
		}
		BaseRestDataResource.syncCollection(instance.getPatientListConditions(), patientListConditions);
		for (PatientListCondition patientListCondition : instance.getPatientListConditions()) {
			patientListCondition.setPatientList(instance);
		}
	}

	@PropertySetter("ordering")
	public void setOrdering(PatientList instance, List<PatientListOrder> ordering) {
		if (instance.getOrdering() == null) {
			instance.setOrdering(new ArrayList<PatientListOrder>(ordering.size()));
		}
		BaseRestDataResource.syncCollection(instance.getOrdering(), ordering);
		for (PatientListOrder order : instance.getOrdering()) {
			order.setPatientList(instance);
		}
	}

	@PropertySetter(value = "headerTemplate")
	public void setHeaderTemplate(PatientList instance, String headerTemplate) {
		if (StringUtils.isEmpty(instance.getHeaderTemplate())) {
			instance.setHeaderTemplate(PatientListTemplate.getInstance().
			        getDefaultHeaderTemplate());
		} else {
			instance.setHeaderTemplate(StringEscapeUtils.escapeHtml(headerTemplate));
		}
	}

	@PropertyGetter(value = "headerTemplate")
	public String getHeaderTemplate(PatientList instance) {
		String template = "";
		if (instance.getHeaderTemplate() != null) {
			template = StringEscapeUtils.unescapeHtml(instance.getHeaderTemplate());
		}

		return template;
	}

	@PropertySetter(value = "bodyTemplate")
	public void setBodyTemplate(PatientList instance, String bodyTemplate) {
		if (StringUtils.isEmpty(instance.getBodyTemplate())) {
			instance.setBodyTemplate(PatientListTemplate.getInstance().
			        getDefaultBodyTemplate());
		} else {
			instance.setBodyTemplate(StringEscapeUtils.escapeHtml(bodyTemplate));
		}
	}

	@PropertyGetter(value = "bodyTemplate")
	public String getBodyTemplate(PatientList instance) {
		String template = "";
		if (instance.getBodyTemplate() != null) {
			template = StringEscapeUtils.unescapeHtml(instance.getBodyTemplate());
		}

		return template;
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
		return getRepresentationDescription(new DefaultRepresentation());
	}
}
