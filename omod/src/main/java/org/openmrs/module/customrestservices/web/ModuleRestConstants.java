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
package org.openmrs.module.customrestservices.web;

import org.openmrs.module.openhmis.commons.web.WebConstants;
import org.openmrs.module.webservices.rest.web.RestConstants;

/**
 * Constants class for REST urls.
 */
public class ModuleRestConstants extends WebConstants {
	public static final String MODULE_REST_ROOT = RestConstants.VERSION_2 + "/custom/";
	public static final String PHOTOS_RESOURCE = MODULE_REST_ROOT + "photos";
	public static final String VISIT_EDIT_RESOURCE = MODULE_REST_ROOT + "visitedit";
	public static final String CONCEPT_ANSWER_RESOURCE = MODULE_REST_ROOT + "conceptanswer";
	public static final String VISIT_NOTE_RESOURCE = MODULE_REST_ROOT + "visitnote";
	public static final String DIAGNOSES_RESOURCE = MODULE_REST_ROOT + "diagnoses";
	public static final String OBS_RESOURCE = MODULE_REST_ROOT + "obs";
}
