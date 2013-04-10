/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.processor.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Karl
 */
public class Leader extends Thread{
    private LinkedBlockingQueue<Runnable> workPool;
    private Worker[] workersPool;
    private int remainingWorkCount;
    private boolean isWorking;
    
    public Leader(int nbWorkers){
        this.workPool = new LinkedBlockingQueue<>();
        this.remainingWorkCount = 0;
        this.isWorking = false;
        
        this.workersPool = new Worker[nbWorkers];
        for (int i = 0; i < this.workersPool.length; i++) {
            Worker worker = new Worker(this);
            worker.start();
            this.workersPool[i] = worker;
        }
    }
    
    public LinkedBlockingQueue<Runnable> getRemainingWork(){
        return this.workPool;
    }
    
    public synchronized void aWorkIsDone(){
        this.remainingWorkCount--;
    }
    
    public synchronized void checkRemainingWork(){
        if(this.remainingWorkCount == 0){
            this.isWorking = false;
        }
    }
    
    public synchronized void doWork(Runnable[] work) throws LeaderOccupedException{
        if(this.isWorking){
            throw new LeaderOccupedException();
        }
        this.isWorking = true;
        this.remainingWorkCount = work.length;
        for (int i = 0; i < work.length; i++) {
            try {
                this.workPool.put(work[i]);
            } catch (InterruptedException ex) {
                Logger.getLogger(Leader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
