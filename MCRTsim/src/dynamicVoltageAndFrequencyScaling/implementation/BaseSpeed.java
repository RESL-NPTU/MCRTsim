/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicVoltageAndFrequencyScaling.implementation;

import dynamicVoltageAndFrequencyScaling.DynamicVoltageAndFrequencyScalingMethod;
import simulation.CriticalSection;
import simulation.Speed;
import simulation.Task;

/**
 *
 * @author ShiuJia
 */
public class BaseSpeed extends DynamicVoltageAndFrequencyScalingMethod
{
    Double basicSpeed, higherSpeed;
    public BaseSpeed()
    {
        this.setName("BaseSpeed");
    }
    
    @Override
    public void definedSpeed()
    {
        double u = 0;
        for(Task t : this.getDynamicVoltageRegulator().getCore(0).getTaskSet())
        {
            u += this.U(t);
        }
        this.basicSpeed = u * this.getDynamicVoltageRegulator().getCore(0).getTaskSet(0).getMaxProcessingSpeed();
        this.higherSpeed = this.getDynamicVoltageRegulator().getMaxFrequencyOfSpeed();
        int i = 0;
    }
    
    @Override
    public void scalingVoltage()
    {
        if(this.getDynamicVoltageRegulator().getCore(0).getWorkingJob()!= null)
        {
            if(this.getDynamicVoltageRegulator().getCore(0).getWorkingJob().getLockedResource().size() > 0)
            {
                this.getDynamicVoltageRegulator().setCurrentSpeed(this.higherSpeed);
            }
            else
            {
                this.getDynamicVoltageRegulator().setCurrentSpeed(this.basicSpeed);
            }
        }
    }
    
    private double U(Task task)
    {
        return ((double)task.getComputationAmount() + (double)this.B(task)) / (double)task.getPeriod();
    }
    
    private int B(Task task)//PCP Blocking Time
    {
        int maxBlock = 0;
        for(Task t : this.getDynamicVoltageRegulator().getCore(0).getTaskSet())
        {
            if(t != task && task.isPriorityHigher(t.getPriority()) > 0)
            {
                for(CriticalSection cs : t.getCriticalSectionSet())
                {
                    if(cs.getResources().isPriorityHigher(task.getPriority()) >= 0)
                    {
                        maxBlock = (int)(cs.getEndTime() - cs.getStartTime());
                    }
                }
            }
        }
        return maxBlock;
    }
}
