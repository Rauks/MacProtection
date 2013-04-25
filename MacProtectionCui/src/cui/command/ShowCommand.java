package cui.command;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import core.MacAlgorithm;
import core.MacAlgorithmException;
import core.check.CheckReader;
import core.processor.MacProcessor;
import core.processor.MacProcessorEvent;
import core.processor.MacProcessorException;
import core.processor.MacProcessorListener;
import core.tree.Folder;
import cui.MacProtectionActionsFactory;
import cui.tree.DetailedTree;
import cui.tree.HashedFileWithState;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public class ShowCommand implements MacProtectionCommand {

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

        FlaggedOption opt3 = new FlaggedOption("source")
                .setStringParser(JSAP.STRING_PARSER)
                .setRequired(false)
                .setDefault(".")
                .setShortFlag('s')
                .setLongFlag("source");
        jsap.registerParameter(opt3);

        FlaggedOption opt4 = new FlaggedOption("tree")
                .setStringParser(JSAP.BOOLEAN_PARSER)
                .setRequired(false)
                .setDefault("true")
                .setShortFlag('t')
                .setLongFlag(JSAP.NO_LONGFLAG);
        opt4.setHelp("List recursively all files and folders");
        jsap.registerParameter(opt4);

        //-no-checksum

        return jsap;
    }

    @Override
    public void process(JSAPResult config) {
        try {
            String opt_password = config.getString("password");
            String opt_algo = config.getString("algo");
            String opt_dirToScan = config.getString("source");

            File dirToScan = new File(opt_dirToScan);
            MacAlgorithm algorithm;

            algorithm = new MacAlgorithm(opt_algo);

            System.out.println("Scan '" + dirToScan + "' directory");

            //Processing a physical repertory
            MacProcessor p = MacProtectionActionsFactory.scanDirectory(dirToScan, algorithm, opt_password, MacProcessor.MacOutput.HEXADECIMAL);
            Folder physicalRoot = p.getResult();

            System.out.println();
            
            DetailedTree detailedTree = new DetailedTree(physicalRoot);

            //if( physicalRoot.getFile(opt_algo))
            for (Iterator<Map.Entry<String, HashedFileWithState>> it = detailedTree.create(physicalRoot).entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, HashedFileWithState> en = it.next();
                System.out.println(" - " + en.getValue().getState() + " " + en.getValue().getHash() + " " + en.getKey());
            }
        } catch (FileNotFoundException | MacProcessorException | MacAlgorithmException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
