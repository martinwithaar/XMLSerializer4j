package org.xmlserializer4j;

import org.w3c.dom.Element;

/**
 * 
 * @author Martin
 *
 */
public abstract class AbsSerializer<T> implements TypeSerializer<T> {

	@Override
	public Element serialize(XMLSerializer xmlSerializer, String elementName, T object) throws XMLSerializeException {
		Element element = xmlSerializer.getDocument().createElement(elementName);
		Class<?> clazz = object.getClass();
		if(!clazz.isAnonymousClass()) {
			element.setAttribute(XMLSerializer.CLASS, clazz.getName());
		}
		return element;
	}
}
