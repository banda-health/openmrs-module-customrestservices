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

import org.openmrs.module.openhmis.commons.api.entity.model.BaseSerializableOpenmrsMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class that represents a patient list definition.
 */
public class PatientList extends BaseSerializableOpenmrsMetadata {

	private Integer patientListId;
	private List<PatientListCondition> patientListConditions;
	private List<PatientListOrder> patientListOrders;
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

	public String getDisplayTemplate() {
		return displayTemplate;
	}

	public void setDisplayTemplate(String displayTemplate) {
		this.displayTemplate = displayTemplate;
	}

	public List<PatientListOrder> getPatientListOrders() {
		return patientListOrders;
	}

	public void setPatientListOrders(List<PatientListOrder> patientListOrders) {
		this.patientListOrders = patientListOrders;
	}

	public void addSelectionRule(PatientListCondition selectionRule) {
		if (selectionRule == null) {
			throw new NullPointerException("The selection rule to add must be defined.");
		}

		if (this.patientListConditions == null) {
			this.patientListConditions = new ArrayList<PatientListCondition>();
		}

		selectionRule.setPatientList(this);

		this.patientListConditions.add(selectionRule);
	}

	public void addSortOrder(PatientListOrder sortOrder) {
		if (sortOrder == null) {
			throw new NullPointerException("The sort order to add must be defined.");
		}

		if (this.patientListOrders == null) {
			this.patientListOrders = new ArrayList<PatientListOrder>();
		}

		sortOrder.setPatientList(this);

		this.patientListOrders.add(sortOrder);
	}
}
