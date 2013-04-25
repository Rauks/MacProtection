package cui.command;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

/**
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
        
    }
}
