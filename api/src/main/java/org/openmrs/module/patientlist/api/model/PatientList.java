/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.module.patientlist.api.model;

import org.openmrs.BaseOpenmrsMetadata;

import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Set;

/**
 * Model class that represents a patient list definition.
 */
public class PatientList extends BaseOpenmrsMetadata {

	private Integer patientListId;

	@OneToMany(fetch = FetchType.EAGER)
	private List<PatientListCondition> patientListConditions;

	@OneToMany(fetch = FetchType.EAGER)
	private Set<PatientListOrder> patientListOrders;

	private String displayTemplate;

	@Override
	public Integer getId() {
		return this.patientListId;
	}

	@Override
	public void setId(Integer id) {
		this.patientListId = id;
	}

	public List<PatientListCondition> getPatientListConditions() {
		return patientListConditions;
	}

	public void setPatientListConditions(
	        List<PatientListCondition> patientListConditions) {
		this.patientListConditions = patientListConditions;
	}

	public Set<PatientListOrder> getPatientListOrders() {
		return patientListOrders;
	}

	public void setPatientListOrders(Set<PatientListOrder> patientListOrders) {
		this.patientListOrders = patientListOrders;
	}

	public String getDisplayTemplate() {
		return displayTemplate;
	}

	public void setDisplayTemplate(String displayTemplate) {
		this.displayTemplate = displayTemplate;
	}
}
