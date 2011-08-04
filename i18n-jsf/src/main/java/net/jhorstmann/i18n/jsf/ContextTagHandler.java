package net.jhorstmann.i18n.jsf;

import javax.faces.component.UIComponent;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;
import java.io.IOException;

public class ContextTagHandler extends TagHandler {
    public ContextTagHandler(TagConfig config) {
        super(config);
    }

    @Override
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
        TagHelper.applyHandler(ctx, tag, nextHandler, parent, "context");
    }
}
