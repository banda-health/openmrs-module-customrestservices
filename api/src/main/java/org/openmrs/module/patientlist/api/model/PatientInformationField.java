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

import org.openmrs.OpenmrsData;
import org.openmrs.module.openhmis.commons.api.f.Func1;

/**
 * Model class that represents an patient information field.
 * @param <T> The source object class.
 */
public class PatientInformationField<T extends OpenmrsData> {
	private String prefix;
	private String name;
	private String mappingFieldName;
	private Class<?> dataType;
	private Func1<T, Object> getValueFunc;
	private String attributeTypeConfig;

	public PatientInformationField(String prefix, String name, Class<?> dataType,
	    Func1<T, Object> getValueFunc, String mappingFieldName, String attributeTypeConfig) {
		this.prefix = prefix;
		this.name = name;
		this.dataType = dataType;
		this.getValueFunc = getValueFunc;
		this.mappingFieldName = mappingFieldName;
		this.attributeTypeConfig = attributeTypeConfig;
	}

	public String getPrefix() {
		return prefix;
	}

	protected void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public Class<?> getDataType() {
		return dataType;
	}

	protected void setDataType(Class<?> dataType) {
		this.dataType = dataType;
	}

	protected void setValueFunc(Func1<T, Object> func) {
		this.getValueFunc = func;
	}

	public Object getValue(T source) {
		return getValueFunc.apply(source);
	}

	public String getMappingFieldName() {
		return mappingFieldName;
	}

	public void setMappingFieldName(String mappingFieldName) {
		this.mappingFieldName = mappingFieldName;
	}

	public String getAttributeTypeConfig() {
		return attributeTypeConfig;
	}

	public void setAttributeTypeConfig(String attributeTypeConfig) {
		this.attributeTypeConfig = attributeTypeConfig;
	}
}
