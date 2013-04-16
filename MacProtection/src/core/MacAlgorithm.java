/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * List and use of the {@link javax.crypto.Mac} usable algorithms.
 * 
 * @author Karl
 */
public class MacAlgorithm {
    /**
     * A {@link List} containing the available Mac algorithms.
     */
    public static List<String> AVAILABLE_ALGORITHMS = getAlgorithms();
    
    private String algorithm;
    
    /**
     * Construct a MacAlgorithm by his algorithms name.
     * 
     * @param algorithm The Hmac algorithms name.
     */
    public MacAlgorithm(String algorithm) throws MacAlgorithmException{
        if(!AVAILABLE_ALGORITHMS.contains(algorithm)){
            throw new MacAlgorithmException("Mac algorithm unavailable.");
        }
        this.algorithm = algorithm;
    }
    
    /**
     * Returns a {@link List} containing the available Mac algorithms.
     * 
     * @return A set containing the available Mac algorithms.
     */
    private static List<String> getAlgorithms(){
        Provider[] providers = Security.getProviders();
        List<String> macs = new ArrayList<>();
        
        for (int i = 0; i != providers.length; i++) {
            for(Iterator<Object> it = providers[i].keySet().iterator(); it.hasNext();){
                String entry = (String) it.next();
                if (entry.startsWith("Mac.") && entry.endsWith(" SupportedKeyFormats")) {
                    entry = entry.substring(4);
                    macs.add(entry.substring(0, entry.length() - 20));
                }
            }
        }
        Collections.sort(macs);
        return macs;
    }
    
    /**
     * Returns the string form of this Hmac algoritm.
     * 
     * @return The Hmac algorithms.
     */
    @Override
    public String toString(){
        return this.algorithm;
    }
}
