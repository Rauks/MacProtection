package cui.command;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import core.MacAlgorithm;
import core.MacAlgorithmException;
import core.processor.MacProcessor;
import core.processor.MacProcessorException;
import core.tree.Folder;
import cui.MacProtectionActionsFactory;
import cui.MacProtectionOptionsFactory;
import cui.tree.DetailedTree;
import cui.tree.FileWithState;
import java.io.File;
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

        MacProtectionOptionsFactory.password(jsap);
        MacProtectionOptionsFactory.algorithm(jsap);
        MacProtectionOptionsFactory.source(jsap);
        MacProtectionOptionsFactory.tree(jsap);

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

            for (Iterator<Map.Entry<String, FileWithState>> it = detailedTree.create(physicalRoot).entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, FileWithState> en = it.next();
                if (en.getValue().isHashedFile()) {
                    System.out.println(" - " + en.getValue().getState() + " " + en.getValue().getHashedFile().getHash() + " " + en.getKey());
                } else {
                    System.out.println(" - " + en.getValue().getState() + " DIRECTORY                        " + en.getKey());
                }
            }
        } catch (MacProcessorException | MacAlgorithmException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
