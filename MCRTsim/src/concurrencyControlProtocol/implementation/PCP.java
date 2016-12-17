/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencyControlProtocol.implementation;

import concurrencyControlProtocol.ConcurrencyControlProtocol;
import simulation.DataSetting;
import simulation.Final;
import simulation.Job;
import simulation.LockInfo;
import simulation.Resources;

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
                if(this.getDataSetting().getResourceSet().getResources(i).getAccessSet().getTask(j).isPriorityHigher(this.getDataSetting().getResourceSet().getResources(i).getPriorityCeiling()) > 0)
                {
                    this.getDataSetting().getResourceSet().getResources(i).setPriorityCeiling(this.getDataSetting().getResourceSet().getResources(i).getAccessSet().getTask(j).getPriority());
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
            if(j.isPriorityHigher(j.getLocationCore().getSystemPriorityCeiling(j)) > 0) //檢查與系統Ceiling：通過
            {
                j.getLocationCore().setSystemPriorityCeiling(r);
                j.lock(r);
                return true;
            }
            else //檢查與系統Ceiling：阻擋
            {
                if(this.isPIP())
                {
                    j.getLocationCore().getResourcesOfSystemPriorityCeiling().blocked(j);
                }
                return false;
            }
        }
        else //檢查使用資源是否已被Lock：阻擋(在同資源不可搶先得情況下，不必要)
        {
            //is R
            if(this.isPIP())
            {
                j.getLocationCore().getResourcesOfSystemPriorityCeiling().blocked(j);
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
        j.getLocationCore().restoreSystemTempCeiling();
        System.out.println("unLock: R"+l.getResources().getID());
    }
}

