/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemEnvironment;

import WorkLoad.CriticalSection;
import WorkLoad.Job;
import WorkLoad.SharedResource;
import concurrencyControlProtocol.ConcurrencyControlProtocol;
import mcrtsim.Definition;
import mcrtsim.Definition.JobStatus;

/**
 *
 * @author ShiuJia
 */
public class Controller
{
    private ConcurrencyControlProtocol protocol;
    private Processor parentProcessor;
    
    public Controller()
    {
        this.protocol = null;
        this.parentProcessor = null;
    }
    
    /*Operating*/
    public void preAction()
    {
        this.protocol.preAction(this.parentProcessor);
        
        //System.out.println("Controll:PreAction");
    }
    
    public void checkJobArrives(Job j)
    {
        //System.out.println("Controll:JobArrives");
        this.protocol.jobArrivesAction(j);
    }
    
    public  void jobPreemptedAction(Core c)//需要釐清BUG情形
    {
        if(c.getSchedulingInfoSet().size()>0)
        {
            Job previousJob = c.getSchedulingInfoSet().lastElement().getJob();
            Job nextJob = c.getLocalReadyQueue().peek();
            if(previousJob != null && previousJob.getStatus() == JobStatus.COMPUTING && previousJob != nextJob)//符合此條件就代表發生搶先
            {
                this.protocol.jobPreemptedAction(previousJob, nextJob);
            }
        }
    }
    
    public void checkJobExecute(Job j)
    {
        //System.out.println("Controll:JobExecute");
        this.protocol.jobExecuteAction(j);
    }
    
    public boolean checkJobLock(Job j)
    {
        //System.out.println("Controll:JobLock");
        while(j.getCriticalSectionSet().peek() != null && (j.getCriticalSectionSet().peek().getRelativeStartTime() <= Math.floor(j.getProgressAmount())))
        {
            CriticalSection cs = j.getCriticalSectionSet().peek();
            
            SharedResource blockingRes = this.protocol.jobLockAction(j, cs.getUseSharedResource());
            
            if(blockingRes != null)
            {   
                this.checkBlockedAction(j, blockingRes);
                
                return false;
            }
            j.getCurrentCore().isChangeLock=true;
            
            //DVSAction
            this.parentProcessor.getDynamicVoltageRegulator().checkJobLock(j, cs.getUseSharedResource());
        }
        return true;
    }
    
    public void checkJobUnlock(Job j)
    {
        //System.out.println("Controll:JobUnlock");
        while(!j.getEnteredCriticalSectionSet().empty() && (j.getEnteredCriticalSectionSet().peek().getRelativeEndTime() <= Math.floor(j.getProgressAmount())))
        {
            CriticalSection cs = j.getEnteredCriticalSectionSet().peek();
            j.getCurrentCore().isChangeLock=true;
            //DVSAction
            this.parentProcessor.getDynamicVoltageRegulator().checkJobUnlock(j, cs.getUseSharedResource());
            this.protocol.jobUnlockAction(j, cs.getUseSharedResource());
        }
    }
    
    public void jobCompletedAction(Job j)
    {
        //System.out.println("Controll:JobComplete");
        this.protocol.jobCompletedAction(j);
    }
    
    public void checkJobDeadline(Job j)
    {
        //System.out.println("Controll:JobDeadline");
        while(j.getEnteredCriticalSectionSet().size() != 0)
        {
            CriticalSection cs = j.getEnteredCriticalSectionSet().peek();
            this.protocol.jobUnlockAction(j, cs.getUseSharedResource());
        }
        
        this.protocol.jobDeadlineAction(j);
    }
    
    public boolean checkBlockedAction(Job j, SharedResource r)
    {
        //CCAction
        this.protocol.jobBlockedAction(j, r);
        
        //DVSAction
        this.parentProcessor.getDynamicVoltageRegulator().checkBlockAction(j, r);
        
        return true;
    }
    
    /*SetValue*/
    public void setConcurrencyControlProtocol(ConcurrencyControlProtocol p)
    {
        p.setParentController(this);
        this.protocol = p;
    }
    
    public void setParentProcessor(Processor p)
    {
        this.parentProcessor = p;
    }
    
    /*GetValue*/
    public ConcurrencyControlProtocol getConcurrencyControlProtocol()
    {
        return this.protocol;
    }
    
    public Processor getParentProcessor()
    {
        return this.parentProcessor;
    }
}
