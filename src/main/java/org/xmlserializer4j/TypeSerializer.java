package org.xmlserializer4j;

import org.w3c.dom.Element;

/**
 * <p>Interface for implementing type serializers.</p>
 * @author Martin
 *
 */
public interface TypeSerializer<T> {
	/**
	 * <p>Serializes an object to an XML element within the <code>XMLSerializer</code>'s context.</p>
	 * @param xmlSerializer
	 * @param elementName
	 * @param object
	 * @return
	 * @throws XMLSerializeException
	 */
	public Element serialize(XMLSerializer xmlSerializer, String elementName, T object) throws XMLSerializeException;
	/**
	 * <p>Deserializes an XML element into an object within the <code>XMLSerializer</code>'s context.</p>
	 * @param xmlSerializer
	 * @param element
	 * @param object optional runtime object to deserialize into
	 * @return
	 * @throws XMLSerializeException
	 */
	public T deserialize(XMLSerializer xmlSerializer, Element element, T object) throws XMLSerializeException;
}
