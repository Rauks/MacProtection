package cui;

import core.MacAlgorithm;
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

/**
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public class MacProtectionActionsFactory {

    public static MacProcessor scanDirectory(File dirToScan, MacAlgorithm algorithm, String opt_password, MacProcessor.MacOutput mcOutput) throws MacProcessorException {
        System.out.println();

        MacProcessor p = new MacProcessor(dirToScan, algorithm, opt_password, mcOutput);

        p.addMacProcessorListener(new MacProcessorListener() {
            private int state_process = 0;
            private boolean introIsPrinted;

            @Override
            public void macProcessorPerformed(MacProcessorEvent evt) {
                float state = evt.getProcessedFiles() / (float) evt.getTotalFiles() * 100;

                if (!this.introIsPrinted) {
                    System.out.println("Numbers of files : " + evt.getTotalFiles());
                    System.out.print("\tScan progres : ");
                    this.introIsPrinted = true;
                }

                if (Math.floor(state / 2) > this.state_process) {
                    System.out.print("|");
                    this.state_process = (int) Math.floor(state / 2);
                }
            }
        });
        p.process();

        System.out.println();

        return p;
    }

    public static CheckReader checkReader(String opt_file, MacAlgorithm algorithm, String opt_password) throws FileNotFoundException, CheckReaderReadingException, CheckMacException, NoSuchAlgorithmException, InvalidKeyException {
        System.out.println();
        System.out.println("Compare with check file '" + opt_file + "' ... ");

        System.out.print("\tReading ... ");
        CheckReader cr = new CheckReader(new FileInputStream(new File(opt_file)), algorithm, opt_password);
        cr.read();
        System.out.println(" DONE !");

        return cr;
    }

    public static CheckWriter checkWriter(String opt_file, Folder physicalRoot, MacAlgorithm algorithm, String opt_password) throws FileNotFoundException, CheckWriterWritingException, CheckMacException, NoSuchAlgorithmException, InvalidKeyException {
        System.out.println();
        System.out.print("Writing chech file '" + opt_file + "' ... ");
        CheckWriter cw = new CheckWriter(new FileOutputStream(new File(opt_file)), physicalRoot, algorithm, opt_password);
        cw.write();
        System.out.println(" DONE ! ");
        
        return cw;
    }
}
