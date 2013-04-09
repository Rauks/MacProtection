/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.check;

import core.MacAlgorithm;
import core.tree.Folder;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * Utils to read a {@link Folder} check file.
 * 
 * @see Folder
 * 
 * @author Karl
 */
public class CheckReader extends CheckMac{
    private InputStream in;
    private Folder root;
    
    /**
     * Create a <code>CheckWriter</code> who read an {@link InputStream} to get the datas of a check file and build a {@link Folder} tree.
     * 
     * @param in the input stream.
     * @param algorithm the algorithm used to calculate the Mac hash.
     * @param key the key seed used to calculate the Mac hash.
     */
    public CheckReader(InputStream in, MacAlgorithm algorithm, String key){
        super(algorithm, key);
        this.in = in;
    }
    
    /**
     * Construct a {@link Folder} tree that is retrievable with {@link CheckReader#getRootFolder}.
     * 
     * @throws CheckReaderMacException In case of check file rejection. This append when the inner Mac hash and the calculated hash of differ.
     * @throws CheckReaderReadingException In case of check file structure error. The file is not a check file or may be corrupted.
     */
    public void read() throws CheckReaderMacException, CheckReaderReadingException{
        try(ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(this.in))){
            int hashSize = ois.readInt();
            byte[] filigramHash = new byte[hashSize];
            ois.readFully(filigramHash);
            try {
                Folder f = (Folder) ois.readObject();
                byte[] hash = this.getCheckMac(f);
                if(!Arrays.equals(filigramHash, hash)){
                    throw new CheckReaderMacException("The inner Mac hash is invalid.");
                }
                this.root = f;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(CheckReader.class.getName()).log(Level.WARNING, null, ex);
                throw new CheckReaderReadingException("Error in folder tree reading.");
            }
        } catch (IOException ex) {
            Logger.getLogger(CheckReader.class.getName()).log(Level.WARNING, null, ex);
            throw new CheckReaderReadingException("Error in check file reading.");
        }
    }
    
    /**
     * Returns the builded {@link Folder} tree.
     * 
     * @return the builded {@link Folder} tree.
     * @see CheckReader#read
     */
    public Folder getRootFolder(){
        return this.root;
    }
}
