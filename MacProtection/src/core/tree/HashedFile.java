/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.tree;

import java.io.File;
import java.io.Serializable;
import java.util.Objects;

/**
 *  Used to represent a file in a {@link Folder] tree.
 * 
 * @author Karl
 */
public class HashedFile extends ParentedTreeElement implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String hash;
    private long size;

    /**
     * Create a {@code HashedFile} from a {@code file} and his {@code hash}.
     * 
     * @param name The name of the file.
     * @param hash The hash of the file.
     */
    public HashedFile(File file, String hash) {
        this.name = file.getName();
        this.size = file.length();
        this.hash = hash;
    }

    /**
     * Create a {@code HashedFile} from a {@code file}, the hash will be {@code null}.
     * 
     * @param name The name of the file.
     */
    public HashedFile(File file) {
        this(file, null);
    }
    
    /**
     * Create a {@code HashedFile} from another {@code hashedFile}. Can be used to make a copy of a {@code HashedFile}.
     * 
     * @param name The name of the file.
     */
    public HashedFile(HashedFile hashedFile) {
        this.name = hashedFile.name;
        this.size = hashedFile.size;
        this.hash = hashedFile.hash;
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
     * Return the size of this {@code HashedFile}.
     * 
     * @return The size of this file.
     */
    public long getSize() {
        return size;
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
     * Change the size of this {@code HashedFile}.
     * 
     * @param size The new size.
     */
    public void setName(long size) {
        this.size = size;
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
        return Objects.hashCode(this.name);
    }

    /**
     * Indicates whether some other object is "equal to" this one. A {@code HashedFile} is equals to another if they have the same {@code name} and {@code hash}.
     * 
     * @warning The parents are not checked. Two equals {@code HashedFile} with differents parents can be equals.
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
        if (!Objects.equals(this.size, other.size)) {
            return false;
        }
        return true;
    }
}