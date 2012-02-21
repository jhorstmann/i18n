package net.jhorstmann.i18n.mojo;

import java.io.File;
import java.io.IOException;
import net.jhorstmann.i18n.GettextResourceBundle;
import net.jhorstmann.i18n.tools.ResourceBundleCompiler;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.fedorahosted.tennera.jgettext.Catalog;
import org.fedorahosted.tennera.jgettext.PoParser;

/**
 * Generates ressource bundles.
 *
 * @goal dist
 * @phase generate-resources
 *
 * @author Jörn Horstmann
 */
public class DistMojo extends AbstractGettextMojo {

    /**
     * The package and file name of the generated class or properties files.
     * @parameter expression="${targetBundle}"
     * @required
     */
    protected String targetBundle;
    /**
     * The locale of the messages in the source code.
     * @parameter expression="${sourceLocale}" default-value="en"
     * @required
     */
    protected String sourceLocale;

    static String getLocale(File file) {
        String fileName = file.getName();
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    void processLocaleImpl(File inputFile, String locale) throws IOException, MojoExecutionException {
        String className = targetBundle;
        if (locale != null && locale.length() > 0) {
            className += "_" + locale;
        }

        Catalog catalog = new PoParser().parseCatalog(inputFile);

        ResourceBundleCompiler.compileFile(catalog, GettextResourceBundle.class.getName(), className, classesDirectory);
    }

    void processLocale(File inputFile, String locale) throws MojoExecutionException {
        try {
            processLocaleImpl(inputFile, locale);
        } catch (IOException ex) {
            throw new MojoExecutionException("Could not create ResourceBundle for input " + inputFile + " and locale " + locale, ex);
        }
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // create output directory if it doesn't exists
        classesDirectory.mkdirs();

        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir(poDirectory);
        ds.setIncludes(new String[]{"**/*.po"});
        ds.scan();


        boolean processedSourceLocale = false;

        String[] files = ds.getIncludedFiles();
        for (int i = 0; i < files.length; i++) {
            getLog().info("Processing " + files[i]);

            File inputFile = new File(poDirectory, files[i]);
            String locale = getLocale(inputFile);
            getLog().info("Creating ResourceBundle for " + files[i] + " with locale " + locale);
            processLocale(inputFile, locale);

            if (sourceLocale != null && sourceLocale.length() > 0 && locale.equals(sourceLocale)) {
                processedSourceLocale = true;
            }
        }

        if (keysFile.exists()) {
            if (!processedSourceLocale && sourceLocale != null && sourceLocale.length() > 0) {
                processLocale(keysFile, sourceLocale);
            }
        }
        
    }
}
