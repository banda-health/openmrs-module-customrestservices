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
	
	var base = angular.module('app.genericEntityController');
	base.controller("EntityController", EntityController);
	EntityController.$inject = ['$stateParams', '$injector', '$scope', '$filter', 'EntityRestFactory', 'PatientListModel', 'PatientListConditionModel', 'EntityFunctions'];
	
	var ENTITY_NAME = "list";
	
	function EntityController($stateParams, $injector, $scope, $filter, EntityRestFactory, PatientListModel, PatientListConditionModel, EntityFunctions) {
		var self = this;
		
		var entity_name_message_key = "patientlist.page";
		
		// @Override
		self.setRequiredInitParameters = self.setRequiredInitParameters || function() {
				self.bindBaseParameters(PATIENT_LIST_MODULE_NAME, ENTITY_NAME, entity_name_message_key, PATIENT_LIST_RELATIVE_CANCEL_PAGE_URL);
				self.checkPrivileges(TASK_MANAGE_PATIENT_LIST_METADATA);
			};
		
		/**
		 * Initializes and binds any required variable and/or function specific to entity.page
		 * @type {Function}
		 */
		// @Override
		self.bindExtraVariablesToScope = self.bindExtraVariablesToScope
			|| function(uuid) {
				/* bind variables.. */
				$scope.uuid = uuid;
				$scope.patientListConditionArray = [];
				self.addPatientListCondition();
				$scope.patientListSortOrderArray = [];
				
				$scope.patientListSortOrder = function (entity) {
					if (entity.ordering.field != null && entity.ordering.sortOrder != null) {
						entity.ordering.id = entity.ordering.field + "_" + entity.ordering.sortOrder;
						self.getNewPatientListSortOrder(entity.ordering);
					}
				}
				
				$scope.patientListCondition = function (entity) {
					if (entity.patientListConditions.field != null && entity.patientListConditions.operator != null && entity.patientListConditions.value != null) {
						entity.patientListConditions.id = entity.patientListConditions.field + "_" + entity.patientListConditions.value;
						self.getNewPatientListCondition(entity.patientListConditions);
					}
				}
			};
		
		
		self.getNewPatientListSortOrder = self.getNewPatientListSortOrder || function (newPatientListSortOrder) {
				var index = EntityFunctions.findIndexByKeyValue($scope.patientListSortOrderArray, newPatientListSortOrder.id);
				if (index < 0) {
					$scope.patientListSortOrderArray.push(newPatientListSortOrder);
				} else {
					$scope.patientListSortOrderArray[index] = newPatientListSortOrder;
				}
				
				/*
				 * This loop is to remove any stock that had the actualQuantity updated and before saving changed again to either a value
				 * equal to null or a value equal to the quantity
				 * */
				for (var i = 0; i < $scope.patientListSortOrderArray.length; i++) {
					$scope.patientListSortOrderArray[i].conditionOrder = EntityFunctions.findIndexByKeyValue($scope.patientListSortOrderArray, $scope.patientListSortOrderArray[i].id);
				}
			};
		
		self.getNewPatientListCondition = self.getNewPatientListCondition || function (newPatientListCondition) {
				var index = EntityFunctions.findIndexByKeyValue($scope.patientListConditionArray, newPatientListCondition.id);
				if (index < 0) {
					$scope.patientListConditionArray.push(newPatientListCondition);
				} else {
					$scope.patientListConditionArray[index] = newPatientListCondition;
				}
				
				/*
				 * This loop is to remove any stock that had the actualQuantity updated and before saving changed again to either a value
				 * equal to null or a value equal to the quantity
				 * */
				for (var i = 0; i < $scope.patientListConditionArray.length; i++) {
					$scope.patientListConditionArray[i].conditionOrder = EntityFunctions.findIndexByKeyValue($scope.patientListConditionArray, $scope.patientListConditionArray[i].id);
				}
			};
			
		/**
		 * All post-submit validations are done here.
		 * @return boolean
		 */
		// @Override
		self.validateBeforeSaveOrUpdate = self.validateBeforeSaveOrUpdate || function() {
				if (!angular.isDefined($scope.entity.name) || $scope.entity.name === '') {
					$scope.submitted = true;
					return false;
				}
				
				var sortOrder = $scope.patientListSortOrderArray;
				for (var i = 0; i < sortOrder.length; i++) {
					delete sortOrder[i]['$$hashKey'];
					delete sortOrder[i]['id'];
				}
				
				var patientListCondition = $scope.patientListConditionArray;
				for (var r = 0; r < patientListCondition.length; r++) {
					delete patientListCondition[r]['$$hashKey'];
					delete patientListCondition[r]['id'];
				}
				
				console.log($scope.patientListConditionArray);
				
				if ($scope.patientListConditionArray.length != 0) {
					$scope.entity = {
						'ordering': sortOrder,
						"name": $scope.entity.name,
						"description": $scope.entity.description,
						"patientListConditions": patientListCondition
					};
					$scope.loading = true;
				} else {
					return false;
				}
				
				return true;
			};
		
		self.addPatientListCondition = self.addPatientListCondition || function() {
				if ($scope.conditionField != null || $scope.conditionOperator != null || $scope.conditionValue != null) {
					var addPatientListCondition = true;
					for (var i = 0; i < $scope.patienListConditions.length; i++) {
						var patientListCondition = $scope.patienListConditions[i];
						if (!patientListCondition.selected) {
							addPatientListCondition = false;
							break;
						}
					}
					if (addPatientListCondition) {
						var patientListCondition = new PatientListConditionModel('', 1, '');
						$scope.patienListConditions.push(patientListCondition);
					}
				}
			};
		
		/* ENTRY POINT: Instantiate the base controller which loads the page */
		$injector.invoke(base.GenericEntityController, self, {
			$scope: $scope,
			$filter: $filter,
			$stateParams: $stateParams,
			EntityRestFactory: EntityRestFactory,
			GenericMetadataModel: PatientListModel
		});
	}
})();
