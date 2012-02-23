package net.jhorstmann.i18n.jsp;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class AttributeTagSupport extends BodyTagSupport {

    private Object value;
    private boolean valueSet;

    protected abstract void updateAttribute(TranslationTag tr, Object value);

    @Override
    public void release() {
        super.release();
        value = null;
        valueSet = false;
    }

    @Override
    public int doStartTag() throws JspException {
        return EVAL_BODY_BUFFERED;
    }

    @Override
    public int doEndTag() throws JspException {
        TranslationTag tr = (TranslationTag) findAncestorWithClass(this, TranslationTag.class);
        if (tr == null) {
            throw new IllegalStateException("Ancestor TranslationTag not found");
        } else if (valueSet) {
            updateAttribute(tr, value);
        } else {
            BodyContent content = getBodyContent();
            if (content != null) {
                updateAttribute(tr, content.getString().trim());
                content.clearBody();
            }
        }
        return EVAL_PAGE;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
        this.valueSet = true;
    }
}
