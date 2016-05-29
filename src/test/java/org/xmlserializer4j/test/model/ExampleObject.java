package org.xmlserializer4j.test.model;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.xmlserializer4j.DefaultSerializer;
import org.xmlserializer4j.annotation.Inclusion;
import org.xmlserializer4j.annotation.XMLInclude;
import org.xmlserializer4j.annotation.XMLTypeOverride;
import org.xmlserializer4j.annotation.XMLTypeSerializer;

/**
 * <p>Example class for testing a variety of primitive and object types.</p>
 * @author Martin
 *
 */
@SuppressWarnings("unused")
public class ExampleObject {
	// Static fields can be serialized and deserialized too: even if they're final!
	protected static final Map<String, String> PRE_EXISTING_MAP = new LinkedHashMap<String, String>();
	private static String STATIC_STRING = "This object will be re-constructed upon deserialization,"
			+ "unlike static final fields which are only re-populated"
			+ "like the map on the previous line";
	// Static final primitives are omitted by default. They are unchangeable anyway
	private static final int JUST_SOME_NUMBER = 1337;
	
	// Arrays, collections and maps
	private int[] primes = new int[] { 2, 3, 5, 7, 11 };
	private Object[] objects = new Object[] { null, new Integer(86400), null };
	private ExampleObject[] justAnArray;
	private List<ExampleObject> someList;
	private Map<Integer, ExampleObject> someMap;
	
	// Primitives are serialized without type specification because their type is always known
	private int howMuchN;
	private boolean isSuperCool = true;
	private double equity = 1000000.00;
	
	// Some more typical objects
	private String helloWorld = "Hello world!";
	private Date date = new Date();
	private URL url;
	
	// By default null value fields are omitted but you can override this behavior
	@XMLInclude(include = Inclusion.ALWAYS)
	private Object aNullObject = null;
	
	// New object classes with with their very own TypeSerializer
	private File file = new File("LICENSE");
	private Class<?> clazz = Object.class;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aNullObject == null) ? 0 : aNullObject.hashCode());
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		long temp;
		temp = Double.doubleToLongBits(equity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + ((helloWorld == null) ? 0 : helloWorld.hashCode());
		result = prime * result + howMuchN;
		result = prime * result + (isSuperCool ? 1231 : 1237);
		result = prime * result + Arrays.hashCode(justAnArray);
		result = prime * result + Arrays.hashCode(objects);
		result = prime * result + Arrays.hashCode(primes);
		result = prime * result + ((someList == null) ? 0 : someList.hashCode());
		result = prime * result + ((someMap == null) ? 0 : someMap.hashCode());
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
		ExampleObject other = (ExampleObject) obj;
		if (aNullObject == null) {
			if (other.aNullObject != null)
				return false;
		} else if (!aNullObject.equals(other.aNullObject))
			return false;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (Double.doubleToLongBits(equity) != Double.doubleToLongBits(other.equity))
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
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
		if (!Arrays.equals(objects, other.objects))
			return false;
		if (!Arrays.equals(primes, other.primes))
			return false;
		if (someList == null) {
			if (other.someList != null)
				return false;
		} else if (!someList.equals(other.someList))
			return false;
		if (someMap == null) {
			if (other.someMap != null)
				return false;
		} else if (!someMap.equals(other.someMap))
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
	 * @param depth
	 * @return
	 */
	public static final ExampleObject getInstance(int depth) {
		// Populate map just once!
		if(PRE_EXISTING_MAP.isEmpty()) {
			PRE_EXISTING_MAP.put("Hello", "World");
			PRE_EXISTING_MAP.put("Weather", "nice & hot");
			PRE_EXISTING_MAP.put("Drink", "Martini on the rocks");
		}
		ExampleObject object = new ExampleObject();
		object.justAnArray = new ExampleObject[depth];
		object.someList = new ArrayList<ExampleObject>();
		object.someMap = new LinkedHashMap<Integer, ExampleObject>();
		for(int i = 0; i < depth; i++) {
			ExampleObject child = getInstance(depth - 1);
			object.justAnArray[i] = child;
			object.someMap.put(i, child);
		}
		object.someList = new ArrayList<ExampleObject>(Arrays.asList(object.justAnArray));
		object.howMuchN = depth;
		try {
			object.url = new URL("https://www.google.com");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return object;
	}
}