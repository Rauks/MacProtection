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
     * Read all the datas of the {@link InputStream} and calculate the MAC hash.
     * <p>
     * Use {@link #getMacBytes()} or {@link #getMacString()} in order to retrieve the MAC hash value. 
     * 
     * @return the number of bytes readed.
     */
    public int readAll() {
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
    
    /**
     * Reads up to <code>len</code> bytes of data from the input stream into an array of bytes.
     * An attempt is made to read as many as <code>len</code> bytes, but a smaller number may be read.
     * The number of bytes actually read is returned as an integer.
     * <p>
     * This method blocks until input data is available, end of file is detected, or an exception is thrown.
     * <p>
     * The MAC hash is updated.
     * <p>
     * Use {@link #getMacBytes()} or {@link #getMacString()} in order to retrieve the partial MAC hash value. 
     * 
     * @param b the buffer into which the data is read.
     * @param off the start offset in array <code>b</code> at which the data is written.
     * @param len the maximum number of bytes to read.
     * @return the total number of bytes read into the buffer, or <code>-1</code> if there is no more data because the end of the stream has been reached.
     * @throws IOException if an I/O error occurs.
     * @see java.io.InputStream#read()
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int readed = in.read(b, off, len);
        if (readed != -1) {
            mac.update(b, off, readed);
        }
        return readed;
    }
    
    /**
     * Reads the next byte of data from the input stream. The value byte is
     * returned as an <code>int</code> in the range <code>0</code> to
     * <code>255</code>.
     * <p>
     * The MAC hash is updated.
     * <p>
     * Use {@link #getMacBytes()} or {@link #getMacString()} in order to retrieve the partial MAC hash value. 
     * 
     * @return the next byte of data, or -1 if the end of the stream is reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read() throws IOException {
        int readed = in.read();
        if (readed != -1) {
            mac.update((byte) readed);
        }
        return readed;
    }
}
