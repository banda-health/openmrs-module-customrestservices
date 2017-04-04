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
package org.openmrs.module.patientlist.api;

import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataServiceTest;
import org.openmrs.module.patientlist.api.model.PatientList;

public class IPatientListDataServiceTest extends IMetadataDataServiceTest<IPatientListService, PatientList> {
	private static final String PATIENT_LIST_DATASET = TestConstants.BASE_DATASET_DIR + "PatientListTest.xml";

	@Override
	public void before() throws Exception {
		super.before();
		executeDataSet(PATIENT_LIST_DATASET);
		executeDataSet(TestConstants.CORE_DATASET);

	}

	@Override
	public PatientList createEntity(boolean valid) {
		PatientList patientList = new PatientList();
		if (valid) {
			patientList.setName("Test new patient list");
		}

		patientList.setDescription("new patient list description");
		patientList.setRetired(false);
		return patientList;
	}

	@Override
	protected int getTestEntityCount() {
		return 1;
	}

	@Override
	protected void updateEntityFields(PatientList entity) {}

	@Override
	protected void assertEntity(PatientList expected, PatientList actual) {
		super.assertEntity(expected, actual);
	}
}
