package cui;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import cui.command.MacProtectionCommandInit;
import cui.command.MacProtectionCommand;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public class MacProtectionCui {

    protected TreeMap<String, MacProtectionCommand> commands = new TreeMap<>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new MacProtectionCui();
    }

    MacProtectionCui() {
        commands.put("init", new MacProtectionCommandInit());
    }

    public static void callFactory(String string, String[] args) throws CuiMessageException {

        MacProtectionCui MPC = new MacProtectionCui();

        if (!MPC.getCommands().containsKey(string)) {
            throw new CuiMessageException("Unknown command");
        }

        try {
            MacProtectionCommand command = MPC.getCommands().get(string);
            JSAP jsap;
            jsap = command.initCall();
            JSAPResult config = jsap.parse(args);

            // check whether the command line was valid, and if it wasn't,
            // display usage information and exit.
            if (!config.success()) {
                System.err.println();
                System.err.println("Usage: java " + command.getClass().getName());
                System.err.println("\t" + jsap.getUsage());
                System.err.println();
                System.exit(1);
            }

            command.process(config);
        } catch (JSAPException ex) {
            System.out.println(ex);
        }
    }

    public TreeMap<String, MacProtectionCommand> getCommands() {
        return this.commands;
    }
}
