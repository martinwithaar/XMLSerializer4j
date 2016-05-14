package org.xmlserializer4j.test;
import org.junit.Test;
import org.xmlserializer4j.XMLSerializeException;
import org.xmlserializer4j.XMLSerializer;
import org.xmlserializer4j.test.model.CircularTestObject;

import static org.junit.Assert.*;

/**
 * <p>Unit test for testing serialization and deserialization on circular references.</p>
 * @author Martin
 *
 */
public class CircularReferenceTest {
	
    @Test public void testCircularReference() throws XMLSerializeException {
    	
    	// Create test object
    	CircularTestObject original = CircularTestObject.getInstance(10);
    	
    	// Create serializer & serialize test object
		XMLSerializer xmlSerializer = new XMLSerializer();
		xmlSerializer.serialize(original);
		
		// Deserialize object
		CircularTestObject parent = (CircularTestObject) xmlSerializer.deserialize();
		CircularTestObject child;
		while((child = parent.childReference) != null) {
			assertEquals(parent, child.circularReference);
			parent = child;
		}
    }
}
