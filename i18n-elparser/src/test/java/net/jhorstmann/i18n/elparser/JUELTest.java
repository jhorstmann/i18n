package net.jhorstmann.i18n.elparser;

import de.odysseus.el.tree.ExpressionNode;
import de.odysseus.el.tree.FunctionNode;
import de.odysseus.el.tree.Tree;
import de.odysseus.el.tree.impl.Builder;
import org.junit.Test;

public class JUELTest {

    @Test
    public void test1() {
        Tree tree = new Builder().build("#{test + fn:bla('abc', 1, 2, 3)}");
        System.out.println(tree);
        System.out.println(tree.getClass());
        System.out.println(tree.isDeferred());
        for (FunctionNode fn :  tree.getFunctionNodes()) {
            System.out.println(fn.getName());
            int count = fn.getParamCount();
            System.out.println(count);
        }
        ExpressionNode root = tree.getRoot();
    }
    
}
