package net.jhorstmann.i18n.jsf;

import javax.faces.component.UIComponent;
import javax.faces.view.facelets.*;
import java.io.IOException;

class TagHelper {
    static void applyHandler(FaceletContext ctx, Tag tag, FaceletHandler nextHandler, UIComponent comp, String contentAttribute) throws IOException {
        if (nextHandler instanceof TextHandler) {
            TextHandler textHandler = (TextHandler) nextHandler;
            String message = textHandler.getText(ctx).trim();
            if (!message.isEmpty()) {
                comp.getAttributes().put(contentAttribute, message);
            }
        } else if (nextHandler instanceof CompositeFaceletHandler) {
            StringBuilder content = new StringBuilder();
            FaceletHandler[] handlers = ((CompositeFaceletHandler) nextHandler).getHandlers();
            for (int i = 0, len = handlers.length; i < len; i++) {
                if (handlers[i] instanceof TextHandler) {
                    TextHandler textHandler = (TextHandler) handlers[i];
                    content.append(textHandler.getText(ctx));
                } else {
                    handlers[i].apply(ctx, comp);
                }
            }
            String message = content.toString().trim();
            if (!message.isEmpty()) {
                comp.getAttributes().put(contentAttribute, message);
            }
        } else {
            nextHandler.apply(ctx, comp);
        }
    }

}
