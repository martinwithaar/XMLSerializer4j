package org.xmlserializer4j;

import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

/**
 * <p><code>AbsSerializer</code> implementation for serializing <code>URL</code> objects.</p>
 * @author Martin
 *
 */
public class URLSerializer extends AbsSerializer<URL> {
	
	/*
	 * Interface implementations
	 */
	
	@Override
	public Element serialize(XMLSerializer xmlSerializer, String elementName, URL url) throws XMLSerializeException {
		Element element = super.serialize(xmlSerializer, elementName, url);
		element.setTextContent(url.toExternalForm());
		return element;
	}

	@Override
	public URL deserialize(XMLSerializer xmlSerializer, Element element, URL url) throws XMLSerializeException {
		if(url == null) {
			try {
				url = new URL(element.getTextContent());
			} catch (MalformedURLException e) {
				throw new XMLSerializeException(e);
			} catch (DOMException e) {
				throw new XMLSerializeException(e);
			}
		}
		return url;
	}
}
