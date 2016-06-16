/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.patient_list.api;

import static org.junit.Assert.*;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.patient_list.api.model.PatientList;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataServiceTest;

public class PatientListDataServiceTest extends IMetadataDataServiceTest<PatientListDataService, PatientList> {
	@Override
	public PatientList createEntity(boolean b) {
		return null;
	}

	@Override
	protected int getTestEntityCount() {
		return 0;
	}

	@Override
	protected void updateEntityFields(PatientList patientList) {

	}
}
