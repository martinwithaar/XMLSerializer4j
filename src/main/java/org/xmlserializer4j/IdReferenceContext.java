package org.xmlserializer4j;

import java.util.Map;

/**
 * <p><code>AbsObjectReferenceContext</code> implementation that references to objects with incrementing object id's.</p>
 * @author Martin
 *
 */
public class IdReferenceContext extends AbsObjectReferenceContext {
	
	/*
	 * Attributes
	 */
	
	private int index = 1;
	
	/*
	 * Overrides
	 */
	
	@Override
	public String getReference(Object object) {
		Map<Object, String> referenceMap = getReferenceMap();
		if(referenceMap.containsKey(object) && referenceMap.get(object) == null) {
			String reference = String.valueOf(index++);
			referenceMap.put(object, reference);
			updateObjectId(object);
		}
		//if(isReferenced(object)) {
		//	updateObjectId(object);
		//}
		return super.getReference(object);
	}
	
	/*
	 * Interface implementations
	 */
	
	@Override
	public String createReference(Object object) {
		// Reference value is generated when first requested
		getReferenceMap().put(object, null);
		//getReferenceMap().put(object, String.valueOf(index++));
		return null;
	}
}
