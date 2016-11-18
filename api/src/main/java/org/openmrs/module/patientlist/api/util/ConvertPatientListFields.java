package org.openmrs.module.patientlist.api.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Convert Patient List schema fields to entity fields.
 */
public class ConvertPatientListFields {

	protected static final Log LOG = LogFactory.getLog(ConvertPatientListFields.class);

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
	 * Search given schema name and return the corresponding entity field name Example: p.birth_date will returns p.birthdate
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

	/**
	 * Search given schema name and if found, returns the corresponding value
	 * @param type
	 * @param schemaFieldName
	 * @param instance
	 * @param <T>
	 * @return
	 */
	public static <T> T getFieldValue(Class type, String schemaFieldName, T instance) {
		List<Field> classFields = retrieveFields(new LinkedList<Field>(), type);
		schemaFieldName = schemaFieldName.replaceAll("_", "");
		String[] schemaTableColumn = schemaFieldName.split("\\.");
		try {
			for (Field classField : classFields) {
				String field = classField.getName();
				String[] fieldSubs = field.split("\\.");
				String classFieldName = fieldSubs[fieldSubs.length - 1];
				if (StringUtils.containsIgnoreCase(classFieldName, schemaTableColumn[1])) {
					classField.setAccessible(true);
					Object o = classField.get(instance);
					return (T)classField.get(instance);
				}
			}
		} catch (Exception ex) {
			LOG.error("Unable to read field value " + ex);
		}

		return null;
	}
}
