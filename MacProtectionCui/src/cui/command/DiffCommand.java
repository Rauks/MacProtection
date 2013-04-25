package cui.command;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import core.MacAlgorithm;
import core.MacAlgorithmException;
import core.check.CheckMacException;
import core.check.CheckReader;
import core.check.CheckReaderReadingException;
import core.processor.MacProcessor;
import core.processor.MacProcessorException;
import core.tree.Folder;
import cui.MacProtectionActionsFactory;
import cui.MacProtectionOptionsFactory;
import cui.tree.DetailedTree;
import cui.tree.FileState;
import cui.tree.FileWithState;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public class DiffCommand implements MacProtectionCommand {

    @Override
    public JSAP initCall() throws JSAPException {
        JSAP jsap = new JSAP();

        MacProtectionOptionsFactory.password(jsap);
        MacProtectionOptionsFactory.algorithm(jsap);
        MacProtectionOptionsFactory.checkFile(jsap);
        MacProtectionOptionsFactory.source(jsap);
        MacProtectionOptionsFactory.tree(jsap);

        //-no-checksum

        return jsap;
    }

    @Override
    public void process(JSAPResult config) {

        try {
            String opt_source = config.getString("source");
            String opt_password = config.getString("password");
            String opt_algo = config.getString("algo");
            String opt_check_file = config.getString("check_file");
            boolean opt_tree = config.getBoolean("tree");

            File dirToScan = new File(opt_source);
            MacAlgorithm algorithm;

            algorithm = new MacAlgorithm(opt_algo);

            System.out.println("Scan '" + dirToScan + "' directory");

            //Processing a physical repertory
            MacProcessor p = MacProtectionActionsFactory.scanDirectory(dirToScan, algorithm, opt_password, MacProcessor.MacOutput.HEXADECIMAL);
            Folder physicalRoot = p.getResult();

            //Get a folder tree from the validation file
            CheckReader cr = MacProtectionActionsFactory.checkReader(opt_check_file, algorithm, opt_password);
            Folder validationFolder = cr.getRootFolder();

            //Physical directory validation
            boolean isConformTo = physicalRoot.isConformTo(validationFolder);
            System.out.println();
            System.out.print("Validation result : ");
            System.out.println(isConformTo);

            System.out.println();
            System.out.println("Detailed tree :");

            // Show or not the DetailedTree
            if (opt_tree) {
                DetailedTree detailedTree = new DetailedTree(physicalRoot);
                for (Iterator<Map.Entry<String, FileWithState>> it = detailedTree.create(validationFolder).entrySet().iterator(); it.hasNext();) {
                    Map.Entry<String, FileWithState> en = it.next();
                    if (en.getValue().getState() == FileState.EQUAL) {
                        continue;
                    }
                    if (en.getValue().isHashedFile()) {
                        System.out.println(" - " + en.getValue().getState() + " " + en.getValue().getHashedFile().getHash() + " " + en.getKey());
                    } else {
                        System.out.println(" - " + en.getValue().getState() + " DIRECTORY                        " + en.getKey());
                    }
                }
            }
        } catch (FileNotFoundException | CheckReaderReadingException | CheckMacException | NoSuchAlgorithmException | InvalidKeyException ex) {
            System.err.println(ex.getMessage());
        } catch (MacProcessorException | MacAlgorithmException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "Show changes between source directory and checkfile";
    }
}
