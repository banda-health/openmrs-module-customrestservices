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

import org.openmrs.logic.op.Operator;

/**
 * The allowable {@link PatientListOperator} operators.
 */
public enum PatientListOperator {

	LIKE(Operator.CONTAINS),
	EQUALS(Operator.EQUALS),
	GT(Operator.GT),
	GTE(Operator.GTE),
	LT(Operator.LT),
	LTE(Operator.LTE),
	NOT_EQUALS,
	BETWEEN,
	NULL,
	NOT_NULL,
	DEFINED,
	NOT_DEFINED,
	RELATIVE;

	private Operator operator;

	private PatientListOperator() {}

	private PatientListOperator(Operator operator) {
		this.operator = operator;
	}

}
