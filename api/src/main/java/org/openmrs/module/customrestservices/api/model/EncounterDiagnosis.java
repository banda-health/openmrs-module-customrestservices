package org.openmrs.module.customrestservices.api.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * EncounterDiagnosis POJO
 */
public class EncounterDiagnosis implements Serializable {

	@JsonProperty
	private String certainty;
	@JsonProperty
	private String order;
	@JsonProperty
	private String diagnosis;
	@JsonProperty
	private String existingObs;

	public String getCertainty() {
		return certainty;
	}

	public void setCertainty(String certainty) {
		this.certainty = certainty;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getExistingObs() {
		return existingObs;
	}

	public void setExistingObs(String existingObs) {
		this.existingObs = existingObs;
	}
}
