/**
 * 
 */
package net.remesch.db.schema;

import java.lang.reflect.Field;

/**
 * @author Alexander Remesch
 *
 */
public class DatabaseFieldMap implements Comparable<DatabaseFieldMap> {
	private String classFieldName;
	private String dbFieldName;
	private String fieldType;
	private boolean ignore;
	private boolean inacessible;
	private Field field;
	/**
	 * @return the classFieldName
	 */
	public String getClassFieldName() {
		return classFieldName;
	}
	/**
	 * @param classFieldName the classFieldName to set
	 */
	public void setClassFieldName(String classFieldName) {
		this.classFieldName = classFieldName;
	}
	/**
	 * @return the dbFieldName
	 */
	public String getDbFieldName() {
		return dbFieldName;
	}
	/**
	 * @param dbFieldName the dbFieldName to set
	 */
	public void setDbFieldName(String dbFieldName) {
		this.dbFieldName = dbFieldName;
	}
	/**
	 * @return the fieldType
	 */
	public String getFieldType() {
		return fieldType;
	}
	/**
	 * @param fieldType the fieldType to set
	 */
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	/**
	 * @return the ignore
	 */
	public boolean isIgnore() {
		return ignore;
	}
	/**
	 * @param ignore the ignore to set
	 */
	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}
	/**
	 * @return the inacessible
	 */
	public boolean isInacessible() {
		return inacessible;
	}
	/**
	 * @param inacessible the inacessible to set
	 */
	public void setInacessible(boolean inacessible) {
		this.inacessible = inacessible;
	}
	/**
	 * @param field the field to set
	 */
	public void setField(Field field) {
		this.field = field;
	}
	/**
	 * @return the field
	 */
	public Field getField() {
		return field;
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DatabaseFieldMap arg0) {
		return classFieldName.compareTo(arg0.classFieldName);
	}
}
