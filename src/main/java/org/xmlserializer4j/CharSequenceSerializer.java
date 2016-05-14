package org.xmlserializer4j;

import org.w3c.dom.Element;

/**
 * <p><code>AbsSerializer</code> implementation for serializing <code>CharSequence</code> implementations.</p>
 * @author Martin
 *
 */
public class CharSequenceSerializer extends AbsSerializer<CharSequence> {

	@Override
	public Element serialize(XMLSerializer xmlSerializer, String elementName, CharSequence object) throws XMLSerializeException {
		Element element = super.serialize(xmlSerializer, elementName, object);
		element.setTextContent(object.toString());
		return element;
	}
	
	@Override
	public CharSequence deserialize(XMLSerializer xmlSerializer, Element element, CharSequence object) throws XMLSerializeException {
		return new String(element.getTextContent());
	}
}
