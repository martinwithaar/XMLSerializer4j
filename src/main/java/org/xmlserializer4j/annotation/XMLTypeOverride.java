package org.xmlserializer4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotation for overriding the type of a field or class type.
 * This will let the annotated element be treated as if it is of the given type.</p>
 * @author Martin
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface XMLTypeOverride {	
	Class<?> clazz() default Object.class;
}
