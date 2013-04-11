/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.tree;

/**
 *  Used to represent a file in a {@link Folder] tree.
 * 
 * @author Karl
 */
public class File {
    private String name;
    private String hash;

    /**
     * Create a {@code File} with a {@code name} and his {@code hash}.
     * 
     * @param name The name of the file.
     * @param hash The hash of the file.
     */
    public File(String name, String hash) {
        this.name = name;
        this.hash = hash;
    }

    /**
     * Create a {@code File} with a {@code name} only, the hash will be {@code null}.
     * 
     * @param name The name of the file.
     */
    public File(String name) {
        this(name, null);
    }
    
    /**
     * Return the name of this {@code File}.
     * 
     * @return The name of this file.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the hash of this {@code File}.
     * 
     * @return The hash of this file.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Change the name of this {@code File}.
     * 
     * @param name The new name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Change the hash of this {@code File}.
     * 
     * @param hash The new hash.
     */
    public void setHash(String hash) {
        this.hash = hash;
    }
}
