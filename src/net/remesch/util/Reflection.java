/**
 * 
 */
package net.remesch.util;

import java.lang.reflect.Field;
import java.util.ArrayList;

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
	public static <T> Field[] getFields(Class<T> c) {
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
}
