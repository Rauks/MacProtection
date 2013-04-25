package cui.command;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import cui.MacProtectionCui;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public class HelpCommand implements MacProtectionCommand {

    @Override
    public JSAP initCall() throws JSAPException {
        JSAP jsap = new JSAP();

        UnflaggedOption opt1 = new UnflaggedOption("command")
                .setStringParser(JSAP.STRING_PARSER)
                .setRequired(false);
        jsap.registerParameter(opt1);

        return jsap;
    }

    @Override
    public void process(JSAPResult config) {
        String opt_command = config.getString("command");

        System.out.println("Help");
        System.out.println();

        if (opt_command != null && !opt_command.isEmpty()) {
            MacProtectionCommand entry = MacProtectionCui.getCommands().get(opt_command);
            try {
                JSAP jsap = entry.initCall();
                System.out.print("   ");
                System.out.print(opt_command);
                System.out.print("\t");
                System.out.print(jsap.getUsage());
                System.out.print(implode("\n\t\t", jsap.getHelp().split("\n"), true));
                System.out.println("\n");
            } catch (JSAPException ex) {
                System.err.println(ex);
            }
        } else {

            for (Map.Entry<String, MacProtectionCommand> entry : MacProtectionCui.getCommands().entrySet()) {
                try {
                    JSAP jsap = entry.getValue().initCall();
                    System.out.print("   ");
                    System.out.print(entry.getKey());
                    System.out.print("\t");
                    System.out.print(jsap.getUsage());
                    System.out.println("\n");
                } catch (JSAPException ex) {
                    Logger.getLogger(HelpCommand.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static String implode(String delim, String[] args, boolean first) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            if (i >= (first ? 0 : 1)) {
                sb.append(delim);
            }

            sb.append(args[i]);
        }

        return sb.toString();
    }
}
