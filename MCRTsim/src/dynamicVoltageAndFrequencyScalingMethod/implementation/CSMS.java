/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicVoltageAndFrequencyScalingMethod.implementation;

import SystemEnvironment.Core;
import SystemEnvironment.Processor;
import WorkLoad.Job;
import WorkLoad.SharedResource;
import dynamicVoltageAndFrequencyScalingMethod.DynamicVoltageAndFrequencyScalingMethod;

/**
 *
 * @author ShiuJia
 */
public class CSMS extends DynamicVoltageAndFrequencyScalingMethod
{
    double ls;
    public CSMS()
    {
        this.setName("CriticalSection Maximum Speed");
    }
    
    @Override
    public void definedSpeed(Processor p)
    {
        ls = p.getTaskSet().getProcessingSpeed();
        
        
    }

    @Override
    public void jobArrivesProcessorAction(Job j, Processor p)
    {
        
    }

    @Override
    public void jobArrivesCoreAction(Job j, Core c)
    {
        
    }

    @Override
    public void coresExecuteAction()
    {
        
    }

    @Override
    public void coreExecuteAction(Core c)
    {
        if(c.getWorkingJob() != null)
        {
            if(c.getWorkingJob().getEnteredCriticalSectionSet().size() > 0)
            {
                c.setCurrentSpeed(c.getParentCoreSet().getCoreSpeedSet().getMaxFrequencyOfSpeed());
            }
            else
            {
                c.setCurrentSpeed(ls);
            }
        }
    }

    @Override
    public void jobLockAction(Job j, SharedResource r)
    {
        
    }

    @Override
    public void jobUnlockAction(Job j, SharedResource r)
    {
        
    }

    @Override
    public void jobCompleteAction(Job j)
    {
        
    }
    
    @Override
    public void checkEndSystemTimeAction(long systemTime){
        
    }

    @Override
    public void jobMissDeadlineAction(Job j)
    {
        
    }

    @Override
    public void jobBlockedAction(Job blockedJob, SharedResource blockingRes)
    {
        
    }

    @Override
    public void jobFirstExecuteAction(Job j)
    {
        
    }

    @Override
    public void jobEveryExecuteAction(Job j)
    {
        
    }
}
