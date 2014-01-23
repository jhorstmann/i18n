package net.jhorstmann.i18n.mojo;

import net.jhorstmann.i18n.tools.MessageBundle;
import net.jhorstmann.i18n.tools.xgettext.MessageExtractor;
import net.jhorstmann.i18n.xgettext.asm.AsmMessageExtractor;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal classesGettext
 * @phase process-classes
 */
public class ClassesGettextMojo extends AbstractGettextMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Skipping gettext.");
            return;
        }
        MessageBundle bundle = loadMessageBundle();

        MessageExtractor extractor = new AsmMessageExtractor(bundle, getJavaFunctions());
        int errorCount = extractMessages(extractor, classesDirectory, new String[] { "**/*.class" }, new String[] {});

        if (errorCount > 0) {
            throw new MojoExecutionException("Encountered " + errorCount + " error(s) while extracting messages from classes");
        } else {
            saveMessageBundle(bundle);
        }
    }
}
