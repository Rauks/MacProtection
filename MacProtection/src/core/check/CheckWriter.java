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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
     * @throws CheckMacException In case of check file structure error. The file is not a check file or may be corrupted.
     * @throws CheckWriterWritingException In case of file writing error.
     * @throws NoSuchAlgorithmException If no Provider supports a MacSpi implementation for the specified algorithm.
     * @throws InvalidKeyException If the given key is inappropriate for initializing this Mac.
     * @see java.util.zip.GZIPOutputStream
     */
    public void write() throws CheckWriterWritingException, CheckMacException, NoSuchAlgorithmException, InvalidKeyException{
        try(ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(this.out))){
                byte[] hash = this.getCheckMac(this.folder);
                oos.writeInt(hash.length);
                oos.write(hash);
                oos.writeObject(this.folder);
                oos.flush();
        } catch (IOException ex) {
            throw new CheckWriterWritingException("Error in check file writing.");
        }
    }
}
