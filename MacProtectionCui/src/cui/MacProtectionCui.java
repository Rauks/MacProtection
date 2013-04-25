package cui;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import cui.command.MacProtectionCommand;
import java.util.TreeMap;

/**
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public class MacProtectionCui {

    public static TreeMap<String, MacProtectionCommand> commands = new TreeMap<>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new MacProtectionCui();
    }

    /**
     * Initialize differents {@link MacProtectionCommand}
     */
    public MacProtectionCui() {
        // initialize the commands
        MacProtectionCui.commands.put("show", new cui.command.ShowCommand());
        MacProtectionCui.commands.put("diff", new cui.command.DiffCommand());
        MacProtectionCui.commands.put("export", new cui.command.ExportCommand());
        MacProtectionCui.commands.put("algos", new cui.command.AlgosCommand());
        MacProtectionCui.commands.put("help", new cui.command.HelpCommand());
    }

    /**
     *
     * @param string First argument (command name implement
     * {@link MacProtectionCommand}}
     * @param args String[] Other arguments
     * @throws CuiMessageException Catch and transform errors into
     * {@link CuiMessageException}
     */
    public static void callFactory(String string, String[] args) throws CuiMessageException {

        MacProtectionCui MPC = new MacProtectionCui();

        // Check if user command exists
        if (!MacProtectionCui.getCommands().containsKey(string)) {
            throw new CuiMessageException("Unknown command");
        }

        try {
            MacProtectionCommand command = MacProtectionCui.getCommands().get(string);
            JSAP jsap;
            jsap = command.initCall();
            JSAPResult config = jsap.parse(args);

            // print out specific error messages describing the problems
            // with the command line, THEN print usage, THEN print full
            // help.  This is called "beating the user with a clue stick."
            for (java.util.Iterator errs = config.getErrorMessageIterator(); errs.hasNext();) {
                System.err.println("Error: " + errs.next());
            }

            // check whether the command line was valid, and if it wasn't,
            // display usage information and exit.
            if (!config.success()) {
                System.err.println();
                System.err.println("Usage: java " + command.getClass().getName());
                System.err.println("\t" + jsap.getUsage());
                System.err.println();
                System.exit(1);
            }

            // execute the command
            command.process(config);
        } catch (JSAPException ex) {
            throw new CuiMessageException(ex);
        }
    }

    /**
     * Return the {
     *
     * @MacProtectionCommand} and his String key
     *
     * @return TreeMap<String, MacProtectionCommand> Command
     */
    public static TreeMap<String, MacProtectionCommand> getCommands() {
        return MacProtectionCui.commands;
    }
}
