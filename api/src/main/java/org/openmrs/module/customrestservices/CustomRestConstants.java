package org.openmrs.module.customrestservices;

/**
 * CustomREST constants
 */
public class CustomRestConstants {

	private static final String OBSERVATION_FIELDS = "(uuid,display,comment,value,groupMembers,concept:(uuid,display),"
	        + "encounter:ref,dateCreated,creator:(uuid,display,person:(uuid,display)),voided)";
	public static final String ENCOUNTER_REPRESENTATION = "(uuid,display,encounterDatetime,patient:ref,location:ref,"
	        + "form:ref,encounterType:ref,obs:" + OBSERVATION_FIELDS + ",creator:(uuid,display,person:(uuid,display)),"
	        + "dateCreated,changedBy:ref,dateChanged,voided,visit)";
	public static final String VISIT_NOTE_2 = "visit note 2";
	public static final String VISIT_NOTE = "Visit Note";
	public static final String TEXT_OF_ENCOUNTER_NOTE = "text of encounter note";
	public static final String VOID_PATIENT_SUMMARY_MESSAGE = "void patient summary obs";
	public static final String CREATE_PATIENT_SUMMARY_MESSAGE = "create merged patient summary obs";
}
