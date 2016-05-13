package org.xmlserializer4j.test.model;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xmlserializer4j.annotation.XMLInclude;

/**
 * <p>Test class for testing a variety of primitive and object types.</p>
 * @author Martin
 *
 */
@SuppressWarnings("unused")
public class TestObject {
	// Static fields can be serialized and deserialized too: even if they're final!
	protected static final Map<String, String> PRE_EXISTING_MAP = new LinkedHashMap<String, String>();
	
	private static String STATIC_STRING = "This object will be re-constructed upon deserialization,"
			+ "unlike static final fields which are only re-populated"
			+ "like the map on the previous line";
	// Static final primitives are omitted by default. They are already set and are unchangeable anyway.
	private static final int JUST_SOME_NUMBER = 1337;
	
	// Arrays, collections and maps
	private TestObject[] justAnArray;
	private List<TestObject> listy;
	private Map<Integer, TestObject> mappetyMap;
	
	// Primitives are serialized without any class or type specification because their type is always known
	private int howMuchN;
	private boolean isSuperCool = true;
	private double equity = 1000000.00;
	// Some more array magic
	private int[] primes = new int[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29 };
	private Object[] objects = new Object[] { null, new Integer(86400), null };
	
	// Some more typical objects
	//@XMLTypeOverride(clazz = Object.class)
	private String helloWorld = "Hello world!";
	private Date date = new Date();
	
	// By default null value fields are omitted but you can override this behavior
	@XMLInclude
	private Object aNullObject = null;
	private URL url;
	
	/**
	 * Empty constructor
	 */
	public TestObject() {
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aNullObject == null) ? 0 : aNullObject.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		long temp;
		temp = Double.doubleToLongBits(equity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((helloWorld == null) ? 0 : helloWorld.hashCode());
		result = prime * result + howMuchN;
		result = prime * result + (isSuperCool ? 1231 : 1237);
		result = prime * result + Arrays.hashCode(justAnArray);
		result = prime * result + ((listy == null) ? 0 : listy.hashCode());
		result = prime * result + ((mappetyMap == null) ? 0 : mappetyMap.hashCode());
		result = prime * result + Arrays.hashCode(objects);
		result = prime * result + Arrays.hashCode(primes);
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestObject other = (TestObject) obj;
		if (aNullObject == null) {
			if (other.aNullObject != null)
				return false;
		} else if (!aNullObject.equals(other.aNullObject))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (Double.doubleToLongBits(equity) != Double.doubleToLongBits(other.equity))
			return false;
		if (helloWorld == null) {
			if (other.helloWorld != null)
				return false;
		} else if (!helloWorld.equals(other.helloWorld))
			return false;
		if (howMuchN != other.howMuchN)
			return false;
		if (isSuperCool != other.isSuperCool)
			return false;
		if (!Arrays.equals(justAnArray, other.justAnArray))
			return false;
		if (listy == null) {
			if (other.listy != null)
				return false;
		} else if (!listy.equals(other.listy))
			return false;
		if (mappetyMap == null) {
			if (other.mappetyMap != null)
				return false;
		} else if (!mappetyMap.equals(other.mappetyMap))
			return false;
		if (!Arrays.equals(objects, other.objects))
			return false;
		if (!Arrays.equals(primes, other.primes))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	
	/**
	 * <p>Recursively constructs instances of this class with depth <code>depth</code>.</p>
	 * @param howMuchN
	 * @return
	 */
	public static final TestObject getInstance(int depth) {
		// Populate map just once!
		if(PRE_EXISTING_MAP.isEmpty()) {
			PRE_EXISTING_MAP.put("Hello", "World");
			PRE_EXISTING_MAP.put("Weather", "nice & hot");
			PRE_EXISTING_MAP.put("Drink", "Martini on the rocks");
		}
		TestObject to = new TestObject();
		to.justAnArray = new TestObject[depth];
		to.listy = new ArrayList<TestObject>();
		to.mappetyMap = new LinkedHashMap<Integer, TestObject>();
		for(int i = 0; i < depth; i++) {
			TestObject child = getInstance(depth - 1);
			to.justAnArray[i] = child;
			to.mappetyMap.put(i, child);
		}
		to.listy = new ArrayList<TestObject>(Arrays.asList(to.justAnArray));
		to.howMuchN = depth;
		try {
			to.url = new URL("https://www.google.com");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return to;
	}
}