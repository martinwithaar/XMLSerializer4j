package org.xmlserializer4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

/**
 * <p><code>AbsSerializer</code> implementation for serializing <code>Date</code> objects.</p>
 * @author Martin
 *
 */
public class DateSerializer extends AbsSerializer<Date> {
	
	/*
	 * Attributes
	 */
	
	private DateFormat dateFormat;
	
	/*
	 * Constructor(s)
	 */
	
	public DateSerializer() {
		this(null);
	}
	
	public DateSerializer(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	/*
	 * Class methods
	 */
	
	/**
	 * <p>Returns the <code>DateFormat</code> used for serialization.</p>
	 * @return
	 */
	public DateFormat getDateFormat() {
		return dateFormat;
	}
	
	/**
	 * <p>Sets the <code>DateFormat</code> used for serialization.</p>
	 * @param dateFormat
	 */
	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	/*
	 * Interface implementations
	 */

	@Override
	public Element serialize(XMLSerializer xmlSerializer, String elementName, Date date) {
		Element element = super.serialize(xmlSerializer, elementName, date);
		element.setTextContent(dateFormat != null ? dateFormat.format(date) : String.valueOf(date.getTime()));
		return element;
	}

	@Override
	public Date deserialize(XMLSerializer xmlSerializer, Element element, Date date) {
		try {
			return date == null ? dateFormat != null ? dateFormat.parse(element.getTextContent()) : new Date(Long.valueOf(element.getTextContent())) : date;
		} catch (DOMException e) {
			throw new RuntimeException(e);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
