/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencyControlProtocol.implementation;

import concurrencyControlProtocol.ConcurrencyControlProtocol;
import java.util.Vector;
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
public class SRP extends ConcurrencyControlProtocol //問題一
{
    private DataSetting dataSetting;
    
    public SRP(DataSetting ds)
    {
        super(ds);
        this.dataSetting = ds;
        this.setName("Stack Resource Policy");
        this.setPIP(true);
        
        Vector<Integer> in = new Vector<Integer>();
        for(int i = 0; i < this.getDataSetting().getTaskSet().size(); i++)
        {
            in.add(0);
        }
        for(int i = 0; i < this.getDataSetting().getTaskSet().size() - 1; i++)
        {
            for(int j = i + 1; j < this.getDataSetting().getTaskSet().size(); j++)
            {
                if(this.getDataSetting().getTaskSet(i).getPeriod() < this.getDataSetting().getTaskSet(j).getPeriod())
                {
                    in.set(i, in.get(i) - 1);
                }
                else
                {
                    in.set(j, in.get(j) - 1);
                }
            }
        }
        for(int i = 0; i < this.getDataSetting().getTaskSet().size(); i++)
        {
            this.getDataSetting().getTaskSet(i).setPreemptionLevel(new Priority(in.get(i) + this.getDataSetting().getTaskSet().size()));
        }
        
        for(int i = 0; i < this.getDataSetting().getResourceSet().size(); i++)
        {
            this.getDataSetting().getResourceSet().getResources(i).setPreemptionLevelCeiling(Final.Ohm);
            for(int j = 0; j < this.getDataSetting().getResourceSet().getResources(i).getAccessSet().size(); j++)
            {
                if(this.getDataSetting().getResourceSet().getResources(i).getAccessSet().getTask(j).isPreemptionLevelHigher(this.getDataSetting().getResourceSet().getResources(i).getPreemptionLevelCeiling()) > 0)
                {
                    this.getDataSetting().getResourceSet().getResources(i).setPreemptionLevelCeiling(this.getDataSetting().getResourceSet().getResources(i).getAccessSet().getTask(j).getPreemptionLevel());
                }
            }
        }
    }
    
        @Override
    public boolean leadLock(Job j) 
    {
        if(j.isPreemptionLevelHigher(j.getLocationCore().getSystemPreemptionLevel(j))>0)
        {  
            for(Resources resources : j.getCriticalSectionSet().getResourcesSet(j.getTask()))
            {
                if(resources.getLeftResourceAmount() == 0 && resources.checkWhoLockedResource(j) == null)
                {
                    if(this.isPIP())
                    {
                        resources.blocked(j);
                    }
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
            j.getLocationCore().setSystemPreemptionLevel(r);
            j.lock(r);
            return true;
        }
        else //檢查SystemPreemptionLevelCeiling：阻擋
        {
            if(this.isPIP())
            {
                j.getLocationCore().getResourceOfSystemPreemptionLevel().blocked(j);
                
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

    @Override
    public double getBlockingTime(TaskSet ts,Task t) 
    {   
        
        return 0;
    }
}

