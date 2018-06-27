/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicVoltageAndFrequencyScalingMethod;

import SystemEnvironment.Core;
import SystemEnvironment.DynamicVoltageRegulator;
import SystemEnvironment.Processor;
import WorkLoad.Job;
import WorkLoad.SharedResource;

/**
 *
 * @author ShiuJia
 */
public abstract class DynamicVoltageAndFrequencyScalingMethod
{
    private String name;
    private DynamicVoltageRegulator parentRegulator;
    
    public DynamicVoltageAndFrequencyScalingMethod()
    {
        this.name = null;
        this.parentRegulator = null;
    }
    
    /*Operating*/
    public abstract void definedSpeed(Processor p);
    //public abstract boolean scalingVoltage();
    public abstract void jobArrivesProcessorAction(Job j, Processor p);
    public abstract void jobArrivesCoreAction(Job j, Core c);
    public abstract void coresExecuteAction();
    public abstract void coreExecuteAction(Core c);
    public abstract void jobFirstExecuteAction(Job j);
    public abstract void jobEveryExecuteAction(Job j);
    public abstract void jobLockAction(Job j, SharedResource r);
    public abstract void jobUnlockAction(Job j, SharedResource r);
    public abstract void jobCompleteAction(Job j);
    public abstract void checkEndSystemTimeAction(long systemTime);
    public abstract void jobMissDeadlineAction(Job j);
    public abstract void jobBlockedAction(Job blockedJob, SharedResource blockingRes);
    
    /*SetValue*/
    public void setName(String n)
    {
        this.name = n;
    }
    
    public void setParentRegulator(DynamicVoltageRegulator r)
    {
        this.parentRegulator = r;
    }
    
    /*GetValue*/
    public String getName()
    {
        return this.name;
    }
    
    public DynamicVoltageRegulator getParentRegulator()
    {
        return this.parentRegulator;
    }
}
