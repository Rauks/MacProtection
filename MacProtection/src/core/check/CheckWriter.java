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
     * Create a {@link CheckWriter} who write a check file based on a {@link folder} into an {@link OutputStream}.
     * 
     * @param out The check file output stream.
     * @param folder The folder.
     * @param algorithm The Mac algorithm used to certifiate the check file.
     * @param key The password used to certifiate the check file.
     */
    public CheckWriter(OutputStream out, Folder folder, MacAlgorithm algorithm, String key){
        super(algorithm, key);
        this.folder = folder;
        this.out = out;
    }
    
    /**
     * Write the check file into the {@link OutputStream}.
     * <p/>
     * The folder is serialized, the validation Mac hash is added. The output stream is GZIPed.
     * 
     * @see java.util.zip.GZIPOutputStream
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