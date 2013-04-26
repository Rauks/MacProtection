package cui.command;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import core.MacAlgorithm;

/**
 * "algos" command.
 * <p/>
 * Return a list of all available algorithms
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public class AlgosCommand implements MacProtectionCommand {

    @Override
    public JSAP initCall() throws JSAPException {
        JSAP jsap = new JSAP();

        return jsap;
    }

    @Override
    public void process(JSAPResult config) {
        //List and choice of the Hmac algorithm.
        System.out.println("Available algorithms :");
        for (String alg : MacAlgorithm.AVAILABLE_ALGORITHMS) {
            System.out.println(" - " + alg);
        }
    }

    @Override
    public String getDescription() {
        return "List all algorithms";
    }
}
