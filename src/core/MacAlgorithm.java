/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Karl
 */
public enum MacAlgorithm {
    HmacMD5("HmacMD5"),
    HmacSHA1("HmacSHA1"),
    HmacSHA256("HmacSHA256"),
    HmacSHA384("HmacSHA384"),
    HmacSHA512("HmacSHA512");
    
    private String algorithm;
    
    /**
     * Construct the enum values with their algorithms.
     * 
     * @param algorithm the Hmac algorithms as defined in RFC 2104.
     */
    private MacAlgorithm(String algorithm){
        this.algorithm = algorithm;
    }
    
    /**
     * Returns an {@link Set} containing the constants of this enum.
     * 
     * @return a set containing the constants of this enum.
     */
    public static Set<MacAlgorithm> getAlgorithms(){
        return new HashSet<>(Arrays.asList(MacAlgorithm.values()));
    }
    
    /**
     * Returns the string form of this Hmac algoritm as defined in RFC 2104.
     * 
     * @return the Hmac algorithms as defined in RFC 2104.
     */
    @Override
    public String toString(){
        return this.algorithm;
    }
}
