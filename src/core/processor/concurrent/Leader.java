/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.processor.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A {@code Leader} is used to processing a lot of {@link Runnable} task.
 * 
 * @author Karl
 */
public class Leader extends Thread{
    private LinkedBlockingQueue<Runnable> workPool;
    private Worker[] workersPool;
    private int remainingWorkCount;
    private boolean isWorking;
    
    /**
     * Create a {@code Leader} with a pool of {@code nbWorkers} {@link Worker}. 
     * 
     * @param nbWorkers The number of workers in the leader pool.
     */
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
    
    /**
     * Return the works that have not been executed yet.
     * 
     * @return The remaining works.
     */
    public LinkedBlockingQueue<Runnable> getRemainingWork(){
        return this.workPool;
    }
    
    /**
     * Notify that a work is done.
     */
    public synchronized void aWorkIsDone(){
        this.remainingWorkCount--;
    }
    
    /**
     * Request a working state check.
     */
    public synchronized void checkRemainingWork(){
        if(this.remainingWorkCount == 0){
            this.isWorking = false;
        }
    }
    
    /**
     * Give new works to do to the {@code Leader}.
     * 
     * @param work
     * @throws LeaderOccupedException 
     */
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
    
    /**
     * Stop all the workers threads on {@code Leader} garbage collection.
     * 
     * @throws Throwable In case of error in the finalize process.
     */
    @Override
    protected void finalize() throws Throwable{
        super.finalize();
        for (int i = 0; i < this.workersPool.length; i++) {
            this.workersPool[i].interrupt();
        }
        
    }
}
