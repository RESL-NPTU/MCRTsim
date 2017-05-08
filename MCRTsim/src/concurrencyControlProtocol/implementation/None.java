/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencyControlProtocol.implementation;

import concurrencyControlProtocol.ConcurrencyControlProtocol;
import simulation.DataSetting;
import simulation.Job;
import simulation.LockInfo;
import simulation.Resources;
import simulation.Task;
import simulation.TaskSet;

/**
 *
 * @author YC
 */
public class None extends ConcurrencyControlProtocol
{

    public None(DataSetting ds) 
    {
        super(ds);
        this.setName("None");
    }

    @Override
    public boolean leadLock(Job j) 
    {
        return true;
    }

    @Override
    public boolean lock(Job j, Resources r) 
    {
        return true;
    }

    @Override
    public void unlock(Job j, LockInfo l) 
    {
    }

    @Override
    public void jobCompleted(Job j) 
    {
    }

    @Override
    public double getBlockingTime(TaskSet ts, Task t) 
    {
        return 0;
    }
    
}
