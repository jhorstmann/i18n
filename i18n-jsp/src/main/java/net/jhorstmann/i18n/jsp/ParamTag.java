package net.jhorstmann.i18n.jsp;

public class ParamTag extends AttributeTagSupport {

    @Override
    protected void updateAttribute(TranslationTag tr, Object content) {
        tr.addParam(content);
    }

}
