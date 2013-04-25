package cui.command;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

/**
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public interface MacProtectionCommand {

    public JSAP initCall() throws JSAPException;

    public void process(JSAPResult config);

    public String getDescription();
}
