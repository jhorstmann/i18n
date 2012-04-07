package net.jhorstmann.i18n.mojo;

import net.jhorstmann.i18n.tools.MessageBundle;
import net.jhorstmann.i18n.tools.xgettext.MessageExtractor;
import net.jhorstmann.i18n.xgettext.asm.AsmMessageExtractor;
import net.jhorstmann.i18n.xgettext.web.WebMessageExtractor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal gettext
 * @phase process-classes
 */
public class GettextMojo extends AbstractGettextMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        MessageBundle bundle = loadMessageBundle();
        int errorCount = 0;

        if (classesDirectory.exists()) {
            MessageExtractor extractor = new AsmMessageExtractor(bundle, getJavaFunctions());
            errorCount += extractMessages(extractor, classesDirectory, new String[]{"**/*.class"}, new String[]{});
        }

        if (webappDirectory.exists()) {
            MessageExtractor extractor = new WebMessageExtractor(bundle);
            errorCount += extractMessages(extractor, webappDirectory, getWebappIncludes(), getWebappExcludes());
        }

        if (errorCount > 0) {
            throw new MojoExecutionException("Encountered " + errorCount + " error(s) while extracting messages from classes");
        } else {
            saveMessageBundle(bundle);
        }
    }
}
