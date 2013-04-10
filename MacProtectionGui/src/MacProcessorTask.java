
import core.MacAlgorithm;
import core.processor.MacProcessor;
import core.processor.MacProcessor.MacOutput;
import core.processor.MacProcessorEvent;
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
 *
 * @author Karl
 */
public class MacProcessorTask extends Task{
    private ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper(0d);
    
    public ReadOnlyDoubleProperty processProgressProperty(){
        return this.progress.getReadOnlyProperty();
    }
    
    private File dirToScan;
    private MacAlgorithm algorithm;
    private String key;
    private MacProcessor.MacOutput macOutput;

    public MacProcessorTask(File dirToScan, MacAlgorithm algorithm, String key, MacOutput macOutput) {
        this.dirToScan = dirToScan;
        this.algorithm = algorithm;
        this.key = key;
        this.macOutput = macOutput;
    }
    
    @Override
    protected Folder call() throws Exception {
        MacProcessor p = new MacProcessor(this.dirToScan, this.algorithm, this.key, this.macOutput);
        p.addMacProcessorListener(new MacProcessorListener() {
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
        p.process();
        return p.getResult();
    }
}
