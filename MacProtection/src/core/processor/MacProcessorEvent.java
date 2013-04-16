/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.processor;

/**
 * A semantic event which indicates that a {@link MacProcessor} process progress occurred. The event is passed to every {@link MacProcessorListener} object that registered to receive such events using {@link MacProcessor#addMacProcessListener}.
 * 
 * @author Karl
 */
public class MacProcessorEvent {
    /**
     * Used to describe the processing state of the {@link MacProcessor}.
     * The {@link ProcessinsState} is {@code STARTED} on processing process starting then {@code STARTED} during the process and finally {@code FINISHED} at the end. In case of processor error the state is {@code CANCELED}.
     */
    public enum ProcessingState{STARTED, RUNNING, FINISHED, CANCELLED}
    
    private int totalFiles;
    private int processedFiles;
    private ProcessingState state;
    
    /**
     * Create a {@code MacProcessorEvent} used to describe the processing state of a {@link MacProcessor}.
     * 
     * @param totalFiles The total number of files processing.
     * @param processedFiles The number of files already processed.
     * @param state The processing state of the {@link MacProcessor}.
     * @see ProcessingState
     */
    public MacProcessorEvent(int totalFiles, int processedFiles, ProcessingState state) {
        this.totalFiles = totalFiles;
        this.processedFiles = processedFiles;
        this.state = state;
    }

    /**
     * Return the total number of files that the {@link MacProcessor} who send this event is processing.
     * 
     * @return The total number of files processing.
     */
    public int getTotalFiles() {
        return this.totalFiles;
    }
    
    /**
     * Return the number of already processed files by the {@link MacProcessor} who send this event.
     * 
     * @return The number of already processed files.
     */
    public int getProcessedFiles() {
        return this.processedFiles;
    }
    
    /**
     * Used to get the processing state of the {@link MacProcessor} who send this event.
     * The {@link ProcessinsState} is {@code STARTED} on processing process starting then {@code STARTED} during the process and finally {@code FINISHED} at the end. In case of processor error the state is {@code CANCELED}.
     * 
     * @return The current {@link MacProcessor} processing state.
     */
    public ProcessingState getState(){
        return this.state;
    }
}
