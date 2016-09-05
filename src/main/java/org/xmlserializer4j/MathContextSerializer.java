package org.xmlserializer4j;

import java.math.MathContext;
import java.math.RoundingMode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MathContextSerializer extends AbsSerializer<MathContext> {
	
	private static final String PRECISION = "precision";
	private static final String ROUNDING_MODE = "roundingMode";
	
	@Override
	public Element serialize(XMLSerializer xmlSerializer, String elementName, MathContext mathContext) throws XMLSerializeException {
		Document document = xmlSerializer.getDocument();
		Element element = super.serialize(xmlSerializer, elementName, mathContext);
		
		Element precisionElement = document.createElement(PRECISION);
		precisionElement.setTextContent(String.valueOf(mathContext.getPrecision()));
		element.appendChild(precisionElement);
		
		Element roundingModeElement = document.createElement(ROUNDING_MODE);
		roundingModeElement.setTextContent(mathContext.getRoundingMode().toString());
		element.appendChild(roundingModeElement);
		
		return element;
	}

	@Override
	public MathContext deserialize(XMLSerializer xmlSerializer, Element element, MathContext mathContext) throws XMLSerializeException {
		if(mathContext == null) {
			NodeList precisionList = element.getElementsByTagName(PRECISION);
			Element precisionElement = (Element) precisionList.item(0);
			
			NodeList roundingModeList = element.getElementsByTagName(ROUNDING_MODE);
			Element roundingModeElement = (Element) roundingModeList.item(0);
			
			int precision = Integer.valueOf(precisionElement.getTextContent());
			RoundingMode roundingMode = RoundingMode.valueOf(roundingModeElement.getTextContent());
			
			mathContext = new MathContext(precision, roundingMode);
		}
		return mathContext;
	}

}
