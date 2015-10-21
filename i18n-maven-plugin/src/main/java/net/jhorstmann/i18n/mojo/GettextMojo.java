package net.jhorstmann.i18n.mojo;

import java.text.MessageFormat;

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

    /**
     * Relativize the path for source-references to the project-root.
     *
     * @parameter expression="${relativizeSrcRefPaths}" default-value="false"
     */
    protected boolean relativizeSrcRefPaths;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Skipping gettext.");
            return;
        }
        MessageBundle bundle = loadMessageBundle();
        int errorCount = 0;

        if (!poDirectory.exists() || !poDirectory.canWrite()) {
            getLog().warn(MessageFormat.format("Target-Directory <{0}> does not exist or is unwritable, skipping", poDirectory.getAbsolutePath()));
            return;
        }

        if (classesDirectory.exists()) {
            MessageExtractor extractor = new AsmMessageExtractor(bundle, getJavaFunctions(), srcRefPaths);
            errorCount += extractMessages(extractor, classesDirectory, new String[] { "**/*.class" }, new String[] {});
        }

        if (webappDirectory.exists()) {
            MessageExtractor extractor = new WebMessageExtractor(bundle, getELFunctions(), projectRoot, relativizeSrcRefPaths, srcRefPaths);
            errorCount += extractMessages(extractor, webappDirectory, getWebappIncludes(), getWebappExcludes());
        }

        if (errorCount > 0) {
            throw new MojoExecutionException("Encountered " + errorCount + " error(s) while extracting messages from classes");
        } else {
            saveMessageBundle(bundle);
        }
    }
}
