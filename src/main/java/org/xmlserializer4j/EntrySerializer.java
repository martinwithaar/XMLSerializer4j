package org.xmlserializer4j;

import java.util.Map.Entry;

import org.w3c.dom.Element;

/**
 * <p><code>AbsSerializer</code> implementation for serializing <code>Map.Entry</code> implementations.</p>
 * @author Martin
 *
 */
public class EntrySerializer implements TypeSerializer<Entry<?, ?>> {
	
	protected static final String KEY = "key";
	protected static final String VALUE = "value";
	
	/*
	 * Interface implementations
	 */

	@Override
	public Element serialize(XMLSerializer xmlSerializer, String elementName, Entry<?, ?> entry) {
		Element element = xmlSerializer.getDocument().createElement(elementName);
		
		Element keyElement = xmlSerializer.serializeToElement(entry.getKey(), KEY, null);
		if(keyElement != null) {
			element.appendChild(keyElement);
		}
		
		Element valueElement = xmlSerializer.serializeToElement(entry.getValue(), VALUE, null);
		if(valueElement != null) {
			element.appendChild(valueElement);
		}
		
		return element;
	}

	@Override
	public Entry<?, ?> deserialize(XMLSerializer xmlSerializer, Element element, Entry<?, ?> entry) {
		throw new UnsupportedOperationException();
	}
}
