package org.xmlserializer4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.xmlserializer4j.annotation.Inclusion;
import org.xmlserializer4j.annotation.XMLInclude;

/**
 * <p>Default <code>AbsSerializer</code> implementation for serializing objects.</p>
 * @author Martin
 *
 */
public class DefaultSerializer extends AbsSerializer<Object> implements OnReferenceListener {
	
	/*
	 * Attributes
	 */
	
	private Map<String, Set<UnsatisfiedField>> unsatisfiedReferences;
	private Queue<Set<UnsatisfiedField>> setRecycler;
	
	/*
	 * Constructor(s)
	 */
	
	public DefaultSerializer() {
		this.unsatisfiedReferences = new HashMap<String, Set<UnsatisfiedField>>();
		this.setRecycler = new LinkedList<Set<UnsatisfiedField>>();
	}
	
	/*
	 * Interface implementations
	 */
	
	@Override
	public Element serialize(XMLSerializer xmlSerializer, String elementName, Object object) throws XMLSerializeException {
		Element element = super.serialize(xmlSerializer, elementName, object);
		Document document = xmlSerializer.getDocument();
		Class<?> clazz = object.getClass();
		
		boolean includePrimitiveZeroFalse = xmlSerializer.isEnabled(XMLSerializer.INCLUDE_PRIMITIVE_ZERO_FALSE);
		boolean includeParentclassFields = xmlSerializer.isEnabled(XMLSerializer.INCLUDE_PARENTCLASS_FIELDS);
		boolean includeScopeAlways = xmlSerializer.isEnabled(XMLSerializer.INCLUDE_SCOPE_ALWAYS);
		Set<String> fieldNames = new HashSet<String>();
		do {
			Field[] fields = clazz.getDeclaredFields();
			for(Field field: fields) {
				
				String fieldName = field.getName();
				
				// Handle static fields according to settings
				int modifiers = field.getModifiers();
				if(Modifier.isStatic(modifiers)) {
					Set<Field> serializedStaticFields = xmlSerializer.getSerializedStaticFields();
					// There is no need for serializing unchangeable attributes
					if(	serializedStaticFields.contains(field) ||
						!xmlSerializer.isEnabled(XMLSerializer.INCLUDE_STATIC) ||
						(Modifier.isFinal(modifiers) && field.getType().isPrimitive() && !xmlSerializer.isEnabled(XMLSerializer.INCLUDE_STATIC_FINAL_PRIMITIVES))) {
						continue;
					}
					serializedStaticFields.add(field);
				}
				
				// Serialize field value
				XMLInclude include = field.getAnnotation(XMLInclude.class);
				if(!xmlSerializer.isEnabled(XMLSerializer.EXCLUDE_ALL) && (include == null || Inclusion.NEVER  != include.include())) {
					Class<?> fieldClazz = field.getType();
					if(field.getType().isPrimitive()) {
						boolean accessible = field.isAccessible();
						field.setAccessible(true);
						String textContent = null;
						try {
							if(byte.class.equals(fieldClazz)) {
								byte value = field.getByte(object);
								if(includePrimitiveZeroFalse || value != 0) {
									textContent = String.valueOf(value);
								}
							} else if(short.class.equals(fieldClazz)) {
								short value = field.getShort(object);
								if(includePrimitiveZeroFalse || value != 0) {
									textContent = String.valueOf(value);
								}
							} else if(int.class.equals(fieldClazz)) {
								int value = field.getInt(object);
								if(includePrimitiveZeroFalse || value != 0) {
									textContent = String.valueOf(value);
								}
							} else if(long.class.equals(fieldClazz)) {
								long value = field.getLong(object);
								if(includePrimitiveZeroFalse || value != 0L) {
									textContent = String.valueOf(value);
								}
							} else if(float.class.equals(fieldClazz)) {
								float value = field.getFloat(object);
								if(includePrimitiveZeroFalse || value != 0.0f) {
									textContent = String.valueOf(value);
								}
							} else if(double.class.equals(fieldClazz)) {
								double value = field.getDouble(object);
								if(includePrimitiveZeroFalse || value != 0.0d) {
									textContent = String.valueOf(value);
								}
							} else if(boolean.class.equals(fieldClazz)) {
								boolean value = field.getBoolean(object);
								if(includePrimitiveZeroFalse || value) {
									textContent = String.valueOf(value);
								}
							} else if(char.class.equals(fieldClazz)) {
								char value = field.getChar(object);
								if(includePrimitiveZeroFalse || value != '\u0000') {
									textContent = String.valueOf(value);
								}
							} else {
								throw new XMLSerializeException("Primitive field is of unknown type");
							}
						} catch(IllegalAccessException e) {
							throw new XMLSerializeException(e);
						} finally {
							// Restore accessible
							field.setAccessible(accessible);
						}
						if(textContent != null) {
							Element child = document.createElement(fieldName);
							child.setTextContent(textContent);
							if(includeScopeAlways || fieldNames.contains(fieldName)) {
								child.setAttribute(XMLSerializer.SCOPE, clazz.getName());
							}
							element.appendChild(child);
						}
					} else {
						Object value;
						boolean accessible = field.isAccessible();
						field.setAccessible(true);
						try {
							value = field.get(object);
						} catch (IllegalAccessException e) {
							throw new XMLSerializeException(e);
						} finally {
							// Restore accessible
							field.setAccessible(accessible);
						}
						Element child = xmlSerializer.serializeToElement(value, fieldName, field);
						if(child != null) {
							if(includeScopeAlways || fieldNames.contains(fieldName)) {
								child.setAttribute(XMLSerializer.SCOPE, clazz.getName());
							}
							element.appendChild(child);
						}
					}
					fieldNames.add(fieldName);
				}
			}
		} while(includeParentclassFields && (clazz = clazz.getSuperclass()) != null);
		return element;
	}

	@Override
	public Object deserialize(XMLSerializer xmlSerializer, Element element, Object object) throws XMLSerializeException {
		String clazzName = element.getAttribute(XMLSerializer.CLASS);
		try {
			Class<?> clazz = Class.forName(clazzName);
			if(object == null) {
				object = clazz.newInstance();
			}
			// Parse child nodes
			boolean includeParentclassFields = xmlSerializer.isEnabled(XMLSerializer.INCLUDE_PARENTCLASS_FIELDS);
			NodeList childNodes = element.getChildNodes();
			for(int i = 0, n = childNodes.getLength(); i < n; i++) {
				try {
					Element childElement = (Element) childNodes.item(i);
					Class<?> scope = childElement.hasAttribute(XMLSerializer.SCOPE) ? Class.forName(childElement.getAttribute(XMLSerializer.SCOPE)) : null;
					while(clazz != null) {
						if(scope != null && !clazz.equals(scope)) {
							clazz = clazz.getSuperclass();
							continue;
						}
						
						try {
							Field field = clazz.getDeclaredField(childElement.getNodeName());
							boolean accessible = field.isAccessible();
							field.setAccessible(true);
							if(!field.getType().isPrimitive()) {
								int modifiers = field.getModifiers();
								if(Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
									Object value;
									try {
										value = field.get(object);
									} catch (IllegalAccessException e) {
										throw new XMLSerializeException(e);
									}
									value = xmlSerializer.deserializeElement(childElement, value);
								} else {
									Object value = xmlSerializer.deserializeElement(childElement, null);
									field.set(object, value);
									if(childElement.hasAttribute(XMLSerializer.REF) && value == null) {
										// Unsatisfied reference
										String reference = childElement.getAttribute(XMLSerializer.REF);
										Set<UnsatisfiedField> fields = unsatisfiedReferences.get(reference);
										if(fields == null) {
											fields = setRecycler.isEmpty() ? new HashSet<UnsatisfiedField>(): setRecycler.poll();
											unsatisfiedReferences.put(reference, fields);
										}
										fields.add(new UnsatisfiedField(object, field));
									}
								}
							} else {
								setPrimitiveField(object, field, childElement.getTextContent());
							}
							field.setAccessible(accessible);
							break;
						} catch (NoSuchFieldException e) {
							clazz = includeParentclassFields ? clazz.getSuperclass() : null;
						}
					}
				} catch(ClassCastException e) {
					// Do nothing
				}
			}
			return object;
		} catch (ClassNotFoundException e) {
			throw new XMLSerializeException(e);
		} catch (InstantiationException e) {
			throw new XMLSerializeException(e);
		} catch (IllegalAccessException e) {
			throw new XMLSerializeException(e);
		} catch (SecurityException e) {
			throw new XMLSerializeException(e);
		}
	}
	
	@Override
	public void onReferenceFound(String reference, Object object) throws XMLSerializeException {
		Set<UnsatisfiedField> unsatisfiedFields = unsatisfiedReferences.remove(reference);
		if(unsatisfiedFields != null) {
			for(UnsatisfiedField unsatisfiedField: unsatisfiedFields) {
				try {
					unsatisfiedField.satisfy(object);
				} catch (IllegalAccessException e) {
					throw new XMLSerializeException(e);
				}
			}
			unsatisfiedFields.clear();
			setRecycler.offer(unsatisfiedFields);
		}
	}
	
	/*
	 * Static methods
	 */
	
	/**
	 * <p>Sets a primitive field on an object with the given <code>value</code>.</p>
	 * @param object
	 * @param field
	 * @param value
	 * @throws NumberFormatException
	 * @throws XMLSerializeException
	 * @throws IllegalAccessException
	 */
	private static final void setPrimitiveField(Object object, Field field, String value) throws NumberFormatException, XMLSerializeException, IllegalAccessException {
		Class<?> fieldClazz = field.getType();
		if(byte.class.equals(fieldClazz)) {
			field.setByte(object, Byte.valueOf(value));
		} else if(short.class.equals(fieldClazz)) {
			field.setShort(object, Short.valueOf(value));
		} else if(int.class.equals(fieldClazz)) {
			field.setInt(object, Integer.valueOf(value));
		} else if(long.class.equals(fieldClazz)) {
			field.setLong(object, Long.valueOf(value));
		} else if(float.class.equals(fieldClazz)) {
			field.setFloat(object, Float.valueOf(value));
		} else if(double.class.equals(fieldClazz)) {
			field.setDouble(object, Double.valueOf(value));
		} else if(boolean.class.equals(fieldClazz)) {
			field.setBoolean(object, Boolean.valueOf(value));
		} else if(char.class.equals(fieldClazz)) {
			field.setChar(object, Character.valueOf(value.charAt(0)));
		} else {
			throw new XMLSerializeException("Primitive field is of unknown type");
		}
	}
	
	/*
	 * Nested classes
	 */
	
	/**
	 * 
	 * @author Martin
	 *
	 */
	private static class UnsatisfiedField {
		private Object object;
		private Field field;
		
		private UnsatisfiedField(Object object, Field field) {
			super();
			this.object = object;
			this.field = field;
		}
		
		public void satisfy(Object value) throws IllegalAccessException {
			boolean accessible = field.isAccessible();
			field.setAccessible(true);
			field.set(object, value);
			field.setAccessible(accessible);
		}
	}
}
