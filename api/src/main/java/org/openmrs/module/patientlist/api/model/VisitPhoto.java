package org.openmrs.module.patientlist.api.model;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * VisitPhoto model
 */
public class VisitPhoto extends BaseOpenmrsData {

	private Integer visitPhotoId;

	private Visit visit;

	private Patient patient;

	private Provider provider;

	private String fileCaption;

	private String instructions;

	private RequestBody request;

	public Visit getVisit() {
		return visit;
	}

	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public String getFileCaption() {
		return fileCaption;
	}

	public void setFileCaption(String fileCaption) {
		this.fileCaption = fileCaption;
	}

	public RequestBody getRequest() {
		return request;
	}

	public void setRequest(RequestBody request) {
		this.request = request;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	@Override
	public Integer getId() {
		return visitPhotoId;
	}

	@Override
	public void setId(Integer id) {
		visitPhotoId = id;
	}
}
