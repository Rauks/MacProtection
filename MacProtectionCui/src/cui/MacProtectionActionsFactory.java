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
 * Different actions used into {@link MacProtectionCommand}
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public class MacProtectionActionsFactory {

    /**
     * Scan {@code dirToScan} with {@code algorithm} and {@code password}
     *
     * @param dirToScan Directory to scan
     * @param algorithm Algorihm to use
     * @param password Password used to crypt datas
     * @param mcOutput
     * @return {@link MacProcessor}
     * @throws MacProcessorException
     */
    public static MacProcessor scanDirectory(File dirToScan, MacAlgorithm algorithm, String password, MacProcessor.MacOutput mcOutput) throws MacProcessorException {
        System.out.println();

        MacProcessor p = new MacProcessor(dirToScan, algorithm, password, mcOutput);

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

    /**
     * Check {@code checkFile}
     *
     * @param checkFile Check file
     * @param algorithm Algorihm to use
     * @param password Password used to crypt datas
     * @return {@link CheckReader}
     * @throws FileNotFoundException
     * @throws CheckReaderReadingException
     * @throws CheckMacException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static CheckReader checkReader(String checkFile, MacAlgorithm algorithm, String password) throws FileNotFoundException, CheckReaderReadingException, CheckMacException, NoSuchAlgorithmException, InvalidKeyException {
        System.out.println();
        System.out.println("Compare with check file '" + checkFile + "' ... ");

        System.out.print("\tReading ... ");
        CheckReader cr = new CheckReader(new FileInputStream(new File(checkFile)), algorithm, password);
        cr.read();
        System.out.println(" DONE !");

        return cr;
    }

    /**
     * Create and write the {@code checkFile}
     *
     * @param checkFile File to save
     * @param physicalRoot {@link Folder} to write
     * @param algorithm Algorihm to use
     * @param password Password used to crypt datas
     * @return {@link CheckWriter}
     * @throws FileNotFoundException
     * @throws CheckWriterWritingException
     * @throws CheckMacException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static CheckWriter checkWriter(String checkFile, Folder physicalRoot, MacAlgorithm algorithm, String password) throws FileNotFoundException, CheckWriterWritingException, CheckMacException, NoSuchAlgorithmException, InvalidKeyException {
        System.out.println();
        System.out.print("Writing chech file '" + checkFile + "' ... ");
        CheckWriter cw = new CheckWriter(new FileOutputStream(new File(checkFile)), physicalRoot, algorithm, password);
        cw.write();
        System.out.println(" DONE ! ");

        return cw;
    }
}
