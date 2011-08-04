package net.jhorstmann.i18n.mojo;

import net.jhorstmann.i18n.xgettext.MessageExtractor;
import net.jhorstmann.i18n.xgettext.asm.AsmMessageExtractor;
import net.jhorstmann.i18n.xgettext.web.WebMessageExtractor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.fedorahosted.tennera.jgettext.Catalog;

/**
 * @goal gettext
 * @phase process-classes
 */
public class GettextMojo extends AbstractGettextMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Catalog catalog = loadCatalog();
        int errorCount = 0;

        if (classesDirectory.exists()) {
            MessageExtractor extractor = new AsmMessageExtractor(catalog, getJavaFunctions());
            errorCount += extractMessages(extractor, classesDirectory, new String[]{"**/*.class"}, new String[]{});
        }

        if (webappDirectory.exists()) {
            MessageExtractor extractor = new WebMessageExtractor(catalog);
            errorCount += extractMessages(extractor, webappDirectory, getWebappIncludes(), getWebappExcludes());
        }

        if (errorCount > 0) {
            throw new MojoExecutionException("Encountered " + errorCount + " error(s) while extracting messages from classes");
        } else {
            saveCatalog(catalog);
        }
    }
}
