package org.xmlserializer4j;

import org.w3c.dom.Element;

/**
 * <p><code>AbsSerializer</code> implementation for serializing <code>Class</code> objects.</p>
 * @author Martin
 *
 */
public class ClassSerializer extends AbsSerializer<Class<?>> {
	
	@Override
	public Element serialize(XMLSerializer xmlSerializer, String elementName, Class<?> clazz) throws XMLSerializeException {
		Element element = super.serialize(xmlSerializer, elementName, clazz);
		element.setTextContent(clazz.getName());
		return element;
	}

	@Override
	public Class<?> deserialize(XMLSerializer xmlSerializer, Element element, Class<?> clazz) throws XMLSerializeException {
		try {
			return clazz == null ? Class.forName(element.getTextContent()) : clazz;
		} catch (ClassNotFoundException e) {
			throw new XMLSerializeException(e);
		}
	}
}
