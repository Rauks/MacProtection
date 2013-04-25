package cui;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                throw new CuiMessageException("You must specify at least one argument");
            }

            ArrayList<String> optionsArgs = new ArrayList<>();
            for (int i = 1; i < args.length; i++) {
                optionsArgs.add(args[i]);
            }

            MacProtectionCui.callFactory(args[0], (String[]) optionsArgs.toArray(new String[optionsArgs.size()]));

        } catch (CuiMessageException e) {
            System.out.println(e.getMessage());
            System.out.println();
            System.out.println("Read the documentation (readme) or use `help` command");
            System.out.println();
            try {
                MacProtectionCui.callFactory("help", new String[] {});
            } catch (CuiMessageException ex) {
                System.err.println(ex);
            }
        }
    }
}
