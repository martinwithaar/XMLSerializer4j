package org.xmlserializer4j;

import java.io.File;

import org.w3c.dom.Element;

/**
 * <p><code>AbsSerializer</code> implementation for serializing <code>File</code> objects.</p>
 * @author Martin
 *
 */
public class FileSerializer extends AbsSerializer<File> {
	
	@Override
	public Element serialize(XMLSerializer xmlSerializer, String elementName, File file) throws XMLSerializeException {
		Element element = super.serialize(xmlSerializer, elementName, file);
		element.setTextContent(file.getPath());
		return element;
	}

	@Override
	public File deserialize(XMLSerializer xmlSerializer, Element element, File file) {
		return file == null ? new File(element.getTextContent()) : file;
	}
}
