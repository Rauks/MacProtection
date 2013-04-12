/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.tree;

import core.tree.HashedFile;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * A {@link HashedFile} observator providing JavaFX properties for gui binding.
 * 
 * @see javafx.beans.property.Property
 * 
 * @author Karl
 */
public class ObservableHashedFile {
    private final SimpleStringProperty hash;
    private final SimpleStringProperty name;
    private final SimpleLongProperty size;
    
    /**
     * Create an observable {@link HashedFile}.
     * 
     * @param file The HashedFile to encapsulate.
     */
    public ObservableHashedFile(HashedFile file) {
        this.name = new SimpleStringProperty(file.getName());
        this.hash = new SimpleStringProperty(file.getHash());
        this.size = new SimpleLongProperty(file.getSize());
    }

    /**
     * Return the hash property.
     * 
     * @return The hash property.
     */
    public String getHash() {
        return this.hash.get();
    }

    /**
     * Return the name property.
     * 
     * @return The name property.
     */
    public String getName() {
        return this.name.get();
    }

    /**
     * Return the size property.
     * 
     * @return The size property.
     */
    public long getSize() {
        return this.size.get();
    }
}
