package net.jhorstmann.i18n.xgettext.web;

import java.io.StringReader;
import java.util.concurrent.atomic.AtomicReference;
import junit.framework.Assert;
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
        ELParser parser = new ELParser(new StringReader("#{a + tr:tr('abc', 1)}"));
        AstCompositeExpression expr = parser.CompositeExpression();
        final AtomicReference<String> ref = new AtomicReference<String>();
        expr.accept(new NodeVisitor() {

            @Override
            public void visit(Node node) throws Exception {
                if (node instanceof AstFunction) {
                    AstFunction fun = (AstFunction) node;
                    int count = fun.jjtGetNumChildren();
                    if ("tr".equals(fun.getLocalName()) && count >= 1) {
                        Node child0 = fun.jjtGetChild(0);
                        if (child0 instanceof AstString) {
                            AstString str = (AstString) child0;
                            ref.set(str.getString());
                        }
                    }
                }
            }
        });

        Assert.assertEquals("abc", ref.get());
    }
}
