package org.xmlserializer4j;

/**
 * <p>Entry class for assigning a <code>TypeSerializer</code> implementation to a <code>Class</code> type.</p>
 * @author Martin
 *
 */
public class SerializerEntry {
	
	/*
	 * Attributes
	 */
	
	private Class<?> targetClass;
	private TypeSerializer<?> serializer;
	
	/*
	 * Constructor(s)
	 */
	
	public SerializerEntry(Class<?> targetClass, TypeSerializer<?> serializer) {
		super();
		this.targetClass = targetClass;
		this.serializer = serializer;
	}
	
	/*
	 * Class methods
	 */

	/**
	 * <p>Returns the target class.</p>
	 * @return
	 */
	public Class<?> getTargetClass() {
		return targetClass;
	}

	/**
	 * <p>Returns the <code>TypeSerializer</code> implementation.</p>
	 * @return
	 */
	public TypeSerializer<?> getSerializer() {
		return serializer;
	}
}