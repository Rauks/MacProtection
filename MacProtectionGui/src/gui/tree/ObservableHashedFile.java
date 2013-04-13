/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.tree;

import core.tree.HashedFile;
import java.util.Objects;
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
    private final SimpleStringProperty checkHash;
    private boolean isValid;
    
    /**
     * Create an observable {@link HashedFile}.
     * 
     * @param file The HashedFile to encapsulate.
     */
    public ObservableHashedFile(HashedFile file) {
        this.name = new SimpleStringProperty(file.getName());
        this.hash = new SimpleStringProperty(file.getHash());
        this.size = new SimpleLongProperty(file.getSize());
        this.checkHash = new SimpleStringProperty();
        this.isValid = true;
    }

    /**
     * Return the hash property.
     * 
     * @return The hash property.
     */
    public String getHash() {
        return this.hash.get();
    }
    
    public void setInvalid(){
        this.isValid = false;
    }
    
    /**
     * Define the {@code check hash} for this ObservableHashedFile
     * 
     * @param check The check hash.
     */
    public void setCheckHash(String check){
        this.checkHash.set(check);
    }
    
    /**
     * Return {@code true} if a check hash has been defined.
     * 
     * @return {@code true} if a check hash has been defined.
     */
    public boolean hasCheckHash(){
        return this.checkHash.isNotNull().get();
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
    
    /**
     * Compare the hash and the checkHash. Return {@code true} if they are the same or if no check hash are defined.
     * 
     * @return {@code true} if the hash and the check hash are the same or if no check hash are defined.
     */
    public boolean isValid(){
        if(!this.isValid){
            return false;
        }
        if(this.checkHash.isNotNull().get()){
            return this.checkHash.get().equals(this.hash.get());
        }
        return true;
    }
    
    /**
     * Returns a hash code value for the object. This method is supported for the benefit of hash tables such as those provided by {@link HashMap}.
     * <p/>
     * Two ObservableHashedFile with contained HashedFile with the same {@code name} will have the same hashCode.
     * 
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.name.get());
        return hash;
    }
    
    /**
     * Indicates whether some other object is "equal to" this one. A {@code ObservableHashedFile} is equals to another if their contained {{@link HashedFile} have the same {@code name} and {@code hash}.
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
        final ObservableHashedFile other = (ObservableHashedFile) obj;
        if (!Objects.equals(this.name.get(), other.name.get())) {
            return false;
        }
        return true;
    }
    
    
}
