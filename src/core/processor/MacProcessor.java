/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.processor;

import core.MacAlgorithm;
import core.MacInputStream;
import core.tree.Folder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import usecase.MacProtection;

/**
 * Build the {@link Folder} tree of a real folder.
 * <p/>
 * To build the tree use {@link #process} and to get que result tree use {@link #getResult}.
 * 
 * @author Karl
 */
public class MacProcessor {
    public enum MacOutput{BASE64, HEXADECIMAL}
    
    private Folder root;
    
    private File dirToScan;
    private MacAlgorithm algorithm;
    private String key;
    private MacOutput macOutput;
    
    /**
     * Create a MacProcessor.
     * The Mac algorithm used is {@code algorithm} and the secret key seed used is {@code key}.
     * The mac is saved in {@code macOutput} form.
     * <p/>
     * To run the scan processus see {@link #process}.
     * 
     * @param dirToScan The folder containing the files and sub-folders to be scanned.
     * @param algorithm The algorithm used to calculate the Mac hash of the files.
     * @param key The key seed used to calculate the Mac hash of the files.
     * @param macOutput The Mac hash output form.
     * @see MacAlgorithm
     * @see MacOutput
     */
    public MacProcessor(File dirToScan, MacAlgorithm algorithm, String key, MacOutput macOutput) throws MacProcessorException{
        if(!dirToScan.isDirectory()){
            throw new MacProcessorException("The folder to scan is not a folder.");
        }
        this.dirToScan = dirToScan;
        this.algorithm = algorithm;
        this.key = key;
        this.macOutput = macOutput;
    }
    
    /**
     * Constructs a {@link Folder} node from {@code dir}. The folders scturcture is builded recurcively and the Mac hash are calculated for all the folder's files.
     * 
     * @param dir The dir used to build the {@link Folder} node.
     * @return The builded {@link Folder} node.
     */
    private Folder initFolder(File dir) throws MacProcessorException{
        if(!dir.isDirectory()){
            throw new MacProcessorException("The folder to initialize is not a folder.");
        }
        Folder f = new Folder(dir.getName());
        for(File file : dir.listFiles()){
            if(file.isFile()){
                try(MacInputStream mis = new MacInputStream(new FileInputStream(file), algorithm, key.getBytes())){
                    mis.readAll();
                    switch(this.macOutput){
                        case BASE64:
                            f.addFile(file.getName(), mis.getMacBase64());
                            break;
                        case HEXADECIMAL:
                            f.addFile(file.getName(), mis.getMacHex());
                            break;
                    }
                } catch (IOException | NoSuchAlgorithmException ex) {
                    Logger.getLogger(MacProtection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if(file.isDirectory()){
                f.addFolder(initFolder(file));
            }
        }
        return f;
    }
    
    /**
     * Construct a {@link Folder} tree that is retrievable with {@link #getResult}.
     * 
     * @warning The process may take some time. Take a cup of cofee !
     */
    public void process(){
        try {
            this.root = this.initFolder(this.dirToScan);
        } catch (MacProcessorException ex) {
            Logger.getLogger(MacProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Return the {@link Folder} tree builded by this processor.
     * 
     * @return The root of the {@link Folder} tree.
     * @see #process
     */
    public Folder getResult(){
        return this.root;
    }
}
