/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.check;

import core.MacAlgorithm;
import core.tree.Folder;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
public class CheckWriter extends CheckMac{
    private OutputStream out;
    private Folder folder;
    
    /**
     * Create a <code>CheckWriter</code> who write a check file for a <code>folder</code> into the {@link OutputStream} <code>out</code>.
     * 
     * @param out the output stream.
     * @param folder the folder.
     * @param algorithm the algorithm used to calculate the Mac hash.
     * @param key the key seed used to calculate the Mac hash.
     */
    public CheckWriter(OutputStream out, Folder folder, MacAlgorithm algorithm, String key){
        super(algorithm, key);
        this.folder = folder;
        this.out = out;
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
                byte[] hash = this.getCheckMac(this.folder);
                oos.writeInt(hash.length);
                oos.write(hash);
                oos.writeObject(this.folder);
                oos.flush();
        } catch (IOException ex) {
            Logger.getLogger(CheckWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
