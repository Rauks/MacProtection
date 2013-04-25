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
import core.check.CheckWriter;
import core.check.CheckWriterWritingException;
import core.processor.MacProcessor;
import core.processor.MacProcessorEvent;
import core.processor.MacProcessorException;
import core.processor.MacProcessorListener;
import core.tree.Folder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import usecase.MacProtection;

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

        FlaggedOption opt4 = new FlaggedOption("dirToScan")
                .setStringParser(JSAP.STRING_PARSER)
                .setRequired(false)
                .setDefault(".")
                .setShortFlag('d')
                .setLongFlag("dirToScan");
        opt4.setHelp("Directory to scan");
        jsap.registerParameter(opt4);

        return jsap;
    }

    @Override
    public void process(JSAPResult config) {

        String opt_dirToScan = config.getString("dirToScan");
        String opt_password = config.getString("password");
        String opt_algo = config.getString("algo");
        String opt_file = config.getString("file");

        File dirToScan = new File(opt_dirToScan);
        MacAlgorithm algorithm;

        try {
            algorithm = new MacAlgorithm(opt_algo);

            //Processing a physical repertory
            MacProcessor p = new MacProcessor(dirToScan, algorithm, opt_password, MacProcessor.MacOutput.HEXADECIMAL);

            p.addMacProcessorListener(new MacProcessorListener() {
                private int state_process = 0;
                private boolean introIsPrinted;

                @Override
                public void macProcessorPerformed(MacProcessorEvent evt) {
                    float state = evt.getProcessedFiles() / (float) evt.getTotalFiles() * 100;

                    if (!this.introIsPrinted) {
                        System.out.println("Numbers of files : " + evt.getTotalFiles());
                        System.out.print("\tProgres : ");
                        this.introIsPrinted = true;
                    }

                    if (Math.floor(state / 2) > this.state_process) {
                        System.out.print("|");
                        this.state_process = (int) Math.floor(state / 2);
                    }

                    //System.out.println(evt.getProcessedFiles() + "/" + (float) evt.getTotalFiles() + " - " + state + " - " + state/10);
                }
            });
            p.process();
            Folder physicalRoot = p.getResult();

            System.out.println();

            //Creation of check file for the folder
            System.out.print("WRITING CHECK FILE '" + opt_file + "' ... ");
            CheckWriter cw = new CheckWriter(new FileOutputStream(new File(opt_file)), physicalRoot, algorithm, opt_password);
            cw.write();
            System.out.println(" DONE ! ");

            //Get a folder tree from the validation file
            Folder validationFolder = null;
            System.out.print("READING CHECK FILE ... ");
            CheckReader cr = new CheckReader(new FileInputStream(new File(opt_file)), algorithm, opt_password);
            cr.read();
            validationFolder = cr.getRootFolder();
            System.out.println(" DONE !");

            //Physical directory validation
            System.out.print("VALIDATION RESULT : ");
            System.out.println(physicalRoot.isConformTo(validationFolder));

        } catch (FileNotFoundException | CheckWriterWritingException | CheckReaderReadingException | CheckMacException | NoSuchAlgorithmException | InvalidKeyException ex) {
            System.err.println(ex.getMessage());
        } catch (MacProcessorException | MacAlgorithmException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
