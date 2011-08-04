package net.jhorstmann.i18n.xgettext.asm.poc;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import org.objectweb.asm.tree.analysis.AnalyzerException;

public class ConstantTrackerTest {

    public static class TestClass {

        private static final String HELLO = "Hello World!";

        public void test() {
            PrintStream ps = System.out;
            ps.println(HELLO);
        }
    }

    public static void main(String[] args) throws IOException, AnalyzerException {
        InputStream in = ConstantTrackerTest.class.getResourceAsStream("ConstantTrackerTest$TestClass.class");
        try {
            ConstantTracker.findConstantArgumentsToPrintln(in);
        } finally {
            in.close();
        }
    }
}
