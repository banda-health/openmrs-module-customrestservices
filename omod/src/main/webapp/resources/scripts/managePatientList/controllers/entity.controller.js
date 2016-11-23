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
				$scope.checkBoxInput = false;
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
					console.log(listCondition.value + "I was clicked");
					if (listCondition.field != null && listCondition.operator != null && listCondition.value != null) {
						listCondition.id = listCondition.field + "_" + listCondition.value;
						listCondition.selected = true;
						self.getNewPatientListCondition(listCondition);
						self.addListCondition();
					}
				};
				
				$scope.inputsValueChange  = function (listCondition) {
					$scope.listConditionId = listCondition.id;
					for (var i = 0; i < $scope.fields.length; i++) {
						var datatype = null;
						if ($scope.fields[i].field == listCondition.field) {
							datatype = $scope.fields[i].desc.dataType;
							
							
							if (datatype == "java.lang.String") {
								$scope.textInput = true;
								$scope.dropdownInput = false;
								$scope.dateInput = false;
								$scope.numberInput = false;
								$scope.checkBoxInput = false;
							} else if (datatype == "java.util.Date") {
								$scope.textInput = false;
								$scope.dropdownInput = false;
								$scope.dateInput = true;
								$scope.numberInput = false;
								$scope.numberInput = false;
							} else if (datatype == "java.lang.Integer") {
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
								} else if (listCondition.field == "p.gender") {
									$scope.conceptAnswers = [{display: 'Female', uuid: "F"}, {display: 'Male', uuid: "M"}];
								}
							} else if (datatype == "java.lang.Boolean") {
								$scope.textInput = false;
								$scope.dropdownInput = false;
								$scope.dateInput = false;
								$scope.numberInput = false;
								$scope.checkBoxInput = true;
							} else {
								$scope.textInput = true;
								$scope.dropdownInput = false;
								$scope.dateInput = false;
								$scope.numberInput = false;
								$scope.checkBoxInput = false;
							}
						}
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
					listCondition.setConditionOrder(selectedListCondition);
					listCondition.setId(selectedListCondition);
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
				$scope.fields = data.results;
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
				
				if ($scope.entity.headerTemplate === "" || angular.isDefined($scope.entity.headerTemplate || $scope.entity.headerTemplate == null)) {
					$scope.entity.headerTemplate = null;
				}

				if ($scope.entity.bodyTemplate === "" || angular.isDefined($scope.entity.bodyTemplate || $scope.entity.bodyTemplate == null)) {
					$scope.entity.bodyTemplate = null;
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
					if (patientListCondition[r].selected == false) {
						patientListCondition.splice(r, 1);
					} else {
						delete patientListCondition[r]['selected'];
						patientListCondition[r]['conditionOrder'] = r;
						
					}
				}
				console.log(sortOrder);
				
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
