/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.tree;

import java.io.Serializable;

/**
 *
 * @author Karl
 */
abstract class ParentedTreeElement implements Serializable {
    private Folder parent = null;
    
    /**
     * Return the parent of this element.
     * 
     * @return The parent of this element.
     */
    public Folder getParent(){
        return this.parent;
    }
    
    /**
     * Set the parent of this element.
     * 
     * @param parent The designed parent for this element.
     */
    protected void setParent(Folder parent) throws TreeElementException{
        if(this.parent != null){
            throw new TreeElementException("The element can have only one parent.");
        }
        this.parent = parent;
    }
}
