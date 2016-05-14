package org.xmlserializer4j;

/**
 * <p>Interface for listening for references. In case of a circular reference the reference cannot be immediately satisfied.
 * That is why certain <code>TypeSerializer</code> implementations should implement this interface.</p>
 * @author Martin
 *
 */
public interface OnReferenceListener {
	/**
	 * <p>This method is called when a referenced object has been found.</p>
	 * @param reference
	 * @param object
	 * @throws XMLSerializeException
	 */
	public void onReferenceFound(String reference, Object object) throws XMLSerializeException;
}
