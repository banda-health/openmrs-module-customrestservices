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
			default:
				throw new IllegalArgumentException("Invalid operator " + operator);
		}
	}
}
