package net.jhorstmann.i18n.jsp;

public class MessageTag extends AttributeTagSupport {

    @Override
    protected void updateAttribute(TranslationTag tr, Object content) {
        tr.setMessage((String)content);
    }

}
