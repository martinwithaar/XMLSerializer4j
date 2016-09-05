package org.xmlserializer4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
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
	public Element serialize(XMLSerializer xmlSerializer, String elementName, Number number) throws XMLSerializeException {
		Element element = super.serialize(xmlSerializer, elementName, number);
		element.setTextContent(numberFormat.format(number));
		return element;
	}

	@Override
	public Number deserialize(XMLSerializer xmlSerializer, Element element, Number number) throws XMLSerializeException {
		try {
			String clazzName = element.getAttribute(XMLSerializer.CLASS);
			Class<?> clazz = Class.forName(clazzName);
			if(BigDecimal.class.equals(clazz)) {
				try {
					DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
					decimalFormat.setParseBigDecimal(true);
					return decimalFormat.parse(element.getTextContent());
				} catch(ClassCastException e) {
					throw new XMLSerializeException(e);
				}
			} else if(BigInteger.class.equals(clazz)) {
				try {
					DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
					decimalFormat.setParseBigDecimal(true);
					BigDecimal bigDecimal = (BigDecimal) decimalFormat.parse(element.getTextContent());
					return bigDecimal.toBigInteger();
				} catch(ClassCastException e) {
					throw new XMLSerializeException(e);
				}
			}
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
			throw new XMLSerializeException("Element of unexpected Number type");
		} catch (DOMException e) {
			throw new XMLSerializeException(e);
		} catch (ParseException e) {
			throw new XMLSerializeException(e);
		} catch (ClassNotFoundException e) {
			throw new XMLSerializeException(e);
		}
	}
}
