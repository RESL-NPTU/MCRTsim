/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencyControlProtocol;

import SystemEnvironment.Controller;
import SystemEnvironment.Processor;
import WorkLoad.Job;
import WorkLoad.SharedResource;

/**
 *
 * @author ShiuJia
 */
public abstract class ConcurrencyControlProtocol
{
    private String name;
    private boolean isPIP;
    private Controller parentController;
    
    public ConcurrencyControlProtocol()
    {
        this.name = null;
        this.isPIP = false;
        this.parentController = null;
    }
    
    /*Operating*/
    public abstract void preAction(Processor p);
    public abstract void jobArrivesAction(Job j);
    public abstract void jobPreemptedAction(Job preemptedJob , Job newJob);//preemptedJob 被搶先的工作(Lower Priority Job)，newJob搶先的工作(Higher Priority Job)
    public abstract void jobExecuteAction(Job j);
    public abstract SharedResource jobLockAction(Job j, SharedResource r);
    public abstract void jobBlockedAction(Job blockedJob,SharedResource blockingRes);
    public abstract void jobUnlockAction(Job j, SharedResource r);
    public abstract void jobCompletedAction(Job j);
    public abstract void jobDeadlineAction(Job j);
    
    /*SetValue*/
    public void setName(String n)
    {
        this.name = n;
    }
    
    public void setPIP(boolean b)
    {
        this.isPIP = b;
    }
    
    public void setParentController(Controller c)
    {
        this.parentController = c;
    }
    
    /*GetValue*/
    public String getName()
    {
        return this.name;
    }
    
    public boolean isPIP()
    {
        return this.isPIP;
    }
    
    public Controller getParentController()
    {
        return this.parentController;
    }
}
