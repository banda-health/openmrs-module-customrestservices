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
	EntityController.$inject = ['$stateParams', '$injector', '$scope', '$filter', 'EntityRestFactory', 'PatientListModel',
		'PatientListConditionModel', 'PatientListFunctions', 'EntityFunctions', 'PatientListRestfulService'];
	
	var ENTITY_NAME = "list";
	
	function EntityController($stateParams, $injector, $scope, $filter, EntityRestFactory, PatientListModel,
	                          PatientListConditionModel, PatientListFunctions, EntityFunctions, PatientListRestfulService) {
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
			|| function (uuid) {
				/* bind variables.. */
				var civilStatusUuid = "1054AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
				var conceptAnswersLimit = null;
				$scope.uuid = uuid;
				$scope.listConditions = [];
				$scope.patientListConditionArray = [];
				$scope.patientListSortOrderArray = [];
				$scope.textInput = true;
				$scope.dropdownInput = false;
				$scope.dateInput = false;
				$scope.numberInput = false;
				$scope.radioButtonInput = false;
				$scope.conceptAnswers = [];
				$scope.selectListCondition = self.selectListCondition;
				$scope.removeListCondition = self.removeListCondition;
				PatientListRestfulService.loadFields(self.onLoadFieldsSuccessful);
				
				if ($scope.entity !== undefined) {
					self.addExistingListConditions();
				} else {
					self.addListCondition();
				}
				
				$scope.patientListSortOrder = function (listOrdering) {
					if (listOrdering.field != null && listOrdering.sortOrder != null) {
						listOrdering.id = listOrdering.field + "_" + listOrdering.sortOrder;
						self.getNewPatientListSortOrder(listOrdering);
					}
				};
				
				$scope.patientListCondition = function (listCondition) {
					if (listCondition.field != null && listCondition.operator != null && listCondition.value != null) {
						listCondition.id = listCondition.field + "_" + listCondition.value;
						self.getNewPatientListCondition(listCondition);
						self.addListCondition();
					}
				};
				
				$scope.inputsValueChange  = function (listCondition) {
					if (listCondition.field == "p.given_name" || listCondition.field == "p.family_name"
						|| listCondition.field == "v.note.primary_diagnosis" || listCondition.field == "p.attr.birthplace") {
						$scope.textInput = true;
						$scope.dropdownInput = false;
						$scope.dateInput = false;
						$scope.numberInput = false;
						$scope.radioButtonInput = false;
					} else if (listCondition.field == "p.birth_date" || listCondition.field == "v.start_date"
						|| listCondition.field == "v.end_date") {
						$scope.textInput = false;
						$scope.dropdownInput = false;
						$scope.dateInput = true;
						$scope.numberInput = false;
						$scope.numberInput = false;
					} else if (listCondition.field == "v.vitals.weight" || listCondition.field == "v.attr.ward") {
						$scope.textInput = false;
						$scope.dropdownInput = false;
						$scope.dateInput = false;
						$scope.numberInput = true;
						$scope.numberInput = true;
					} else if (listCondition.field == "p.attr.civil_status" || listCondition.field == "p.gender") {
						$scope.textInput = false;
						$scope.dropdownInput = true;
						$scope.dateInput = false;
						$scope.numberInput = false;
						$scope.numberInput = false;
						
						if (listCondition.field == "p.attr.civil_status") {
							PatientListRestfulService.loadConceptAnswers(PATIENT_LIST_MODULE_NAME, conceptAnswersLimit, civilStatusUuid, self.onConceptAnswersSuccessful);
						} else  if (listCondition.field == "p.gender") {
							$scope.conceptAnswers = [{display: 'Female', uuid: "F" },{ display: 'Male', uuid: "M" }];
						}
					} else {
						$scope.textInput = false;
						$scope.dropdownInput = false;
						$scope.dateInput = false;
						$scope.numberInput = false;
						$scope.radioButtonInput = true;
					}
				};
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
				console.log("Demo Demo Dmeo Dmeo Dmeo Dmoe ")
				var index = EntityFunctions.findIndexByKeyValue($scope.listConditions, newPatientListCondition.id);
				if (index < 0) {
					$scope.listConditions.push(newPatientListCondition);
				} else {
					$scope.listConditions[index] = newPatientListCondition;
				}
				
				/*
				 * This loop is to remove any stock that had the actualQuantity updated and before saving changed again to either a value
				 * equal to null or a value equal to the quantity
				 * */
				for (var i = 0; i < $scope.listConditions.length; i++) {
					$scope.listConditions[i].conditionOrder = EntityFunctions.findIndexByKeyValue($scope.listConditions, $scope.listConditions[i].id);
				}
			};
		
		self.addExistingListConditions = self.addExistingListConditions || function () {
				PatientListFunctions.populateExistingPatientListCondition($scope.entity.patientListConditions, $scope.listConditions, $scope);
			};
		
		self.addListCondition = self.addListCondition || function () {
				console.log("Inside the method ");
				var addListCondition = true;
				for (var i = 0; i < $scope.listConditions.length; i++) {
					var listCondition = $scope.listConditions[i];
					if (!listCondition.selected) {
						addListCondition = false;
						break;
					}
				}
				if (addListCondition) {
					var listCondition = new PatientListConditionModel('', 1, '');
					$scope.listConditions.push(listCondition);
				}
			};
		
		self.removeListCondition = self.removeListCondition || function(listCondition) {
				//only remove selected line items..
				if (listCondition.selected) {
					var index = $scope.listConditions.indexOf(listCondition);
					if (index !== -1) {
						$scope.listConditions.splice(index, 1);
					}
					
					if ($scope.listConditions.length == 0) {
						self.addListCondition();
					}
				}
			};
		
		self.selectListCondition = self.selectListCondition || function(selectedListCondition, listCondition, index) {
				$scope.listCondition = {};
				if (selectedListCondition !== undefined) {
					listCondition.setField(selectedListCondition);
					listCondition.setOperator(selectedListCondition);
					listCondition.setValue(selectedListCondition);
					listCondition.setSelected(true);
					$scope.listCondition = listCondition;
					
					self.addListCondition();
				}
			};
		
		// call-back functions.
		self.onConceptAnswersSuccessful = self.onConceptAnswersSuccessful || function (data) {
				$scope.conceptAnswers = data.answers;
				console.log(data.answers);
			};
		
		// call-back functions.
		self.onLoadFieldsSuccessful = self.onLoadFieldsSuccessful || function (data) {
				$scope.fields = data;
				console.log(data);
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
				
				var patientListCondition = $scope.listConditions;
				for (var r = 0; r < patientListCondition.length; r++) {
					delete patientListCondition[r]['$$hashKey'];
					delete patientListCondition[r]['id'];
					delete patientListCondition[r]['selected'];
				}
				
				if ($scope.listConditions.length != 0) {
					$scope.entity.ordering = sortOrder;
					$scope.entity.patientListConditions = patientListCondition;
					$scope.loading = true;
				} else {
					return false;
				}
				
				return true;
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
