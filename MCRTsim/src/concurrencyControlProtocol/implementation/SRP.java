/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencyControlProtocol.implementation;

import SystemEnvironment.Processor;
import WorkLoad.Job;
import WorkLoad.Priority;
import WorkLoad.SharedResource;
import WorkLoad.Task;
import concurrencyControlProtocol.ConcurrencyControlProtocol;
import java.util.Stack;
import java.util.Vector;
import mcrtsim.Definition;

/**
 *
 * @author ShiuJia
 */
public class SRP extends ConcurrencyControlProtocol
{
    public Vector<Priority> preemptionLevelForJob = new Vector<Priority>();
    public Vector<Priority> preemptionLevelForRes = new Vector<Priority>();
    public Priority preemptionLevelSystem = Definition.Ohm;
    public Job levelJob = null;
    public SharedResource levelRes = null;
    
    public Stack<SharedResource> lockResource = new Stack<SharedResource>();
    
    public SRP()
    {
        this.setName("Stack Resource Policy");
    }
    
    @Override
    public void preAction(Processor p)
    {
        for(Task t : p.getTaskSet())
        {
            this.preemptionLevelForJob.add(new Priority(t.getPeriod()));
            //System.out.println("SRP:Job" + t.getID() + ":" + this.preemptionLevelForJob.get(t.getID() - 1).getValue());
        }
        
        for(SharedResource r : p.getSharedResourceSet())
        {
            Priority tempPriority = Definition.Ohm;
            for(Task t : r.getAccessTaskSet())
            {
                if(this.preemptionLevelForJob.get(t.getID() - 1).isHigher(tempPriority))
                {
                    tempPriority = this.preemptionLevelForJob.get(t.getID() - 1);
                }
            }
            this.preemptionLevelForRes.add(tempPriority);
            
            //System.out.println("SRP:Res" + r.getID() + ":" + this.preemptionLevelForRes.get(r.getID() - 1).getValue());
        }
    }

    @Override
    public void jobArrivesAction(Job j)
    {
        if(!this.preemptionLevelForJob.get(j.getParentTask().getID() - 1).isHigher(preemptionLevelSystem))
        {
            this.getParentController().checkBlockedAction(j, levelRes);
        }
        else
        {
            if(j.getCriticalSectionSet().size() > 0)
            {
                for(int i = 0; i < j.getCriticalSectionArray().size(); i++)
                {
                    if(j.getCriticalSectionArray().get(i).getUseSharedResource().getIdleResourceNum() <= 0)
                    {
                        if(!j.getCriticalSectionArray().get(i).getUseSharedResource().isGlobal())
                        {
                            this.getParentController().checkBlockedAction(j, j.getCriticalSectionArray().get(i).getUseSharedResource());
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public  void jobPreemptedAction(Job preemptedJob , Job newJob)
    {
    }

    @Override
    public void jobExecuteAction(Job j)
    {
        
    }

    @Override
    public SharedResource jobLockAction(Job j, SharedResource r)
    {
        j.lockSharedResource(r);
        this.lockResource.add(r);
        if(this.preemptionLevelForRes.get(r.getID() - 1).isHigher(preemptionLevelSystem))
        {
            this.preemptionLevelSystem = this.preemptionLevelForRes.get(r.getID() - 1);
            this.levelJob = j;
            this.levelRes = r;
        }
        return null;
    }

    @Override
    public void jobUnlockAction(Job j, SharedResource r)
    {
        if(r.getWaitQueue().size() > 0)
        {
            j.endInheritance();
        }
        j.unLockSharedResource(r);
        //j.setCurrentProiority(j.getOriginalPriority());
        for(int i = 0; i < this.lockResource.size(); i++)
        {
            if(this.lockResource.get(i).getID() == r.getID())
            {
                this.lockResource.remove(i);
            }
        }
        this.lockResource.remove(r);
        this.preemptionLevelSystem = Definition.Ohm;
        this.levelJob = null;
        this.levelRes = null;
        for(int i = 0; i < this.lockResource.size(); i++)
        {
            if(this.preemptionLevelForRes.get(this.lockResource.get(i).getID() - 1).isHigher(this.preemptionLevelSystem))
            {
                this.preemptionLevelSystem = this.preemptionLevelForRes.get(this.lockResource.get(i).getID() - 1);
                this.levelRes = this.lockResource.get(i);
                this.levelJob = this.lockResource.get(i).getWhoLockedLastResource(j); 
            }
        }
    }

    @Override
    public void jobCompletedAction(Job j)
    {

    }

    @Override
    public void jobDeadlineAction(Job j)
    {

    }

    @Override
    public void jobBlockedAction(Job blockedJob, SharedResource blockingRes)
    {
        //System.out.println("SRP= " + blockedJob.getParentTask().getID() + ":" + blockingRes.getID());
        Job blockingJob = blockingRes.getWhoLockedLastResource(blockedJob);
        if(blockingJob == null)
        {
            blockingJob = blockingRes.getResource(0).whoLocked();
        }
        blockingJob.inheritBlockedJobPriority(blockedJob);
        blockingRes.blockedJob(blockedJob);
    }
}
