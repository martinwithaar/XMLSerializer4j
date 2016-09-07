package org.xmlserializer4j;

import java.net.URI;
import java.net.URISyntaxException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

/**
 * <p><code>AbsSerializer</code> implementation for serializing <code>URI</code> objects.</p>
 * @author Martin
 *
 */
public class URISerializer extends AbsSerializer<URI> {
	
	/*
	 * Interface implementations
	 */
	
	@Override
	public Element serialize(XMLSerializer xmlSerializer, String elementName, URI uri) throws XMLSerializeException {
		Element element = super.serialize(xmlSerializer, elementName, uri);
		element.setTextContent(uri.toString());
		return element;
	}

	@Override
	public URI deserialize(XMLSerializer xmlSerializer, Element element, URI uri) throws XMLSerializeException {
		if(uri == null) {
			try {
				uri = new URI(element.getTextContent());
			} catch (URISyntaxException e) {
				throw new XMLSerializeException(e);
			} catch (DOMException e) {
				throw new XMLSerializeException(e);
			}
		}
		return uri;
	}
}
