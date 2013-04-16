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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
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
     * Create a {@code CheckWriter} who can read in an {@link InputStream} to get the datas of a check file and build a {@link Folder} tree.
     * 
     * @param in The input stream.
     * @param algorithm The Mac algorithm used in the check file writing process.
     * @param key The password used in the check file writing process.
     * @see CheckWriter
     */
    public CheckReader(InputStream in, MacAlgorithm algorithm, String key){
        super(algorithm, key);
        this.in = in;
    }
    
    /**
     * Construct a {@link Folder} tree from the check file that is retrievable with {@link CheckReader#getRootFolder}.
     * 
     * @throws CheckReaderMacException In case of check file rejection. This append when the inner Mac hash and the calculated verification Mac hash differ.
     * @throws CheckReaderReadingException In case of check file structure error. The file is not a check file or may be corrupted.
     * @throws NoSuchAlgorithmException If no Provider supports a MacSpi implementation for the specified algorithm.
     * @throws InvalidKeyException If the given key is inappropriate for initializing this Mac.
     */
    public void read() throws CheckReaderReadingException, CheckMacException, NoSuchAlgorithmException, InvalidKeyException{
        try(ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(this.in))){
            int hashSize = ois.readInt();
            byte[] filigramHash = new byte[hashSize];
            ois.readFully(filigramHash);
            try {
                Folder f = (Folder) ois.readObject();
                byte[] hash = this.getCheckMac(f);
                if(!Arrays.equals(filigramHash, hash)){
                    throw new CheckMacException("Invalid check file Mac hash.");
                }
                this.root = f;
            } catch (CheckMacException ex) {
                throw new CheckMacException("Invalid check file Mac hash.");
            } catch (ClassNotFoundException ex) {
                throw new CheckReaderReadingException("Error in check file reading.");
            }
        } catch (IOException ex) {
            throw new CheckReaderReadingException("Error in check file reading.");
        }
    }
    
    /**
     * Returns the builded {@link Folder} tree from the check file.
     * 
     * @return The builded {@link Folder} tree.
     * @see CheckReader#read
     */
    public Folder getRootFolder(){
        return this.root;
    }
}
