package net.jhorstmann.i18n.elparser;

import com.sun.el.parser.AstCompositeExpression;
import com.sun.el.parser.AstFunction;
import com.sun.el.parser.AstString;
import com.sun.el.parser.ELParser;
import com.sun.el.parser.Node;
import com.sun.el.parser.NodeVisitor;
import com.sun.el.parser.ParseException;
import java.io.StringReader;
import javax.el.ELException;
import org.junit.Test;

public class GlassfishELTest {
    @Test
    public void test2() throws ParseException {
        AstCompositeExpression expr = new ELParser(new StringReader("#{a + tr:tr('acb', 1)}")).CompositeExpression();
        expr.accept(new NodeVisitor() {

            public void visit(Node node) throws ELException {
                if (node instanceof AstFunction) {
                    AstFunction fun = (AstFunction) node;
                    int count = fun.jjtGetNumChildren();
                    if ("tr".equals(fun.getLocalName()) && count >= 1) {
                        System.out.println(fun.getLocalName());
                        System.out.println(fun.getImage());
                        System.out.println(fun.jjtGetNumChildren());
                        System.out.println(fun.jjtGetChild(0));
                        Node child0 = fun.jjtGetChild(0);
                        if (child0 instanceof AstString) {
                            AstString str = (AstString) child0;
                            System.out.println(str.getString());
                        }
                    }
                }
            }
        });
    }
    
}
