/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tree;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Karl
 */
public class ObservableHashedFile {
    private final SimpleStringProperty hash;
    private final SimpleStringProperty name;
    
    public ObservableHashedFile(String name, String hash) {
        this.name = new SimpleStringProperty(name);
        this.hash = new SimpleStringProperty(hash);
    }

    public String getHash() {
        return this.hash.get();
    }

    public String getName() {
        return this.name.get();
    }
}
