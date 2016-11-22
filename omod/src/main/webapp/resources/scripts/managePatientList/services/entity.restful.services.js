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

(function () {
	'use strict'
	
	angular.module('app.restfulServices').service(
		'PatientListRestfulService', PatientListRestfulService);
	
	PatientListRestfulService.$inject = ['EntityRestFactory', 'PaginationService'];
	
	function PatientListRestfulService(EntityRestFactory, PaginationService) {
		var service;
		service = {
			loadConceptAnswers : loadConceptAnswers,
			loadFields: loadFields
		};
		
		return service;
		
		/**
		 * @param limit
		 * @param onConceptAnswersSuccessful
		 * @param module_name
		 * @param uuid
		 */
		function loadConceptAnswers(module_name, limit, uuid, onConceptAnswersSuccessful) {
			var requestParams = [];
			requestParams['rest_entity_name'] = '';
			requestParams['limit'] = limit;
			EntityRestFactory.setBaseUrl('concept/' + uuid, 'v1');
			EntityRestFactory.loadEntities(requestParams,
				onConceptAnswersSuccessful, errorCallback);
			//reset base url..
			EntityRestFactory.setBaseUrl(module_name);
		}
		
		/**
		 * @param onLoadFieldsSuccessful
		 */
		function loadFields(onLoadFieldsSuccessful) {
			var requestParams = [];
			requestParams['rest_entity_name'] = 'fields';
			EntityRestFactory.loadEntities(requestParams,
				onLoadFieldsSuccessful,
				errorCallback
			);
		}
		
		function errorCallback(error) {
			emr.errorAlert(error);
		}
	}
	
})();
