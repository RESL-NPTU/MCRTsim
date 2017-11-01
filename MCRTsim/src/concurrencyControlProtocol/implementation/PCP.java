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
public class PCP extends ConcurrencyControlProtocol
{
    Vector<Priority> ceilingRes = new Vector<Priority>();
    Priority ceilingSystem = Definition.Ohm;
    Job ceilingJob;
    SharedResource ceilingResource;
    Stack<SharedResource> lockResource = new Stack<SharedResource>();
    
    public PCP()
    {
        this.setName("Priority Ceiling Protocol");
    }

    @Override
    public void preAction(Processor p)
    {
        for(int i = 0; i < p.getSharedResourceSet().size(); i++)
        {
            this.ceilingRes.add(Definition.Ohm);
            for(Task t : p.getSharedResourceSet().get(i).getAccessTaskSet())
            {
                if(t.getPriority().isHigher(this.ceilingRes.get(i)))
                {
                    this.ceilingRes.set(i, t.getPriority());
                }
            }
        }
        
        for(int i = 0; i < this.ceilingRes.size(); i++)
        {
            System.out.println("Res" + (i+1) + ":" + this.ceilingRes.get(i).getValue());
        }
    }

    @Override
    public void jobArrivesAction(Job j)
    {
        
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
        if(r.getLeftResourceAmount() > 0)
        {
            if(j == this.ceilingJob)
            {
                j.lockSharedResource(r);
                this.lockResource.add(r);
                if(this.ceilingRes.get(r.getID() - 1).isHigher(ceilingSystem))
                {
                    this.ceilingJob = j;
                    this.ceilingSystem = this.ceilingRes.get(r.getID() - 1);
                    this.ceilingResource = r;
                }
                return null;
            }
            else
            {
                if(j.getCurrentProiority().isHigher(this.ceilingSystem))
                {
                    j.lockSharedResource(r);
                    this.lockResource.add(r);
                    if(this.ceilingRes.get(r.getID() - 1).isHigher(ceilingSystem))
                    {
                        this.ceilingJob = j;
                        this.ceilingSystem = this.ceilingRes.get(r.getID() - 1);
                        this.ceilingResource = r;
                    }
                    return null;
                }
                else
                {
                    return this.ceilingResource;
                }
            }
        }
        else
        {
            return r;
        }
    }

    @Override
    public void jobUnlockAction(Job j, SharedResource r)
    {
        if(r.getWaitQueue().size() > 0)
        {
            j.endInheritance();
        }
        j.unLockSharedResource(r);
        this.lockResource.pop();
        this.ceilingSystem = Definition.Ohm;
        this.ceilingJob = null;
        this.ceilingResource = null;
        for(int i = 0; i < this.lockResource.size(); i++)
        {
            if(this.ceilingRes.get(this.lockResource.get(i).getID() - 1).isHigher(ceilingSystem))
            {
                ceilingSystem = this.ceilingRes.get(this.lockResource.get(i).getID() - 1);
                this.ceilingResource = this.lockResource.get(i);
                this.ceilingJob = this.lockResource.get(i).getWhoLockedLastResource(j); 
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
        Job blockingJob = blockingRes.getWhoLockedLastResource(blockedJob);
        if(blockingJob == null)
        {
            blockingJob = blockingRes.getResource(0).whoLocked();
        }
        blockingJob.inheritBlockedJobPriority(blockedJob);
        blockingRes.blockedJob(blockedJob);
    }
}
