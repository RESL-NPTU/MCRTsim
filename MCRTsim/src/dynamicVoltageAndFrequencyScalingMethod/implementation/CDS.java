/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicVoltageAndFrequencyScalingMethod.implementation;

import SystemEnvironment.Core;
import SystemEnvironment.Processor;
import WorkLoad.CoreSpeed;
import WorkLoad.Job;
import WorkLoad.SharedResource;
import dynamicVoltageAndFrequencyScalingMethod.DynamicVoltageAndFrequencyScalingMethod;

/**
 *
 * @author ShiuJia
 */
public class CDS extends DynamicVoltageAndFrequencyScalingMethod
{
    double ls, hs;
    public CDS()
    {
        this.setName("CriticalSection Double Speed");
    }
    
    @Override
    public void definedSpeed(Processor p)
    {
        ls = 600;
        
        hs = 3600;
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
                c.setCurrentSpeed(hs);
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
        j.getCurrentCore().setCurrentSpeed(hs);
    }

    @Override
    public void jobUnlockAction(Job j, SharedResource r)
    {
        if(j.getEnteredCriticalSectionSet().size() == 0)
        {
            j.getCurrentCore().setCurrentSpeed(ls);
        }
    }

    @Override
    public void jobCompleteAction(Job j)
    {
        
    }

    @Override
    public void jobDeadlineAction(Job j)
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
