package net.jhorstmann.i18n.jsp;

public class ContextTag extends AttributeTagSupport {

    @Override
    protected void updateAttribute(TranslationTag tr, Object content) {
        tr.setContext((String)content);
    }

}
