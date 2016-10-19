package org.openmrs.module.patientlist.api.model;

import org.openmrs.logic.op.Operator;

/**
 * The allowable {@link PatientListOperator} operators.
 */
public enum PatientListOperator {

	CONTAINS(Operator.CONTAINS),
	EQUALS(Operator.EQUALS),
	//WITHIN(Operator.WITHIN),
	GT(Operator.GT),
	GTE(Operator.GTE),
	LT(Operator.LT),
	LTE(Operator.LTE),
	//BEFORE(Operator.BEFORE),
	//AFTER(Operator.AFTER),
	//IN(Operator.IN),
	ASOF(Operator.ASOF),
	AND(Operator.AND),
	NOT(Operator.NOT),
	LAST(Operator.LAST),
	FIRST(Operator.FIRST),
	DISTINCT(Operator.DISTINCT),
	EXISTS(Operator.EXISTS),
	COUNT(Operator.COUNT),
	AVERAGE(Operator.AVERAGE);

	private Operator operator;

	private PatientListOperator(Operator operator) {
		this.operator = operator;
	}

}
