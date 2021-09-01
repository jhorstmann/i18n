package net.jhorstmann.i18n.tools.xgettext;

public class MessageExtractorException extends Exception {

    public MessageExtractorException(Throwable cause) {
        super(cause);
    }
    
    public MessageExtractorException(String message, Throwable cause) {
        super(message, cause);
    }

}
