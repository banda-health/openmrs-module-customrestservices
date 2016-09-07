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

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.logic.op.Operator;

public class PatientListCondition extends BaseOpenmrsObject {
	
	private Integer patientListConditionId;
	
	private String field;
	
	private Operator operator;
	
	private String value;
	
	@Override
	public Integer getId() {
		return this.patientListConditionId;
	}
	
	@Override
	public void setId(Integer id) {
		this.patientListConditionId = id;
	}
}
