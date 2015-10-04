package net.jhorstmann.i18n.mojo;

import net.jhorstmann.i18n.tools.MessageBundle;
import net.jhorstmann.i18n.tools.xgettext.MessageExtractor;
import net.jhorstmann.i18n.tools.xgettext.MessageExtractorException;
import net.jhorstmann.i18n.tools.xgettext.MessageFunction;
import net.jhorstmann.i18n.xgettext.asm.AsmMessageExtractor;

import net.jhorstmann.i18n.xgettext.web.WebMessageExtractor;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.fedorahosted.tennera.jgettext.HeaderFields;
import org.fedorahosted.tennera.jgettext.HeaderUtil;
import org.fedorahosted.tennera.jgettext.Message;
import org.sonatype.plexus.build.incremental.BuildContext;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
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
     * @parameter default-value="${false}" expression="${gettext.update}"
     */
    boolean update;
    /**
     * @parameter
     */
    String[] javaFunctions;
    /**
     * @parameter
     */
    String[] elFunctions;

    /**
     * Skip this mojo.
     *
     * @parameter expression="${skip}" default-value="false"
     */
    protected boolean skip;

    /**
     * @description E-Mail-address for message-bugs
     * @parameter expression="${msgidBugsAddress}"
     */
    protected String msgidBugsAddress;

    /**
     * @description Name of the project/package
     * @parameter expression="${pkgName}" default-value="${project.artifactId}"
     */
    protected String pkgName;

    /**
     * @description Version of the project/package
     * @parameter expression="${pkgVersion}" default-value="${project.version}"
     */
    protected String pkgVersion;

    /** @parameter default-value="${project}" */
    protected org.apache.maven.project.MavenProject mavenProject;

    /**
     * @parameter default-value="${basedir}"
     */
    protected File projectRoot;

    // Injection of the eclipse-buildcontext for m2e-compatibility
    /** @component */
    protected BuildContext buildContext;

    String[] getWebappIncludes() {
        return webappIncludes != null ? webappIncludes : new String[]{ "**/*.xhtml", "**/*.jspx" };
    }

    String[] getWebappExcludes() {
        return webappExcludes != null ? webappExcludes : new String[] {};
    }

    String[] getPoIncludes() {
        return poIncludes != null ? poIncludes : new String[] { "**/*.po" };
    }

    String[] getPoExcludes() {
        return poExcludes != null ? poExcludes : new String[] {};
    }

    List<MessageFunction> getJavaFunctions() {
        if (javaFunctions == null) {
            return AsmMessageExtractor.DEFAULT_MESSAGE_FUNCTIONS;
        } else {
            int len = javaFunctions.length;
            MessageFunction[] functions = new MessageFunction[len];
            for (int i=0; i<len; i++) {
                functions[i] = MessageFunction.fromJava(javaFunctions[i]);
            }
            return Arrays.asList(functions);
        }
    }

    List<MessageFunction> getELFunctions() {
        if (elFunctions == null) {
            return WebMessageExtractor.DEFAULT_MESSAGE_FUNCTIONS;
        } else {
            int len = elFunctions.length;
            MessageFunction[] functions = new MessageFunction[len];
            for (int i=0; i<len; i++) {
                functions[i] = MessageFunction.fromEL(elFunctions[i]);
            }
            return Arrays.asList(functions);
        }
    }

    private MessageBundle loadMessageBundleImpl() throws IOException {
        if (update && keysFile.exists()) {
            getLog().info("Loading existing keys from " + keysFile);
            return MessageBundle.loadCatalog(keysFile);
        } else {
            getLog().info("Creating new message-bundle");
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
        if (getLog().isDebugEnabled()) {
            getLog().debug(MessageFormat.format("Saving keys to {0} <{1}>", bundle.isTemplate() ? "po-template" : "po-catalog", keysFile));
        }
        bundle.storeCatalog(keysFile);
        if (buildContext != null) {
            buildContext.refresh(keysFile);
        }
    }

    protected void saveMessageBundle(MessageBundle bundle) throws MojoExecutionException {
        fillBundleHeader(bundle);
        try {
            saveMessageBundleImpl(bundle);
        } catch (IOException ex) {
            throw new MojoExecutionException("Could not save message bundle", ex);
        }
    }

    protected void fillBundleHeader(MessageBundle bundle) {
        Message header = bundle.getHeaderMessage();
        if (header == null) {
            getLog().debug("Create new default-header...");
            header = HeaderUtil.generateDefaultHeader();
        }
        HeaderFields fields = HeaderFields.wrap(header);

        if (StringUtils.isNotEmpty(msgidBugsAddress)) {
            getLog().debug(MessageFormat.format("Inserting defined value <{0}>=<{1}>", HeaderFields.KEY_ReportMsgidBugsTo, msgidBugsAddress));
            fields.setValue(HeaderFields.KEY_ReportMsgidBugsTo, msgidBugsAddress);
        }
        if (StringUtils.isNotEmpty(pkgName) || StringUtils.isNotEmpty(pkgVersion))
            fields.setValue(HeaderFields.KEY_ProjectIdVersion, MessageFormat.format("{0} {1}", pkgName, pkgVersion));
        fields.unwrap(header);
        bundle.addMessage(header);
    }

    int extractMessages(MessageExtractor extractor, File basedir, String[] includes, String[] excludes) throws MojoExecutionException {

        getLog().debug(
                "Creating DirectoryScanner with basedir " + basedir + " including " + Arrays.toString(includes) + " and excluding " + Arrays.toString(excludes));

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
