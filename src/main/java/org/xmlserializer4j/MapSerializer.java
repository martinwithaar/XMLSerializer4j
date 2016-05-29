package org.xmlserializer4j;

import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p><code>AbsSerializer</code> implementation for serializing <code>Map</code> implementations.</p>
 * @author Martin
 *
 */
public class MapSerializer extends AbsSerializer<Map<?, ?>> {
	
	private static final String ENTRY = "entry";
	
	/*
	 * Interface implementations
	 */

	@Override
	public Element serialize(XMLSerializer xmlSerializer, String elementName, Map<?, ?> object) throws XMLSerializeException {
		Element element = super.serialize(xmlSerializer, elementName, object);
		for(Entry<?, ?> entry: object.entrySet()) {
			Element child = xmlSerializer.serializeToElement(entry, ENTRY, null);
			if(child != null) {
				element.appendChild(child);
			}
		}
		return element;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<?, ?> deserialize(XMLSerializer xmlSerializer, Element element, Map<?, ?> map) throws XMLSerializeException {
		String clazzName = element.getAttribute(XMLSerializer.CLASS);
		try {
			Class<?> clazz = Class.forName(clazzName);
			Map<Object, Object> objectMap;
			if(map == null) {
				map = objectMap = (Map<Object, Object>) clazz.newInstance();
			} else {
				objectMap = (Map<Object, Object>) map;
			}
			
			// Parse child nodes
			NodeList childNodes = element.getChildNodes();
			for(int i = 0, n = childNodes.getLength(); i < n; i++) {
				Node node = childNodes.item(i);
				try {
					Element childElement = (Element) node;
					
					NodeList keys = childElement.getElementsByTagName(EntrySerializer.KEY);
					Object key = keys.getLength() > 0 ? xmlSerializer.deserializeElement((Element) keys.item(0), null) : null;
					
					NodeList values = childElement.getElementsByTagName(EntrySerializer.VALUE);
					Object value = values.getLength() > 0 ? xmlSerializer.deserializeElement((Element) values.item(0), null) : null;
					
					objectMap.put(key, value);
				} catch(ClassCastException e) {
					// Do nothing
				}
			}
			return map;
		} catch(ClassCastException e) {
			throw new XMLSerializeException(e);
		} catch (ClassNotFoundException e) {
			throw new XMLSerializeException(e);
		} catch (InstantiationException e) {
			throw new XMLSerializeException(e);
		} catch (IllegalAccessException e) {
			throw new XMLSerializeException(e);
		}
	}
}
