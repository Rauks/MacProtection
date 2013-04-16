/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.check;

import core.MacAlgorithm;
import core.MacInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Karl
 */
public abstract class CheckMac {
    private MacAlgorithm algorithm;
    private String key;
    
    public CheckMac(MacAlgorithm algorithm, String key){
        this.algorithm = algorithm;
        this.key = key;
    }
    
    /**
     * Get the Mac hash used to protect a serializable object.
     * 
     * @param object The serializable object.
     * @return The Mac hash in bytes array form.
     * @see javax.crypto.Mac
     */
    protected byte[] getCheckMac(Serializable object) throws IOException, NoSuchAlgorithmException, InvalidKeyException{
        File temp = File.createTempFile("mac", null);
        temp.deleteOnExit();
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(temp))){
            oos.writeObject(object);
            oos.flush();
        }
        try(MacInputStream mis = new MacInputStream(new FileInputStream(temp), this.algorithm, this.key.getBytes())){
            return mis.getMacBytes();
        }
    }
}
