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
public class TH extends DynamicVoltageAndFrequencyScalingMethod
{
    double s1,s2,s3;
    public TH()
    {
        this.setName("Test Half");
    }
    
    @Override
    public void definedSpeed(Processor p)
    {
        s1 = 132;
        
        s2 = 450;
        s3 = 550;
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
        
        
        
        if(c.getCurrentTime()<5000000)
        {
            if(c.getID() == 1)
            {
                c.setCurrentSpeed(s1);
            }
            else
            {
                c.setCurrentSpeed(s2);
            }
        }
        else
        {
            if(c.getID() == 3)
            {
                c.setCurrentSpeed(s3);
            }
            else
            {
                c.setCurrentSpeed(s2);
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
