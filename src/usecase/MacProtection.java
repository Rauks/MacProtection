/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package usecase;

import core.MacAlgorithm;
import core.MacProcessor;
import core.tree.Folder;
import java.io.File;
import java.util.Collection;
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
        MacAlgorithm algorithm = MacAlgorithm.HmacSHA256;
        
        MacProcessor p = new MacProcessor(dirToScan, algorithm, key, MacProcessor.MacOutput.HEXADECIMAL);
        p.process();
        Folder root = p.getResult();
        
        System.out.println(root);
    }
}