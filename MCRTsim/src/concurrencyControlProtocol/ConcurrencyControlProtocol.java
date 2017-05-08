/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencyControlProtocol;

import simulation.DataSetting;
import simulation.Job;
import simulation.LockInfo;
import simulation.Resources;
import simulation.Task;
import simulation.TaskSet;

/**
 *
 * @author ShiuJia
 */
public abstract class ConcurrencyControlProtocol
{
    private String methodName;
    private DataSetting dataSetting;
    private boolean setPIP;
    
    public ConcurrencyControlProtocol(DataSetting ds)
    {
        this.dataSetting = ds;
        this.setPIP = false;
    }
    
    public void setName(String name)
    {
        this.methodName = name;
    }
    
    public String getName()
    {
        return this.methodName;
    }
    
    public void setDataSetting(DataSetting ds)
    {
        this.dataSetting = ds;
    }
    
    public DataSetting getDataSetting()
    {
        return this.dataSetting;
    }
    
    public void setPIP(boolean is)
    {
        this.setPIP = is;
    }
    
    public boolean isPIP()
    {
        return this.setPIP;
    }
    
    public abstract boolean leadLock(Job j);
    
    public abstract boolean lock(Job j, Resources r);
    
    public abstract void unlock(Job j, LockInfo l);
    
    public abstract void jobCompleted(Job j);
    
    public abstract double getBlockingTime(TaskSet ts,Task t);
}

