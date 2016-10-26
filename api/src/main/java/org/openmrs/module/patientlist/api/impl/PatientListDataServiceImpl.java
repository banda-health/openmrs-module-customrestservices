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
import org.openmrs.module.patientlist.api.model.PatientListOrder;
import org.openmrs.module.patientlist.api.security.BasicObjectAuthorizationPrivileges;
import org.openmrs.module.patientlist.api.util.ConvertPatientListFields;
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
			List<Object> paramValues = new ArrayList<Object>();
			// Create query
			String mainQuery = constructQuery(patientList, paramValues);
			Query query = getRepository().createQuery(mainQuery);
			if (paramValues.size() > 0) {
				int index = 0;
				for (Object value : paramValues) {
					query.setParameter(index++, value);
				}
			}

			// set paging params
			Integer count = query.list().size();
			pagingInfo.setTotalRecordCount(count.longValue());
			pagingInfo.setLoadRecordCount(false);

			query = this.createPagingQuery(pagingInfo, query);
			List<Patient> results = query.list();
			count = results.size();
			if (count > 0) {
				if (visitService == null) {
					visitService = Context.getVisitService();
				}

				for (Patient patient : results) {
					Visit visit = null;
					List<Visit> activeVisits = visitService.getActiveVisitsByPatient(patient);
					if (activeVisits.size() > 0) {
						visit = activeVisits.get(0);
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

	private String constructQuery(PatientList patientList, List<Object> paramValues) {
		StringBuilder hql = new StringBuilder("select distinct p from Patient p ");
		if (patientList != null && patientList.getPatientListConditions() != null) {
			// add visit table
			boolean hasVisit = searchField(patientList.getPatientListConditions(), "v.");
			if (hasVisit) {
				hql.append(", Visit v ");
			}

			if (searchField(patientList.getPatientListConditions(), "p.attr")) {
				hql.append(", ConceptName cn");
				hql.append("inner join p.attributes as attr ");
				hql.append("inner join attr.attributeType as attrType ");
			}

			if (searchField(patientList.getPatientListConditions(), "v.attr")) {
				hql.append("inner join v.attributes as vattr ");
				hql.append("inner join vattr.attributeType as vattrType ");
			}

			// join visit table
			if (hasVisit) {
				hql.append("where v.patient = p ");
			}
		}

		if (!StringUtils.contains(hql.toString(), "where")) {
			hql.append(" where ");
		} else {
			hql.append(" and ");
		}

		//apply conditions
		hql.append("(");
		hql.append(applyConditions(patientList.getPatientListConditions(), paramValues));
		hql.append(")");

		//apply ordering
		hql.append(applyOrdering(patientList.getOrdering()));

		return hql.toString();
	}

	private String applyConditions(List<PatientListCondition> patientListConditions, List<Object> paramValues) {
		int count = 0;
		int len = patientListConditions.size();
		StringBuilder hql = new StringBuilder();
		// apply conditions
		for (PatientListCondition condition : patientListConditions) {
			++count;
			if (condition != null) {
				String field = condition.getField();
				if (StringUtils.contains(field, "p.attr.") || StringUtils.contains(field, "v.attr.")) {
					hql.append(handleAttributes(condition, paramValues));
				} else {
					String fieldName;
					if (StringUtils.contains(field, "p.")) {
						fieldName = ConvertPatientListFields.convert(Patient.class, condition.getField());
					} else if (StringUtils.contains(field, "v.")) {
						fieldName = ConvertPatientListFields.convert(Visit.class, condition.getField());
					} else {
						continue;
					}

					if (fieldName == null) {
						LOG.error("Unknown field name: " + condition.getField());
						continue;
					}

					hql.append(fieldName);
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
				}

				if (count < len) {
					hql.append(" or ");
				}
			}
		}

		return hql.toString();
	}

	private String handleAttributes(PatientListCondition condition, List<Object> paramValues) {
		StringBuilder hql = new StringBuilder();
		String attributeName = condition.getField().split("\\.")[2];
		attributeName = attributeName.replaceAll("_", " ").toLowerCase();

		if (StringUtils.contains(condition.getField(), "p.attr.")) {
			hql.append("(lower(attrType.name) = ?");
			//hql.append(" AND ");
			//hql.append("((CONVERT('cn.conceptNameId' AS CHAR) = attr.value");
			hql.append(" AND ");
			//hql.append("LCASE(cn.name) = ?) OR ");
			hql.append("attr.value ");
		} else {
			hql.append("(lower(vattrType.name) = ?");
			hql.append(" AND ");
			hql.append("vattr.valueReference ");
		}

		hql.append(ConvertPatientListOperators.convertOperator(condition.getOperator()));
		hql.append(" ? ");

		paramValues.add(attributeName);
		//paramValues.add(condition.getValue().toLowerCase());
		paramValues.add(condition.getValue());
		hql.append(") ");

		return hql.toString();
	}

	private String applyOrdering(List<PatientListOrder> ordering) {
		//apply ordering
		int count = 0;
		StringBuilder hql = new StringBuilder();
		for (PatientListOrder order : ordering) {
			if (order != null) {
				hql.append(" ");
				if (count++ == 0) {
					hql.append("order by ");
				}

				hql.append(order.getField());
				hql.append(" ");
				hql.append(order.getSortOrder());
				//hql.append(",");
			}
		}

		//remove trailing coma.
		//StringUtils.removeEnd(hql.toString(), ",");
		//LOG.warn(hql.toString());

		return hql.toString();
	}

	private boolean searchField(List<PatientListCondition> patientListConditions, String search) {
		for (PatientListCondition patientListCondition : patientListConditions) {
			if (patientListCondition == null) {
				continue;
			}

			if (StringUtils.contains(patientListCondition.getField(), search)) {
				return true;
			}
		}

		return false;
	}
}
