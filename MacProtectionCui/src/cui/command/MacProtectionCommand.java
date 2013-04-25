package cui.command;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;

/**
 * Interface for command
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public interface MacProtectionCommand {

    /**
     * Initialize the command with JASP environment.
     * <p/>
     * Create differents {@link FlaggedOption}, {@link UnflaggedOption}, ...
     *
     * @throws JSAPException
     * @return JASP
     * @see JASP
     */
    public JSAP initCall() throws JSAPException;

    /**
     * Run the command
     *
     * @param config JASP environment with console arguments
     */
    public void process(JSAPResult config);

    /**
     * Get the description of the command
     *
     * @return String Description of the command
     */
    public String getDescription();
}
