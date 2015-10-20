package net.jhorstmann.i18n;

import com.ibm.icu.text.MessageFormat;
import java.math.BigDecimal;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;

public class MessageFormatTest {
    @Test
    public void testFormatInteger() {
        Assert.assertEquals("1.234", new MessageFormat("{0,number,integer}", Locale.GERMANY).format(new Object[]{Integer.valueOf(1234)}));
    }

    @Test
    public void testFormatIntegerNoGrouping() {
        Assert.assertEquals("1234", new MessageFormat("{0,number,#}", Locale.GERMANY).format(new Object[]{Integer.valueOf(1234)}));
    }
    @Test
    public void testFormatDecimal() {
        Assert.assertEquals("1.234,56", new MessageFormat("{0,number}", Locale.GERMANY).format(new Object[]{new BigDecimal("1234.56")}));
    }

    @Test
    public void testFormatDecimalNoGrouping() {
        Assert.assertEquals("1234,56", new MessageFormat("{0,number,#.00}", Locale.GERMANY).format(new Object[]{new BigDecimal("1234.56")}));
    }

}
