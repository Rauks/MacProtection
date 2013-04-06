/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
        String algorithm = "HmacSHA256";
        
        try(MacInputStream mis = new MacInputStream(FileUtils.openInputStream(dirToScan), algorithm, key.getBytes())){
            mis.readAll();
            System.out.println(mis.getMacString());
            System.out.println("");
        } catch (IOException | NoSuchAlgorithmException ex) {
            Logger.getLogger(MacProtection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Collection<File> files = FileUtils.listFiles(dirToScan, new RegexFileFilter("^(.*?)"), DirectoryFileFilter.DIRECTORY);
        for(Iterator<File> it = files.iterator(); it.hasNext();){
            File f = it.next();
            System.out.println(f);
            try(MacInputStream mis = new MacInputStream(new FileInputStream(f), algorithm, key.getBytes())){
                mis.readAll();
                System.out.println(mis.getMacString());
                System.out.println("");
            } catch (IOException | NoSuchAlgorithmException ex) {
                Logger.getLogger(MacProtection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
