package net.jhorstmann.i18n.mojo;

import net.jhorstmann.i18n.xgettext.MessageExtractor;
import net.jhorstmann.i18n.xgettext.asm.AsmMessageExtractor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.fedorahosted.tennera.jgettext.Catalog;

/**
 * @goal classesGettext
 * @phase process-classes
 */
public class ClassesGettextMojo extends AbstractGettextMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Catalog catalog = loadCatalog();

        MessageExtractor extractor = new AsmMessageExtractor(catalog, getJavaFunctions());
        int errorCount = extractMessages(extractor, classesDirectory, new String[]{"**/*.class"}, new String[]{});

        if (errorCount > 0) {
            throw new MojoExecutionException("Encountered " + errorCount + " error(s) while extracting messages from classes");
        } else {
            saveCatalog(catalog);
        }
    }
}
