package org.xmlserializer4j.test;
import org.junit.Test;
import org.xmlserializer4j.XMLSerializeException;
import org.xmlserializer4j.XMLSerializer;
import org.xmlserializer4j.test.model.ScopeTestChildObject;
import org.xmlserializer4j.test.model.ScopeTestGrandChildObject;

import static org.junit.Assert.*;

import javax.xml.transform.TransformerException;

/**
 * <p>Unit test for testing serialization and deserialization with same named variables and inheritance.</p>
 * @author Martin
 *
 */
public class ScopeTest {
	
    @Test public void testScope() throws XMLSerializeException, TransformerException {
    	
    	// Create test object
    	ScopeTestChildObject original = new ScopeTestGrandChildObject();
    	
    	// Create serializer & serialize test object
		XMLSerializer xmlSerializer = new XMLSerializer();
		//xmlSerializer.disable(XMLSerializer.INCLUDE_PRIMITIVE_ZERO_FALSE);
		xmlSerializer.setTransformer(XMLSerializer.HUMAN_READABLE_TRANSFORMER);
		xmlSerializer.serialize(original, System.out);
		
		// Deserialize object
		ScopeTestChildObject deserialized = (ScopeTestChildObject) xmlSerializer.deserialize();
		assertEquals(deserialized, original);
    }
}
