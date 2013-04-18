package cui.command;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

/**
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public class LsCommand implements MacProtectionCommand {

    @Override
    public JSAP initCall() throws JSAPException {
        JSAP jsap = new JSAP();
        
        FlaggedOption opt1 = new FlaggedOption("password")
                .setStringParser(JSAP.STRING_PARSER)
                .setRequired(true)
                .setShortFlag('p')
                .setLongFlag(JSAP.NO_LONGFLAG);
        jsap.registerParameter(opt1);

        FlaggedOption opt2 = new FlaggedOption("algo")
                .setStringParser(JSAP.STRING_PARSER)
                .setRequired(true)
                .setShortFlag('a')
                .setLongFlag(JSAP.NO_LONGFLAG);
        jsap.registerParameter(opt2);
        
        FlaggedOption opt3 = new FlaggedOption("full")
                .setStringParser(JSAP.BOOLEAN_PARSER)
                .setRequired(false)
                .setDefault("false")
                .setShortFlag('f')
                .setLongFlag(JSAP.NO_LONGFLAG);
        opt3.setHelp("List recursively all files and folders");
        jsap.registerParameter(opt3);

        return jsap;
    }

    @Override
    public void process(JSAPResult config) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
