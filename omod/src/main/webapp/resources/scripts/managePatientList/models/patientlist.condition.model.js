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

	var baseModel = angular.module('app.patientListConditionModel', []);

	function PatientListConditionModel() {

		function PatientListConditionModel(feild, operator, value) {
			this.feild = feild;
			this.operator = operator;
			this.value = value;
			this.selected = false
		}

		PatientListConditionModel.prototype = {

			getFeild: function() {
				return this.feild;
			},

			setFeild: function(feild) {
				this.feild = feild;
			},

			getOperator: function() {
				return this.operator;
			},

			setOperator: function(operator) {
				this.operator = operator;
			},

			getValue: function() {
				return this.value;
			},

			setValue: function(value) {
				this.value = value;
			},
			
			setSelected: function(selected) {
				this.selected = selected;
			},
			
			isSelected: function() {
				return this.selected;
			}
			
		};

		return PatientListConditionModel;
	}

	baseModel.factory("PatientListConditionModel", PatientListConditionModel);
	PatientListConditionModel.$inject = [];
})();
