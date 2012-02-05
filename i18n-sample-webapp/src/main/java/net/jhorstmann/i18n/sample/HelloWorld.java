package net.jhorstmann.i18n.sample;

import javax.faces.bean.ManagedBean;
import net.jhorstmann.i18n.I18N;

@ManagedBean
public class HelloWorld {

    private int num;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getMessage() {
        return I18N.tr("Hello World From Managed Bean");
    }
    
    public String getPluralMessage() {
        return I18N.trn("{0} Test", "{0} Tests", num, num);
    }
}
