package org.xmlserializer4j.test.model;

/**
 * <p>Class for testing serialization and deserialization on circular references.</p>
 * @author Martin
 *
 */
public class CircularTestObject {
	
	/*
	 * Attributes
	 */
	
	public CircularTestObject childReference;
	public CircularTestObject circularReference;
	
	/**
	 * <p>Recursively constructs instances of this class with depth <code>depth</code>.</p>
	 * @param depth
	 * @return
	 */
	public static final CircularTestObject getInstance(int depth) {
		CircularTestObject parent = new CircularTestObject();
		if(depth > 0) {
			CircularTestObject child = getInstance(depth - 1);
			child.circularReference = parent;
			parent.childReference = child;
		}
		return parent;
	}
}
