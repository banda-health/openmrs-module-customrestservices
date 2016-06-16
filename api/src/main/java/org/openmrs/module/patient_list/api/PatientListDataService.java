package org.openmrs.module.patient_list.api;

import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.patient_list.api.model.PatientList;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface PatientListDataService extends IMetadataDataService<PatientList> {

}
