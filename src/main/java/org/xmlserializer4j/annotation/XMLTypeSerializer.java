package org.xmlserializer4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//import com.bogdwellers.java.finance.util.xmlserializer4j.DefaultSerializer;
import org.xmlserializer4j.TypeSerializer;

/**
 * <p>Annotation for specifying a specific <code>TypeSerializer</code>.</p>
 * <p><b>Note:</b> The type serializer implementation must have a no-arg constructor in order to be successfully instantiated.</p>
 * @author Martin
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface XMLTypeSerializer {
	Class<? extends TypeSerializer<?>> clazz();
}
