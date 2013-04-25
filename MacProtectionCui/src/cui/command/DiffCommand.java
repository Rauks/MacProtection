package cui.command;

import cui.tree.HashedFileWithState;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import core.MacAlgorithm;
import core.MacAlgorithmException;
import core.check.CheckMacException;
import core.check.CheckReader;
import core.check.CheckReaderReadingException;
import core.processor.MacProcessor;
import core.processor.MacProcessorEvent;
import core.processor.MacProcessorException;
import core.processor.MacProcessorListener;
import core.tree.Folder;
import cui.MacProtectionActionsFactory;
import cui.tree.DetailedTree;
import cui.tree.FileState;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public class DiffCommand implements MacProtectionCommand {

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

        FlaggedOption opt3 = new FlaggedOption("check_file")
                .setStringParser(JSAP.STRING_PARSER)
                .setRequired(true)
                .setShortFlag('c')
                .setLongFlag("check");
        opt3.setHelp("Checksum file to compare");
        jsap.registerParameter(opt3);

        FlaggedOption opt4 = new FlaggedOption("dirToScan")
                .setStringParser(JSAP.STRING_PARSER)
                .setRequired(false)
                .setDefault(".")
                .setShortFlag('d')
                .setLongFlag("dirToScan");
        opt4.setHelp("Directory to scan");
        jsap.registerParameter(opt4);

        //-no-checksum

        return jsap;
    }

    @Override
    public void process(JSAPResult config) {

        try {
            String opt_dirToScan = config.getString("dirToScan");
            String opt_password = config.getString("password");
            String opt_algo = config.getString("algo");
            String opt_file = config.getString("check_file");

            File dirToScan = new File(opt_dirToScan);
            MacAlgorithm algorithm;

            algorithm = new MacAlgorithm(opt_algo);

            System.out.println("Scan '" + dirToScan + "' directory");

            //Processing a physical repertory
            MacProcessor p = MacProtectionActionsFactory.scanDirectory(dirToScan, algorithm, opt_password, MacProcessor.MacOutput.HEXADECIMAL);
            Folder physicalRoot = p.getResult();

            //Get a folder tree from the validation file
            CheckReader cr = MacProtectionActionsFactory.checkReader(opt_file, algorithm, opt_password);
            Folder validationFolder = cr.getRootFolder();

            //Physical directory validation
            boolean isConformTo = physicalRoot.isConformTo(validationFolder);
            System.out.println();
            System.out.print("VALIDATION RESULT : ");
            System.out.println(isConformTo);
            
            System.out.println();
            System.out.println("Detailed tree :");
            
            DetailedTree detailedTree = new DetailedTree(physicalRoot);
            for (Iterator<Entry<String, HashedFileWithState>> it = detailedTree.create(validationFolder).entrySet().iterator(); it.hasNext();) {
                Entry<String, HashedFileWithState> en = it.next();

                if (en.getValue().getState() != FileState.EQUAL) {
                    System.out.println(" - " + en.getValue().getState() + " " + en.getValue().getHash() + " " + en.getKey());
                }
            }
        } catch (FileNotFoundException | CheckReaderReadingException | CheckMacException | NoSuchAlgorithmException | InvalidKeyException ex) {
            System.err.println(ex.getMessage());
        } catch (MacProcessorException | MacAlgorithmException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
