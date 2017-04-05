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
package org.openmrs.module.patientlist.api.util;

import org.openmrs.module.patientlist.api.model.PatientListOperator;

/**
 * Convert Patient List Operators to literal values.
 */
public class ConvertPatientListOperators {

	public static String convertOperator(PatientListOperator operator) {
		String literalOperator;
		switch (operator) {
			case EQUALS:
				literalOperator = "=";
				return literalOperator;
			case NOT_EQUALS:
				literalOperator = "!=";
				return literalOperator;
			case GT:
				literalOperator = ">";
				return literalOperator;
			case GTE:
				literalOperator = ">=";
				return literalOperator;
			case LT:
				literalOperator = "<";
				return literalOperator;
			case LTE:
				literalOperator = "<=";
				return literalOperator;
			case NULL:
				literalOperator = "IS NULL";
				return literalOperator;
			case NOT_NULL:
				literalOperator = "IS NOT NULL";
				return literalOperator;
			case BETWEEN:
				literalOperator = "BETWEEN";
				return literalOperator;
			case LIKE:
				literalOperator = "LIKE";
				return literalOperator;
			case DEFINED:
				literalOperator = "EXISTS";
				return literalOperator;
			case NOT_DEFINED:
				literalOperator = "NOT EXISTS";
				return literalOperator;
			case RELATIVE:
				literalOperator = "RELATIVE";
				return literalOperator;
			default:
				throw new IllegalArgumentException("Invalid operator " + operator);
		}
	}
}
