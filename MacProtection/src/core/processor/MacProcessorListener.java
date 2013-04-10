/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.processor;

/**
 * The listener interface for receiving {@link MacProcessorEvent} events. The class that is interested in processing a {@link MacProcessorEvent} event implements this interface, and the object created with that class is registered using {@link MacProcessorEvent#addMacProcessListener} method. When the action event occurs, that object's {@link #macProcessorPerformed} method is invoked.
 * 
 * @author Karl
 */
public interface MacProcessorListener {
    /**
     * Invoked on the {@link MacProcessorEvent} reception.
     */
    public void macProcessorPerformed(MacProcessorEvent evt);
}
