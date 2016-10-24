package org.openmrs.module.patientlist.api.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Data service implementation class for {@link PatientListData}'s.
 */
public class PatientListDataServiceImpl extends
        BaseObjectDataServiceImpl<PatientListData, BasicObjectAuthorizationPrivileges>
        implements IPatientListDataService {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

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
		List<PatientListData> patientListDataSet = new ArrayList<PatientListData>();
		try {
			Integer count = 0;
			// Create the query
			StringBuilder hql = new StringBuilder("select distinct p from Patient p inner join p.attributes as attr");
			if (patientList != null && patientList.getPatientListConditions() != null
			        && searchVisitField(patientList.getPatientListConditions())) {
				hql = new StringBuilder("select distinct v from Visit v inner join v.patient p ");
			}

			hql.append(" where ");
			int len = patientList.getPatientListConditions().size();
			List<Object> paramValues = new ArrayList<Object>();
			for (PatientListCondition condition : patientList.getPatientListConditions()) {
				++count;
				if (condition != null) {
					hql.append(condition.getField());
					hql.append(" ");
					hql.append(ConvertPatientListOperators.convertOperator(condition.getOperator()));
					hql.append(" ");
					if (StringUtils.isNotEmpty(condition.getValue())) {
						hql.append("?");
						if (StringUtils.contains(condition.getField().toLowerCase(), "date")) {
							try {
								paramValues.add(sdf.parse(condition.getValue()));
							} catch (ParseException pex) {
								paramValues.add(condition.getValue());
							}
						} else {
							paramValues.add(condition.getValue());
						}
					}

					if (count < len) {
						hql.append(" or ");
					}
				}
			}

			Query query = getRepository().createQuery(hql.toString());
			if (paramValues.size() > 0) {
				int index = 0;
				for (Object value : paramValues) {
					query.setParameter(index++, value);
				}
			}

			// set paging params
			count = query.list().size();
			pagingInfo.setTotalRecordCount(count.longValue());
			pagingInfo.setLoadRecordCount(false);

			query = this.createPagingQuery(pagingInfo, query);
			List results = query.list();
			count = results.size();
			if (count > 0) {
				if (visitService == null) {
					visitService = Context.getVisitService();
				}

				for (Object result : results) {
					Visit visit = null;
					Patient patient;
					if (result instanceof Visit) {
						visit = (Visit)result;
						patient = visit.getPatient();
					} else {
						patient = (Patient)result;
						List<Visit> activeVisits = visitService.getActiveVisitsByPatient(patient);
						if (activeVisits.size() > 0) {
							visit = activeVisits.get(0);
						}
					}

					PatientListData patientListData = new PatientListData(patient, visit, patientList);
					patientListDataSet.add(patientListData);
				}
			}
		} catch (QuerySyntaxException ex) {
			LOG.error(ex.getMessage());
		}

		return patientListDataSet;
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
