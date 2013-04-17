/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.processor;

import core.MacAlgorithm;
import core.MacInputStream;
import core.tree.Folder;
import core.tree.TreeElementException;
import core.tree.HashedFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

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
    
    private int totalFiles;
    private int processedFiles;
    private int encouredErrors;
    private ExecutorService executor;
    
    private HashSet<MacProcessorListener> listeners;
    
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
     * @throws MacProcessorException If the {@code dirToScan} is not a directory.
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
        this.listeners = new HashSet<>();
    }
    
    /**
     * Increment the processed files counter.
     */
    private synchronized void incrProcessedFiles(){
        this.processedFiles++;
    }
    
    /**
     * Increment the error during files processing counter.
     */
    private synchronized void incrEncouredErrors(){
        this.encouredErrors++;
    }
    
    /**
     * Constructs a {@link Folder} node from {@code dir}. The folders scturcture is builded recurcively and the Mac hash are calculated for all the folder's files.
     * 
     * @param dir The dir used to build the {@link Folder} node.
     * @return The builded {@link Folder} node.
     * @throws MacProcessorException If the {@code dir} is not a directory.
     */
    private Folder initFolder(File dir) throws MacProcessorException{
        if(!dir.isDirectory()){
            throw new MacProcessorException("The folder to scan is not a folder.");
        }
        
        final Folder f = new Folder(dir.getName());
        for(final File file : dir.listFiles()){
            if(file.isFile()){
                this.executor.execute(new Runnable(){
                    @Override
                    public void run(){
                        try(MacInputStream mis = new MacInputStream(new FileInputStream(file), algorithm, key.getBytes())){
                            mis.readAll();
                            switch(macOutput){
                                case BASE64:
                                    f.addFile(new HashedFile(file.getName(), mis.getMacBase64(), file.length()));
                                    break;
                                case HEXADECIMAL:
                                    f.addFile(new HashedFile(file.getName(), mis.getMacHex(), file.length()));
                                    break;
                            }
                            incrProcessedFiles();
                            fireMacProcessorListenerEvent(MacProcessorEvent.ProcessingState.RUNNING);
                        } catch (IOException | NoSuchAlgorithmException | TreeElementException | InvalidKeyException ex) {
                            Logger.getLogger(MacProcessor.class.getName()).log(Level.SEVERE, null, ex);
                            if(encouredErrors == 0){ //Error already detected, cancel event already send.
                                incrEncouredErrors();
                                executor.shutdownNow();
                                fireMacProcessorListenerEvent(MacProcessorEvent.ProcessingState.CANCELLED);
                            }
                        }
                    }
                });
            }
            else if(file.isDirectory()){
                try {
                    f.addFolder(initFolder(file));
                } catch (TreeElementException ex) {
                    Logger.getLogger(MacProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    incrEncouredErrors();
                }
            }
        }
        incrProcessedFiles();
        this.fireMacProcessorListenerEvent(MacProcessorEvent.ProcessingState.RUNNING);
        return f;
    }
    
    /**
     * Construct a {@link Folder} tree that is retrievable with {@link #getResult}.
     * 
     * @warning The process may take some time. Take a cup of cofee !
     */
    public void process(){
        this.totalFiles = FileUtils.listFilesAndDirs(dirToScan, new RegexFileFilter("^(.*?)"), DirectoryFileFilter.DIRECTORY).size();
        this.processedFiles = 0;
        this.encouredErrors = 0;
        
        int sysProc = Runtime.getRuntime().availableProcessors();
        sysProc = (sysProc == 1) ? sysProc : (sysProc - 1);
        
        try {
            this.fireMacProcessorListenerEvent(MacProcessorEvent.ProcessingState.STARTED);
            this.executor = Executors.newFixedThreadPool(sysProc);
            this.root = this.initFolder(this.dirToScan);
            this.executor.shutdown();
            this.executor.awaitTermination(1, TimeUnit.DAYS);
            if(this.encouredErrors == 0){ //Error detected, cancel event sended.
                this.fireMacProcessorListenerEvent(MacProcessorEvent.ProcessingState.FINISHED);
            }
        } catch (InterruptedException | MacProcessorException ex) {
            if(this.encouredErrors == 0){ //Error already detected in workers, cancel event already send.
                this.executor.shutdownNow();
                this.fireMacProcessorListenerEvent(MacProcessorEvent.ProcessingState.CANCELLED);
            }
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
    
    /**
     * Add a {@link MacProcessorListener} to this processor.
     * 
     * @param listener The listener to add.
     */
    public void addMacProcessorListener(MacProcessorListener listener){
        this.listeners.add(listener);
    }
    
    /**
     * Remove a {@link MacProcessorListener} from this processor.
     * 
     * @param listener The listener to remove.
     */
    public void removeMacProcessorListener(MacProcessorListener listener){
        this.listeners.remove(listener);
    }
    
    /**
     * Fire a {@link MacProcessorEvent} reporting the current processing state to all the {@link MacProcessorListener} of this processor.
     * 
     * @param state The current processor state.
     */
    private void fireMacProcessorListenerEvent(MacProcessorEvent.ProcessingState state){
        MacProcessorEvent evt = new MacProcessorEvent(this.totalFiles, this.processedFiles, state);
        for(Iterator<MacProcessorListener> it = this.listeners.iterator(); it.hasNext();){
            it.next().macProcessorPerformed(evt);
        }
    } 
}
