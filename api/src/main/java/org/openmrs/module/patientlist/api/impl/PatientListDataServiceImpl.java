package org.openmrs.module.patientlist.api.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.hql.ast.QuerySyntaxException;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseObjectDataServiceImpl;
import org.openmrs.module.patientlist.api.IPatientListDataService;
import org.openmrs.module.patientlist.api.model.PatientList;
import org.openmrs.module.patientlist.api.model.PatientListCondition;
import org.openmrs.module.patientlist.api.model.PatientListData;
import org.openmrs.module.patientlist.api.security.BasicObjectAuthorizationPrivileges;
import org.openmrs.module.patientlist.api.util.ConvertPatientListOperators;

import java.util.ArrayList;
import java.util.List;

/**
 * Data service implementation class for {@link PatientListData}'s.
 */
public class PatientListDataServiceImpl extends
        BaseObjectDataServiceImpl<PatientListData, BasicObjectAuthorizationPrivileges>
        implements IPatientListDataService {

	protected final Log LOG = LogFactory.getLog(this.getClass());

	private VisitService visitService;

	@Override
	protected BasicObjectAuthorizationPrivileges getPrivileges() {
		return new BasicObjectAuthorizationPrivileges();
	}

	@Override
	protected void validate(PatientListData object) {
		return;
	}

	@Override
	public List<PatientListData> getPatientListData(PatientList patientList, PagingInfo pagingInfo) {
		List<PatientListData> results = new ArrayList<PatientListData>();
		try {
			Integer count = 0;
			List<Patient> patients;
			// Create the query
			StringBuilder hql = new StringBuilder("select p from Patient p");
			if (patientList != null && patientList.getPatientListConditions() != null
			        && searchVisitField(patientList.getPatientListConditions())) {
				hql.append(" inner join Visit v ");
			}

			hql.append(" where ");
			int len = patientList.getPatientListConditions().size();
			for (PatientListCondition condition : patientList.getPatientListConditions()) {
				++count;
				if (condition != null) {
					//Criteria criteria = getRepository().createCriteria(getEntityClass());
					//criteria.add(Restrictions.eq("patient.id", patientId));
					hql.append(condition.getField());
					hql.append(" ");
					hql.append(ConvertPatientListOperators.convertOperator(condition.getOperator()));
					hql.append(" ");
					if (StringUtils.isNotEmpty(condition.getValue())) {
						if (condition.getValue() instanceof String) {
							hql.append("'" + condition.getValue() + "'");
						} else {
							hql.append(condition.getValue());
						}
					}
					if (count < len) {
						hql.append(" or ");
					}
				}
			}

			Query query = getRepository().createQuery(hql.toString());

			// set paging params
			count = query.list().size();
			pagingInfo.setTotalRecordCount(count.longValue());
			pagingInfo.setLoadRecordCount(false);

			query = this.createPagingQuery(pagingInfo, query);
			patients = query.list();
			count = patients.size();
			if (count > 0) {
				if (visitService == null) {
					visitService = Context.getVisitService();
				}

				for (Patient patient : patients) {
					Visit activeVisit = null;
					List<Visit> activeVisits = visitService.getActiveVisitsByPatient(patient);
					if (activeVisits.size() > 0) {
						activeVisit = activeVisits.get(0);
					}

					PatientListData patientListData = new PatientListData(patient, activeVisit, patientList);
					results.add(patientListData);
				}
			}
		} catch (QuerySyntaxException ex) {
			LOG.error(ex.getMessage());
		}

		return results;
	}

	private boolean searchVisitField(List<PatientListCondition> patientListConditions) {
		for (PatientListCondition patientListCondition : patientListConditions) {
			if (patientListCondition == null) {
				continue;
			}

			if (StringUtils.contains(patientListCondition.getField(), "v.")) {
				return true;
			}
		}

		return false;
	}
}
