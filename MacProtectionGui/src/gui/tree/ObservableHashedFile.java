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
    private CheckingFlag flag;
    
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
        this.flag = CheckingFlag.VALID;
    }

    /**
     * Return the hash property.
     * 
     * @return The hash property.
     */
    public String getHash() {
        return this.hash.get();
    }
    
    public void setFlag(CheckingFlag flag){
        this.flag = flag;
    }
    
    /**
     * Define the {@code check hash} for this ObservableHashedFile
     * 
     * @param check The check hash.
     */
    public void setCheckHash(String check){
        this.checkHash.set(check);
        if(!this.hash.get().equals(check)){
            this.flag = CheckingFlag.INVALID;
        }
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
    
    public CheckingFlag getFlag(){
        return this.flag;
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
        int tempHash = 7;
        tempHash = 61 * tempHash + Objects.hashCode(this.name.get());
        return tempHash;
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
