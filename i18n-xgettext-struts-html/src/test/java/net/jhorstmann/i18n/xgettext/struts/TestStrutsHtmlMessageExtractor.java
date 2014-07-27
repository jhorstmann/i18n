package net.jhorstmann.i18n.xgettext.struts;

import java.io.File;
import java.io.IOException;
import net.jhorstmann.i18n.tools.MessageBundle;
import net.jhorstmann.i18n.tools.xgettext.MessageExtractorException;
import org.fedorahosted.openprops.Properties;
import org.fedorahosted.tennera.jgettext.Message;
import static org.junit.Assert.*;
import org.junit.Test;

public class TestStrutsHtmlMessageExtractor {

    @Test
    public void testExtractor() throws IOException, MessageExtractorException {
        MessageBundle mb = new MessageBundle();
        StrutsHtmlMessageExtractor ex = new StrutsHtmlMessageExtractor(mb, false);
        ex.extractMessages(new File("src/test/resources/index.jsp"));
        
        assertFalse(mb.isEmpty());
        assertEquals(4, mb.size());
        
        Message title = mb.getMessage("message.title");
        assertNotNull(title);
        assertEquals("message.title", title.getMsgid());

        Message imageSrc = mb.getMessage("image.test.src");
        assertNotNull(imageSrc);
        assertEquals("image.test.src", imageSrc.getMsgid());

        Message imageAlt = mb.getMessage("image.test.alt");
        assertNotNull(imageAlt);
        assertEquals("image.test.alt", imageAlt.getMsgid());

        Message imageTitle = mb.getMessage("image.test.title");
        assertNotNull(imageTitle);
        assertEquals("image.test.title", imageTitle.getMsgid());

        Properties props = mb.toOpenProps();
        props.list(System.out);
    }
}
