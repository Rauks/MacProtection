/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

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
 * <p>
 * To build the tree use {@link MacProcessor#process} and to get que result tree use {@link MacProcessor#getResult}.
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
     * The Mac algorithm used is <code>algorithm</code> and the secret key seed used is <code>key</code>.
     * The mac is saved in <code>macOutput</code> form.
     * <p>
     * To run the scan processus see {@link MacProcessor#process}.
     * 
     * @param dirToScan the folder containing the files and sub-folders to be scanned.
     * @param algorithm the algorithm used to calculate the Mac hash.
     * @param key the key seed used to calculate the Mac hash.
     * @param macOutput the Mac hash output form.
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
     * Constructs a {@link Folder} node from <code>dir</code>. The folders scturcture is builded recurcively and the Mac hash are calculated for all the folder's files.
     * 
     * @param dir the dir used to build the {@link Folder} node;
     * @return the builded {@link Folder} node.
     */
    private Folder initFolder(File dir){
        Folder f = new Folder(dir.getName());
        for(File file : dir.listFiles()){
            System.out.println(file);
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
     * Construct a {@link Folder} tree that is retrievable with {@link MacProcessor#getResult}.
     * 
     * @warning The process may take some time.
     */
    public void process(){
        this.root = this.initFolder(this.dirToScan);
    }
    
    /**
     * Return the {@link Folder} tree builded by this processor.
     * 
     * @return the root of the {@link Folder} tree.
     * @see MacProcessor#process
     */
    public Folder getResult(){
        return this.root;
    }
}
