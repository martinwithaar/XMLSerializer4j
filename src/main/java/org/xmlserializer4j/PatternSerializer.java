package org.xmlserializer4j;

import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * <p><code>TypeSerializer</code> implementation for serializing <code>Pattern</code> objects.</p>
 * <p><b>Note:</b> The <code>Pattern</code> class does <b>NOT</b> override {@link Object#equals(Object)} so when deserialized will never equal the original.</p>
 * @author Martin
 *
 */
public class PatternSerializer extends AbsSerializer<Pattern> {
	
	private static final String PATTERN = "pattern";
	private static final String FLAGS = "flags";
	
	@Override
	public Element serialize(XMLSerializer xmlSerializer, String elementName, Pattern pattern) throws XMLSerializeException {
		Element element = super.serialize(xmlSerializer, elementName, pattern);
		Document document = xmlSerializer.getDocument();
		
		Element patternElement = document.createElement(PATTERN);
		patternElement.setTextContent(pattern.pattern());
		element.appendChild(patternElement);
		
		int flags = pattern.flags();
		if(flags != 0) {
			Element flagsPattern = document.createElement(FLAGS);
			flagsPattern.setTextContent(String.valueOf(flags));
			element.appendChild(flagsPattern);
		}
		
		return element;
	}

	@Override
	public Pattern deserialize(XMLSerializer xmlSerializer, Element element, Pattern pattern) {
		if(pattern == null) {
			NodeList patternList = element.getElementsByTagName(PATTERN);
			String regex = patternList.getLength() > 0 ? patternList.item(0).getTextContent() : null;
			
			NodeList flagsList = element.getElementsByTagName(FLAGS);
			int flags = flagsList.getLength() > 0 ? Integer.valueOf(flagsList.item(0).getTextContent()) : 0;
			
			pattern = Pattern.compile(regex, flags);
		}
		return pattern;
	}
}
