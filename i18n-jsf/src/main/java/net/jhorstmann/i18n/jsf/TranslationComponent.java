package net.jhorstmann.i18n.jsf;

import javax.faces.component.FacesComponent;
import net.jhorstmann.i18n.I18N;

import javax.faces.FacesException;
import javax.faces.component.StateHelper;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static net.jhorstmann.i18n.jsf.FacesResourceBundle.getResourceBundle;

@FacesComponent(value = TranslationComponent.COMPONENT_TYPE)
public class TranslationComponent extends UIComponentBase {
    public static final String COMPONENT_TYPE = "net.jhorstmann.i18n.jsf.TranslationComponent";
    public static final String COMPONENT_FAMILY = "javax.faces.Output";

    enum PropertyKeys {
        message, context, plural, num, comment
    }

    public TranslationComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context) throws IOException {
    }

    @Override
    public void encodeBegin(FacesContext facesContext) throws IOException {
        super.encodeBegin(facesContext);
    }

    private Object[] getParameters() {
        List<UIComponent> children = getChildren();
        if (children == null || children.isEmpty()) {
            return new Object[0];
        } else {
            List<Object> result = new ArrayList<Object>(children.size());
            for (UIComponent comp : children) {
                if (comp instanceof UIParameter) {
                    UIParameter param = (UIParameter) comp;
                    result.add(param.getValue());
                }
            }
            return result.toArray(new Object[result.size()]);
        }
    }

    @Override
    public void encodeEnd(FacesContext facesContext) throws IOException {
        if (isRendered()) {
            StateHelper state = getStateHelper();
            String message = (String) state.eval(PropertyKeys.message);
            String context = (String) state.eval(PropertyKeys.context);
            String plural = (String) state.eval(PropertyKeys.plural);
            Long num = (Long) state.eval(PropertyKeys.num);

            if (message == null) {
                throw new FacesException("Message id is null");
            }
            if (plural != null && num == null) {
                throw new FacesException("Message id '" + message + "' requires num parameter");
            }

            Object[] params = getParameters();
            ResourceBundle bundle = getResourceBundle(facesContext);
            String str = I18N.translate(bundle, context, message, plural, num == null ? 0L : num.longValue(), params);

            facesContext.getResponseWriter().writeText(str, null);
        }
        super.encodeEnd(facesContext);
    }

    public void setMessage(String message) {
        getStateHelper().put(PropertyKeys.message, message);
    }

    public String getMessage() {
        return (String) getStateHelper().get(PropertyKeys.message);
    }

    public void setContext(String context) {
        getStateHelper().put(PropertyKeys.context, context);
    }

    public String getContext() {
        return (String) getStateHelper().get(PropertyKeys.context);
    }

    public void setPlural(String plural) {
        getStateHelper().put(PropertyKeys.plural, plural);
    }

    public String getPlural() {
        return (String) getStateHelper().get(PropertyKeys.plural);
    }

    public void setComment(String comment) {
        getStateHelper().put(PropertyKeys.comment, comment);
    }

    public String getComment() {
        return (String) getStateHelper().get(PropertyKeys.comment);
    }

    public void setNum(Long num) {
        getStateHelper().put(PropertyKeys.num, num);
    }

    public Long getNum() {
        return (Long) getStateHelper().get(PropertyKeys.num);
    }
}
