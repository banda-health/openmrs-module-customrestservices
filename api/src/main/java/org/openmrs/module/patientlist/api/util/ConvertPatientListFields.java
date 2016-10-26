package org.openmrs.module.patientlist.api.util;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Convert Patient List schema fields to entity fields.
 */
public class ConvertPatientListFields {

	/**
	 * Retrieves all fields for the given type
	 * @param classFields
	 * @param type
	 * @return
	 */
	private static List<Field> retrieveFields(List<Field> classFields, Class<?> type) {
		classFields.addAll(Arrays.asList(type.getDeclaredFields()));

		if (type.getSuperclass() != null) {
			classFields = retrieveFields(classFields, type.getSuperclass());
		}

		return classFields;
	}

	/**
	 * Search given schema name and return the corresponding field name Example: p.birth_date will returns p.birthdate
	 * @param type
	 * @param schemaFieldName
	 * @return
	 */
	public static String convert(Class type, String schemaFieldName) {
		List<Field> classFields = retrieveFields(new LinkedList<Field>(), type);
		schemaFieldName = schemaFieldName.replaceAll("_", "");
		String[] schemaTableColumn = schemaFieldName.split("\\.");
		for (Field classField : classFields) {
			String field = classField.getName();
			String[] fieldSubs = field.split("\\.");
			String classFieldName = fieldSubs[fieldSubs.length - 1];
			if (StringUtils.containsIgnoreCase(classFieldName, schemaTableColumn[1])) {
				return schemaTableColumn[0] + "." + classFieldName;
			}
		}
		return null;
	}
}
