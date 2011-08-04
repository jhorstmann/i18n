package net.jhorstmann.i18n;

import java.util.Locale;

public class I18NBenchmark {
    public static void main(String[] args) {
        Locale.setDefault(Locale.GERMANY);
        int hash = 0;
        for (int i=0; i<100000; i++) {
            String msg1 = I18N.tr("hello");
            String msg2 = I18N.tr("helloParam", "World");
            hash += msg1.hashCode();
            hash += msg2.hashCode();
        }
        System.out.println(hash);
    }
}
