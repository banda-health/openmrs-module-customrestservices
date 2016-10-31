<script type="text/javascript">
	var breadcrumbs = [
		{icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
		{
			label: "${ ui.message("coreapps.app.systemAdministration.label")}",
			link: '${ui.pageLink("coreapps", "systemadministration/systemAdministration")}'
		},
		{
			label: "${ ui.message("patientlist.page")}",
			link: '/' + OPENMRS_CONTEXT_PATH + '/patientlist/managePatientList/entities.page#/'
		},
		{label: "${ ui.message("patientlist.define.label")}"}
	];
	
	jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));
</script>

<div ng-show="loading" class="loading-msg">
	<span>${ui.message("openhmis.commons.general.processingPage")}</span>
	<br/>
	<span class="loading-img">
		<img src="${ui.resourceLink("uicommons", "images/spinner.gif")}"/>
	</span>
</div>

<form ng-hide="loading" name="entityForm" class="entity-form" ng-class="{'submitted': submitted}" style="font-size:inherit">
	${ui.includeFragment("openhmis.commons", "editEntityHeaderFragment")}
	
	<input type="hidden" ng-model="entity.uuid"/>
	<div class="row">
		<ul class="table-layout">
			<li class="required">
				<span>${ui.message("general.name")}</span>
			</li>
			<li>
				<input name="entityName" type="text" ng-model="entity.name" class="maximized"
				       placeholder="${ui.message("general.name")}" required/>
			</li>
		</ul>
		<ul class="table-layout">
			<li style="vertical-align: top" class="not-required">
				<span>${ui.message("general.description")}</span>
			</li>
			<li>
				<textarea ng-model="entity.description" placeholder="${ui.message("general.description")}" rows="3"
				          cols="40">
				</textarea>
			</li>
		</ul>
	</div>
	<br/>
	<div class="row detail-section-border-top">
		<br/>
		<div class="col-md-12">
			<table style="margin-bottom:5px;" class="manage-entities-table">
				<thead>
				<tr>
					<th>${ui.message("patientlist.condition.feild.label")}</th>
					<th>${ui.message("patientlist.condition.operator.label")}</th>
					<th>${ui.message("patientlist.condition.value.label")}</th>
				</tr>
				</thead>
				<tbody>
				<tr ng-repeat="listCondition in entity.patientListConditions">
					<td>
						<input class="form-control input-sm" type="text"
						       ng-model="listCondition.field"
						       placeholder="${ui.message("patientlist.condition.feild.label")}"/>
					</td>
					<td>
						<select class="form-control" ng-model="listCondition.operator">
							<option value="">--Select Operator--</option>
							<option value="EQUALS">Equals</option>
							<option value="NOT_EQUALS">Not Equals</option>
							<option value="GT">Greater than</option>
							<option value="LT">Less than</option>
							<option value="GTE">Greater than or Equals</option>
							<option value="LTE">Less than or Equals</option>
							<option value="LIKE">Like</option>
							<option value="BETWEEN">Between</option>
							<option value="NULL">Empty</option>
							<option value="NOT NULL">Not Empty</option>
						</select>
					</td>
					<td>
						<input name="conditionValue" placeholder="${ui.message("patientlist.condition.value.label")}"
						       class="form-control input-sm" type="text" ng-model="listCondition.value"
						       ng-blur="patientListCondition(listCondition)" ng-enter="addPatientListCondition(entity)"/>
					</td>
				</tr>
				</tbody>
			</table>
		</div>
	</div>
	<br/>
	
	<div class="row detail-section-border-top">
		<br/>
		
		<div class="col-md-12">
			<table style="margin-bottom:5px;" class="manage-entities-table ">
				<thead>
				<tr>
					<th>${ui.message("patientlist.sort.order.feild.label")}</th>
					<th>${ui.message("patientlist.sort.order.sortOrder.label")}</th>
				</tr>
				</thead>
				<tbody>
				<tr ng-repeat="listOrdering in entity.ordering">
					<td>
						<input class="form-control input-sm" type="text" ng-model="listOrdering.field"
						       placeholder="${ui.message("patientlist.sort.order.feild.label")}"/>
					</td>
					<td>
						<select class="form-control" ng-model="listOrdering.sortOrder"
						        ng-change="patientListSortOrder(listOrdering)">
							<option value="">--Select Sort Order--</option>
							<option value="0">Ascending</option>
							<option value="1">Descending</option>
						</select>
					</td>
				</tr>
				</tbody>
			</table>
		</div>
	</div>
	<br/>
	<div class="row detail-section-border-top">
		<br/>
		<div class="col-md-6">
			<input type="button" class="cancel" value="${ui.message("general.cancel")}" ng-click="cancel()"/>
		</div>
		<div class="col-md-6">
			<input type="button" class="confirm right" value="${ui.message("general.save")}" ng-click="saveOrUpdate()"/>
		</div>
	</div>
</form>
${ui.includeFragment("openhmis.commons", "retireUnretireDeleteFragment")}
