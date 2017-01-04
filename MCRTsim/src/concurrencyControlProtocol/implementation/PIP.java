/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencyControlProtocol.implementation;

import concurrencyControlProtocol.ConcurrencyControlProtocol;
import simulation.CriticalSection;
import simulation.DataSetting;
import simulation.Final;
import simulation.Job;
import simulation.LockInfo;
import simulation.Priority;
import simulation.Resource;
import simulation.Resources;
import simulation.Task;
import simulation.TaskSet;

/**
 *
 * @author ShiuJia
 */
public class PIP extends ConcurrencyControlProtocol
{
    //Vector<Integer> ResourceCeiling;
    
    public PIP(DataSetting ds)
    {
        super(ds);
        this.setName("Priority Inheritance Protocol");
        this.setPIP(true);
    }

    
    public boolean leadLock(Job j) 
    {
        return true;
    }

    public boolean lock(Job j, Resources r)
    {
        if(r.getLeftResourceAmount() != 0) //檢查使用所有資源是否已被Lock：通過
        {
            j.lock(r);
            return true;
        }
        else //檢查使用資源是否已被Lock：阻擋(在同資源不可搶先得情況下，不必要)
        {
            if(this.isPIP())
            {
                r.blocked(j);
            }
            return false;
        }
    }
    
    @Override
    public void unlock(Job j, LockInfo l)
    {
        j.unLock(l.getResources());
        l.getResources().releaseWaitQueueAllJob();
        j.currentPriorityOfInheritOrRevert();
        
        System.out.println("unLock: R"+l.getResources().getID());
    }
    
    @Override
    public double getBlockingTime(TaskSet ts,Task t) 
    {
        return 0;
    }
}

