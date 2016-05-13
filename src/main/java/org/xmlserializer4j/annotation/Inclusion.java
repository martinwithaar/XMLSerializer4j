package org.xmlserializer4j.annotation;

/**
 * <p>Enumeration for inclusion of Java fields in XML for use with the <code>XMLInclude</code> annotation.</p>
 * @author Martin
 *
 */
public enum Inclusion {
	/**
	 * <p>Always include this field/type even if <code>null</code>.</p>
	 */
	ALWAYS,
	/**
	 * <p>Only include this field/type when not <code>null</code>.</p>
	 */
	NON_NULL,
	/**
	 * <p>Never include this field/type.</p>
	 */
	NEVER
}
