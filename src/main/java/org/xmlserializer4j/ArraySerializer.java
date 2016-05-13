package org.xmlserializer4j;

import java.lang.reflect.Array;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Martin
 *
 */
public class ArraySerializer implements TypeSerializer<Object> {
	
	@Override
	public Element serialize(XMLSerializer xmlSerializer, String elementName, Object array) {
		Document document = xmlSerializer.getDocument();
		Element element = document.createElement(elementName);
		Class<?> clazz = array.getClass();
		Class<?> componentType = clazz.getComponentType();
		element.setAttribute(XMLSerializer.TYPE, componentType.getName());
		
		boolean includeLength = false;
		int n = Array.getLength(array);
		for(int i = 0; i < n; i++) {
			Object object = Array.get(array, i);
			if(componentType.isPrimitive()) {
				Element child = document.createElement(XMLSerializer.ELEM);
				child.setTextContent(String.valueOf(object));
				element.appendChild(child);
			} else {
				Element child = xmlSerializer.serializeToElement(object, XMLSerializer.ELEM, null);
				if(child != null) {
					if(includeLength) {
						child.setAttribute(XMLSerializer.INDEX, String.valueOf(i));
					}
					element.appendChild(child);
				} else {
					includeLength = true;
				}
			}
		}
		if(includeLength) {
			element.setAttribute(XMLSerializer.LENGTH, String.valueOf(n));
		}
		return element;
	}

	@Override
	public Object deserialize(XMLSerializer xmlSerializer, Element element, Object array) {
		String clazzName = element.getAttribute(XMLSerializer.TYPE);
		try {
			Class<?> clazz = XMLSerializer.PRIMITIVE_TYPE_MAP.get(clazzName);
			if(clazz == null) {
				clazz = Class.forName(clazzName);
			}
			NodeList children = element.getChildNodes();
			int n = children.getLength();
			int length;
			if(element.hasAttribute(XMLSerializer.LENGTH)) {
				length = Integer.valueOf(element.getAttribute(XMLSerializer.LENGTH));
			} else {
				length = n;
			}
			if(array == null) {
				array = (Object[]) Array.newInstance(clazz, length);
			}
			for(int i = 0; i < n; i++) {
				try {
					Element child = (Element) children.item(i);
					int index;
					if(child.hasAttribute(XMLSerializer.INDEX)) {
						index = Integer.valueOf(child.getAttribute(XMLSerializer.INDEX));
					} else {
						index = i;
					}
					Object value = xmlSerializer.deserializeElement(child, null);
					Array.set(array, index, value);
				} catch(ClassCastException e) {
					// Do nothing
				}
			}
			return array;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
