package org.xmlserializer4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;

/**
 * <p>Abstract <code>ObjectReferenceContext</code> implementation. Maps serialized objects to their XML elements for resolving object/element references.</p>
 * @author Martin
 *
 */
public abstract class AbsObjectReferenceContext implements ObjectReferenceContext {
	
	/*
	 * Attributes
	 */
	
	private Map<Object, String> referenceMap;
	private Map<Object, Element> elementMap;
	private Map<String, Object> objectMap;
	private Set<Object> referenced;
	
	/*
	 * Constructor(s)
	 */
	
	public AbsObjectReferenceContext() {
		this(new IdentityHashMap<Object, String>(), new IdentityHashMap<Object, Element>());
	}
	
	protected AbsObjectReferenceContext(Map<Object, String> referenceMap, Map<Object, Element> elementMap) {
		this.referenceMap = referenceMap;
		this.elementMap = elementMap;
		this.objectMap = new HashMap<String, Object>();
		this.referenced = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());
	}
	
	/*
	 * Class methods
	 */
	
	/**
	 * 
	 * @return
	 */
	protected Map<Object, String> getReferenceMap() {
		return referenceMap;
	}
	
	/**
	 * 
	 * @param object
	 * @return
	 */
	protected boolean isReferenced(Object object) {
		return referenced.contains(object);
	}
	
	/**
	 * 
	 * @param object
	 */
	protected void updateObjectId(Object object) {
		Element element = elementMap.get(object);
		if(element != null && !element.hasAttribute(XMLSerializer.ID)) {
			element.setAttribute(XMLSerializer.ID, referenceMap.get(object));
		}
	}
	
	/*
	 * Interface implementations
	 */
	
	@Override
	public Object getObject(String reference) {
		return objectMap.get(reference);
	}

	@Override
	public void mapReferencedObject(String reference, Object object) {
		objectMap.put(reference, object);
	}
	
	@Override
	public String getReference(Object object) {
		return referenceMap.get(object);
	}
	
	@Override
	public void setReferenced(Object object) {
		referenced.add(object);
	}
	
	@Override
	public void mapObjectElement(Object object, Element element) {
		elementMap.put(object, element);
		if(isReferenced(object)) {
			updateObjectId(object);
		}
	}
}
