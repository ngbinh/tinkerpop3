package com.tinkerpop.gremlin.groovy.console;

import com.tinkerpop.gremlin.groovy.GremlinLoader;
import jline.console.history.FileHistory;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.IO;
import org.codehaus.groovy.tools.shell.InteractiveShellRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public class Console {

    private static final String HISTORY_FILE = ".gremlin_groovy_history";
    private static final String STANDARD_INPUT_PROMPT = "gremlin> ";
    public static final String STANDARD_RESULT_PROMPT = "==>";

    private static final IO STANDARD_IO = new IO(System.in, System.out, System.err);
    private static final Groovysh GROOVYSH = new Groovysh();

    public Console(final Optional<String> initScriptFile) {
        STANDARD_IO.out.println();
        STANDARD_IO.out.println("         \\,,,/");
        STANDARD_IO.out.println("         (o o)");
        STANDARD_IO.out.println("-----oOOo-(_)-oOOo-----");

        GROOVYSH.setResultHook(new NullResultHookClosure(GROOVYSH));
        // todo: hookup "use" command for plugins.
        /*for (String imps : Imports.getImports()) {
            GROOVYSH.execute("import " + imps);
        }*/
        GROOVYSH.execute("import com.tinkerpop.gremlin.groovy.*");
        GROOVYSH.execute("import com.tinkerpop.gremlin.structure.*");
        GROOVYSH.execute("import com.tinkerpop.gremlin.process.*");
        GROOVYSH.execute("import com.tinkerpop.tinkergraph.*");
        GROOVYSH.execute("import groovy.grape.Grape");

        try {
            GROOVYSH.setResultHook(new ResultHookClosure(GROOVYSH, STANDARD_IO, STANDARD_RESULT_PROMPT));
            GROOVYSH.setHistory(new FileHistory(new File(System.getProperty("user.home") + "/" + HISTORY_FILE)));
            final InteractiveShellRunner runner = new InteractiveShellRunner(GROOVYSH, new PromptClosure(GROOVYSH, STANDARD_INPUT_PROMPT));
            runner.setErrorHandler(new ErrorHookClosure(runner, STANDARD_IO));
            runner.setHistory(new FileHistory(new File(System.getProperty("user.home") + "/" + HISTORY_FILE)));
            GremlinLoader.load();
            if (initScriptFile.isPresent())
                initializeShellWithScript(STANDARD_IO, initScriptFile.get());
            runner.run();
        } catch (IOException e) {
            STANDARD_IO.err.println("Unable to create history file: " + HISTORY_FILE);
        } catch (Error e) {
            //System.err.println(e.getMessage());
        }
    }

    /**
     * Used by the Gremlin.use() function to send Groovysh instance to the plugin.
     */
    public static Groovysh getGroovysh() {
        return GROOVYSH;
    }

    /**
     * Used by the Gremlin.use() function to send IO instance to the plugin.
     */
    public static IO getStandardIo() {
        return STANDARD_IO;
    }

    private void initializeShellWithScript(final IO io, final String initScriptFile) {
        String line = "";
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(initScriptFile), Charset.forName("UTF-8")));
            while ((line = reader.readLine()) != null) {
                GROOVYSH.execute(line);
            }

            reader.close();
        } catch (FileNotFoundException fnfe) {
            io.err.println(String.format("Gremlin initialization file not found at [%s].", initScriptFile));
            System.exit(1);
        } catch (IOException ioe) {
            io.err.println(String.format("Bad line in Gremlin initialization file at [%s].", line));
            System.exit(1);
        }
    }

    public static void main(final String[] args) {
        new Console(args.length == 1 ? Optional.of(args[0]) : Optional.empty());
    }
}