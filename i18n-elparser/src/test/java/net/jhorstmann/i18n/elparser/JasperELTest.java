package net.jhorstmann.i18n.elparser;

import java.io.StringReader;
import org.apache.el.parser.AstCompositeExpression;
import org.apache.el.parser.AstFunction;
import org.apache.el.parser.AstString;
import org.apache.el.parser.ELParser;
import org.apache.el.parser.Node;
import org.apache.el.parser.NodeVisitor;
import org.junit.Test;

public class JasperELTest {

    @Test
    public void test() throws Exception {
        ELParser parser = new ELParser(new StringReader("#{a + tr:tr('acb', 1)}"));
        AstCompositeExpression expr = parser.CompositeExpression();
        expr.accept(new NodeVisitor() {

            @Override
            public void visit(Node node) throws Exception {
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
