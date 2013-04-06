/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Karl
 */
public class MacInputStream extends FilterInputStream{
    private Mac mac;
    private Mac macMarkClone;
    
    /**
     * Create a new MacInputStream from an {@link InputStream}. The seed is using to initialize the secret key.
     * <p>
     * The MAC hash algorithm used is <code>HmacSHA256</code>.
     * 
     * @param is the input stream.
     * @param seed the seed for the secret key.
     * @see java.io.FilterInputStream#in
     * @see javax.crypto.spec.SecretKeySpec
     * @see javax.crypto.Mac
     */
    public MacInputStream(InputStream is, byte[] seed){
        super(is);
        try {
            this.mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec ks = new SecretKeySpec(seed, "HmacSHA256");
            mac.init(ks);
        } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
            Logger.getLogger(MacInputStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Return the MAC hash on a bytes array form.
     * 
     * @return the MAC hash.
     * @see javax.crypto.Mac
     */
    public byte[] getMacBytes(){
        return this.mac.doFinal();
    }
    
    /**
     * Return the MAC hash on a base64 form.
     * 
     * @return the MAC hash.
     * @see javax.crypto.Mac
     */
    public String getMacString(){
        return Base64.encodeBase64String(this.getMacBytes());
    }
    
    /**
     * Marks the current position in this input stream.
     * A subsequent call to the <code>reset</code> method repositions this stream at the last marked position so that subsequent reads re-read the same bytes.
     * <p>
     * The <code>readlimit</code> argument tells this input stream to allow that many bytes to be read before the mark position gets invalidated.
     *
     * @param readlimit the maximum limit of bytes that can be read before the mark position becomes invalid.
     * @see java.io.FilterInputStream#in
     * @see java.io.FilterInputStream#reset()
     */
    @Override
    public void mark(int readLimit){
        if(super.markSupported()){
            super.mark(readLimit);
        }
        try {
            this.macMarkClone = (Mac) this.mac.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(MacInputStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Repositions this stream to the position at the time the
     * <code>mark</code> method was last called on this input stream.
     *
     * @exception  IOException  if the stream has not been marked or if the mark has been invalidated.
     * @see        java.io.FilterInputStream#in
     * @see        java.io.FilterInputStream#mark(int)
     */
    @Override
    public void reset() throws IOException{
        super.reset();
        try {
            this.mac = (Mac) this.macMarkClone.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(MacInputStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Read the {@link InputStream} and calculate the MAC hash.
     * <p>
     * Use {@link #getMacBytes()} or {@link #getMacString()} in order to retrieve the MAC hash value. 
     * 
     * @return the number of bytes readed.
     */
    @Override
    public int read() {
        int read = 0;
        try {
            byte[] buffer = new byte[1024];
            BufferedInputStream bin = new BufferedInputStream(this.in);
            int nl;
            while((nl = bin.read()) != -1){
                read += nl;
                mac.update(buffer, 0, nl);
            }
        } catch (IOException ex) {
            Logger.getLogger(MacInputStream.class.getName()).log(Level.SEVERE, null, ex);
        }
        return read;
    }
}
