package net.jhorstmann.i18n.jsp;

public class PluralTag extends AttributeTagSupport {

    @Override
    protected void updateAttribute(TranslationTag tr, Object content) {
        tr.setPlural((String)content);
    }

}
