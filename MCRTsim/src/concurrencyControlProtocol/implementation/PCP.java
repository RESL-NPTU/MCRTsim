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
import simulation.Resources;
import simulation.Task;
import simulation.TaskSet;

/**
 *
 * @author ShiuJia
 */
public class PCP extends ConcurrencyControlProtocol
{
    //Vector<Integer> ResourceCeiling;
    
    public PCP(DataSetting ds)
    {
        super(ds);
        this.setName("Priority Ceiling Protocol");
        this.setPIP(true);

        for(int i = 0; i < this.getDataSetting().getResourceSet().size(); i++)
        {
            this.getDataSetting().getResourceSet().getResources(i).setPriorityCeiling(Final.Ohm);
            for(int j = 0; j < this.getDataSetting().getResourceSet().getResources(i).getAccessSet().size(); j++)
            {
                Task task = this.getDataSetting().getResourceSet().getResources(i).getAccessSet().getTask(j);
                Resources resources = this.getDataSetting().getResourceSet().getResources(i);
                
                if(task.isPriorityHigher(resources.getPriorityCeiling()) > 0)
                {
                    resources.setPriorityCeiling(task.getPriority());
                }
            }
        }
    }

    
    public boolean leadLock(Job j) 
    {
        return true;
    }

    public boolean lock(Job j, Resources r)
    {
        if(r.getLeftResourceAmount() != 0) //檢查使用所有資源是否已被Lock：通過
        {
            if(j.isPriorityHigher(j.getLocationCore().getSystemPriorityCeiling(j)) > 0) //檢查與System Ceiling：通過
            {
                j.getLocationCore().setSystemPriorityCeiling(r);
                j.lock(r);
                return true;
            }
            else //檢查與System Ceiling：阻擋
            {
                j.getLocationCore().getResourcesOfSystemPriorityCeiling().blocked(j);
                return false;
            }
        }
        else //檢查使用資源是否已被Lock：阻擋
        {
            j.getLocationCore().getResourcesOfSystemPriorityCeiling().blocked(j);
            return false;
        }
    }
    
    @Override
    public void unlock(Job j, LockInfo l)
    {
        j.unLock(l.getResources());//解鎖Resource
        l.getResources().releaseWaitQueueAllJob();//釋放被阻擋的Job
        j.currentPriorityOfInheritOrRevert();//還原Job的Priority
        j.getLocationCore().restoreSystemTempCeiling();//釋放因鎖定Resource所造成的System Ceiling
    }
    
    public void jobCompleted(Job j) 
    {
        
    }
    
    @Override
    public double getBlockingTime(TaskSet ts,Task t) 
    {
        double maxBlock = 0;
        double block=0;
        
        for(int a = 0 ; a < ts.size();a++)
        {
            if(ts.getTask(a).isPriorityHigher(t.getPriority())<0)
            {
                for(CriticalSection cs : ts.getTask(a).getCriticalSectionSet())
                {
                    if(cs.getResources().isPriorityHigher(t.getPriority()) >= 0)
                    {  
                        block= cs.getEndTime() - cs.getStartTime();
                        if(block>maxBlock)
                        {
                            maxBlock= block;
                        }
                    }
                }
            }
        }       
        return maxBlock;
    }
}

