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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

/**
 *
 * @author Karl
 */
public class MacProcessor {
    public enum MacOutput{BASE64, HEXADECIMAL}
    
    private HashMap<String, String> macMap;
    
    /**
     * Create a MacProcessor who will scan the folder <code>dirToScan</code>, retrieves all the files and calculate the Mac hash of each file.
     * The Mac algorithm used is <code>algorithm</code> and the secret key seed used is <code>key</code>.
     * <p>
     * The mac is saved in <code>macOutput</code> form.
     * 
     * @param dirToScan the folder containing the files and sub-folders to be scanned.
     * @param algorithm the algorithm used to calculate the Mac hash.
     * @param key the key seed used to calculate the Mac hash.
     * @param macOutput the Mac hash output form.
     * @see MacAlgorithm
     * @see MacOutput
     */
    public MacProcessor(File dirToScan, MacAlgorithm algorithm, String key, MacOutput macOutput){
        this.macMap = new HashMap<>();
        Collection<File> files = FileUtils.listFiles(dirToScan, new RegexFileFilter("^(.*?)"), DirectoryFileFilter.DIRECTORY);
        for(Iterator<File> it = files.iterator(); it.hasNext();){
            File f = it.next();
            try(MacInputStream mis = new MacInputStream(new FileInputStream(f), algorithm, key.getBytes())){
                mis.readAll();
                switch(macOutput){
                    case BASE64:
                        this.macMap.put(f.getPath(), mis.getMacBase64());
                        break;
                    case HEXADECIMAL:
                        this.macMap.put(f.getPath(), mis.getMacHex());
                        break;
                }
            } catch (IOException | NoSuchAlgorithmException ex) {
                Logger.getLogger(MacProtection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Return the files paths and the Mac hash calculated for the file.
     * 
     * @return 
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(Iterator<Entry<String, String>> it = this.macMap.entrySet().iterator(); it.hasNext();){
            Entry e = it.next();
            sb.append(e.getKey()).append('\n').append(e.getValue()).append('\n').append('\n');
        }
        return sb.toString();
    }
}
