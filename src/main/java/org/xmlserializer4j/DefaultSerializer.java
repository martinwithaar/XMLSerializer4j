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
import org.w3c.dom.Node;
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
	public Element serialize(XMLSerializer xmlSerializer, String elementName, Object object) {
		Element element = super.serialize(xmlSerializer, elementName, object);
		Document document = xmlSerializer.getDocument();
		Class<?> clazz = object.getClass();
		boolean includeParentclassFields = xmlSerializer.isEnabled(XMLSerializer.INCLUDE_PARENTCLASS_FIELDS);
		do {
			Field[] fields = clazz.getDeclaredFields();
			for(Field field: fields) {
				
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
				if(!xmlSerializer.isEnabled(XMLSerializer.EXCLUDE_ALL) && (include == null || Inclusion.NEVER  != include.type())) {
					Class<?> fieldClazz = field.getType();
					if(field.getType().isPrimitive()) {
						boolean accessible = field.isAccessible();
						field.setAccessible(true);
						String textContent;
						try {
							if(byte.class.equals(fieldClazz)) {
								textContent = String.valueOf(field.getByte(object));
							} else if(short.class.equals(fieldClazz)) {
								textContent = String.valueOf(field.getShort(object));
							} else if(int.class.equals(fieldClazz)) {
								textContent = String.valueOf(field.getInt(object));
							} else if(long.class.equals(fieldClazz)) {
								textContent = String.valueOf(field.getLong(object));
							} else if(float.class.equals(fieldClazz)) {
								textContent = String.valueOf(field.getFloat(object));
							} else if(double.class.equals(fieldClazz)) {
								textContent = String.valueOf(field.getDouble(object));
							} else if(boolean.class.equals(fieldClazz)) {
								textContent = String.valueOf(field.getBoolean(object));
							} else if(char.class.equals(fieldClazz)) {
								textContent = String.valueOf(field.getChar(object));
							} else {
								throw new IllegalArgumentException("Primitive field is of unknown type");
							}
						} catch(IllegalAccessException e) {
							throw new RuntimeException(e);
						} finally {
							// Restore accessible
							field.setAccessible(accessible);
						}
						Element child = document.createElement(field.getName());
						child.setTextContent(textContent);
						element.appendChild(child);
					} else {
						Object value;
						boolean accessible = field.isAccessible();
						field.setAccessible(true);
						try {
							value = field.get(object);
						} catch (IllegalArgumentException e) {
							throw new RuntimeException(e);
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						} finally {
							// Restore accessible
							field.setAccessible(accessible);
						}
						Element child = xmlSerializer.serializeToElement(value, field.getName(), field);
						if(child != null) {
							element.appendChild(child);
						}
					}
				}
			}
		} while(includeParentclassFields && (clazz = clazz.getSuperclass()) != null);
		return element;
	}

	@Override
	public Object deserialize(XMLSerializer xmlSerializer, Element element, Object object) {
		String clazzName = element.getAttribute(XMLSerializer.CLASS);
		try {
			Class<?> clazz = Class.forName(clazzName);
			if(object == null) {
				object = clazz.newInstance();
			}
			
			// Parse child nodes
			NodeList childNodes = element.getChildNodes();
			for(int i = 0, n = childNodes.getLength(); i < n; i++) {
				Node node = childNodes.item(i);
				try {
					Field field = clazz.getDeclaredField(node.getNodeName());
					boolean accessible = field.isAccessible();
					field.setAccessible(true);
					if(!field.getType().isPrimitive()) {
						int modifiers = field.getModifiers();
						if(Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
							Object value;
							try {
								value = field.get(object);
							} catch (IllegalArgumentException e) {
								throw new RuntimeException(e);
							} catch (IllegalAccessException e) {
								throw new RuntimeException(e);
							}
							value = xmlSerializer.deserializeElement((Element) node, value);
						} else {
							Element childElement = (Element) node;
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
						setPrimitiveField(object, field, ((Element)node).getTextContent());
					}
					field.setAccessible(accessible);
				} catch(ClassCastException e) {
					// Do nothing
				}
			}
			return object;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void onReferenceFound(String reference, Object object) {
		Set<UnsatisfiedField> unsatisfiedFields = unsatisfiedReferences.remove(reference);
		if(unsatisfiedFields != null) {
			for(UnsatisfiedField unsatisfiedField: unsatisfiedFields) {
				try {
					unsatisfiedField.satisfy(object);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
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
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static final void setPrimitiveField(Object object, Field field, String value) throws NumberFormatException, IllegalArgumentException, IllegalAccessException {
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
			throw new IllegalArgumentException("Primitive field is of unknown type");
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
		
		public void satisfy(Object value) throws IllegalArgumentException, IllegalAccessException {
			boolean accessible = field.isAccessible();
			field.setAccessible(true);
			field.set(object, value);
			field.setAccessible(accessible);
		}
	}
}
