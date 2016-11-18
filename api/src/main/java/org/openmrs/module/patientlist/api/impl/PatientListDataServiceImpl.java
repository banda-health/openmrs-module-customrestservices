package org.openmrs.module.patientlist.api.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.hql.ast.QuerySyntaxException;
import org.openmrs.Patient;
import org.openmrs.Visit;
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
			Query query = getRepository().createQuery(constructHqlQuery(patientList, paramValues));
			// set parameters with actual values
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
			List results = query.list();
			count = results.size();
			if (count > 0) {
				for (Object result : results) {
					Patient patient;
					Visit visit = null;
					if (result instanceof Patient) {
						patient = (Patient)result;
					} else {
						visit = (Visit)result;
						patient = visit.getPatient();
					}

					PatientListData patientListData = new PatientListData(patient, visit, patientList);
					//applyTemplates(patientListData);
					patientListDataSet.add(patientListData);
				}
			}
		} catch (QuerySyntaxException ex) {
			LOG.error(ex.getMessage());
		}

		return patientListDataSet;
	}

	/**
	 * Constructs a patient list with given conditions (and ordering)
	 * @param patientList
	 * @param paramValues
	 * @return
	 */
	private String constructHqlQuery(PatientList patientList, List<Object> paramValues) {
		StringBuilder hql = new StringBuilder();
		if (patientList != null && patientList.getPatientListConditions() != null) {
			if (searchField(patientList.getPatientListConditions(), "v.")) {
				// join visit and patient tables
				hql.append("select distinct v from Visit v inner join v.patient as p ");
			} else {
				// use only the patient table
				hql.append("select distinct p from Patient p ");
			}

			// only join person attributes and attribute types if required to
			if (searchField(patientList.getPatientListConditions(), "p.attr")) {
				hql.append("inner join p.attributes as attr ");
				hql.append("inner join attr.attributeType as attrType ");
			}

			// only join visit attributes and attribute types if required to
			if (searchField(patientList.getPatientListConditions(), "v.attr")) {
				hql.append("inner join v.attributes as vattr ");
				hql.append("inner join vattr.attributeType as vattrType ");
			}
		}

		// add where clause
		hql.append(" where ");

		// apply patient list conditions
		hql.append("(");
		hql.append(applyPatientListConditions(patientList.getPatientListConditions(), paramValues));
		hql.append(")");

		//apply ordering if any
		hql.append(applyPatientListOrdering(patientList.getOrdering()));

		return hql.toString();
	}

	/**
	 * Parse patient list conditions and add create sub queries to be added on the main HQL query. Parameter search values
	 * will be stored separately and later set when running query.
	 * @param patientListConditions
	 * @param paramValues
	 * @return
	 */
	private String applyPatientListConditions(List<PatientListCondition> patientListConditions,
	        List<Object> paramValues) {
		int count = 0;
		int len = patientListConditions.size();
		StringBuilder hql = new StringBuilder();
		// apply conditions
		for (PatientListCondition condition : patientListConditions) {
			++count;
			if (condition != null) {
				String field = condition.getField();
				if (StringUtils.contains(field, "p.attr.") || StringUtils.contains(field, "v.attr.")) {
					hql.append(createAttributeSubQueries(condition, paramValues));
				} else {
					String fieldName;
					// constructs a valid entity field name
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

	/**
	 * Creates hql sub-queries for patient and visit attributes.
	 * @param condition
	 * @param paramValues
	 * @return
	 */
	private String createAttributeSubQueries(PatientListCondition condition, List<Object> paramValues) {
		StringBuilder hql = new StringBuilder();
		String attributeName = condition.getField().split("\\.")[2];
		attributeName = attributeName.replaceAll("_", " ").toLowerCase();

		if (StringUtils.contains(condition.getField(), "p.attr.")) {
			hql.append("(lower(attrType.name) = ?");
			hql.append(" AND ");
			hql.append("attr.value ");
		} else if (StringUtils.contains(condition.getField(), "v.attr.")) {
			hql.append("(lower(vattrType.name) = ?");
			hql.append(" AND ");
			hql.append("vattr.valueReference ");
		}

		hql.append(ConvertPatientListOperators.convertOperator(condition.getOperator()));
		hql.append(" ? ");

		paramValues.add(attributeName);
		paramValues.add(condition.getValue());
		hql.append(") ");

		return hql.toString();
	}

	/**
	 * Order hql query by given fields
	 * @param ordering
	 * @return
	 */
	private String applyPatientListOrdering(List<PatientListOrder> ordering) {
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
				hql.append(",");
			}
		}

		//remove trailing coma.
		return StringUtils.removeEnd(hql.toString(), ",");
	}

	/**
	 * Searches for a given field in the patient list conditions.
	 * @param patientListConditions
	 * @param search
	 * @return
	 */
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

	/**
	 * Apply header and body templates on patient list data
	 * @param patientListData
	 */
	private void applyTemplates(PatientListData patientListData) {
		// apply header template.
		patientListData.setHeaderContent(
		        applyTemplates(patientListData.getPatientList().getHeaderTemplate(), patientListData));

		// apply body template
		patientListData.setBodyContent(
		        applyTemplates(patientListData.getPatientList().getBodyTemplate(), patientListData));
	}

	private String applyTemplates(String template, PatientListData patientListData) {
		String[] fields = StringUtils.substringsBetween(template, "[[", "]]");
		for (String field : fields) {
			Object value = new Object();
			if (StringUtils.contains(field, "p.")) {
				value = ConvertPatientListFields.getFieldValue(
				    Patient.class, field, patientListData.getPatient().getPerson());
			} else if (StringUtils.contains(field, "v.")) {
				value = ConvertPatientListFields.getFieldValue(
				    Visit.class, field, patientListData.getVisit());
			}

			if (value != null) {
				template = StringUtils.replace(template, "[[" + field + "]]", value.toString());
			}
		}

		return template;
	}
}
