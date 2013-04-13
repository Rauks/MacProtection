/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.tree;

import core.tree.Folder;
import java.util.HashSet;
import java.util.Objects;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Karl
 */
public class ObservableFolder {
    private final SimpleObjectProperty<Folder> folder;
    private final HashSet<ObservableHashedFile> files;
    private boolean isValid;
    private boolean isAdded;
    private boolean isDeleted;

    public ObservableFolder(Folder folder) {
        this.folder = new SimpleObjectProperty(folder);
        this.files = new HashSet<>();
        this.isValid = true;
        this.isAdded = false;
        this.isDeleted = false;
    }
    
    public void setInvalide(){
        this.isValid = false;
    }
    
    public void setAdded(){
        this.isDeleted = false;
        this.isAdded = true;
    }
    
    public void setDeleted(){
        this.isDeleted = true;
        this.isAdded = false;
    }
    
    public Folder getFolder(){
        return this.folder.get();
    }
    
    public void addObservableHashedFile(ObservableHashedFile file){
        this.files.add(file);
    }
    
    public HashSet<ObservableHashedFile> getObservableFiles(){
        return this.files;
    }
    
    public boolean isValid(){
        return this.isValid;
    }
    
    public boolean isDeleted(){
        return this.isDeleted;
    }
    
    public boolean isAdded(){
        return this.isAdded;
    }
    
    /**
     * Returns a hash code value for the object. This method is supported for the benefit of hash tables such as those provided by {@link HashMap}.
     * <p/>
     * Two ObservableFolder with contained Folders with the same {@code name} will have the same hashCode.
     * 
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.folder.getName());
        return hash;
    }
    
    /**
     * Indicates whether some other object is "equal to" this one. 
     * To be equals, the contained {{@link Folder} name must be the same between the current folder and the {@code other}.
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
        final ObservableFolder other = (ObservableFolder) obj;
        if (!Objects.equals(this.folder.getName(), other.folder.getName())) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString(){
        return this.folder.get().getName();
    }
}
