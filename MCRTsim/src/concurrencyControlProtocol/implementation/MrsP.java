/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencyControlProtocol.implementation;

import SystemEnvironment.Core;
import SystemEnvironment.Processor;
import WorkLoad.Job;
import WorkLoad.Priority;
import WorkLoad.SharedResource;
import WorkLoad.Task;
import WorkLoadSet.SharedResourceSet;
import concurrencyControlProtocol.ConcurrencyControlProtocol;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;
import mcrtsim.Definition;

/**
 *
 * @author YC
 */
public class MrsP extends ConcurrencyControlProtocol
{
    public MrsP()
    {
        this.setName("Multiprocessor Resource Sharing Protocol");
    }
    
    class ResourceLocalCeiling extends Vector<Dictionary<Core, Priority>>
    {
        public ResourceLocalCeiling(Processor p)
        {
            for(SharedResource r : p.getSharedResourceSet())
            {
                Dictionary<Core, Priority> dictionary = new Hashtable<Core, Priority>();
                this.add(dictionary);
                
                for(Core c : p.getAllCore())
                {
                    Priority priority = Definition.Ohm;
                    for(Task t : r.getAccessTaskSet())
                    {
                        if(t.getLocalCore().equals(c) && t.getPriority().isHigher(priority))
                        {
                            priority = t.getPriority();
                        }
                    }
                    
                    if(!priority.equals(Definition.Ohm))
                    {
                        dictionary.put(c, priority);
                    }
                }
            }
        }
        
        public Priority getPriority(SharedResource r,Core c)
        {
            return this.get(r.getID()-1).get(c);
        }
    }
    
    class ResourceFIFOJobQueue extends Vector<Vector<Job>>
    {
        public ResourceFIFOJobQueue(SharedResourceSet rSet)
        {
            for(int i = 0 ; i<rSet.size() ; i++ )
            {
                this.add(new Vector<Job>());
            }
        }
        
        public void addJob(SharedResource r, Job j)
        {
            if(!this.get(r.getID()-1).contains(j))
            {
                this.get(r.getID()-1).add(j);
            }
        }
        
        public void removeJob(SharedResource r, Job j)
        {
            this.get(r.getID()-1).remove(j);
        }
        
        public Job getFirstJob(SharedResource r)
        {
            return this.get(r.getID()-1).get(0);
        }
    }
    
    
    ResourceLocalCeiling resourceLocalCeiling ;
    public ResourceFIFOJobQueue resourceFIFOJobQueue;
    
    @Override
    public void preAction(Processor p) 
    {
        resourceLocalCeiling = new ResourceLocalCeiling(p);
        resourceFIFOJobQueue = new ResourceFIFOJobQueue(p.getSharedResourceSet());
    }

    @Override
    public void jobArrivesAction(Job j) 
    {
        
    }
    
    @Override
    public  void jobPreemptedAction(Job preemptedJob , Job newJob)
    {
        if(preemptedJob.getCurrentCore() != preemptedJob.getOriginCore())
        {
            preemptedJob.migration(preemptedJob.getOriginCore());
            if(preemptedJob.getEnteredCriticalSectionSet().size()>0)
            {
                SharedResource r = preemptedJob.getEnteredCriticalSectionSet().get(0).getUseSharedResource();
                preemptedJob.inheritPriority(this.resourceLocalCeiling.getPriority(r, preemptedJob.getCurrentCore()));
            }
        }
    }

    @Override
    public void jobExecuteAction(Job j) 
    {
        
    }

    @Override
    public SharedResource jobLockAction(Job j, SharedResource r) 
    {
        this.resourceFIFOJobQueue.addJob(r, j);
        Job firstJob = this.resourceFIFOJobQueue.getFirstJob(r);
        if(r.getIdleResourceNum() > 0 && this.resourceFIFOJobQueue.getFirstJob(r).equals(j))
        {
            j.lockSharedResource(r);
            j.inheritPriority(this.resourceLocalCeiling.getPriority(r, j.getCurrentCore()));
        }
        else if(!j.getCurrentCore().equals(firstJob.getCurrentCore()) && firstJob.getCurrentCore().getWorkingJob() != firstJob)//
        {
            firstJob.migration(j.getCurrentCore());
            Priority p = new Priority(this.resourceLocalCeiling.getPriority(r, j.getCurrentCore()).getValue()-1);//繼承其他Core的resourceCeiling +1 
            firstJob.inheritPriority(p);
            return r;
        }
        else
        {
            j.getCurrentCore().setCoreStatus(Definition.CoreStatus.WAIT);
            return r;
        }

        //需要實做Job 轉移 Core 之方法
        
        return null;
    }
    
    @Override
    public void jobBlockedAction(Job blockedJob, SharedResource blockingRes) 
    {
        //此方法的Block情形是以Job之priority競爭所發生的，因此不需要使用到此副程式
    }

    @Override
    public void jobUnlockAction(Job j, SharedResource r) 
    {
        j.unLockSharedResource(r);
        this.resourceFIFOJobQueue.removeJob(r, j);
        
        if(!j.getCurrentCore().equals(j.getParentTask().getLocalCore()))
        {
            j.migration(j.getOriginCore());
        }
        
        j.endInheritance();
    }

    @Override
    public void jobCompletedAction(Job j) 
    {
        
    }

    @Override
    public void jobDeadlineAction(Job j) 
    {
        //最後再補完
        while(j.getEnteredCriticalSectionSet().size() > 0)
        {
            SharedResource r = j.getEnteredCriticalSectionSet().peek().getUseSharedResource();
            j.unLockSharedResource(r);
            this.resourceFIFOJobQueue.removeJob(r, j);
        }
    }
    
}
