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
		'PatientListConditionModel', 'PatientListFunctions', 'EntityFunctions', 'PatientListRestfulService', 'PatientListOrderingModel',
		'$timeout', '$sce'];

	var ENTITY_NAME = "list";

	function EntityController($stateParams, $injector, $scope, $filter, EntityRestFactory, PatientListModel,
	                          PatientListConditionModel, PatientListFunctions, EntityFunctions, PatientListRestfulService,
	                          PatientListOrderingModel, $timeout, $sce) {
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
				PatientListRestfulService.loadFields(self.onLoadFieldsSuccessful);
				$scope.listConditions = [];
				$scope.listOrderings = [];
				$scope.dropDownEntries = [];
				$scope.removeListCondition = self.removeListCondition;
				$scope.removeListOrdering = self.removeListOrdering;
				$scope.onListConditionDateSuccessfulCallback = self.onListConditionDateSuccessfulCallback;
				// auto-complete search concept function
				$scope.searchConcepts = function(search) {
					return PatientListRestfulService.searchConcepts(PATIENT_LIST_MODULE_NAME, search);
				};
				
				if($scope.entity != undefined) {
					self.addExistingListConditions();
					if($scope.entity.ordering.length > 0) {
						self.addExistingListOrdering();
					} else {
						self.addListOrdering();
					}
					self.livePreview($scope.entity.headerTemplate, $scope.entity.bodyTemplate);
					self.renderTemplate($scope.entity.headerTemplate);
					self.renderTemplate($scope.entity.bodyTemplate);
				} else {
					self.addListCondition();
					self.addListOrdering();
				}
				
				if (uuid == undefined) {
					PatientListRestfulService.preLoadDefaultDisplayTemplate(self.onPreLoadDefaultDisplayTemplateSuccessful);
				}

				$scope.patientListSortOrder = function(listOrdering) {
					if(listOrdering.field != null && listOrdering.sortOrder != null) {
						listOrdering.id = listOrdering.field + "_" + listOrdering.sortOrder;
						listOrdering.selected = true;
						self.getNewPatientListSortOrder(listOrdering);
						self.addListOrdering();
					}
				};

				$scope.patientListCondition = function(listCondition) {
					if(listCondition.field != "" && listCondition.operator != "" && listCondition.value != "") {
						listCondition.id = listCondition.field + "_" + listCondition.value;
						listCondition.selected = true;
						self.getNewPatientListCondition(listCondition);
						self.addListCondition();
					}
					
					if (listCondition.dataType == "org.openmrs.Location") {
						self.selectLocation(listCondition);
					}
				};

				$scope.inputsValueChange = function(listCondition) {
					if (listCondition.value != null) {
						$scope.resetValue(listCondition);
					}
					for(var i = 0; i < $scope.fields.length; i++) {
						var datatype = null;
						if($scope.fields[i].field == listCondition.field) {
							datatype = $scope.fields[i].desc.dataType;
							$scope.valueInputConditions(datatype, listCondition);
						}
					}
				};

				$scope.valueInputConditions = function(datatype, listCondition) {
					if(datatype == "java.lang.String") {
						listCondition.inputType = "textInput";
					} else if(datatype == "java.util.Date") {
						listCondition.inputType = "dateInput";
						PatientListFunctions.onChangeDatePicker(self.onListConditionDateSuccessfulCallback, undefined, listCondition);
					} else if(datatype == "java.lang.Boolean" || datatype == "org.openmrs.customdatatype.datatype.BooleanDatatype") {
						listCondition.inputType = "checkBoxInput"
					} else if(listCondition.field == "p.gender") {
						listCondition.inputType = "dropDownInput";
						$scope.dropDownEntries = [{display: 'Female', value: "F"}, {display: 'Male', value: "M"}];
					} else if(datatype == "org.openmrs.Location") {
						listCondition.inputType = "dropDownInput";
						PatientListRestfulService.loadLocations(PATIENT_LIST_MODULE_NAME, self.onLoadLocationsSuccessful);
						listCondition.dataType = "org.openmrs.Location";
					} else if(datatype == "org.openmrs.Concept") {
						listCondition.inputType = "conceptInput";
					} else if(datatype == "java.lang.Integer") {
						listCondition.inputType = "numberInput";
					} else {
						listCondition.inputType = "textInput";
					}
				};
				
				$scope.resetValue = function (listCondition) {
					listCondition.value = ""
				};
				

				$scope.livePreview = self.livePreview;
				$scope.renderTemplate = self.renderTemplate;

				$scope.selectConcept = self.selectConcept;
				$scope.getConceptName = self.getConceptName;
				$scope.getLocationUuid = self.getLocationUuid
			};

		self.getNewPatientListSortOrder = self.getNewPatientListSortOrder || function(newPatientListSortOrder) {
				var index = EntityFunctions.findIndexByKeyValue($scope.listOrderings, newPatientListSortOrder.id);
				if(index < 0) {
					$scope.listOrderings.push(newPatientListSortOrder);
				} else {
					$scope.listOrderings[index] = newPatientListSortOrder;
				}
			};

		self.getNewPatientListCondition = self.getNewPatientListCondition || function(newPatientListCondition) {
				var index = EntityFunctions.findIndexByKeyValue($scope.listConditions, newPatientListCondition.id);
				if(index < 0) {
					$scope.listConditions.push(newPatientListCondition);
				} else {
					$scope.listConditions[index] = newPatientListCondition;
				}
			};

		self.addExistingListConditions = self.addExistingListConditions || function() {
				PatientListFunctions.populateExistingPatientListCondition($scope.entity.patientListConditions, $scope.listConditions, $scope);
				self.addListCondition();
			};

		self.addExistingListOrdering = self.addExistingListOrdering || function() {
				PatientListFunctions.populateExistingPatientListOrdering($scope.entity.ordering, $scope.listOrderings, $scope);
				self.addListOrdering();
			};

		self.addListCondition = self.addListCondition || function() {
				var addListCondition = true;
				for(var i = 0; i < $scope.listConditions.length; i++) {
					var listCondition = $scope.listConditions[i];
					if(!listCondition.selected) {
						addListCondition = false;
						break;
					}
				}
				if(addListCondition) {
					var listCondition = new PatientListConditionModel('', '', '', 'textInput');
					$scope.listConditions.push(listCondition);
				}
			};

		self.addListOrdering = self.addListOrdering || function() {
				var addListOrdering = true;
				for(var i = 0; i < $scope.listOrderings.length; i++) {
					var listOrdering = $scope.listOrderings[i];
					if(!listOrdering.selected) {
						addListOrdering = false;
						break;
					}
				}
				if(addListOrdering) {
					var listOrdering = new PatientListOrderingModel('', '');
					$scope.listOrderings.push(listOrdering);
				}
			};

		self.removeListCondition = self.removeListCondition || function(listCondition) {
				//only remove selected line items..
				if(listCondition.selected) {
					var index = $scope.listConditions.indexOf(listCondition);
					if(index !== -1) {
						$scope.listConditions.splice(index, 1);
					}

					if($scope.listConditions.length == 0) {
						self.addListCondition();
					}
				}
			};

		self.removeListOrdering = self.removeListOrdering || function(listOrdering) {
				//only remove selected line items..
				if(listOrdering.selected) {
					var index = $scope.listOrderings.indexOf(listOrdering);
					if(index !== -1) {
						$scope.listOrderings.splice(index, 1);
					}

					if($scope.listOrderings.length == 0) {
						self.addListOrdering();
					}
				}
			};

		self.livePreview = self.livePreview || function(headerTemplate, bodyTemplate) {
				// render every 3 seconds
				$timeout(function() {
					PatientListRestfulService.livePreview(
						headerTemplate, bodyTemplate, self.onLivePreviewSuccessful
					)
				}, 3000);
			}

		self.renderTemplate = self.renderTemplate || function(template) {
				return $sce.trustAsHtml(template);
			}

		// call-back functions.
		self.onLoadFieldsSuccessful = self.onLoadFieldsSuccessful || function(data) {
				$scope.fields = data.results;
				$scope.fields = $filter('orderBy')($scope.fields, 'desc.name');
			};
		
		// call-back functions.
		self.onPreLoadDefaultDisplayTemplateSuccessful = self.onPreLoadDefaultDisplayTemplateSuccessful || function (data) {
					$scope.entity.headerTemplate = data.headerTemplate;
					$scope.entity.bodyTemplate = data.bodyTemplate;
					
					self.livePreview(data.headerTemplate, data.bodyTemplate);
					self.renderTemplate(data.headerTemplate);
					self.renderTemplate(data.bodyTemplate);
			};

		// call-back functions.
		self.onLoadLocationsSuccessful = self.onLoadLocationsSuccessful || function(data) {
				$scope.locations = data.results;
				for(var i = 0; i < $scope.locations.length; i++) {
					$scope.locations[i].value = $scope.locations[i].uuid;
				}
				$scope.dropDownEntries = $scope.locations;
			};

		/**
		 * Binds the selected concept item to entity
		 * @type {Function}
		 * @parameter concept
		 */
		self.selectConcept = self.selectConcept || function(concept, listCondition) {
				PatientListRestfulService.getConceptId(concept.uuid, function(data) {
					listCondition.value = concept.display;
					listCondition.valueRef = data["id"];
				});
			};
		
		self.getConceptName = self.getConceptName || function(id, onGetConceptNameSuccessfulCallback) {
				PatientListRestfulService.getConceptName(id, onGetConceptNameSuccessfulCallback);
			};
		
		self.selectLocation = self.selectLocation || function(listCondition) {
				console.log(listCondition);
				PatientListRestfulService.getLocationId(listCondition.value, function(data) {
					listCondition.valueRef = data["id"];
				});
			};

		self.getLocationUuid = self.getLocationUuid || function(id, onGetLocationUuidSuccessfulCallback) {
				PatientListRestfulService.getLocationUuid(id, onGetLocationUuidSuccessfulCallback);
			};

		self.onLivePreviewSuccessful = self.onLivePreviewSuccessful || function(data) {
				$scope.headerContent = data['headerContent'];
				$scope.bodyContent = data['bodyContent'];
			};

		self.onListConditionDateSuccessfulCallback = self.onListConditionDateSuccessfulCallback || function(date) {
				if(date !== undefined) {
					var listConditionValueDate = PatientListFunctions.formatDate(date);
					console.log(listConditionValueDate);
				}
			};

		/**
		 * All post-submit validations are done here.
		 * @return boolean
		 */
		// @Override
		self.validateBeforeSaveOrUpdate = self.validateBeforeSaveOrUpdate || function() {
				if(!angular.isDefined($scope.entity.name) || $scope.entity.name === '') {
					$scope.submitted = true;
					emr.errorAlert('Name required');
					return false;
				}
				
				if($scope.entity.headerTemplate === "" || !angular.isDefined($scope.entity.headerTemplate) || $scope.entity.headerTemplate == null) {
					$scope.entity.headerTemplate = null;
				}
				if($scope.entity.bodyTemplate === "" || !angular.isDefined($scope.entity.bodyTemplate) || $scope.entity.bodyTemplate == null) {
					$scope.entity.bodyTemplate = null;
				}
				
				if($scope.listConditions.length == 1) {
					emr.errorAlert('You are required to input at least one patient list condition');
					return false;
				} else {
					var patientListConditions = [];
					for(var r = 0; r < $scope.listConditions.length; r++) {
						var patientListCondition = $scope.listConditions[r];
						if(patientListCondition.selected == false){
							continue;
						} else {
							var requestCondition = {};
							
							if(patientListCondition.field === "") {
								emr.errorAlert("Condition field required ");
								return false;
							}
							
							if(patientListCondition.operator === "") {
								emr.errorAlert("Condition operator required ");
								return false;
							}
							
							requestCondition['field'] = patientListCondition.field;
							requestCondition['conditionOrder'] = r;
							requestCondition['operator'] = patientListCondition.operator;
							requestCondition['value'] = patientListCondition.value;
							if(patientListCondition.valueRef !== undefined) {
								requestCondition['value'] = patientListCondition.valueRef;
							}
							
							patientListConditions.push(requestCondition);
						}
					}
					
					var ordering = [];
					for(var i = 0; i < $scope.listOrderings.length; i++) {
						var sortOrder = {};
						if($scope.listOrderings[i].selected == false) {
							continue;
						}
						
						if($scope.listOrderings[i].field === "") {
							emr.errorAlert("Sort Order field required ");
							return false;
						}
						
						if($scope.listOrderings[i].sortOrder === "") {
							emr.errorAlert("Input sort order for field " + $scope.listOrderings[i].field);
							return false;
						}
						
						sortOrder.conditionOrder = i;
						sortOrder.field = $scope.listOrderings[i].field;
						sortOrder.sortOrder = $scope.listOrderings[i].sortOrder;
						ordering.push(sortOrder);
					}
					
					$scope.entity.ordering = ordering;
					$scope.entity.patientListConditions = patientListConditions;
					$scope.loading = true;
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
