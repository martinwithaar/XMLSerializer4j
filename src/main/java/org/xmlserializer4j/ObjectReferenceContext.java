package org.xmlserializer4j;

import org.w3c.dom.Element;

/**
 * 
 * @author Martin
 *
 */
public interface ObjectReferenceContext {
	/**
	 * <p>Returns the object for the given reference.</p>
	 * <p>This method is used for deserialization only.</p>
	 * @param reference
	 * @return
	 */
	Object getObject(String reference);
	/**
	 * <p>Maps a referenced object for lookup when the reference is encountered again.</p>
	 * <p>This method is used for deserialization only.</p>
	 * @param reference
	 * @param object
	 */
	void mapReferencedObject(String reference, Object object);
	/**
	 * <p>Creates and returns a reference for <code>object</code>.</p>
	 * @param object
	 * @return
	 */
	String createReference(Object object);
	/**
	 * <p>Returns the reference for <code>object</code> or <code>null</code> if absent.</p>
	 * @param object
	 * @return
	 */
	String getReference(Object object);
	/**
	 * <p>Sets the <code>object</code> as referenced.</p>
	 * @param object
	 */
	void setReferenced(Object object);
	/**
	 * <p>Maps the XML element representing <code>object</code>. This element is referenced when <code>object</code> is encountered again.</p>
	 * @param object
	 * @param element
	 */
	void mapObjectElement(Object object, Element element);
}