package net.jhorstmann.i18n.mojo;

import java.io.File;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * Invokes the msginit tool to create a new po file.
 * 
 * @author Jörn Horstmann
 * @goal init
 */
public class InitMojo extends AbstractGettextMojo {

    /**
     * The msginit command.
     * 
     * @parameter expression="${msginitCmd}" default-value="msginit"
     * @required
     */
    String msginitCmd;
    /**
     * @parameter expression="${locale}"
     * @required
     */
    String locale;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Commandline cl = new Commandline();
        cl.setExecutable(msginitCmd);
        cl.createArg().setValue("-i");
        cl.createArg().setFile(keysFile);
        cl.createArg().setValue("-l");
        cl.createArg().setValue(locale);
        cl.createArg().setValue("-o");
        File poFile = new File(poDirectory, locale + ".po");
        cl.createArg().setFile(poFile);
        cl.createArg().setValue("--no-translator");
        StreamConsumer out = new LoggerStreamConsumer(getLog(), LoggerStreamConsumer.INFO);
        StreamConsumer err = new LoggerStreamConsumer(getLog(), LoggerStreamConsumer.WARN);
        try {
            CommandLineUtils.executeCommandLine(cl, out, err);
            if (poFile.exists() && buildContext != null) {
                buildContext.refresh(poFile);
            }
        } catch (CommandLineException e) {
            throw new MojoExecutionException("Could not execute " + msginitCmd, e);
        }
    }
}
