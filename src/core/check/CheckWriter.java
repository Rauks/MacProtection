/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.check;

import core.MacAlgorithm;
import core.MacInputStream;
import core.tree.Folder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

/**
 * Utils to write a {@link Folder} check file.
 * 
 * @see Folder
 * 
 * @author Karl
 */
public class CheckWriter {
    private OutputStream out;
    private Folder folder;
    private MacAlgorithm algorithm;
    private String key;
    
    /**
     * Create a <code>CheckWriter</code> who write a check file for a <code>folder</code> into the {@link OutputStream} <code>out</code>.
     * 
     * @param out the output stream.
     * @param folder the folder.
     */
    public CheckWriter(OutputStream out, Folder folder, MacAlgorithm algorithm, String key){
        this.out = out;
        this.folder = folder;
        this.algorithm = algorithm;
        this.key = key;
    }
    
    /**
     * Write the check file.
     * <p>
     * The folder is serialized, the check Mac hash is added. The output stream is GZIPed.
     * 
     * @see GZIPOutputStream
     */
    public void write(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(this.out))){
                oos.write(this.getCheckMac());
                oos.writeObject(this.folder);
                oos.flush();
        } catch (IOException ex) {
            Logger.getLogger(CheckWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Get the Mac hash used to protect the check file.
     * 
     * @return the Mac hash.
     * @see javax.crypto.Mac
     */
    private byte[] getCheckMac(){
        try {
            File temp = File.createTempFile("mac", null);
            temp.deleteOnExit();
            try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(temp))){
                oos.writeObject(this.folder);
                oos.flush();
            }
            try(MacInputStream mis = new MacInputStream(new FileInputStream(temp), this.algorithm, this.key.getBytes())){
                return mis.getMacBytes();
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(CheckWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(CheckWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
