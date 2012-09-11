package net.jhorstmann.i18n.mojo;

import net.jhorstmann.i18n.xgettext.asm.AsmMessageExtractor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.jhorstmann.i18n.tools.MessageBundle;
import net.jhorstmann.i18n.tools.xgettext.MessageExtractor;
import net.jhorstmann.i18n.tools.xgettext.MessageExtractorException;
import net.jhorstmann.i18n.tools.xgettext.MessageFunction;

abstract class AbstractGettextMojo extends AbstractMojo {

    /**
     * @parameter default-value="${project.build.sourceDirectory}"
     */
    File sourceDirectory;
    /**
     * @parameter default-value="${project.build.outputDirectory}"
     */
    File classesDirectory;
    /**
     * @parameter default-value="${project.basedir}/src/main/webapp"
     */
    File webappDirectory;
    /**
     * @parameter
     */
    String[] webappIncludes;
    /**
     * @parameter
     */
    String[] webappExcludes;
    /**
     * @parameter default-value="${project.basedir}/src/main/po"
     */
    File poDirectory;
    /**
     * @parameter
     */
    String[] poIncludes;
    /**
     * @parameter
     */
    String[] poExcludes;
    /**
     * @parameter default-value="${project.basedir}/src/main/po/keys.pot"
     */
    File keysFile;
    /**
     * @parameter default-value="${false}" expression="#{gettext.update}"
     */
    boolean update;
    /**
     * @parameter
     */
    MessageFunction[] javaFunctions;
    /**
     * @parameter
     */
    MessageFunction[] elFunctions;

    String[] getWebappIncludes() {
        return webappIncludes != null ? webappIncludes : new String[]{"**/*.xhtml"};
    }

    String[] getWebappExcludes() {
        return webappExcludes != null ? webappExcludes : new String[]{};
    }

    String[] getPoIncludes() {
        return poIncludes != null ? poIncludes : new String[]{"**/*.po"};
    }

    String[] getPoExcludes() {
        return poExcludes != null ? poExcludes : new String[]{};
    }

    List<MessageFunction> getJavaFunctions() {
        return javaFunctions != null ? Arrays.asList(javaFunctions) : AsmMessageExtractor.DEFAULT_MESSAGE_FUNCTIONS;
    }

    private MessageBundle loadMessageBundleImpl() throws IOException {
        if (update && keysFile.exists()) {
            getLog().info("Loading existing keys from " + keysFile);
            return MessageBundle.loadCatalog(keysFile);
        } else {
            return new MessageBundle();
        }
    }

    MessageBundle loadMessageBundle() throws MojoExecutionException {
        try {
            return loadMessageBundleImpl();
        } catch (IOException ex) {
            throw new MojoExecutionException("Could not load message bundle", ex);
        }
    }

    private void saveMessageBundleImpl(MessageBundle bundle) throws IOException {
        File dir = keysFile.getParentFile();
        if (!dir.exists()) {
            getLog().debug("Creating directory for keys file");
            boolean res = dir.mkdirs();
            if (!res) {
                throw new IOException("Could not create directory for keys file");
            }
        }
        getLog().debug("Saving keys to " + keysFile);
        bundle.storeCatalog(keysFile);
    }

    void saveMessageBundle(MessageBundle bundle) throws MojoExecutionException {
        try {
            saveMessageBundleImpl(bundle);
        } catch (IOException ex) {
            throw new MojoExecutionException("Could not save message bundle", ex);
        }
    }

    int extractMessages(MessageExtractor extractor, File basedir, String[] includes, String[] excludes) throws MojoExecutionException {

        getLog().debug("Creating DirectoryScanner with basedir " + basedir + " including " + Arrays.toString(includes) + " and excluding " + Arrays.toString(excludes));

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(basedir);
        if (includes != null) {
            scanner.setIncludes(includes);
        }
        if (excludes != null) {
            scanner.setExcludes(excludes);
        }
        getLog().debug("Scanning directory " + basedir);
        scanner.scan();

        int errorCount = 0;
        for (String name : scanner.getIncludedFiles()) {
            getLog().debug("Processing " + name);
            File file = new File(basedir, name);
            try {
                extractor.extractMessages(file);
            } catch (MessageExtractorException ex) {
                errorCount++;
                getLog().error(ex);
            } catch (IOException ex) {
                errorCount++;
                getLog().error(ex);
            }
        }

        return errorCount;
    }
}
