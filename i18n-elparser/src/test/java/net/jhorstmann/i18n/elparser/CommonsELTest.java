package net.jhorstmann.i18n.elparser;

import java.io.StringReader;
import org.apache.commons.el.Expression;
import org.apache.commons.el.ExpressionString;
import org.apache.commons.el.parser.ELParser;
import org.apache.commons.el.parser.ParseException;
import org.junit.Test;

public class CommonsELTest {

    @Test
    public void test() throws ParseException {
        ELParser parser = new ELParser(new StringReader("${fn:tr('test')}"));
        Object exprString = parser.ExpressionString();
        if (exprString instanceof ExpressionString) {
            Object[] elements = ((ExpressionString)exprString).getElements();
        } else if (exprString instanceof Expression) {
        }
        
    }
}
