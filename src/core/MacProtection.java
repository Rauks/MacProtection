/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

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
        MacAlgorithm algorithm = MacAlgorithm.HmacSHA512;
        
        try(MacInputStream mis = new MacInputStream(FileUtils.openInputStream(dirToScan), algorithm, key.getBytes())){
            mis.readAll();
            System.out.println(mis.getMacBase64());
            System.out.println("");
        } catch (IOException | NoSuchAlgorithmException ex) {
            Logger.getLogger(MacProtection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        MacProcessor p = new MacProcessor(dirToScan, algorithm, key, MacProcessor.MacOutput.BASE64);
        System.out.println(p);
    }
}
