package cui;

/**
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public class MacProtectionCui {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }

    MacProtectionCui() {
        // @TODO create pseudo console
    }

    public static void callFactory(String string, String[] args) {
        System.out.println("call " + string);
        System.out.println("-- with args : ");
        for (String arg : args) {
            System.out.println("\t" + arg);
        }
    }
}
