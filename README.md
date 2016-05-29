# XMLSerializer4j
A simple and effective API for leveraging XML serialization in your Java application.

## Highlights
XMLSerializer4j comes with many features that sets it apart from the rest:
* Serialization of collections and maps out-of-the-box as well as many other popular Java classes
* Circular references are fully supported
* High performance batch serialization
* Annotations for fine-grained control on the type and field level
* Serialization of static fields
* Restores static final objects to their former state

## Quickstart guide
Serializing an object to a file is as easy as:
```java
XMLSerializer xmlSerializer = new XMLSerializer();
xmlSerializer.serialize(myObject, "MySerializedObject.xml");
```
Deserialize the file like this:

```java
XMLSerializer xmlSerializer = new XMLSerializer("MySerializedObject.xml");
Object myObject = xmlSerializer.deserialize(myObject);
```

By default the XML output omits line breaks and unnecessary whitespace. In order to make the XML output human-readable with line breaks and indentation included do this:

```java
XMLSerializer xmlSerializer = new XMLSerializer();
xmlSerializer.setTransformer(XMLSerializer.HUMAN_READABLE_TRANSFORMER);
xmlSerializer.serialize(myObject, "MySerializedObject.xml");
```

Use *GZIPOutputStream* and *GZIPInputStream* to greatly reduce the size of the serialized XML output:

Serialize:
```java
XMLSerializer xmlSerializer = new XMLSerializer();
OutputStream os = null;
try {
	os = new GZIPOutputStream(new FileOutputStream("MySerializedObject.xml"));
	xmlSerializer.serialize(myObject, os);
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
```

Deserialize:

```java
Object myObject;
InputStream is = null;
try {
	is = new GZIPInputStream(new FileInputStream("MySerializedObject.xml"));
	xmlSerializer = new XMLSerializer(is);
	myObject = xmlSerializer.deserialize();
} finally {
	if(is != null) {
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
```

## XML output

Java objects are serialized in different ways based on their type. If no custom *TypeSerializer* is found for a given type the default serialization mechanism is used.

Consider the following example object class:
```java
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
	@XMLInclude
	private Object aNullObject = null;

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
```

An instance of the class is serialized like so:

```java
// Create test object
Object object = ExampleObject.getInstance(1);

// Create serializer & serialize test object
XMLSerializer xmlSerializer = new XMLSerializer();
xmlSerializer.setTransformer(XMLSerializer.HUMAN_READABLE_TRANSFORMER);
xmlSerializer.serialize(object, System.out);
```

The resulting XML output looks like this:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<rootObject class="org.xmlserializer4j.test.model.ExampleObject">
    <primes type="int">
        <elem>2</elem>
        <elem>3</elem>
        <elem>5</elem>
        <elem>7</elem>
        <elem>11</elem>
    </primes>
    <objects length="3" type="java.lang.Object">
        <elem class="java.lang.Integer" index="1">86,400</elem>
    </objects>
    <justAnArray type="org.xmlserializer4j.test.model.ExampleObject">
        <elem class="org.xmlserializer4j.test.model.ExampleObject" id="1">
            <primes type="int">
                <elem>2</elem>
                <elem>3</elem>
                <elem>5</elem>
                <elem>7</elem>
                <elem>11</elem>
            </primes>
            <objects length="3" type="java.lang.Object">
                <elem class="java.lang.Integer" index="1">86,400</elem>
            </objects>
            <justAnArray type="org.xmlserializer4j.test.model.ExampleObject"/>
            <someList class="java.util.ArrayList"/>
            <someMap class="java.util.LinkedHashMap"/>
            <howMuchN>0</howMuchN>
            <isSuperCool>true</isSuperCool>
            <equity>1000000.0</equity>
            <helloWorld class="java.lang.String" id="2">Hello world!</helloWorld>
            <date class="java.util.Date">1463236620067</date>
            <url class="java.net.URL">https://www.google.com</url>
            <aNullObject null="true"/>
        </elem>
    </justAnArray>
    <someList class="java.util.ArrayList">
        <elem ref="1"/>
    </someList>
    <someMap class="java.util.LinkedHashMap">
        <entry>
            <key class="java.lang.Integer">0</key>
            <value ref="1"/>
        </entry>
    </someMap>
    <howMuchN>1</howMuchN>
    <isSuperCool>true</isSuperCool>
    <equity>1000000.0</equity>
    <helloWorld ref="2"/>
    <date class="java.util.Date">1463236620067</date>
    <url class="java.net.URL">https://www.google.com</url>
    <aNullObject null="true"/>
</rootObject>
```

As you can see above, references to the same object instance are reused using id's and corresponding *ref* attribute values. This way the same object is never instantiated twice.

## Settings
Various settings can be enabled or disabled altering the serialization behavior. Some of them are enabled by default whereas some have to be enabled explicitly.

**Available settings:**
* *INCLUDE_NULL_VALUES* - Include all *null* values
* *INCLUDE_PARENTCLASS_FIELDS* - Include all parent class fields (enabled by default)
* *INCLUDE_STATIC* - Include static fields
* *INCLUDE_STATIC_FINAL_PRIMITIVES* - Include static final primitives (these can't actually mutate)
* *EXCLUDE_ALL* - Exclude all fields unless explicitly included
* *INCLUDE_HASHCODE* - Include hashcodes as attributes

## Annotations
XMLSerializer4j comes with a series of annotations that enable fine-grained control on the type and field level.
This way you can control exactly which types and fields are serialized and in what way.

**Available annotations:**
* *XMLInclude* - overrides default inclusion settings
* *XMLTypeSerializer* - use custom type serializer for type/field
* *XMLTypeOverride* - consider object of custom class type for type/field

Example of how to use annotations on fields:

```java
class MyObject {
    @XMLInclude
    String myString = null; // This field is always included
  
    @XMLInclude(include = Inclusion.NEVER)
    Object someObject; // This field is never included
  
    @XMLInclude(include = Inclusion.NON_NULL)
    Object nonNullObject = new Object(); // This field is only included when not null (default behavior)
  
    @XMLTypeSerializer(clazz = CustomMyObjectSerializer.class)
    MyObject myObject; // Use a different TypeSerializer implementation for this field
}
```

## Extensible API
Define the way objects are serialized and deserialized by writing and registering your own *TypeSerializer* implementation.

Add your own *TypeSerializer* implementation to an *XMLSerializer* instance like this:
```java
XMLSerializer xmlSerializer = new XMLSerializer();
LinkedList<SerializerEntry> serializers = (LinkedList<SerializerEntry>) xmlSerializer.getSerializers();
serializers.addFirst(new SerializerEntry(MyObject.class, new MyObjectSerializer()));
```
**Note:** The placement of the serializer in the list is important: the first encountered serializer that is applicable is used.
