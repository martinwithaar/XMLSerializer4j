package org.xmlserializer4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotation for setting inclusion rules for specific fields or types.</p>
 * @author Martin
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface XMLInclude {
	public Inclusion type() default Inclusion.ALWAYS;
}
