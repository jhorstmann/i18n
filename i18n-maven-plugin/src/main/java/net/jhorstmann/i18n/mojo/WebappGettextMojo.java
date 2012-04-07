package net.jhorstmann.i18n.mojo;

import net.jhorstmann.i18n.tools.MessageBundle;
import net.jhorstmann.i18n.tools.xgettext.MessageExtractor;
import net.jhorstmann.i18n.xgettext.web.WebMessageExtractor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal webappGettext
 * @phase process-resources
 */
public class WebappGettextMojo extends AbstractGettextMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        MessageBundle bundle = loadMessageBundle();
        MessageExtractor extractor = new WebMessageExtractor(bundle);

        int errorCount = extractMessages(extractor, webappDirectory, getWebappIncludes(), getWebappExcludes());

        if (errorCount > 0) {
            throw new MojoExecutionException("Encountered " + errorCount + " error(s) while extracting messages from webapp");
        } else {
            saveMessageBundle(bundle);
        }
    }
}
