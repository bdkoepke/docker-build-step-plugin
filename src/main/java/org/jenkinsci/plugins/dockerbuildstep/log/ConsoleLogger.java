package org.jenkinsci.plugins.dockerbuildstep.log;

import hudson.model.BuildListener;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 * A helper class which offers various types of logging. Currently it provides plain logging directly into console log
 * and annotated log via {@link DockerConsoleAnnotator}.
 * 
 * @author vjuranek
 * 
 */
public class ConsoleLogger {

    private final BuildListener listener;
    private final DockerConsoleAnnotator annotator;

    public ConsoleLogger(BuildListener listener, DockerConsoleAnnotator annotator) {
        this.listener = listener;
        this.annotator = annotator;
    }


    public ConsoleLogger(BuildListener listener) {
        this(listener, new
                DockerConsoleAnnotator(listener.getLogger()));
    }

    public BuildListener getListener() {
        return listener;
    }

    protected PrintStream getLogger() {
        return listener.getLogger();
    }

    public void logInfo(String message) {
        logAnnot("[Docker] INFO: ", message);
    }
    
    public void logWarn(String message) {
        logAnnot("[Docker] WARN: ", message);
    }
    
    public void logError(String message) {
        logAnnot("[Docker] ERROR: ", message);
    }
    
    /**
     * Logs annotated messages
     * 
     * @param message
     *            message to be annotated
     */
    public void logAnnot(String prefix, String message) {
        byte[] msg = (prefix + message + "\n").getBytes(Charset.defaultCharset());
        try {
            annotator.eol(msg, msg.length);
        } catch (IOException e) {
            getLogger().println("Problem with writing into console log: " + e.getMessage());
        }
    }

    /**
     * Logs plain text messages directly into console
     * 
     * @param message
     *            message in plain text
     */
    public void log(String message) {
        getLogger().println(message);
    }
}
