package org.openmrs.module.patientlist.api.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.op.Operator;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.patientlist.api.IPatientListDataService;
import org.openmrs.module.patientlist.api.IPatientListService;
import org.openmrs.module.patientlist.api.IPatientListDataServiceTest;
import org.openmrs.module.patientlist.api.model.*;

import java.util.List;

public class PatientListDataServiceImplTest extends IPatientListDataServiceTest {

	private IPatientListService patientListService;
	private IPatientListDataService patientListDataService;
	private IPatientListDataServiceTest patientListDataServiceTest;
	private PatientService patientService;
	private VisitService visitService;

	@Before
	public void before() throws Exception {
		super.before();

		patientListService = createService();
		patientListDataServiceTest = new IPatientListDataServiceTest();
		patientListDataService = Context.getService(IPatientListDataService.class);
		patientService = Context.getPatientService();
		visitService = Context.getVisitService();
	}

	@Override
	protected IPatientListService createService() {
		return Context.getService(IPatientListService.class);
	}

	@Test
	public void patientList_shouldCreateSingleSelectionRuleWithoutSortOrder() throws Exception {
		PatientList patientList = patientListDataServiceTest.createEntity(true);

		PatientListCondition selectionRule = new PatientListCondition();
		selectionRule.setField("p.age");
		selectionRule.setOperator(PatientListOperator.GT);
		selectionRule.setValue("20");
		selectionRule.setConditionOrder(1);

		patientList.addCondition(selectionRule);

		patientListService.save(patientList);
		Context.flushSession();

		Assert.assertNotNull(patientList);
		Assert.assertEquals("p.age", patientList.getPatientListConditions().get(0).getField());
	}

	@Test
	public void patientList_shouldCreateMultipleSelectionRulesWithoutSortOrder() throws Exception {
		PatientList patientList = patientListDataServiceTest.createEntity(true);

		PatientListCondition selectionRule = new PatientListCondition();
		selectionRule.setField("p.age");
		selectionRule.setOperator(PatientListOperator.GT);
		selectionRule.setValue("20");
		selectionRule.setConditionOrder(1);

		patientList.addCondition(selectionRule);

		selectionRule = new PatientListCondition();
		selectionRule.setField("p.gender");
		selectionRule.setOperator(PatientListOperator.EQUALS);
		selectionRule.setValue("M");
		selectionRule.setConditionOrder(2);

		patientList.addCondition(selectionRule);

		patientListService.save(patientList);
		Context.flushSession();

		Assert.assertNotNull(patientList);
		Assert.assertEquals("p.gender", patientList.getPatientListConditions().get(1).getField());
	}

	@Test
	public void patientList_shouldCreateSingleSelectionRuleWithSortOrder() throws Exception {
		PatientList patientList = patientListDataServiceTest.createEntity(true);

		PatientListCondition selectionRule = new PatientListCondition();
		selectionRule.setField("p.age");
		selectionRule.setOperator(PatientListOperator.GT);
		selectionRule.setValue("20");
		selectionRule.setConditionOrder(1);

		patientList.addCondition(selectionRule);

		PatientListOrder order = new PatientListOrder();
		order.setField("p.name");
		order.setSortOrder("asc");
		order.setConditionOrder(1);

		patientList.addSortOrder(order);

		patientListService.save(patientList);
		Context.flushSession();

		Assert.assertNotNull(patientList);
		Assert.assertEquals("asc", patientList.getOrdering().get(0).getSortOrder());
	}

	@Test
	public void patientList_shouldCreateSingleSelectionRuleWithMultipleSortOrders() throws Exception {
		PatientList patientList = patientListDataServiceTest.createEntity(true);

		PatientListCondition selectionRule = new PatientListCondition();
		selectionRule.setField("p.age");
		selectionRule.setOperator(PatientListOperator.GT);
		selectionRule.setValue("20");
		selectionRule.setConditionOrder(1);

		patientList.addCondition(selectionRule);

		PatientListOrder order = new PatientListOrder();
		order.setField("p.name");
		order.setSortOrder("asc");
		order.setConditionOrder(1);

		patientList.addSortOrder(order);

		order = new PatientListOrder();
		order.setField("p.id");
		order.setSortOrder("asc");
		order.setConditionOrder(2);

		patientList.addSortOrder(order);

		patientListService.save(patientList);
		Context.flushSession();

		Assert.assertNotNull(patientList);
		Assert.assertEquals("p.id", patientList.getOrdering().get(1).getField());
	}

	@Test
	public void patientList_shouldCreateListWithSinglePatientDetail() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition givenNameCondition = conditions.get(0);

		Assert.assertEquals("p.givenName", givenNameCondition.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(givenNameCondition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(2, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCreateListWithMultiplePatientDetails() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition givenNameCondition = conditions.get(0);
		PatientListCondition ageCondition = conditions.get(8);

		Assert.assertEquals("p.givenName", givenNameCondition.getField());
		Assert.assertEquals("p.age", ageCondition.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(givenNameCondition);
		patientList.getPatientListConditions().add(ageCondition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(1, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCreateListWithSinglePersonAttribute() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();
		PatientListCondition condition = conditions.get(9);

		Assert.assertEquals("p.attr.Phone", condition.getField());

		patientList.getPatientListConditions().clear();
		patientList.getPatientListConditions().add(condition);

		PagingInfo pagingInfo = new PagingInfo();
		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		Assert.assertNotNull(patientListDataSet);
		Assert.assertEquals(1, patientListDataSet.size());
	}

	@Test
	public void patientList_shouldCreateListWithMultiplePersonAttributes() throws Exception {}

	@Test
	public void patientList_shouldCreateListWithSingleVisitDetail() throws Exception {}

	@Test
	public void patientList_shouldCreateListWithSingleVisitAttribute() throws Exception {}

	@Test
	public void patientList_shouldCreateListWithMultipleVisitAttributes() throws Exception {}

	@Test
	public void patientList_shouldCreatePatientListWithSingleConditionSingleSortOrder() throws Exception {}

	@Test
	public void patientList_shouldCreatePatientListWithMultipleConditionsSingleSortOrder() throws Exception {}

	@Test
	public void patientList_shouldCreatePatientListWithSingleConditionMultipleSortOrder() throws Exception {}

	@Test
	public void patientList_shouldCreatePatientListWithMultipleConditionsMultipleSortOrder() throws Exception {}

	@Test
	public void patientList_shouldCreatePatientListWithNotEqualOperator() throws Exception {}

	@Test
	public void patientList_shouldCreatePatientListWithGreaterThanOperator() throws Exception {}

	@Test
	public void patientList_shouldCreatePatientListWithGreaterThanEqualOperator() throws Exception {}

	@Test
	public void patientList_shouldCreatePatientListWithLesserThanOperator() throws Exception {}

	@Test
	public void patientList_shouldCreatePatientListWithLesserThanEqualOperator() throws Exception {}

	@Test
	public void patientList_shouldCreatePatientListWithLikeOperator() throws Exception {}

	@Test
	public void patientList_shouldCreatePatientListWithBetweenOperator() throws Exception {}

	@Test
	public void patientList_shouldCreatePatientListWithNullOperator() throws Exception {}

	@Test
	public void patientList_shouldCreatePatientListWithNotNullOperator() throws Exception {}

	@Test
	public void patientList_shouldCreatePatientListWithDefinedOperator() throws Exception {}

	@Test
	public void patientList_shouldCreatePatientListWithNotDefinedOperator() throws Exception {}
}
