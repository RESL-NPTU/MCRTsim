/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicVoltageAndFrequencyScaling.implementation;

import dynamicVoltageAndFrequencyScaling.DynamicVoltageAndFrequencyScalingMethod;
import simulation.Task;
import java.math.BigDecimal;
import simulation.CriticalSection;

/**
 *
 * @author ShiuJia
 */
public class BaseSpeed extends DynamicVoltageAndFrequencyScalingMethod
{
    Double baseSpeed;
    public BaseSpeed()
    {
        this.setName("Base speed");
    }
    
    @Override
    public void definedSpeed()
    {
        BigDecimal u = new BigDecimal("0");
        for(Task t : this.getDynamicVoltageRegulator().getCore(0).getTaskSet())
        {
            BigDecimal C = new BigDecimal(Integer.toString(t.getComputationAmount()));
            BigDecimal B = new BigDecimal(Double.toString(((double)B(t))));
            BigDecimal T = new BigDecimal(Integer.toString(t.getPeriod()));
            u = u.add((C.add(B)).divide(T,10,/*BigDecimal.ROUND_HALF_UP*/BigDecimal.ROUND_UP));
            System.out.println("C="+C+", B="+B+", T="+T+", U="+ u);
        }
        BigDecimal ms = new BigDecimal(Double.toString(this.getDynamicVoltageRegulator().getCore(0).getTaskSet(0).getMaxProcessingSpeed()));
        this.baseSpeed = (u.multiply(ms)).doubleValue();
        
        System.out.println("MS="+ms+", U="+u);
    }
    
    @Override
    public void scalingVoltage()
    {
        if(this.getDynamicVoltageRegulator().getCore(0).getWorkingJob()!= null)
        {
            this.getDynamicVoltageRegulator().setCurrentSpeed(this.baseSpeed);
        }
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
                        maxBlock = (int)(cs.getEndTime() - cs.getStartTime()) > maxBlock ? (int)(cs.getEndTime() - cs.getStartTime()) : maxBlock;
                    }
                }
            }
        }
        return maxBlock;
    }
}
