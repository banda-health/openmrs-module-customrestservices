package org.openmrs.module.patientlist.api.model;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Patient;
import org.openmrs.Visit;

import java.util.List;

/**
 * Model class that represents patient list data.
 */
public class PatientListData extends BaseOpenmrsData {

	private Patient patient;
	private Visit activeVisit;
	private PatientList patientList;

	public PatientListData() {}

	public PatientListData(Patient patient, Visit activeVisit, PatientList patientList) {
		this.patient = patient;
		this.activeVisit = activeVisit;
		this.patientList = patientList;
	}

	@Override
	public Integer getId() {
		return null;
	}

	@Override
	public void setId(Integer id) {}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Visit getActiveVisit() {
		return activeVisit;
	}

	public void setActiveVisit(Visit activeVisit) {
		this.activeVisit = activeVisit;
	}

	public PatientList getPatientList() {
		return patientList;
	}

	public void setPatientList(PatientList patientList) {
		this.patientList = patientList;
	}
}
