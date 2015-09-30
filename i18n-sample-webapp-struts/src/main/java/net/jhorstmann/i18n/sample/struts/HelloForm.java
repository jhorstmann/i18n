package net.jhorstmann.i18n.sample.struts;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public class HelloForm extends ActionForm {

    private String name;

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if (name == null || name.trim().length() == 0) {
            errors.add("name", new ActionMessage("error.name.empty"));
        }
        return errors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
