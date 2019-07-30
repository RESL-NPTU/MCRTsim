/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencyControlProtocol.implementation;

import SystemEnvironment.Processor;
import WorkLoad.Job;
import WorkLoad.SharedResource;
import WorkLoadSet.SharedResourceSet;
import concurrencyControlProtocol.ConcurrencyControlProtocol;
import java.util.Vector;
import static mcrtsim.MCRTsim.println;


/**
 *
 * @author ShiuJia
 */

    class SuspensionQueueSet extends Vector<Vector<Job>>
    {
        public SuspensionQueueSet(SharedResourceSet rSet)
        {
            for(int i = 0 ; i<rSet.size() ; i++)
            {
                this.add(new Vector<Job>());
            }
        }
        
        public Vector<Job> getSuspensionQueue(SharedResource r)
        {
            return this.get(r.getID()-1);
        }
    }
    
public class PIP extends ConcurrencyControlProtocol
{   
    SuspensionQueueSet SQS;
    
    public PIP()
    {
        this.setName("Priority Inherit Protocol");
    }

    @Override
    public void preAction(Processor p)
    {
        SQS = new SuspensionQueueSet(p.getSharedResourceSet());
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
    public void jobFirstExecuteAction(Job j)
    {
        
    }

    @Override
    public SharedResource checkJobLockAction(Job j, SharedResource r)
    {
        if(r.getLeftResourceAmount() > 0)
        {
            j.lockSharedResource(r);
            return null;
        }
        else
        {
            return r;
        }
    }

    @Override
    public void jobUnlockAction(Job j, SharedResource r)
    {
        for(Job job : SQS.getSuspensionQueue(r))
        {
            job.setSuspended(false);
        }
        
        r.releasePIPQueueJob();
        j.unLockSharedResource(r); 
        j.endInheritance();
        j.recoverInheritance();
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
            blockingResource.getPIPQueue().remove(j);

            SQS.getSuspensionQueue(blockingResource).remove(j);
        }
    }

    @Override
    public void jobBlockedAction(Job blockedJob, SharedResource blockingRes)
    {
        Job blockingJob = blockingRes.getWhoLockedLastResource(blockedJob);
        
        blockingJob.inheritPriority(blockedJob.getCurrentProiority());
        blockingRes.addJob2PIPQueue(blockedJob);
        
        blockedJob.setSuspended(true);
        SQS.getSuspensionQueue(blockingRes).add(blockedJob);
        
        println("blockingJob("+blockingJob.getParentTask().getID()+")"+"= "+ blockingJob.getCurrentProiority().getValue());
        println("blockedJob("+blockedJob.getParentTask().getID()+")"+"= "+ blockedJob.getCurrentProiority().getValue());
    }

    @Override
    public boolean checkJobFirstExecuteAction(Job j) 
    {
        return true;
    }
}
