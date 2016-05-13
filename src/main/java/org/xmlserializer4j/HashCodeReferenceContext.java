package org.xmlserializer4j;

import java.util.HashMap;

import org.w3c.dom.Element;

/**
 * <p>Experimental <code>AbsObjectReferenceContext</code> implementation that references to objects using their hashcodes.</p>
 * <p>This means that objects merely need to have the same hashcode to be referred. In the case of hashcode collisions this causes
 * unpredictable behavior.</p>
 * @author Martin
 *
 */
public class HashCodeReferenceContext extends AbsObjectReferenceContext {
	
	/*
	 * Constructor(s)
	 */
	
	/**
	 * 
	 */
	public HashCodeReferenceContext() {
		super(new HashMap<Object, String>(), new HashMap<Object, Element>());
	}
	
	/*
	 * Overrides
	 */
	
	@Override
	public String getReference(Object object) {
		if(isReferenced(object)) {
			updateObjectId(object);
		}
		return super.getReference(object);
	}
	
	/*
	 * Interface implementations
	 */
	
	@Override
	public String createReference(Object object) {
		String reference = String.valueOf(object.hashCode());
		getReferenceMap().put(object, reference);
		return reference;
	}
}
