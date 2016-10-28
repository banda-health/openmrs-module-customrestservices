package org.openmrs.module.patientlist.api.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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

	@Before
	public void before() throws Exception {
		super.before();

		patientListService = createService();
		patientListDataServiceTest = new IPatientListDataServiceTest();
		patientListDataService = Context.getService(IPatientListDataService.class);
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
	public void patientList_shouldCreateListWithSinglePatientDetail() throws Exception {}

	@Test
	public void patientList_shouldCreateListWithMultplePatientDetails() throws Exception {}

	@Test
	public void patientList_shouldCreateListWithSinglePatientAttribute() throws Exception {}

	@Test
	public void patientList_shouldCreateListWithMultiplePatientAttributes() throws Exception {}

	@Test
	public void patientList_shouldCreateListWithSingleVisitDetail() throws Exception {}

	@Test
	public void patientList_shouldCreateListWithMultiplePatientDetails() throws Exception {}

	@Test
	public void patientList_shouldCreateListWithSingleVisitAttribute() throws Exception {}

	@Test
	public void patientList_shouldCreateListWithMultipleVisitAttributes() throws Exception {}

	@Test
	public void patientList_shouldCreatePatientListWithMultipleConditions() throws Exception {
		PatientList patientList = patientListService.getById(0);

		List<PatientListCondition> conditions = patientList.getPatientListConditions();

		Assert.assertEquals(3, conditions.size());

		//PatientListCondition patientAttributeCondition = conditions.get(2);

		PagingInfo pagingInfo = new PagingInfo();

		List<PatientListData> patientListDataSet = patientListDataService.getPatientListData(patientList, pagingInfo);

		//Assert.assertEquals(1, patientListDataSet.size());
		Assert.assertNotNull(patientListDataSet);

	}
}
