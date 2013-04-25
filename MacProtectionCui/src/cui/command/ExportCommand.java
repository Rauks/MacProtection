package cui.command;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import core.MacAlgorithm;
import core.MacAlgorithmException;
import core.check.CheckMacException;
import core.check.CheckReader;
import core.check.CheckReaderReadingException;
import core.check.CheckWriterWritingException;
import core.processor.MacProcessor;
import core.processor.MacProcessorException;
import core.tree.Folder;
import cui.MacProtectionActionsFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public class ExportCommand implements MacProtectionCommand {

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

        FlaggedOption opt3 = new FlaggedOption("file")
                .setStringParser(JSAP.STRING_PARSER)
                .setRequired(true)
                .setShortFlag('f')
                .setLongFlag("file");
        opt3.setHelp("File to save");
        jsap.registerParameter(opt3);

        FlaggedOption opt4 = new FlaggedOption("source")
                .setStringParser(JSAP.STRING_PARSER)
                .setRequired(false)
                .setDefault(".")
                .setShortFlag('s')
                .setLongFlag("source");
        opt4.setHelp("Directory to scan");
        jsap.registerParameter(opt4);

        return jsap;
    }

    @Override
    public void process(JSAPResult config) {

        try {
            String opt_dirToScan = config.getString("source");
            String opt_password = config.getString("password");
            String opt_algo = config.getString("algo");
            String opt_file = config.getString("file");

            File dirToScan = new File(opt_dirToScan);
            MacAlgorithm algorithm;

            algorithm = new MacAlgorithm(opt_algo);

            System.out.println("Scan '" + dirToScan + "' directory");

            //Processing a physical repertory
            MacProcessor p = MacProtectionActionsFactory.scanDirectory(dirToScan, algorithm, opt_password, MacProcessor.MacOutput.HEXADECIMAL);
            Folder physicalRoot = p.getResult();

            //Creation of check file for the folder
            MacProtectionActionsFactory.checkWriter(opt_file, physicalRoot, algorithm, opt_password);

            //Get a folder tree from the validation file
            CheckReader cr = MacProtectionActionsFactory.checkReader(opt_file, algorithm, opt_password);
            Folder validationFolder = cr.getRootFolder();

            //Physical directory validation
            boolean isConformTo = physicalRoot.isConformTo(validationFolder);
            System.out.println();
            System.out.print("Validation result : ");
            System.out.println(isConformTo);
        } catch (FileNotFoundException | CheckWriterWritingException | CheckReaderReadingException | CheckMacException | NoSuchAlgorithmException | InvalidKeyException ex) {
            System.err.println(ex.getMessage());
        } catch (MacProcessorException | MacAlgorithmException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
