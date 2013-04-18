package cui.command;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
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
        return jsap;
    }

    @Override
    public void process(JSAPResult config) {
        System.out.println("Help");
        System.out.println();

        for (Map.Entry<String, MacProtectionCommand> entry : MacProtectionCui.getCommands().entrySet()) {
            try {
                System.out.print("\t");
                System.out.print(entry.getKey());
                System.out.print("\t");
                System.out.print(entry.getValue().initCall().getUsage());
                System.out.println();
            } catch (JSAPException ex) {
                Logger.getLogger(HelpCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
