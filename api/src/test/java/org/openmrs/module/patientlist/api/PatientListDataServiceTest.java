package org.openmrs.module.patientlist.api;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataServiceTest;
import org.openmrs.module.patientlist.api.model.PatientList;

public class PatientListDataServiceTest extends IMetadataDataServiceTest<PatientListDataService, PatientList> {
	public static final String PATIENT_LIST_DATASET = TestConstants.BASE_DATASET_DIR + "PatientListTest.xml";

	@Override
	public PatientList createEntity(boolean valid) {
		return null;
	}

	@Override
	protected int getTestEntityCount() {
		return 0;
	}

	@Override
	protected void updateEntityFields(PatientList entity) {

	}

	@Override
	public void before() throws Exception {
		super.before();

		executeDataSet(PATIENT_LIST_DATASET);
	}

}
