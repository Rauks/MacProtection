/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.tree;

import java.util.Iterator;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Karl
 */
public class ObservableFolder {
    private final SimpleStringProperty name;
    private final SimpleSetProperty<ObservableHashedFile> files;
    private final SimpleSetProperty<ObservableFolder> folders;

    public ObservableFolder(SimpleStringProperty name) {
        this.name = name;
        this.files = new SimpleSetProperty<>();
        this.folders = new SimpleSetProperty<>();
    }
    
    public void addObservableHashedFile(ObservableHashedFile file){
        this.files.add(file);
    }
    
    public void addObservableFolder(ObservableFolder folder){
        this.folders.add(folder);
    }
    
    public boolean isValid(){
        for(Iterator<ObservableHashedFile> it = this.files.iterator(); it.hasNext();){
            ObservableHashedFile file = it.next();
            if(!file.isValid()){
                return false;
            }
        }
        for(Iterator<ObservableFolder> it = this.folders.iterator(); it.hasNext();){
            ObservableFolder folder = it.next();
            if(!folder.isValid()){
                return false;
            }
        }
        return true;
    }
}
