package net.jhorstmann.i18n.xgettext.web;

import java.util.List;
import org.apache.el.parser.AstFunction;
import org.apache.el.parser.AstString;
import org.apache.el.parser.Node;
import org.apache.el.parser.NodeVisitor;
import javax.el.ELException;
import net.jhorstmann.i18n.xgettext.MessageFunction;
import org.fedorahosted.tennera.jgettext.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GettextNodeVisitor implements NodeVisitor, Constants {
    private static final Logger log = LoggerFactory.getLogger(GettextNodeVisitor.class);
    private ExtractorHandler extractor;
    private List<MessageFunction> functions;

    public GettextNodeVisitor(ExtractorHandler extractor) {
        this.extractor = extractor;
        this.functions = extractor.getFunctions();
    }
    
    @Override
    public void visit(Node node) throws ELException {
        if (node instanceof AstFunction) {
            AstFunction fun = (AstFunction) node;
            String prefix = fun.getPrefix();
            String uri = extractor.getNamespaceURI(prefix);
            String name = fun.getLocalName();
            int count = fun.jjtGetNumChildren();
            
            log.debug("Visiting function call to {}:{} ({})", new Object[]{prefix, name, uri});

            for (MessageFunction mfn : functions) {
                log.debug("Trying to match parameters for {} ({})", mfn.getName(), mfn.getNamespace());
                if (mfn.getNamespace().equals(uri) && mfn.getName().equals(name) && count >= mfn.getParameterCount()) {
                    int contextIdx = mfn.getContextIndex();
                    int messageIdx = mfn.getMessageIndex();
                    int pluralIdx = mfn.getPluralIndex();
                    String message = null;
                    String context = null;
                    String plural = null;
                    if (messageIdx >= 0 && messageIdx < count) {
                        Node messageNode = fun.jjtGetChild(messageIdx);
                        if (messageNode instanceof AstString) {
                            message = ((AstString)messageNode).getString();
                        } else {
                            log.warn("Message for {} is not constant", new Object[]{name});
                            return;
                        }
                    }
                    if (contextIdx >= 0 && contextIdx < count) {
                        Node contextNode = fun.jjtGetChild(contextIdx);
                        if (contextNode instanceof AstString) {
                            context = ((AstString)contextNode).getString();
                        } else {
                            log.warn("Message context for {} is not constant", new Object[]{name});
                            return;
                        }
                    }
                    if (pluralIdx >= 0 && pluralIdx < count) {
                        Node pluralNode = fun.jjtGetChild(pluralIdx);
                        if (pluralNode instanceof AstString) {
                            plural = ((AstString)pluralNode).getString();
                        } else {
                            log.warn("Message context for {} is not constant", new Object[]{name});
                            return;
                        }
                    }
                    if (message != null) {
                        Message msg = new Message();
                        msg.setMsgid(message);
                        if (context != null) {
                            msg.setMsgctxt(context);
                        }
                        if (plural != null) {
                            msg.setMsgidPlural(plural);
                        }
                        log.debug("Found message {}", msg);

                        extractor.addMessage(context, message, plural, null);
                    }
                }
            }
            
        }
    }
}
