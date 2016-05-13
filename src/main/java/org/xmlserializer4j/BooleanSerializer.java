package org.xmlserializer4j;

import org.w3c.dom.Element;

public class BooleanSerializer extends AbsSerializer<Boolean> {
	
	@Override
	public Element serialize(XMLSerializer xmlSerializer, String elementName, Boolean bool) {
		Element element = super.serialize(xmlSerializer, elementName, bool);
		element.setTextContent(bool.toString());
		return element;
	}

	@Override
	public Boolean deserialize(XMLSerializer xmlSerializer, Element element, Boolean bool) {
		return bool == null ? Boolean.valueOf(element.getTextContent()) : bool;
	}

}
