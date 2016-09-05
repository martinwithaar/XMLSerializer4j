package org.xmlserializer4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.xmlserializer4j.annotation.Inclusion;
import org.xmlserializer4j.annotation.XMLInclude;
import org.xmlserializer4j.annotation.XMLTypeOverride;
import org.xmlserializer4j.annotation.XMLTypeSerializer;

/**
 * <p>XML serializer class that encodes Java objects into XML representations.</p>
 * @author Martin
 *
 */
public class XMLSerializer {
	
	/**
	 * 
	 */
	public static final DocumentBuilder DEFAULT_DOCUMENT_BUILDER;
	static {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		try {
			DEFAULT_DOCUMENT_BUILDER = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
	/**
	 * <p>Human readable transformer that performs indentation of output data. Set to 4 spaces per indent.</p>
	 */
	public static final Transformer HUMAN_READABLE_TRANSFORMER;
	static {
		try {
			HUMAN_READABLE_TRANSFORMER = TRANSFORMER_FACTORY.newTransformer();
			HUMAN_READABLE_TRANSFORMER.setOutputProperty(OutputKeys.INDENT, "yes");
			HUMAN_READABLE_TRANSFORMER.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * <p>Default transformer.</p>
	 */
	public static final Transformer DEFAULT_TRANSFORMER;
	static {
		try {
			DEFAULT_TRANSFORMER = TRANSFORMER_FACTORY.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	
	// Settings
	/**
	 * <p>Serialize null values.</p>
	 */
	public static final Setting INCLUDE_NULL_VALUES = new Setting("INCLUDE_NULL_VALUES");
	/**
	 * <p>Includes primitives <code>0</code> and <code>false</code> values. These are not strictly necessary for correct deserialization.</p>
	 */
	public static final Setting INCLUDE_PRIMITIVE_ZERO_FALSE = new Setting("INCLUDE_PRIMITIVE_ZERO_FALSE");
	/**
	 * <p>Include parent class fields.</p>
	 */
	public static final Setting INCLUDE_PARENTCLASS_FIELDS = new Setting("INCLUDE_PARENTCLASS_FIELDS");
	/**
	 * <p>Include static fields.</p>
	 */
	public static final Setting INCLUDE_STATIC = new Setting("INCLUDE_STATIC");
	/**
	 * <p>Include static final primitive values.</p>
	 */
	public static final Setting INCLUDE_STATIC_FINAL_PRIMITIVES = new Setting("INCLUDE_STATIC_FINAL_PRIMITIVES");
	/**
	 * <p>Always includes the scope attribute for resolving duplicate field names in parent classes.</p>
	 */
	public static final Setting INCLUDE_SCOPE_ALWAYS = new Setting("INCLUDE_SCOPE_ALWAYS");
	/**
	 * <p>Excludes all fields by default unless an explicit <code>XMLInclude</code> annotation is present.</p>
	 */
	public static final Setting EXCLUDE_ALL = new Setting("EXCLUDE_ALL");
	/**
	 * <p>Include object hash codes as attributes. Useful for debugging purposes.</p>
	 */
	public static final Setting INCLUDE_HASHCODE = new Setting("INCLUDE_HASHCODE");
	
	private static final Set<Setting> DEFAULT_SETTINGS;
	static {
		Set<Setting> settings = new LinkedHashSet<Setting>();
		settings.add(INCLUDE_PRIMITIVE_ZERO_FALSE);
		settings.add(INCLUDE_PARENTCLASS_FIELDS);
		//settings.add(INCLUDE_STATIC);
		DEFAULT_SETTINGS = Collections.unmodifiableSet(settings);
	}
	
	private static final List<SerializerEntry> DEFAULT_SERIALIZERS;
	static {
		List<SerializerEntry> serializers = new LinkedList<SerializerEntry>();
		serializers.add(new SerializerEntry(Pattern.class, new PatternSerializer()));
		serializers.add(new SerializerEntry(URL.class, new URLSerializer()));
		serializers.add(new SerializerEntry(File.class, new FileSerializer()));
		serializers.add(new SerializerEntry(Date.class, new DateSerializer()));
		serializers.add(new SerializerEntry(Class.class, new ClassSerializer()));
		serializers.add(new SerializerEntry(Iterable.class, new IterableSerializer()));
		serializers.add(new SerializerEntry(Map.class, new MapSerializer()));
		serializers.add(new SerializerEntry(Entry.class, new EntrySerializer()));
		serializers.add(new SerializerEntry(Number.class, new NumberSerializer()));
		serializers.add(new SerializerEntry(Boolean.class, new BooleanSerializer()));
		serializers.add(new SerializerEntry(CharSequence.class, new CharSequenceSerializer()));
		serializers.add(new SerializerEntry(Object[].class, new ArraySerializer()));
		serializers.add(new SerializerEntry(Object.class, new DefaultSerializer()));
		DEFAULT_SERIALIZERS = Collections.unmodifiableList(serializers);
	}
	
	private static final Class<?>[] PRIMITIVE_TYPES = new Class<?>[] { byte.class, short.class, int.class, long.class, float.class, double.class, boolean.class, char.class };
	protected static final Map<String, Class<?>> PRIMITIVE_TYPE_MAP;
	static {
		Map<String, Class<?>> map = new HashMap<String, Class<?>>();
		for(Class<?> clazz: PRIMITIVE_TYPES) {
			map.put(clazz.getSimpleName(), clazz);
		}
		PRIMITIVE_TYPE_MAP = Collections.unmodifiableMap(map);
	}
	
	/**
	 * <p>Root object element name.</p>
	 */
	private static final String ROOT_OBJECT = "rootObject";
	/**
	 * <p>Array/collection element element name.</p>
	 */
	protected static final String ELEM = "elem";
	/**
	 * <p><code>null</code> value attribute key.</p>
	 */
	protected static final String NULL = "null";
	/**
	 * <p>Class name attribute key.</p> 
	 */
	protected static final String CLASS = "class";
	/**
	 * <p>Scope class name attribute key.</p>
	 */
	protected static final String SCOPE = "scope";
	/**
	 * <p>Array component type attribute key.</p>
	 */
	protected static final String TYPE = "type";
	/**
	 * <p>Array length attribute key.</p>
	 */
	protected static final String LENGTH = "length";
	/**
	 * <p>Array element index attribute key.</p>
	 */
	protected static final String INDEX = "index";
	/**
	 * <p>Object id attribute key.</p>
	 */
	protected static final String ID = "id";
	/**
	 * <p>Object reference attribute key.</p>
	 */
	protected static final String REF = "ref";
	/**
	 * <p>Boolean attribute value <code>true</code>.</p>
	 */
	private static final String TRUE = "true";
	/**
	 * <p>Boolean attribute value <code>false</code>.</p>
	 */
	//private static final String FALSE = "false";
	/**
	 * <p>Object hashcode attribute key.</p>
	 */
	private static final String HASHCODE = "hashCode";
	/**
	 * <p>Custom serializer attribute key.</p>
	 */
	private static final String SERIALIZER = "serializer";
	/**
	 * <p>Custom type override attribute key.</p>
	 */
	private static final String OVERRIDE = "override";
	
	/*
	 * Attributes
	 */
	
	private Document document;
	private Transformer transformer;
	private List<SerializerEntry> serializers;
	private Set<Setting> settings;
	private Map<Class<?>, TypeSerializer<?>> cacheMap;
	private Map<Class<? extends TypeSerializer<?>>, TypeSerializer<?>> customSerializerMap;
	private Map<Object, Integer> circularReferences;
	private Set<Field> serializedStaticFields;
	private Set<OnReferenceListener> onReferenceListeners;
	private ObjectReferenceContext objectReferenceContext;
	
	/*
	 * Constructor(s)
	 */
	
	/**
	 * <p>Constructs a default <code>XMLSerializer</code> instance.</p>
	 */
	public XMLSerializer() {
		this(new LinkedList<SerializerEntry>(DEFAULT_SERIALIZERS), DEFAULT_DOCUMENT_BUILDER.newDocument(), DEFAULT_TRANSFORMER);
	}
	
	/**
	 * <p>Constructs a default <code>XMLSerializer</code> instance.</p>
	 * @param file
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public XMLSerializer(File file) throws SAXException, IOException {
		this(new LinkedList<SerializerEntry>(DEFAULT_SERIALIZERS), DEFAULT_DOCUMENT_BUILDER.parse(file), DEFAULT_TRANSFORMER);
	}
	
	/**
	 * <p>Constructs a default <code>XMLSerializer</code> instance.</p>
	 * @param uri
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public XMLSerializer(String uri) throws SAXException, IOException {
		this(new LinkedList<SerializerEntry>(DEFAULT_SERIALIZERS), DEFAULT_DOCUMENT_BUILDER.parse(uri), DEFAULT_TRANSFORMER);
	}
	
	/**
	 * <p>Constructs a default <code>XMLSerializer</code> instance.</p>
	 * @param is
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public XMLSerializer(InputStream is) throws SAXException, IOException {
		this(new LinkedList<SerializerEntry>(DEFAULT_SERIALIZERS), DEFAULT_DOCUMENT_BUILDER.parse(is), DEFAULT_TRANSFORMER);
	}
	
	/**
	 * <p>Constructs a default <code>XMLSerializer</code> instance.</p>
	 * @param is
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public XMLSerializer(InputSource is) throws SAXException, IOException {
		this(new LinkedList<SerializerEntry>(DEFAULT_SERIALIZERS), DEFAULT_DOCUMENT_BUILDER.parse(is), DEFAULT_TRANSFORMER);
	}
	
	/**
	 * <p>Constructs a default <code>XMLSerializer</code> instance.</p>
	 * @param document
	 */
	public XMLSerializer(Document document) {
		this(new LinkedList<SerializerEntry>(DEFAULT_SERIALIZERS), document, DEFAULT_TRANSFORMER);
	}
	
	/**
	 * <p>Constructs an <code>XMLSerializer</code> instance using a custom serializer list.</p>
	 * @param serializers
	 * @param document
	 * @param transformer
	 */
	public XMLSerializer(List<SerializerEntry> serializers, Document document, Transformer transformer) {
		this.serializers = serializers;
		this.document = document;
		this.transformer = transformer;
		
		this.settings = new LinkedHashSet<Setting>(DEFAULT_SETTINGS);
		this.cacheMap = new HashMap<Class<?>, TypeSerializer<?>>();
		this.customSerializerMap = new HashMap<Class<? extends TypeSerializer<?>>, TypeSerializer<?>>();
		this.circularReferences = new IdentityHashMap<Object, Integer>();
		this.serializedStaticFields = new HashSet<Field>();
		
		this.onReferenceListeners = new HashSet<OnReferenceListener>();
		for(SerializerEntry entry: serializers) {
			TypeSerializer<?> serializer = entry.getSerializer();
			if(serializer instanceof OnReferenceListener) {
				onReferenceListeners.add((OnReferenceListener) serializer);
			}
		}
		this.objectReferenceContext = new IdReferenceContext();
	}
	
	/*
	 * Class methods
	 */
	
	/**
	 * <p>Returns the XML document.</p>
	 * @return
	 */
	public Document getDocument() {
		return document;
	}
	
	/**
	 * <p>Returns the transformer.</p>
	 * @return
	 */
	public Transformer getTransformer() {
		return transformer;
	}
	
	/**
	 * <p>Sets the transformer.</p>
	 * <p>Use {@link #HUMAN_READABLE_TRANSFORMER} to generate human-readable XML files that include linebreaks and indentation.</p>
	 * @param transformer
	 */
	public void setTransformer(Transformer transformer) {
		this.transformer = transformer;
	}
	
	/**
	 * <p>Returns the serializer list.</p>
	 * <p>The default settings use a <code>LinkedList</code> so adding your own <code>SerializerEntry</code> implementations can be easily done
	 * by using {@link LinkedList#addFirst(Object)}.</p>
	 * <p>The first serializer in the list that is applicable for serializing a particular object is used and cached for reuse for that particular object class.
	 * Thusly the last serializer in the list is an instance of the <code>DefaultSerializer</code> by default.</p>
	 * <p><b>Note:</b> When the list is altered after a serialization pass a call to {@link #clearCache()} must be made
	 * in order to avoid the cache interfering with the updated configuration.</p>
	 * @return
	 */
	public List<SerializerEntry> getSerializers() {
		return serializers;
	}
	
	/**
	 * <p>Sets the object reference context.</p>
	 * @param objectReferenceContext
	 */
	public void setObjectReferenceContext(ObjectReferenceContext objectReferenceContext) {
		this.objectReferenceContext = objectReferenceContext;
	}
	
	/**
	 * <p>Enables a given setting.</p>
	 * @param setting
	 */
	public void enable(Setting setting) {
		settings.add(setting);
	}
	
	/**
	 * <p>Disables a given setting.</p>
	 * @param setting
	 */
	public void disable(Setting setting) {
		settings.remove(setting);
	}
	
	/**
	 * <p>Indicates whether a setting is enabled.</p>
	 * @return
	 */
	public boolean isEnabled(Setting setting) {
		return settings.contains(setting);
	}
	
	/**
	 * <p>Returns the appropriate registered <code>TypeSerializer</code> for the given <code>Class</code> 
	 * or <code>null</code> when none could be found.</p>
	 * <p>When a <code>TypeSerializer</code> implementation is found it is cached for that particular class.
	 * This enables a fast lookup the second time an object of the same class must be serialized.</p>
	 * @param clazz
	 * @return
	 */
	public TypeSerializer<?> getSerializerByClass(Class<?> clazz) {
		TypeSerializer<?> serializer = cacheMap.get(clazz);
		if(serializer != null) {
			return serializer;
		}
		for(SerializerEntry entry: serializers) {
			if(entry.getTargetClass().isAssignableFrom(clazz)) {
				serializer = entry.getSerializer();
				cacheMap.put(clazz, serializer);
				return serializer;
			}
		}
		return null;
	}
	
	/**
	 * <p>Serializes an object to XML and returns the document containing the result.</p>
	 * @param object
	 * @throws XMLSerializeException
	 */
	public Document serialize(Object object) throws XMLSerializeException {
		Element element = serializeToElement(object, ROOT_OBJECT, null);
		if(element != null) {
			document.appendChild(element);
		}
		circularReferences.clear();
		serializedStaticFields.clear();
		return document;
	}
	
	/**
	 * <p>Serializes an object to XML and saves the result.</p>
	 * @param object
	 * @param result
	 * @throws XMLSerializeException
	 */
	public void serialize(Object object, StreamResult result) throws XMLSerializeException {
		serialize(object);
		DOMSource source = new DOMSource(document);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new XMLSerializeException(e);
		}
	}
	
	/**
	 * <p>Serializes an object to XML and saves the result to an <code>OutputStream</code>.</p>
	 * @param object
	 * @param os
	 * @throws XMLSerializeException
	 */
	public void serialize(Object object, OutputStream os) throws XMLSerializeException {
		serialize(object, new StreamResult(os));
	}
	
	/**
	 * <p>Serializes an object to XML and saves the result to a <code>File</code>.</p>
	 * @param object
	 * @param file
	 * @throws XMLSerializeException
	 */
	public void serialize(Object object, File file) throws XMLSerializeException {
		try {
			serialize(object, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			throw new XMLSerializeException(e);
		}
	}
	
	/**
	 * <p>Serializes an object to XML and saves the result to a filepath.</p>
	 * @param object
	 * @param filepath
	 * @throws XMLSerializeException
	 */
	public void serialize(Object object, String filepath) throws XMLSerializeException {
		serialize(object, new File(filepath));
	}
	
	/**
	 * <p>Serializes an object to an XML element.</p>
	 * @param object
	 * @param elementName
	 * @param annotatedElement optional source of annotations for the object
	 * @throws XMLSerializeException
	 */
	@SuppressWarnings("unchecked")
	public Element serializeToElement(Object object, String elementName, AnnotatedElement annotatedElement) throws XMLSerializeException {
		XMLInclude include = annotatedElement != null ? annotatedElement.getAnnotation(XMLInclude.class) : null;
		if(object != null) {
			Class<?> clazz = object.getClass();
			if(include == null) {
				include = clazz.getAnnotation(XMLInclude.class);
			}
			if((!isEnabled(EXCLUDE_ALL) && (include == null || Inclusion.NEVER != include.include()))) {
				String reference = objectReferenceContext != null ? objectReferenceContext.getReference(object) : null;
				Integer circularReferenceCount = circularReferences.get(object);
				if(circularReferenceCount == null) {
					circularReferenceCount = 0;
				}
				if(reference == null && circularReferenceCount == 0) {
					try {
						if(objectReferenceContext != null) {
							objectReferenceContext.createReference(object);
						}
						// Avoid cyclic dependencies
						circularReferences.put(object, ++circularReferenceCount);
						// Check for a custom type serializer annotation
						XMLTypeSerializer typeSerializer = clazz.getAnnotation(XMLTypeSerializer.class);
						if(typeSerializer == null && annotatedElement != null) {
							typeSerializer = annotatedElement.getAnnotation(XMLTypeSerializer.class);
						}
						Element element;
						if(typeSerializer != null) {
							// Reuse or create custom serializer
							Class<? extends TypeSerializer<?>> serializerClazz = typeSerializer.clazz();
							TypeSerializer<Object> serializer = (TypeSerializer<Object>) customSerializerMap.get(serializerClazz);
							if(serializer == null) {
								try {
									serializer = (TypeSerializer<Object>) serializerClazz.newInstance();
									customSerializerMap.put(serializerClazz, serializer);
								} catch (InstantiationException e) {
									throw new XMLSerializeException(e);
								} catch (IllegalAccessException e) {
									throw new XMLSerializeException(e);
								}
							}
							element = serializer.serialize(this, elementName, object);
							element.setAttribute(SERIALIZER, serializerClazz.getName());
						} else if(clazz.isArray()) {
							// Get array specific serializer
							TypeSerializer<Object> serializer = (TypeSerializer<Object>) getSerializerByClass(Object[].class);
							element = serializer.serialize(this, elementName, object);
						} else {
							// Check for type override annotation
							XMLTypeOverride typeOverride = clazz.getAnnotation(XMLTypeOverride.class);
							if(typeOverride == null && annotatedElement != null) {
								typeOverride = annotatedElement.getAnnotation(XMLTypeOverride.class);
							}
							// Get appropriate serializer for class
							TypeSerializer<Object> serializer = (TypeSerializer<Object>) getSerializerByClass(typeOverride != null ? typeOverride.clazz() : clazz);
							if(serializer == null) {
								throw new XMLSerializeException("No serializer found for class: " + clazz.getCanonicalName());
							}
							element = serializer.serialize(this, elementName, object);
							if(typeOverride != null) {
								element.setAttribute(OVERRIDE, typeOverride.clazz().getName());
							}
						}
						if(objectReferenceContext != null) {
							objectReferenceContext.mapObjectElement(object, element);
						}
						if(isEnabled(INCLUDE_HASHCODE)) {
							element.setAttribute(HASHCODE, String.valueOf(object.hashCode()));
						}
						return element;
					} finally {
						circularReferences.put(object, --circularReferenceCount);
					}
				} else {
					if(reference != null) {
						// Reference not set here yet
						Element element = document.createElement(elementName);
						element.setAttribute(REF, reference);
						if(objectReferenceContext != null) {
							objectReferenceContext.setReferenced(object);
						}
						return element;
					}
				}
			}
		} else {
			if(isEnabled(INCLUDE_NULL_VALUES) || (include != null && Inclusion.ALWAYS == include.include())) {
				// Add special null element
				Element element = document.createElement(elementName);
				element.setAttribute(NULL, TRUE);
				return element;
			}
		}
		return null;
	}
	
	/**
	 * <p>Returns a <code>Set</code> with already serialized static fields.</p>
	 * @return
	 */
	protected Set<Field> getSerializedStaticFields() {
		return serializedStaticFields;
	}
	
	/**
	 * <p>Deserializes the XML document.</p>
	 * @return
	 * @throws XMLSerializeException
	 */
	public Object deserialize() throws XMLSerializeException {
		Element element = document.getDocumentElement();
		return element != null ? deserializeElement(element, null) : null;
	}
	
	/**
	 * <p>Deserializes an XML element.</p>
	 * @param element
	 * @param object optional runtime object to deserialize into
	 * @return
	 * @throws XMLSerializeException
	 */
	@SuppressWarnings("unchecked")
	public Object deserializeElement(Element element, Object object) throws XMLSerializeException {
		if(element.hasAttribute(SERIALIZER)) {
			// Reuse or create custom serializer
			Class<? extends TypeSerializer<?>> serializerClazz;
			try {
				serializerClazz = (Class<? extends TypeSerializer<?>>) Class.forName(element.getAttribute(SERIALIZER));
			} catch (ClassNotFoundException e) {
				throw new XMLSerializeException(e);
			}
			TypeSerializer<Object> serializer = (TypeSerializer<Object>) customSerializerMap.get(serializerClazz);
			if(serializer == null) {
				try {
					serializer = (TypeSerializer<Object>) serializerClazz.newInstance();
					customSerializerMap.put(serializerClazz, serializer);
				} catch (InstantiationException e) {
					throw new XMLSerializeException(e);
				} catch (IllegalAccessException e) {
					throw new XMLSerializeException(e);
				}
			}
			object = serializer.deserialize(this, element, object);
		} else if(element.hasAttribute(CLASS)) {
			try {
				Class<?> clazz;
				if(element.hasAttribute(OVERRIDE)) {
					clazz = Class.forName(element.getAttribute(OVERRIDE));
				} else {
					clazz = Class.forName(element.getAttribute(CLASS));
				}
				if(object != null && !clazz.equals(object.getClass())) {
					throw new XMLSerializeException("Runtime object class is not equal to serialized class; expected: " + clazz.getName() + " encountered: " + object.getClass().getName());
				}
				TypeSerializer<Object> serializer = (TypeSerializer<Object>) getSerializerByClass(clazz);
				if(serializer == null ) {
					throw new XMLSerializeException("No serializer found for class: " + clazz.getCanonicalName());
				}
				object = serializer.deserialize(this, element, object);
			} catch (ClassNotFoundException e) {
				throw new XMLSerializeException(e);
			}
		} else if(element.hasAttribute(TYPE)) {
			TypeSerializer<Object[]> serializer = (TypeSerializer<Object[]>) getSerializerByClass(Object[].class);
			if(serializer == null ) {
				throw new XMLSerializeException("No serializer found for class: " + Object[].class.getCanonicalName());
			}
			object = serializer.deserialize(this, element, (Object[]) object);
		} else if(element.hasAttribute(REF)) {
			if(objectReferenceContext != null) {
				return objectReferenceContext.getObject(element.getAttribute(REF));
			} else {
				throw new XMLSerializeException("Object reference context not set");
			}
		} else if(TRUE.equals(element.getAttribute(NULL))) {
			object = null;
		} else {
			throw new XMLSerializeException("Could not deserialize element");
		}
		if(element.hasAttribute(ID)) {
			String reference = element.getAttribute(ID);
			if(objectReferenceContext != null) {
				objectReferenceContext.mapReferencedObject(reference, object);
			}
			for(OnReferenceListener onReferenceListener: onReferenceListeners) {
				onReferenceListener.onReferenceFound(reference, object);
			}
		}
		return object;
	}
	
	/**
	 * <p>Clears the serializer cache.</p>
	 * <p>This is only necessary when the cache is already populated and the serializer configuration has been altered.</p>
	 */
	public void clearCache() {
		cacheMap.clear();
		customSerializerMap.clear();
	}
	
	/*
	 * Nested classes
	 */
	
	/**
	 * 
	 * @author Martin
	 *
	 */
	private static class Setting {
		private String name;

		private Setting(String name) {
			super();
			this.name = name;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
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
			Setting other = (Setting) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Setting [name=" + name + "]";
		}
	}
}