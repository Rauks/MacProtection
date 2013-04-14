/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.tree;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

/**
 * Used to represent a folder structure. The folders can have sub-folders and files.
 * Each file is represented by his {@code name} and his {@code hash} and each folder by his {@code name}.
 * <p/>
 * The conformity of a folder relative to another folder reference can be checked with {@link #isConformTo}.
 * 
 * @author Karl
 */
public class Folder extends ParentedTreeElement implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private HashSet<Folder> folders;
    private HashSet<HashedFile> files;
    
    private Folder parent = null;
    
    private String name;
    
    /**
     * Create a folder.
     * 
     * @param name The name of the folder.
     */
    public Folder(String name){
        this.name = name;
        this.folders = new HashSet<>();
        this.files = new HashSet<>();
    }
    
    /**
     * Add a sub-folder into the folder.
     * 
     * @param folder The sub-folder to add. 
     */
    public void addFolder(Folder folder) throws TreeElementException{
        folder.setParent(this);
        this.folders.add(folder);
    }
    
    /**
     * Add a {@link HashedFile} into the folder.
     * 
     * @param file The HashedFile to add.
     */
    public void addFile(HashedFile file) throws TreeElementException{
        file.setParent(this);
        this.files.add(file);
    }
    
    /**
     * Get a file of this folder by his name.
     * 
     * @param name The name of the file.
     * @return The file or {@code null} if there is no file with this name.
     */
    public HashedFile getFile(String name){
        for(Iterator<HashedFile> it = this.getFiles().iterator(); it.hasNext();){
            HashedFile f = it.next();
            if(f.getName().equals(name)){
                return f;
            }
        }
        return null;
    }
    
    /**
     * Get the files of this folder with their hash.
     * 
     * @warning Return only the files of the folder, to get the files of the sub-folders recurcively see {@link #getAllFiles}.
     * @return The files.
     * @see HashedFile
     */
    public HashSet<HashedFile> getFiles(){
        return new HashSet<>(this.files);
    }
    
    /**
     * Get the files of this folder with their hash recurcively.
     * 
     * @warning Return all the files recurcively, to get only the files of the folder and not of the sub-folders see {@link #getFiles}.
     * @return The files.
     * @see HashedFile
     */
    public HashSet<HashedFile> getAllFiles(){
        HashSet<HashedFile> out = new HashSet<>();
        out.addAll(this.getFiles());
        for(Iterator<Folder> it = this.getSubFolders().iterator(); it.hasNext();){
            out.addAll(it.next().getAllFiles());
        }
        return out;
    }
    
    /**
     * Get the name of this folder.
     * 
     * @return The name of this folder. 
     */
    public String getName(){
        return this.name;
    }
    
    /**
     * Get a sub-folder by his name.
     * 
     * @param name The name of the sub-folder.
     * @return The sub-folder or {@code null} if there is no sub-folder with this name.
     */
    public Folder getSubFolder(String name){
        for(Iterator<Folder> it = this.getSubFolders().iterator(); it.hasNext();){
            Folder f = it.next();
            if(f.getName().equals(name)){
                return f;
            }
        }
        return null;
    }
    
    /**
     * Get the direct sub-folders of this folder.
     * 
     * @warning Only the direct sub-folders are returned, to get the sub-folders recurcively see {@link #getAllFolders}.
     * @return The sub-folders.
     */
    public HashSet<Folder> getSubFolders(){
        return  new HashSet<>(this.folders);
    }
    
    /**
     * Get all the sub-folders of this folder recurcively.
     * 
     * @warning Return all the sub-folders recurcively, to get only the direct sub-folders see {@link #getFolders}.
     * @return The sub-folders.
     */
    public HashSet<Folder> getAllSubFolders(){
        HashSet<Folder> out = new HashSet<>();
        out.addAll(this.getSubFolders());
        for(Iterator<Folder> it = this.getSubFolders().iterator(); it.hasNext();){
            out.addAll(it.next().getAllSubFolders());
        }
        return out;
    }
    
    /**
     * Check of the folder is conform to an {@code other} folder. 
     * To be conform the folder structure must be the same between the current folder and the {@code other}, 
     * the contained files must have the same {@code names} and the same {@code hash}.
     * 
     * @warning The hashs must have the same representation.
     * @param other The folder used to validate the current folder.
     * @return {@code true} if the current folder is conform.
     */
    public boolean isConformTo(Folder other){
        return this.equals(other);
    }
    
    /**
     * Return all the files and the sub-folders of this folder.
     * 
     * @return All the files and the sub-folders view.
     */
    @Override
    public String toString(){
        return this.name;
    }
    /**
     * Returns a hash code value for the object. This method is supported for the benefit of hash tables such as those provided by {@link HashMap}.
     * <p/>
     * Two Folders with the same {@code name} will have the same hashCode.
     * 
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(this.name);
    }
    
    /**
     * Indicates whether some other object is "equal to" this one. 
     * To be equals, the folder structure must be the same between the current folder and the {@code other}, 
     * the contained files, sub-folders and parents must be equals to.
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
        final Folder other = (Folder) obj;
        if (!Objects.equals(this.folders, other.folders)) {
            return false;
        }
        if (!Objects.equals(this.files, other.files)) {
            return false;
        }
        if (!Objects.equals(this.parent, other.parent)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
}
