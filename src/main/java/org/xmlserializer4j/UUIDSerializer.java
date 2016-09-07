package org.xmlserializer4j;

import java.util.UUID;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

/**
 * <p><code>AbsSerializer</code> implementation for serializing <code>UUID</code> objects.</p>
 * @author Martin
 *
 */
public class UUIDSerializer extends AbsSerializer<UUID> {
	
	/*
	 * Interface implementations
	 */
	
	@Override
	public Element serialize(XMLSerializer xmlSerializer, String elementName, UUID uuid) throws XMLSerializeException {
		Element element = super.serialize(xmlSerializer, elementName, uuid);
		element.setTextContent(uuid.toString());
		return element;
	}

	@Override
	public UUID deserialize(XMLSerializer xmlSerializer, Element element, UUID uuid) throws XMLSerializeException {
		if(uuid == null) {
			try {
				uuid = UUID.fromString(element.getTextContent());
			} catch (DOMException e) {
				throw new XMLSerializeException(e);
			}
		}
		return uuid;
	}
}
