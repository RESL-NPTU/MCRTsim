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
import static mcrtsim.MCRTsim.println;

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

    @Override
    public boolean checkJobFirstExecuteAction(Job j) 
    {
        return true;
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
    ResourceFIFOJobQueue resourceFIFOJobQueue;
    
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
    public  void jobPreemptedAction(Job preemptedJob , Job nextJob)//進入此函式就代表，呼叫此函式的Core當前的CostQueue內沒有Cost
    {
        if(preemptedJob.getCurrentCore() != preemptedJob.getOriginCore())
        {
            preemptedJob.migrateTo(preemptedJob.getOriginCore());
            if(preemptedJob.getEnteredCriticalSectionSet().size()>0)
            {
                SharedResource r = preemptedJob.getEnteredCriticalSectionSet().get(0).getUseSharedResource();
                preemptedJob.raisePriority(this.resourceLocalCeiling.getPriority(r, preemptedJob.getCurrentCore()),0);//這時的preemptedJob.getCurrentCore() == preemptedJob.getOriginCore();
            }
        }
    }

    @Override
    public void jobFirstExecuteAction(Job j) 
    {
        
    }

    @Override
    public SharedResource checkJobLockAction(Job j, SharedResource r) 
    {
        this.resourceFIFOJobQueue.addJob(r, j);
        Job firstJob = this.resourceFIFOJobQueue.getFirstJob(r);
        if(r.getIdleResourceNum() > 0 && this.resourceFIFOJobQueue.getFirstJob(r).equals(j))
        {
            j.lockSharedResource(r);
            if(this.resourceLocalCeiling.getPriority(r, j.getCurrentCore()).isHigher(j.getCurrentProiority()))
            {
                j.raisePriority(this.resourceLocalCeiling.getPriority(r, j.getCurrentCore()),0);
            }
        }
        else if(!j.getCurrentCore().equals(firstJob.getCurrentCore()) && firstJob.getCurrentCore().getLocalReadyQueue().peek() != firstJob)//
        {
            if(firstJob.migrateTo(j.getCurrentCore()))
            {   
                firstJob.raisePriority(this.resourceLocalCeiling.getPriority(r, j.getCurrentCore()), 1);//拉高其他Core的resourceCeiling +1 
                
                if(firstJob == firstJob.getCurrentCore().getLocalReadyQueue().peek())
                {
                    println("firstJob == firstJob.getCurrentCore().getLocalReadyQueue().peek()");
                }
                else
                {
                    println("firstJob != firstJob.getCurrentCore().getLocalReadyQueue().peek()");
                }
                
                println("Proiority:"+(-firstJob.getCurrentProiority().getValue())+","+(-firstJob.getCurrentCore().getLocalReadyQueue().peek().getCurrentProiority().getValue()));
                if(firstJob.getCurrentProiority().isHigher(firstJob.getCurrentCore().getLocalReadyQueue().peek().getCurrentProiority()))
                {
                    println("OOOOOOOOOH!!!!");
                }
                
            }
            else
            {
                j.getCurrentCore().setCoreStatus(Definition.CoreStatus.WAIT);
            }
            return r;
        }
        else
        {
            j.getCurrentCore().setCoreStatus(Definition.CoreStatus.WAIT);
            return r;
        }
        
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
            j.migrateTo(j.getOriginCore());
        }
        
        /*找出尚未解鎖的資源中最高的resourcePriorityCeiling*/
        Priority resourcePriorityCeiling = Definition.Ohm;
        for(int i = 0 ; i<j.getEnteredCriticalSectionArray().size() ; i++)
        {
            SharedResource sr = j.getEnteredCriticalSectionArray().get(i).getUseSharedResource();
            if(this.resourceLocalCeiling.getPriority(sr, j.getOriginCore()).isHigher(resourcePriorityCeiling))
            {
                resourcePriorityCeiling = this.resourceLocalCeiling.getPriority(sr, j.getOriginCore());
            }
        }
        
        if(resourcePriorityCeiling.isHigher(j.getOriginalPriority()))
        {
            j.raisePriority(resourcePriorityCeiling, 0);
        }
        else
        {
            j.setCurrentProiority(j.getOriginalPriority());
        }
        
    }

    @Override
    public void jobCompletedAction(Job j) 
    {
        
    }

    @Override
    public void jobMissDeadlineAction(Job j) 
    {
        SharedResource blockingResource = j.getBlockingResource();
        
        if(blockingResource != null)
        {
            this.resourceFIFOJobQueue.removeJob(blockingResource, j);
        }
    }
    
}
