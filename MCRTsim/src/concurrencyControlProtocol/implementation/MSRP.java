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
import java.util.Stack;
import java.util.Vector;
import mcrtsim.Definition;

/**
 *
 * @author ShiuJia
 */
public class MSRP extends SRP
{
    public class PreemptionCore
    {
        public Priority preemptionLevelSystem = Definition.Ohm;
        public Job levelJob = null;
        public SharedResource levelRes = null;
    
        public Stack<SharedResource> lockResource = new Stack<SharedResource>();
    }
    
    public Vector<PreemptionCore> preemptionCore = new Vector<PreemptionCore>();
    public Vector<Vector<Job>> globalResourceFIFOJobQueue = new Vector<Vector<Job>>();
    
    public MSRP()
    {
        this.setName("Multiprocessor Stack Resource Policy");
    }
    
    public void preAction(Processor p)
    {
        super.preAction(p);
        for(int i = 0; i < p.getAllCore().size(); i++)
        {
            this.preemptionCore.add(new PreemptionCore());
        }
        for(int i = 0; i < p.getSharedResourceSet().size(); i++)
        {
            this.globalResourceFIFOJobQueue.add(new Vector<Job>());
        }
    }
    
    public void jobArrivesAction(Job j)
    {
        this.preemptionLevelSystem = this.preemptionCore.get(j.getCurrentCore().getID() -1).preemptionLevelSystem;
        this.levelJob = this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelJob;
        this.levelRes = this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelRes;
        this.lockResource = this.preemptionCore.get(j.getCurrentCore().getID() - 1).lockResource;
        super.jobArrivesAction(j);
    }
    
    public SharedResource jobLockAction(Job j, SharedResource r)
    {
        this.preemptionLevelSystem = this.preemptionCore.get(j.getCurrentCore().getID() - 1).preemptionLevelSystem;
        this.levelJob = this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelJob;
        this.levelRes = this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelRes;
        this.lockResource = this.preemptionCore.get(j.getCurrentCore().getID() - 1).lockResource;
        
        if(r.isGlobal())
        {
            if(!this.globalResourceFIFOJobQueue.get(r.getID()-1).contains(j))
            {
                this.globalResourceFIFOJobQueue.get(r.getID()-1).add(j);
            }
            
            if(r.getIdleResourceNum() > 0 && this.globalResourceFIFOJobQueue.get(r.getID()-1).get(0).equals(j))
            {
                j.lockSharedResource(r);
                this.lockResource.add(r);
                j.getCurrentCore().isPreemption=false;
                
                this.preemptionCore.get(j.getCurrentCore().getID() - 1).preemptionLevelSystem = this.preemptionLevelSystem;
                this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelJob = this.levelJob;
                this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelRes = this.levelRes;
                this.preemptionCore.get(j.getCurrentCore().getID() - 1).lockResource = this.lockResource;
                
                return null;
            }
            else
            {
                j.getCurrentCore().isPreemption = false;
                j.getCurrentCore().setCoreStatus(Definition.CoreStatus.WAIT);
                
                this.preemptionCore.get(j.getCurrentCore().getID() - 1).preemptionLevelSystem = this.preemptionLevelSystem;
                this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelJob = this.levelJob;
                this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelRes = this.levelRes;
                this.preemptionCore.get(j.getCurrentCore().getID() - 1).lockResource = this.lockResource;
            
                return r;
            }
        }
        else
        {
            SharedResource temp = super.jobLockAction(j, r);
            
            this.preemptionCore.get(j.getCurrentCore().getID() - 1).preemptionLevelSystem = this.preemptionLevelSystem;
            this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelJob = this.levelJob;
            this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelRes = this.levelRes;
            this.preemptionCore.get(j.getCurrentCore().getID() - 1).lockResource = this.lockResource;
            
            return temp;
        }
    }
    
    public void jobUnlockAction(Job j, SharedResource r)
    {
        this.preemptionLevelSystem = this.preemptionCore.get(j.getCurrentCore().getID() - 1).preemptionLevelSystem;
        this.levelJob = this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelJob;
        this.levelRes = this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelRes;
        this.lockResource = this.preemptionCore.get(j.getCurrentCore().getID() - 1).lockResource;
        //System.out.println("MSRP Unlock= " + r.getID() + ":" + r.isGlobal() + ":" + this.waitGlobalResourceJob.get(r.getID() - 1).size());
        if(r.isGlobal())
        {
            j.unLockSharedResource(r);
            this.lockResource.remove(r);

            boolean lockGlobal = false;
            if(j.getEnteredCriticalSectionSet().size() > 0)
            {
                for(int i = 0; i < j.getEnteredCriticalSectionSet().size(); i++)
                {
                    if(j.getEnteredCriticalSectionSet().get(i).getUseSharedResource().isGlobal())
                    {
                        lockGlobal = true;
                    }
                }
            }

            if(!lockGlobal)
            {
                j.getCurrentCore().isPreemption=true;
            }

            this.globalResourceFIFOJobQueue.get(r.getID() - 1).remove(j);
        }
        else
        {
            super.jobUnlockAction(j, r);
            this.preemptionCore.get(j.getCurrentCore().getID() - 1).preemptionLevelSystem = this.preemptionLevelSystem;
            this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelJob = this.levelJob;
            this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelRes = this.levelRes;
            this.preemptionCore.get(j.getCurrentCore().getID() - 1).lockResource = this.lockResource;
        }
        
        this.preemptionCore.get(j.getCurrentCore().getID() - 1).preemptionLevelSystem = this.preemptionLevelSystem;
        this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelJob = this.levelJob;
        this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelRes = this.levelRes;
        this.preemptionCore.get(j.getCurrentCore().getID() - 1).lockResource = this.lockResource;
        
    }
    
    public void jobBlockedAction(Job blockedJob, SharedResource blockingRes)
    {
        if(!blockingRes.isGlobal())
        {
            super.jobBlockedAction(blockedJob, blockingRes);
        }
    }
    
    public void jobDeadlineAction(Job j)
    {
        //System.out.println("MSRP Deadline= J(" + j.getParentTask().getID() + ")= " + j.getLocalCore().getID() + ":" + j.getEnteredCriticalSectionSet().size());
        this.preemptionLevelSystem = this.preemptionCore.get(j.getCurrentCore().getID() - 1).preemptionLevelSystem;
        this.levelJob = this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelJob;
        this.levelRes = this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelRes;
        this.lockResource = this.preemptionCore.get(j.getCurrentCore().getID() - 1).lockResource;
                
        if(j.getEnteredCriticalSectionSet().size() > 0)
        {
            while(j.getEnteredCriticalSectionSet().size() > 0)
            {
                SharedResource r = j.getEnteredCriticalSectionSet().peek().getUseSharedResource();
                if(r.isGlobal())
                {
                    j.unLockSharedResource(r);
                    this.lockResource.remove(r);
                    j.getCurrentCore().isPreemption=true;
                    this.globalResourceFIFOJobQueue.get(r.getID() - 1).remove(j);
                }
                else
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
                }
            }
            
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
            
            this.preemptionCore.get(j.getCurrentCore().getID() - 1).preemptionLevelSystem = this.preemptionLevelSystem;
            this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelJob = this.levelJob;
            this.preemptionCore.get(j.getCurrentCore().getID() - 1).levelRes = this.levelRes;
            this.preemptionCore.get(j.getCurrentCore().getID() - 1).lockResource = this.lockResource;
        }
    }
}
