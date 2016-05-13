package org.xmlserializer4j;

import java.text.NumberFormat;
import java.text.ParseException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

/**
 * <p><code>AbsSerializer</code> implementation for serializing <code>Number</code> objects.</p>
 * @author Martin
 *
 */
public class NumberSerializer extends AbsSerializer<Number> {
	
	/*
	 * Attributes
	 */
	
	private NumberFormat numberFormat;
	
	/*
	 * Constructor(s)
	 */
	
	public NumberSerializer() {
		this(NumberFormat.getInstance());
	}
	
	public NumberSerializer(NumberFormat numberFormat) {
		this.numberFormat = numberFormat;
	}
	
	/*
	 * Class methods
	 */
	
	/**
	 * <p>Returns the <code>NumberFormat</code> used for serialization.</p>
	 * @return
	 */
	public NumberFormat getNumberFormat() {
		return numberFormat;
	}
	
	/**
	 * <p>Sets the <code>NumberFormat</code> used for serialization.</p>
	 * @param numberFormat
	 */
	public void setNumberFormat(NumberFormat numberFormat) {
		this.numberFormat = numberFormat;
	}
	
	/*
	 * Interface implementations
	 */
	
	@Override
	public Element serialize(XMLSerializer xmlSerializer, String elementName, Number number) {
		Element element = super.serialize(xmlSerializer, elementName, number);
		element.setTextContent(numberFormat.format(number));
		return element;
	}

	@Override
	public Number deserialize(XMLSerializer xmlSerializer, Element element, Number number) {
		try {
			String clazzName = element.getAttribute(XMLSerializer.CLASS);
			Class<?> clazz = Class.forName(clazzName);
			if(number == null) {
				number = numberFormat.parse(element.getTextContent());
			}
			if(Byte.class.equals(clazz)) {
				return number.byteValue();
			} else if(Short.class.equals(clazz)) {
				return number.shortValue();
			} else if(Integer.class.equals(clazz)) {
				return number.intValue();
			} else if(Long.class.equals(clazz)) {
				return number.longValue();
			} else if(Float.class.equals(clazz)) {
				return number.floatValue();
			} else if(Double.class.equals(clazz)) {
				return number.doubleValue();
			}
			throw new IllegalArgumentException("Element of unexpected Number type");
		} catch (DOMException e) {
			throw new RuntimeException(e);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
