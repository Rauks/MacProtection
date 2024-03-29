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
 * A {@code MacInputStream} contains some other input stream, which it uses as its basic source of data.
 * A Mac hash is calculated during the input stream reading and can be retrieved with {@link #getMacBytes}, {@link #getMacHex} or {@link #getMacBase64}.
 * 
 * @see javax.crypto.Mac
 * @see InputStream
 * 
 * @author Karl
 */
public class MacInputStream extends FilterInputStream{
    private Mac mac;
    private Mac macMarkClone;

    /**
     * Create a new {@code MacInputStream} from an {@link InputStream}. The seed is using to initialize the secret key.
     * 
     * @param is The input stream.
     * @param algorithm The Mac algorithm used to calculate the Mac hash.
     * @param seed The seed for the secret key used to calculate the Mac hash.
     * @throws NoSuchAlgorithmException If no Provider supports a MacSpi implementation for the specified algorithm.
     * @throws InvalidKeyException If the given key is inappropriate for initializing this Mac.
     * @see javax.crypto.Mac
     */
    public MacInputStream(InputStream is, MacAlgorithm algorithm, byte[] seed) throws NoSuchAlgorithmException, InvalidKeyException{
        super(is);
        this.mac = Mac.getInstance(algorithm.toString());
        SecretKeySpec ks = new SecretKeySpec(seed, algorithm.toString());
        this.mac.init(ks);
    }
    
    /**
     * Return the Mac hash on a bytes array form.
     * 
     * @return The Mac hash.
     * @see javax.crypto.Mac
     * @see java.io.InputStream
     */
    public byte[] getMacBytes(){
        return this.mac.doFinal();
    }
    
    /**
     * Return the Mac hash on a base64 form.
     * 
     * @return The Mac hash.
     * @see javax.crypto.Mac
     */
    public String getMacBase64(){
        return Base64.encodeBase64String(this.getMacBytes());
    }
    
    /**
     * Return the Mac hash on a hexadecimal form.
     * 
     * @return The Mac hash.
     * @see javax.crypto.Mac
     */
    public String getMacHex(){
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        byte[] macBytes = this.getMacBytes();
        char[] hexChars = new char[macBytes.length * 2];
        int value;
        for (int i = 0; i < macBytes.length; i++) {
            value = macBytes[i] & 0xff;
            hexChars[i * 2] = hexArray[value >>> 4];
            hexChars[i * 2 + 1] = hexArray[value & 0x0f];
        }
        return String.valueOf(hexChars);
    }
    
    /**
     * Marks the current position in this input stream.
     * A subsequent call to the {@link #reset} method repositions this stream at the last marked position so that subsequent reads re-read the same bytes.
     * <p/>
     * The {@code readlimit} argument tells this input stream to allow that many bytes to be read before the mark position gets invalidated.
     *
     * @param readlimit The maximum limit of bytes that can be read before the mark position becomes invalid.
     * @see java.io.FilterInputStream#in
     * @see java.io.FilterInputStream#reset
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
     * Repositions this stream to the position at the time the {@link #mark} method was last called on this input stream.
     *
     * @exception IOException If the stream has not been marked or if the mark has been invalidated.
     * @see java.io.FilterInputStream#in
     * @see java.io.FilterInputStream#mark(int)
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
     * Read all the datas of the {@link InputStream} and calculate the Mac hash.
     * <p/>
     * Use {@link #getMacBytes} or {@link #getMacString} in order to retrieve the Mac hash value. 
     * 
     * @return the number of bytes readed.
     * @see #read
     */
    public int readAll() {
        int read = 0;
        try {
            byte[] buffer = new byte[1024];
            BufferedInputStream bin = new BufferedInputStream(this.in);
            int nl;
            while((nl = bin.read()) != -1){
                read += nl;
                this.mac.update(buffer, 0, nl);;
            }
        } catch (IOException ex) {
            Logger.getLogger(MacInputStream.class.getName()).log(Level.SEVERE, null, ex);
        }
        return read;
    }
    
    /**
     * Reads up to {@code len} bytes of data from the input stream into an array of bytes.
     * An attempt is made to read as many as {@code len} bytes, but a smaller number may be read.
     * The number of bytes actually read is returned as an integer.
     * <p/>
     * This method blocks until input data is available, end of file is detected, or an exception is thrown.
     * <p/>
     * The Mac hash is updated.
     * <p/>
     * Use {@link #getMacBytes()} or {@link #getMacString()} in order to retrieve the partial Mac hash value. 
     * 
     * @param b The buffer into which the data is read.
     * @param off The start offset in array {@code b} at which the data is written.
     * @param len The maximum number of bytes to read.
     * @return The total number of bytes read into the buffer, or {@code -1} if there is no more data because the end of the stream has been reached.
     * @throws IOException If an I/O error occurs.
     * @see #read
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int readed = this.in.read(b, off, len);
        if (readed != -1) {
            this.mac.update(b, off, readed);
        }
        return readed;
    }
    
    /**
     * Reads the next byte of data from the input stream.
     * The value byte is returned as an {@code int} in the range {@code 0} to {@code 255}.
     * <p/>
     * The Mac hash is updated.
     * <p/>
     * Use {@link #getMacBytes} or {@link #getMacString} in order to retrieve the partial Mac hash value. 
     * 
     * @return The next byte of data, or {@code -1} if the end of the stream is reached.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public int read() throws IOException {
        int readed = this.in.read();
        if (readed != -1) {
            this.mac.update((byte) readed);
        }
        return readed;
    }
    
    /**
     * Reads some number of bytes from the input stream and stores them into the buffer array {@code b}.
     * The number of bytes actually read is returned as an integer. 
     * This method blocks until input data is available, end of file is detected, or an exception is thrown.
     * <p/>
     * If the length of {@code b} is zero, then no bytes are read and {@code 0} is returned; 
     * otherwise, there is an attempt to read at least one byte.
     * If no byte is available because the stream is at the end of the file, the value {@code -1} is returned;
     * otherwise, at least one byte is read and stored into {@code b}.
     * <p/>
     * The Mac hash is updated.
     * <p/>
     * Use {@link #getMacBytes} or {@link #getMacString} in order to retrieve the partial Mac hash value. 
     * 
     * @param b The buffer into which the data is read.
     * @return The total number of bytes read into the buffer, or <code>-1</code> if there is no more data because the end of the stream has been reached.
     * @throws IOException If an I/O error occurs.
     * @see #read
     */
    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }
}
