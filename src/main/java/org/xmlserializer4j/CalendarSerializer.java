package org.xmlserializer4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

/**
 * <p><code>AbsSerializer</code> implementation for serializing <code>Calendar</code> objects.</p>
 * @author Martin
 *
 */
public class CalendarSerializer extends AbsSerializer<Calendar> {
	
	/*
	 * Attributes
	 */
	
	private DateFormat dateFormat;
	
	/*
	 * Constructor(s)
	 */
	
	public CalendarSerializer() {
		this(null);
	}
	
	public CalendarSerializer(DateFormat dateFormat) {
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
	public Element serialize(XMLSerializer xmlSerializer, String elementName, Calendar cal) throws XMLSerializeException {
		Element element = super.serialize(xmlSerializer, elementName, cal);
		Date date = cal.getTime();
		element.setTextContent(dateFormat != null ? dateFormat.format(date) : String.valueOf(date.getTime()));
		return element;
	}

	@Override
	public Calendar deserialize(XMLSerializer xmlSerializer, Element element, Calendar cal) throws XMLSerializeException {
		try {
			if(cal == null) {
				cal = Calendar.getInstance();
				Date date = dateFormat != null ? dateFormat.parse(element.getTextContent()) : new Date(Long.valueOf(element.getTextContent()));
				cal.setTime(date);
			}
			return cal;
		} catch (DOMException e) {
			throw new XMLSerializeException(e);
		} catch (ParseException e) {
			throw new XMLSerializeException(e);
		}
	}
}
