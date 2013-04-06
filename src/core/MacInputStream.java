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
    private Mac macDonald;
    
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
    
    public byte[] getMacBytes(){
        return this.mac.doFinal();
    }
    public String getMacString(){
        return Base64.encodeBase64String(this.getMacBytes());
    }
    
    @Override
    public void mark(int readLimit){
        if(super.markSupported()){
            super.mark(readLimit);
        }
        try {
            this.macDonald = (Mac) this.mac.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(MacInputStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void reset() throws IOException{
        super.reset();
        try {
            this.mac = (Mac) this.macDonald.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(MacInputStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
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
