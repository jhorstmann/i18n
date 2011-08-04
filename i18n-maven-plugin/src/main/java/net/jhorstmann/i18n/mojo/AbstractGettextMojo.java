package net.jhorstmann.i18n.mojo;

import net.jhorstmann.i18n.xgettext.MessageExtractor;
import net.jhorstmann.i18n.xgettext.MessageExtractorException;
import net.jhorstmann.i18n.xgettext.MessageFunction;
import net.jhorstmann.i18n.xgettext.asm.AsmMessageExtractor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.fedorahosted.tennera.jgettext.Catalog;
import org.fedorahosted.tennera.jgettext.PoParser;
import org.fedorahosted.tennera.jgettext.PoWriter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
     * @parameter default-value="${project.basedir}/src/main/po/keys.pot"
     */
    File keysFile;
    /**
     * @parameter default-value="${false}"
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

    List<MessageFunction> getJavaFunctions() {
        return javaFunctions != null ? Arrays.asList(javaFunctions) : AsmMessageExtractor.DEFAULT_MESSAGE_FUNCTIONS;
    }

    private Catalog loadCatalogImpl() throws IOException {
        if (update && keysFile.exists()) {
            getLog().info("Loading existing keys from " + keysFile);
            PoParser parser = new PoParser();
            return parser.parseCatalog(keysFile);
        } else {
            Catalog catalog = new Catalog(true);
            return catalog;
        }
    }

    Catalog loadCatalog() throws MojoExecutionException {
        try {
            return loadCatalogImpl();
        } catch (IOException ex) {
            throw new MojoExecutionException("Could not load message catalog", ex);
        }
    }

    private void saveCatalogImpl(Catalog catalog) throws IOException {
        File dir = keysFile.getParentFile();
        if (!dir.exists()) {
            getLog().debug("Creating directory for keys file");
            boolean res = dir.mkdirs();
            if (!res) {
                throw new IOException("Could not create directory for keys file");
            }
        }
        getLog().debug("Saving keys to " + keysFile);
        PoWriter writer = new PoWriter();
        writer.setGenerateHeader(!update);
        writer.write(catalog, keysFile);
    }

    void saveCatalog(Catalog catalog) throws MojoExecutionException {
        try {
            saveCatalogImpl(catalog);
        } catch (IOException ex) {
            throw new MojoExecutionException("Could not save message catalog", ex);
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
