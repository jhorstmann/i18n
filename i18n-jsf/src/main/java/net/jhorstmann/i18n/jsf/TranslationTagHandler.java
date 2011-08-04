package net.jhorstmann.i18n.jsf;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import java.io.IOException;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;

public class TranslationTagHandler extends ComponentHandler {
    public TranslationTagHandler(ComponentConfig config) {
        super(config);
    }

    @Override
    public void applyNextHandler(FaceletContext ctx, UIComponent comp) throws IOException, FacesException {
        TagHelper.applyHandler(ctx, tag, nextHandler, comp, "message");
        if (comp.getAttributes().containsKey("plural")) {
            TagAttribute num = getRequiredAttribute("n");
            assert(num != null);
        }
    }
}
