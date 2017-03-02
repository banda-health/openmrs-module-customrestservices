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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Store default templates.
 */
public class PatientListTemplate {

	private static final String HEADER_TEMPLATE_FILE = "defaultHeaderTemplate.html";
	private static final String BODY_TEMPLATE_FILE = "defaultBodyTemplate.html";

	private final Log LOG = LogFactory.getLog(this.getClass());

	private PatientListTemplate() {}

	public static PatientListTemplate getInstance() {
		return Holder.INSTANCE;
	}

	public String getDefaultHeaderTemplate() {
		return getTemplateFile(HEADER_TEMPLATE_FILE);
	}

	public String getDefaultBodyTemplate() {
		return getTemplateFile(BODY_TEMPLATE_FILE);
	}

	private String getTemplateFile(String filename) {
		StringBuilder sb = new StringBuilder();
		try {
			InputStream resource = this.getClass().getClassLoader().getResourceAsStream(filename);
			BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
			String line;
			while ((line = reader.readLine()) != null)
				sb.append(line);

			reader.close();
		} catch (IOException io) {
			LOG.error("error reading file " + filename + ", " + io);
		}

		return sb.toString();
	}

	private static class Holder {
		private static final PatientListTemplate INSTANCE = new PatientListTemplate();
	}
}
