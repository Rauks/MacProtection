/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.processor.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
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
        while(true){
            try {
                Runnable work = this.leader.getRemainingWork().take();
                System.out.println("Worker " + this.getId() + " start to work.");
                work.run();
                this.leader.aWorkIsDone();
                System.out.println("Worker " + this.getId() + " finished his work.");
                this.leader.checkRemainingWork();
            } catch (InterruptedException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
