/**
 * 
 */
package net.remesch.util;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.remesch.db.schema.DatabaseField;
import net.remesch.db.schema.DatabaseFieldMap;

/**
 * @author Alexander Remesch
 *
 */
public class Reflection {
	/**
	 * Get all fields of a class - include fields from subclasses (as getFields()) and
	 * private fields (as getDeclaredFields())
	 * @param <T>
	 * @param c
	 * @return
	 */
	public static <T> Field[] getFieldNames(Class<T> c) {
		ArrayList<Field> result = new ArrayList<Field>();
		for (Field field : c.getFields()) {
			result.add(field);
		}
		for (Field field : c.getDeclaredFields()) {
			if (!result.contains(field)) {
				result.add(field);
			}
		}
		Field fields[] = new Field[result.size()];
		return result.toArray(fields);
	}
	/**
	 * Get an ArrayList of all field-related information needed to load a database table into a class
	 * @param <T>
	 * @param c
	 * @return
	 */
	public static <T> ArrayList<DatabaseFieldMap> getFields(Class<T> c) {
		ArrayList<DatabaseFieldMap> result = new ArrayList<DatabaseFieldMap>();
		for (int i = 0; i != 2; i++) {
			Field fields[];
			switch(i) {
			case 0:
				// all public & private fields in classes
				fields = c.getDeclaredFields();
				break;
			case 1:
				// all public fields in classes & superclasses
				fields = c.getFields();
				break;
			default:
				throw new AssertionError("i = " + i);
			}
			for (Field field : fields) {
				if (field.isAnnotationPresent(net.remesch.db.schema.Ignore.class))
					continue;
				if (result.contains(field))
					continue;
				DatabaseFieldMap fieldMap = new DatabaseFieldMap();
				fieldMap.setClassFieldName(field.getName());
				fieldMap.setFieldType(field.getType().getName());
				fieldMap.setField(field);
				if (field.isAnnotationPresent(net.remesch.db.schema.DatabaseField.class)) {
					DatabaseField dbf = field.getAnnotation(net.remesch.db.schema.DatabaseField.class);
					fieldMap.setDbFieldName(dbf.fieldName());
				} else {
					fieldMap.setDbFieldName(fieldMap.getClassFieldName());
				}
				if (!field.isAccessible()) {
					field.setAccessible(true);
					fieldMap.setInacessible(true);
				} else {
					fieldMap.setInacessible(false);
				}
				result.add(fieldMap);
			}
		}
		return result;
	}
}
