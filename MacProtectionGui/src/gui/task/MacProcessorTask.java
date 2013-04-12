package gui.task;


import core.MacAlgorithm;
import core.processor.MacProcessor;
import core.processor.MacProcessor.MacOutput;
import core.processor.MacProcessorEvent;
import core.processor.MacProcessorException;
import core.processor.MacProcessorListener;
import core.tree.Folder;
import java.io.File;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.concurrent.Task;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Used to perform a {@link MacProcessor} process.
 * 
 * @author Karl
 */
public class MacProcessorTask extends Task{
    private ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper(0d);
    
    private MacProcessor processor;
    
    /**
     * Return the ProgressProperty of this Task.
     * 
     * @return The ProgressProperty of this Task.
     */
    public ReadOnlyDoubleProperty processProgressProperty(){
        return this.progress.getReadOnlyProperty();
    }

    /**
     * Create a {@code MacProcessorTask} performig a {@link MacProcessor} process.
     * 
     * @param dirToScan The folder containing the files and sub-folders to be scanned.
     * @param algorithm The algorithm used to calculate the Mac hash of the files.
     * @param key The key seed used to calculate the Mac hash of the files.
     * @param macOutput The Mac hash output form.
     * @throws MacProcessorException If the {@code dirToScan} is not a directory.
     * @see MacAlgorithm
     * @see MacOutput
     */
    public MacProcessorTask(File dirToScan, MacAlgorithm algorithm, String key, MacOutput macOutput) throws MacProcessorException {
        this.processor = new MacProcessor(dirToScan, algorithm, key, macOutput);
        processor.addMacProcessorListener(new MacProcessorListener() {
            @Override
            public void macProcessorPerformed(MacProcessorEvent evt) {
                switch(evt.getState()){
                    case RUNNING:
                        progress.set((double)evt.getProcessedFiles() / (double)evt.getTotalFiles());
                        break;
                    case CANCELED:
                        progress.set(0d);
                        break;
                    case STARTED:
                        progress.set(0d);
                        break;
                    case FINISHED: 
                        progress.set(1d);
                        break;
                }
            }
        });
    }
    
    /**
     * Invoked when the execution is requested by a {@link Thread}. A {@link Folder} tree is builded using a {@link MacProcessor}.
     * 
     * @return The builded {@link Folder} tree.
     * @throws Exception An unhandled exception which occurred during the background operation.
     */
    @Override
    protected Folder call() throws Exception {
        processor.process();
        return processor.getResult();
    }
}
