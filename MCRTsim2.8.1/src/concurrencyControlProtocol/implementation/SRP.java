/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencyControlProtocol.implementation;

import SystemEnvironment.Processor;
import WorkLoad.CriticalSection;
import WorkLoad.Job;
import WorkLoad.Priority;
import WorkLoad.SharedResource;
import WorkLoad.Task;
import WorkLoadSet.TaskSet;
import java.util.Stack;
import java.util.Vector;
import mcrtsim.Definition;

/**
 *
 * @author ShiuJia
 */
public class SRP extends PIP
{
    public Vector<Priority> preemptionLevelForTask = new Vector<Priority>();
    public Vector<Priority> preemptionLevelForRes = new Vector<Priority>();
    public Priority preemptionLevelSystem = Definition.Ohm;
    public Job levelJob = null;
    public SharedResource levelRes = null;
    
    public Stack<SharedResource> lockResource = new Stack<SharedResource>();
    
    public Vector<Long> blockingTimeForTask = new Vector<Long>();
    
    public SRP()
    {
        this.setName("Stack Resource Policy");
    }
    
    @Override
    public void preAction(Processor p)
    {
        super.preAction(p);
        for(Task t : p.getTaskSet())
        {
            this.preemptionLevelForTask.add(new Priority(t.getPeriod()));
            //println("SRP:Job" + t.getID() + ":" + this.preemptionLevelForJob.get(t.getID() - 1).getValue());
        }
        
        for(SharedResource r : p.getSharedResourceSet())
        {
            Priority tempPriority = Definition.Ohm;
            for(Task t : r.getAccessTaskSet())
            {
                if(this.preemptionLevelForTask.get(t.getID() - 1).isHigher(tempPriority))
                {
                    tempPriority = this.preemptionLevelForTask.get(t.getID() - 1);
                }
            }
            this.preemptionLevelForRes.add(tempPriority);
            
            //println("SRP:Res" + r.getID() + ":" + this.preemptionLevelForRes.get(r.getID() - 1).getValue());
        }
        
        this.setBlockingTime(p.getTaskSet());
    }

    @Override
    public void jobArrivesAction(Job j)
    {
        
    }

    @Override
    public boolean checkJobFirstExecuteAction(Job j)//已加入global resource判斷
    {
        if(!this.preemptionLevelForTask.get(j.getParentTask().getID() - 1).isHigher(preemptionLevelSystem))
        {
            this.getParentController().checkBlockedAction(j, levelRes);
            return false;
        }
        else
        {
            if(j.getNotEnteredCriticalSectionSet().size() > 0)
            {
                for(int i = 0; i < j.getNotEnteredCriticalSectionArray().size(); i++)
                {
                    if(j.getNotEnteredCriticalSectionArray().get(i).getUseSharedResource().getIdleResourceNum() <= 0)
                    {
                        if(!j.getNotEnteredCriticalSectionArray().get(i).getUseSharedResource().isGlobal())
                        {
                            this.getParentController().checkBlockedAction(j, j.getNotEnteredCriticalSectionArray().get(i).getUseSharedResource());
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    }
    
    @Override
    public SharedResource checkJobLockAction(Job j, SharedResource r)
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
        super.jobUnlockAction(j, r);
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
            if(!this.lockResource.get(i).isGlobal())
            {
                if(this.preemptionLevelForRes.get(this.lockResource.get(i).getID() - 1).isHigher(this.preemptionLevelSystem))
                {
                    this.preemptionLevelSystem = this.preemptionLevelForRes.get(this.lockResource.get(i).getID() - 1);
                    this.levelRes = this.lockResource.get(i);
                    this.levelJob = this.lockResource.get(i).getWhoLockedLastResource(j);
                }
            }
        }
        
    }

    @Override
    public void jobCompletedAction(Job j)
    {

    }

    @Override
    public void jobMissDeadlineAction(Job j)
    {
        super.jobMissDeadlineAction(j);
    }

    @Override
    public void jobBlockedAction(Job blockedJob, SharedResource blockingRes)
    {
        super.jobBlockedAction(blockedJob, blockingRes);
    }
    
    
    public long getBlockingTime(Task t) 
    {   
        return this.blockingTimeForTask.get(t.getID()-1);
    }
    
    private void setBlockingTime(TaskSet ts) 
    {   
        for(Task t : ts)
        {
            long maxBlockingTime = 0;
            for(Task task : ts)
            {
                if(t != task && this.preemptionLevelForTask.get(t.getID()-1).isHigher(this.preemptionLevelForTask.get(task.getID()-1)))
                {
                    for(CriticalSection cs : task.getCriticalSectionSet())
                    {
                        if(this.preemptionLevelForRes.get(cs.getUseSharedResource().getID()-1).compare(this.preemptionLevelForTask.get(t.getID()-1)) >= 0)
                        {
                            maxBlockingTime = cs.getRelativeEndTime() - cs.getRelativeStartTime() > maxBlockingTime ? cs.getRelativeEndTime() - cs.getRelativeStartTime() : maxBlockingTime;
                        }
                    }
                }
            }
            this.blockingTimeForTask.add(maxBlockingTime);
        }
    }
}
