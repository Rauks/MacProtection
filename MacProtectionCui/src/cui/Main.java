package cui;

import java.util.ArrayList;

/**
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //String[] args = new String[] {"help"};
        
        try {
            if( args.length < 1 ) {
                throw new CuiMessageException("You must specify at least one argument");
            }
            
            ArrayList<String> optionsArgs = new ArrayList<>();
            for( int i = 1 ; i < args.length ; i++ ) {
                optionsArgs.add(args[i]);
            }
            
            MacProtectionCui.callFactory(args[0], (String[]) optionsArgs.toArray(new String[optionsArgs.size()]));
            
        } catch(CuiMessageException e) {
            System.out.println(e);
            System.out.println("Read the documentation (readme) or use `help` command");
        }
    }
}
