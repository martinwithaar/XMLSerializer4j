package org.xmlserializer4j;

import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p><code>AbsSerializer</code> implementation for serializing <code>Iterable</code> implementations.</p>
 * @author Martin
 *
 */
public class IterableSerializer extends AbsSerializer<Iterable<?>> {
	
	@Override
	public Element serialize(XMLSerializer xmlSerializer, String elementName, Iterable<?> iterable) {
		Element element = super.serialize(xmlSerializer, elementName, iterable);
		for(Object childObject: iterable) {
			Element child = xmlSerializer.serializeToElement(childObject, XMLSerializer.ELEM, null);
			if(child != null) {
				element.appendChild(child);
			}
		}
		return element;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Iterable<?> deserialize(XMLSerializer xmlSerializer, Element element, Iterable<?> iterable) {
		String clazzName = element.getAttribute(XMLSerializer.CLASS);
		try {
			Class<?> clazz = Class.forName(clazzName);
			Collection<Object> collection;
			if(iterable == null) {
				collection = (Collection<Object>) clazz.newInstance();
			} else {
				collection = (Collection<Object>) iterable;
			}
			
			// Parse child nodes
			NodeList childNodes = element.getChildNodes();
			for(int i = 0, n = childNodes.getLength(); i < n; i++) {
				Node node = childNodes.item(i);
				try {
					Object childObject = xmlSerializer.deserializeElement((Element) node, null);
					collection.add(childObject);
				} catch(ClassCastException e) {
					// Do nothing
				}
			}
			return collection;
		} catch(ClassCastException e) {
			throw new RuntimeException("Only Collection implementations are deserializable", e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
