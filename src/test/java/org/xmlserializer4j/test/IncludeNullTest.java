package org.xmlserializer4j.test;
import static org.junit.Assert.*;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlserializer4j.XMLSerializeException;
import org.xmlserializer4j.XMLSerializer;
import org.xmlserializer4j.test.model.ExampleObject;

public class IncludeNullTest {
	@Test public void testIncludeNull() throws XMLSerializeException {
		
		// Create test object
    	Object original = ExampleObject.getInstance(0);
    	
    	// Create serializer & serialize test object
		XMLSerializer xmlSerializer = new XMLSerializer();
		Document document = xmlSerializer.serialize(original);
		NodeList nullObjects = document.getElementsByTagName("aNullObject");
		
		int n = nullObjects.getLength();
		assertTrue("There should be at least 1 aNullObject element present", n > 0);
		
		for(int i = 0; i < n; i++) {
			try {
				Element element = (Element) nullObjects.item(i);
				String nullAttribute = element.getAttribute("null");
				// Check if original & deserialized object are equal
				assertEquals("true", nullAttribute);
			} catch(ClassCastException e) {
				// Do nothing
			}
		}
	}
}
