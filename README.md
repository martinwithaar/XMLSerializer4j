# xmlserializer4j
A simple and effective API for leveraging XML serialization in your Java application with ease.

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
  
    @XMLInclude(inclusion = Inclusion.NEVER)
    Object someObject; // This field is never included
  
    @XMLInclude(inclusion = Inclusion.NON_NULL)
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
