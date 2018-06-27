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
        
        //println("Controll:PreAction");
    }
    
    public void checkJobArrives(Job j)
    {
        //println("Controll:JobArrives");
        this.protocol.jobArrivesAction(j);
    }
    
    public  void jobPreemptedAction(Job preemptedJob, Job nextJob)//需要釐清BUG情形
    {
        this.protocol.jobPreemptedAction(preemptedJob, nextJob);
    }
    
    
    public boolean checkFirstExecuteAction(Job j)
    {
        if(j.getProgressAmount() == 0)
        {
            return this.protocol.checkJobFirstExecuteAction(j);
        }
        return true;
    }
    
    public boolean checkJobLock(Job j)
    {
        //println("Controll:JobLock");
        
        while(j.getNotEnteredCriticalSectionSet().peek() != null && (j.getNotEnteredCriticalSectionSet().peek().getRelativeStartTime() <= Math.floor(j.getProgressAmount())))
        {
            CriticalSection cs = j.getNotEnteredCriticalSectionSet().peek();

            SharedResource blockingRes = this.protocol.checkJobLockAction(j, cs.getUseSharedResource());

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
    
    public void JobFirstExecuteAction(Job j)
    {
        this.protocol.jobFirstExecuteAction(j);
    }
    
    public void checkJobUnlock(Job j)
    {
        //println("Controll:JobUnlock");
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
        //println("Controll:JobComplete");
        while(j.getEnteredCriticalSectionSet().size() != 0)
        {
            CriticalSection cs = j.getEnteredCriticalSectionSet().peek();
            this.protocol.jobUnlockAction(j, cs.getUseSharedResource());
        }
        
        this.protocol.jobCompletedAction(j);
    }
    
    public void checkJobDeadline(Job j)
    {
        //println("Controll:JobDeadline");
        while(j.getEnteredCriticalSectionSet().size() != 0)
        {
            CriticalSection cs = j.getEnteredCriticalSectionSet().peek();
            this.protocol.jobUnlockAction(j, cs.getUseSharedResource());
        }
        
        this.protocol.jobMissDeadlineAction(j);
    }
    
    public boolean checkBlockedAction(Job blockedJob, SharedResource blockingRes)
    {
        blockedJob.setBlockingResource(blockingRes);
        
        //CCAction
        this.protocol.jobBlockedAction(blockedJob, blockingRes);
        
        //DVSAction
        this.parentProcessor.getDynamicVoltageRegulator().checkBlockAction(blockedJob, blockingRes);
        
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
