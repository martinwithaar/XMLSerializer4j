package org.xmlserializer4j.test;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlserializer4j.XMLSerializer;
import org.xmlserializer4j.test.model.ExampleObject;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * <p>Unit test for serializing to a gzipped file and deserializing that file back to an object.</p> 
 * @author Martin
 *
 */
public class GZIPSerializeTest {
	
	private static final String FILENAME = "serialized.xml.gz";
	
    @Test public void testSerializeDeserialize() throws IOException, SAXException {
    	
    	// Create test object
    	Object original = ExampleObject.getInstance(3);
    	
    	// Create serializer & serialize test object
		XMLSerializer xmlSerializer = new XMLSerializer();
		OutputStream os = null;
		try {
			os = new GZIPOutputStream(new FileOutputStream(FILENAME));
			xmlSerializer.serialize(original, os);
			os.flush();
		} finally {
			if(os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		Object deserialized;
		InputStream is = null;
		try {
			is = new GZIPInputStream(new FileInputStream(FILENAME));
			xmlSerializer = new XMLSerializer(is);
			deserialized = xmlSerializer.deserialize();
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		// Check if original & deserialized object are equal
		assertEquals(original, deserialized);
    }
}
