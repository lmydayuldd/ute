/**
 * 
 */
package net.remesch.db.schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Marks a field as to be ignored by the database access routines.  This
 * annotation has no effect on the normal "runtime" behavior of the field
 * or its content.</p>
 *
 * @author Alexander Remesch
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Ignore {}
