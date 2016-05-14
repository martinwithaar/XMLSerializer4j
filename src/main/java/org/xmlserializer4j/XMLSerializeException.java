package org.xmlserializer4j;

import java.io.IOException;

/**
 * <p>Signals that a serialization error has occurred.</p>
 * @author Martin
 *
 */
public class XMLSerializeException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6308471564646143352L;

	/**
	 * 
	 */
    public XMLSerializeException() {
        super();
    }

    /**
     * 
     * @param message
     */
    public XMLSerializeException(String message) {
        super(message);
    }

    /**
     * 
     * @param message
     * @param cause
     */
    public XMLSerializeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 
     * @param cause
     */
    public XMLSerializeException(Throwable cause) {
        super(cause);
    }
}
