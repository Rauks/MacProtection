/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.processor.concurrent;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Karl
 */
public class Worker extends Thread{
    private Leader leader;
    
    public Worker(Leader leader){
        this.leader = leader;
    }
    
    @Override
    public synchronized void run(){
        while(!this.isInterrupted()){
            try {
                Runnable work = this.leader.getRemainingWork().take();
                work.run();
                this.leader.aWorkIsDone();
                this.leader.checkRemainingWork();
            } catch (InterruptedException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
