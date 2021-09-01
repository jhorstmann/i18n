package net.jhorstmann.i18n.mojo;

import java.io.File;
import java.io.IOException;

import net.jhorstmann.i18n.GettextResourceBundle;
import net.jhorstmann.i18n.tools.MessageBundle;
import net.jhorstmann.i18n.tools.ResourceBundleCompiler;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.sonatype.plexus.build.incremental.BuildContext;

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
     * 
     * @parameter expression="${targetBundle}"
     * @required
     */
    protected String targetBundle;
    /**
     * The locale of the messages in the source code.
     * 
     * @parameter expression="${sourceLocale}" default-value="en"
     * @required
     */
    protected String sourceLocale;

    /**
     * @parameter default-value="${project}"
     * */
    private org.apache.maven.project.MavenProject mavenProject;

    private static String getLocale(File file) {
        String fileName = file.getName();
        // TODO: check correct locale name for "sr@latin"
        return fileName.substring(0, fileName.lastIndexOf('.')).replace("@", "_");
    }

    private void processLocaleImpl(File inputFile, String locale) throws IOException, MojoExecutionException {
        String className = targetBundle;
        if (locale != null && locale.length() > 0) {
            className += "_" + locale;
        }
        MessageBundle bundle = MessageBundle.loadCatalog(inputFile);

        ResourceBundleCompiler.compileFile(bundle, GettextResourceBundle.class.getName(), className, classesDirectory);
    }

    private void processLocale(File inputFile, String locale) throws MojoExecutionException {
        try {
            processLocaleImpl(inputFile, locale);
        } catch (IOException ex) {
            throw new MojoExecutionException("Could not create ResourceBundle for input " + inputFile + " and locale " + locale, ex);
        }
    }

    private void touch(File file) {
        if (!file.exists()) {
            try {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                getLog().warn("Could not touch file: " + file.getName(), e);
                buildContext.addMessage(file, 0, 0, "Could not touch file: " + file.getName(), BuildContext.SEVERITY_WARNING, e);
            }
        }
        buildContext.refresh(file); // inform Eclipse-Workspace about file-modifications
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Skipping gettext.");
            return;
        }
        // create output directory if it doesn't exists
        classesDirectory.mkdirs();

        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir(poDirectory);
        ds.setIncludes(getPoIncludes());
        ds.setExcludes(getPoExcludes());
        ds.scan();

        boolean processedSourceLocale = false;

        String[] files = ds.getIncludedFiles();
        for (int i = 0; i < files.length; i++) {

            File inputFile = new File(poDirectory, files[i]);
            if (buildContext == null || buildContext.hasDelta(inputFile)) {
                getLog().info("Processing " + files[i]);
                String locale = getLocale(inputFile);
                getLog().info("Creating ResourceBundle for " + files[i] + " with locale " + locale);
                processLocale(inputFile, locale);

                if (sourceLocale != null && sourceLocale.length() > 0 && locale.equals(sourceLocale)) {
                    processedSourceLocale = true;
                }
            }
        }

        if (keysFile.exists()) {
            if (!processedSourceLocale && sourceLocale != null && sourceLocale.length() > 0) {
                processLocale(keysFile, sourceLocale);
            }
            if (buildContext != null) {
                buildContext.refresh(keysFile);
                buildContext.refresh(classesDirectory);
            }
        }

        // Create empty default message bundle
        File msgBundle = new File(classesDirectory, targetBundle.replace('.', '/') + ".properties");
        touch(msgBundle);

        Resource res = new Resource();
        res.setDirectory(classesDirectory.getAbsolutePath());
        mavenProject.addResource(res);
    }
}
