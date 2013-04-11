/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.tree;

import java.io.Serializable;
import java.util.Objects;

/**
 *  Used to represent a file in a {@link Folder] tree.
 * 
 * @author Karl
 */
public class HashedFile implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String hash;

    /**
     * Create a {@code HashedFile} with a {@code name} and his {@code hash}.
     * 
     * @param name The name of the file.
     * @param hash The hash of the file.
     */
    public HashedFile(String name, String hash) {
        this.name = name;
        this.hash = hash;
    }

    /**
     * Create a {@code HashedFile} with a {@code name} only, the hash will be {@code null}.
     * 
     * @param name The name of the file.
     */
    public HashedFile(String name) {
        this(name, null);
    }
    
    /**
     * Return the name of this {@code HashedFile}.
     * 
     * @return The name of this file.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the hash of this {@code HashedFile}.
     * 
     * @return The hash of this file.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Change the name of this {@code HashedFile}.
     * 
     * @param name The new name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Change the hash of this {@code HashedFile}.
     * 
     * @param hash The new hash.
     */
    public void setHash(String hash) {
        this.hash = hash;
    }
    
    /**
     * Returns a hash code value for the object. This method is supported for the benefit of hash tables such as those provided by {@link HashMap}.
     * <p/>
     * Two HashedFiles with the same {@code name} and {@code hash} will have the same hashCode.
     * 
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        int tempHash = 5;
        tempHash = 59 * tempHash + Objects.hashCode(this.name);
        tempHash = 59 * tempHash + Objects.hashCode(this.hash);
        return tempHash;
    }

    /**
     * Indicates whether some other object is "equal to" this one. A {@code HashFile} is equals to another if they have the same {@code name} and {@code hash}.
     * 
     * @param obj The reference object with which to compare.
     * @return {true} if the objetcs are "equals".
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HashedFile other = (HashedFile) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.hash, other.hash)) {
            return false;
        }
        return true;
    }
}
