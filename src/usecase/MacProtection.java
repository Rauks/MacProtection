/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package usecase;

import core.MacAlgorithm;
import core.MacProcessor;
import core.check.CheckWriter;
import core.tree.Folder;
import java.io.File;
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
        
        MacProcessor p = new MacProcessor(dirToScan, algorithm, key, MacProcessor.MacOutput.HEXADECIMAL);
        p.process();
        Folder root = p.getResult();
        
        System.out.println(root);
        
        try {
            System.out.println("Writing check file...");
            CheckWriter cw = new CheckWriter(new FileOutputStream(new File("check.test")), root, algorithm, key);
            cw.write();
            System.out.println("Done.");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MacProtection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
