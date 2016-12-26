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
import simulation.Priority;
import simulation.Resources;

/**
 *
 * @author ShiuJia
 */
public class MSRP extends SRP
{
    public MSRP(DataSetting ds)
    {
        super(ds);
        this.setName("Multiprocessor Stack Resource Policy");
    }
    
    public boolean leadLock(Job j) 
    {
        if(j.isPreemptionLevelHigher(j.getLocationCore().getSystemPreemptionLevel(j))>0)
        {  
            for(Resources resources : j.getCriticalSectionSet().getResourcesSet(j.getTask()))
            {
                if(resources.getLeftResourceAmount() == 0 && resources.checkWhoLockedResource(j) == null)
                {
                    if(resources.isGlobal() == true)
                    {
                        return true;
                    }
                    resources.blocked(j);
                    return false;
                }
            }
        return true;
        }
        j.getLocationCore().getResourceOfSystemPreemptionLevel().blocked(j);
        return false;
    }
    
    public boolean lock(Job j, Resources r)
    {
        if(j.isPreemptionLevelHigher(j.getLocationCore().getSystemPreemptionLevel(j)) > 0) //檢查SystemPreemptionLevelCeiling：通過
        {
            if(r.getLeftResourceAmount() == 0 && r.checkWhoLockedResource(j) == null)
            {
                if(r.isGlobal() == true)
                {
                    j.getLocationCore().setPreemptible(false);
                    j.getLocationCore().changeStatus("wait");
                    r.blocked(j);
                    return false;
                }
                j.getLocationCore().getResourceOfSystemPreemptionLevel().blocked(j);
                return false;
            }
            else
            {
                if(r.isGlobal() == true)
                {
                    j.getLocationCore().setPreemptible(false);
                }
                j.lock(r);
                j.getLocationCore().setSystemPreemptionLevel(r);
                return true;
            }
        }
        else //檢查SystemPreemptionLevelCeiling：阻擋
        {
            j.getLocationCore().getResourceOfSystemPreemptionLevel().blocked(j);
            return false;
        }
    }
    
    @Override
    public void unlock(Job j, LockInfo l)
    {
        j.unLock(l.getResources());
        if(l.getResources().isGlobal())
        {
            if(l.getResources().getWaitQueue().size() > 0)
            {
               // l.getResources().releaseWaitQueueJob(j.getLocationCore());
                l.getResources().getWaitQueue().get(0).getLocationCore().changeStatus("start");
                l.getResources().releaseWaitQueueJob(l.getResources().getWaitQueue().get(0));
                
            }
            j.getLocationCore().setPreemptible(true);
        }
        else
        {
            l.getResources().releaseWaitQueueAllJob();
        }
        j.currentPriorityOfInheritOrRevert();
        j.getLocationCore().restoreSystemTempCeiling();
        System.out.println("unLock: R"+l.getResources().getID());
    }
}
