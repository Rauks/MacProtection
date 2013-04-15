/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package usecase;

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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Karl
 */
public class MacProtection {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File dirToScan = new File(".");
        String key = "testKey";
        MacAlgorithm algorithm = MacAlgorithm.HmacSHA256;
            
        try {
            //Processing a physical repertory
            MacProcessor p = new MacProcessor(dirToScan, algorithm, key, MacProcessor.MacOutput.HEXADECIMAL);
            p.addMacProcessorListener(new MacProcessorListener() {
                @Override
                public void macProcessorPerformed(MacProcessorEvent evt) {
                    System.out.println(evt.getState() + " : " + evt.getProcessedFiles() + "/" + evt.getTotalFiles());
                }
            });
            p.process();
            Folder physicalRoot = p.getResult();
            
            System.out.println();
            System.out.println(physicalRoot);
            
            //Creation of check file for the folder
            try {
                System.out.println("WRITING CHECK FILE...");
                CheckWriter cw = new CheckWriter(new FileOutputStream(new File("check.test")), physicalRoot, algorithm, key);
                cw.write();
                System.out.println("DONE.");
            } catch (CheckWriterWritingException | CheckMacException | FileNotFoundException ex) {
                Logger.getLogger(MacProtection.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //Get a folder tree from the validation file
            Folder validationFolder = null;
            try {
                System.out.println("READING CHECK FILE...");
                CheckReader cr = new CheckReader(new FileInputStream(new File("check.test")), algorithm, key);
                cr.read();
                validationFolder = cr.getRootFolder();
                System.out.println("DONE.");
            } catch (CheckMacException | CheckReaderReadingException | FileNotFoundException ex) {
                Logger.getLogger(MacProtection.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //Physical directory validation
            System.out.println("VALIDATION RESULT : ");
            System.out.println(physicalRoot.isConformTo(validationFolder));
            
        } catch (MacProcessorException ex) {
            Logger.getLogger(MacProtection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
