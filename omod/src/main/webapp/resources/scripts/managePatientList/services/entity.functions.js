/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 *
 */

(function() {
	'use strict';
	
	var app = angular.module('app.patientListFunctionsFactory', []);
	app.service('PatientListFunctions', PatientListFunctions);
	
	PatientListFunctions.$inject = ['EntityFunctions', 'PatientListConditionModel'];
	
	function PatientListFunctions(EntityFunctions, PatientListConditionModel) {
		var service;
		
		service = {
			populateExistingPatientListCondition: populateExistingPatientListCondition
		};
		
		return service;
		
		function populateExistingPatientListCondition(listConditions, populatedListConditions, $scope) {
			for (var i = 0; i < listConditions.length; i++) {
				var listCondition = listConditions[i];
				var listConditionModel = new PatientListConditionModel(listCondition.field, listCondition.operator, listCondition.value);
				listConditionModel.setSelected(true);
				populatedListConditions.push(listConditionModel);
				
				$scope.listConditon= listConditionModel;
				
				// load item details
				//$scope.loadItemDetails(listCondition.item.uuid, listConditionModel);
			}
		}
	}
})();
