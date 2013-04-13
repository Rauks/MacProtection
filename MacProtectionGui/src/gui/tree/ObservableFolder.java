/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.tree;

import core.tree.Folder;
import java.util.HashSet;
import java.util.Iterator;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 *
 * @author Karl
 */
public class ObservableFolder {
    private final SimpleObjectProperty<Folder> folder;
    private final HashSet<ObservableHashedFile> files;
    private boolean isValid;

    public ObservableFolder(Folder folder) {
        this.folder = new SimpleObjectProperty(folder);
        this.files = new HashSet<>();
        this.isValid = true;
    }
    
    public void setInvalide(){
        this.isValid = false;
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
    
    @Override
    public String toString(){
        return this.folder.get().getName();
    }
}
