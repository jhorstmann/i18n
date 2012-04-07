package net.jhorstmann.i18n.tools.xgettext;

import java.io.File;
import java.io.IOException;

public interface MessageExtractor {
    public void extractMessages(File file) throws IOException, MessageExtractorException;
    
}
