/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.tree;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Used to represent a folder structure. The folders can have sub-folders and files.
 * Each file is represented by his {@code name} and his {@code hash} and each folder by his {@code name}.
 * <p/>
 * The conformity of a folder relative to another folder reference can be checked with {@link #isConformTo}.
 * 
 * @author Karl
 */
public class Folder implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private HashSet<Folder> folders;
    private HashMap<String, String> files;
    
    private String name;
    
    /**
     * Create a folder.
     * 
     * @param name The name of the folder.
     */
    public Folder(String name){
        this.name = name;
        this.folders = new HashSet<>();
        this.files = new HashMap<>();
    }
    
    /**
     * Add a sub-folder into the folder;
     * 
     * @param folder The sub-folder to add. 
     */
    public void addFolder(Folder folder){
        this.folders.add(folder);
    }
    
    /**
     * Add a file into the folder
     * 
     * @param name The name of the file to add.
     * @param hash The hash of the file.
     */
    public void addFile(String name, String hash){
        this.files.put(name, hash);
    }
    
    /**
     * Get the hash of a file by his name.
     * 
     * @param name The name of the file.
     * @return The hash of the file or {@code null} if there is no file with this name.
     */
    public String getHash(String name){
        return this.files.get(name);
    }
    
    /**
     * Get the files of this folder with their hash.
     * 
     * @warning Return only the files of the folder, to get the files of the sub-folders recurcively see {@link #getAllFiles}.
     * @return The files.
     */
    public HashMap<String, String> getFiles(){
        return this.files;
    }
    
    /**
     * Get the files of this folder with their hash recurcively.
     * 
     * @warning Return all the files recurcively, to get only the files of the folder and not of the sub-folders see {@link #getFiles}.
     * @return The files.
     */
    public HashMap<String, String> getAllFiles(){
        HashMap<String, String> out = new HashMap<>();
        out.putAll(this.getFiles());
        for(Iterator<Folder> it = this.getSubFolders().iterator(); it.hasNext();){
            out.putAll(it.next().getAllFiles());
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
        return this.folders;
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
        //Same folder name
        if(!this.getName().equals(other.getName())){
            return false;
        }
        //Same number of files
        if(this.getFiles().size() != other.getFiles().size()){
            return false;
        }
        //Same number of sub-folders
        if(this.getSubFolders().size() != other.getSubFolders().size()){
            return false;
        }
        //Same files hashs
        for(Iterator<Entry<String, String>> it = other.getFiles().entrySet().iterator(); it.hasNext();){
            Entry<String, String> oFile = it.next();
            String oHash = oFile.getValue();
            String oName = oFile.getKey();
            if(this.getHash(oName) == null || !this.getHash(oName).equals(oHash)){
                return false;
            }
        }
        //Sub-folders conformity
        for(Iterator<Folder> it = this.getSubFolders().iterator(); it.hasNext();){
            Folder oFolder = it.next();
            String oName = oFolder.getName();
            if(this.getSubFolder(oName) == null || !this.getSubFolder(oName).isConformTo(oFolder)){
                return false;
            }
        }
        return true;
    }
    
    /**
     * Return all the files and the sub-folders of this folder.
     * 
     * @return All the files and the sub-folders view.
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("FOLDERS:").append('\n');
        HashSet<Folder> allFolders;
        if((allFolders = this.getAllSubFolders()) != null){
            for(Iterator<Folder> it = allFolders.iterator(); it.hasNext();){
                sb.append(it.next().getName()).append('\n');
            }
        }
        sb.append('\n').append("FILES:").append('\n');
        HashMap<String, String> allFiles;
        if((allFiles = this.getAllFiles()) != null){
            for(Iterator<Entry<String, String>> it = allFiles.entrySet().iterator(); it.hasNext();){
                Entry e = it.next();
                sb.append("{").append(e.getValue()).append("} ").append(e.getKey()).append('\n');
            }   
        }
        return sb.toString();
    }
}
