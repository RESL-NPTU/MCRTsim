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
import WorkLoad.Task;
import concurrencyControlProtocol.implementation.SRP;
import dynamicVoltageAndFrequencyScalingMethod.DynamicVoltageAndFrequencyScalingMethod;
import java.util.Stack;
import java.util.Vector;
import mcrtsim.MCRTsimMath;

/**
 *
 * @author YC
 * 
 * Paper : An SRP-based energy-efficient scheduling algorithm for dependent real-time tasks
 */
public class BTS extends DynamicVoltageAndFrequencyScalingMethod 
{
    
    private double baseSpeed = 0;
    private double S_b = 0;
    private Vector<hightSpeed> hightSpeedArrays = new Vector<hightSpeed>();//搜尋用
    
    public class hightSpeed
    {
        Job job;
        double speed;
    }
    
    public BTS()
    {
        this.setName("Blocking-Time stealing");
    }
    
    @Override
    public void definedSpeed(Processor p) 
    {
        
        if(p.getController().getConcurrencyControlProtocol().getClass().getSimpleName().equals("SRP"))
        {
            SRP srp = (SRP)p.getController().getConcurrencyControlProtocol();
            switch(p.getSchedulingAlgorithm().getClass().getSimpleName())
            {
                case "RMS":
                    double n = p.getTaskSet().size();
                    for(Task t : p.getTaskSet())
                    {
                        this.S_b += ((double)(t.getComputationAmount() + srp.getBlockingTime(t)))/t.getRelativeDeadline();
                    }
                    
                    this.S_b = this.S_b / ((double)(n)*((Math.pow(2,1/(double)(n)))-1));

                    baseSpeed = MCRTsimMath.mul(this.S_b, p.getTaskSet().getProcessingSpeed());
                    
                    this.S_b = MCRTsimMath.div(baseSpeed ,p.getCore(0).getParentCoreSet().getCoreSpeedSet().getMaxFrequencyOfSpeed());
                    if(this.S_b>1)this.S_b=1;
                    
                break;
                    
                case "EDF":
                    for(Task t : p.getTaskSet())
                    {
                        this.S_b += ((double)(t.getComputationAmount() + srp.getBlockingTime(t)))/t.getRelativeDeadline();
                        
                    }
                    
                    baseSpeed = MCRTsimMath.mul(this.S_b, p.getTaskSet().getProcessingSpeed());
                    this.S_b = MCRTsimMath.div(baseSpeed ,p.getCore(0).getParentCoreSet().getCoreSpeedSet().getMaxFrequencyOfSpeed());
                    if(this.S_b>1)this.S_b=1;
                break;

                default:
                    baseSpeed = 0;
            }
        }
    }

    @Override
    public void jobArrivesProcessorAction(Job j, Processor p) {
    }

    @Override
    public void jobArrivesCoreAction(Job j, Core c) {
    }

    @Override
    public void coresExecuteAction() {
    }

    @Override
    public void coreExecuteAction(Core c) 
    {
        if(c.getWorkingJob()!=null && !c.getWorkingJob().getEnteredCriticalSectionSet().isEmpty())
        {
            c.setCurrentSpeed(baseSpeed);
        }
        else if(!this.hightSpeedArrays.isEmpty())
        {
            boolean isFoundSpeed = false;
            
            for(hightSpeed hs : this.hightSpeedArrays)
            {
                if(hs.job == c.getWorkingJob())
                {
                    c.setCurrentSpeed(hs.speed);
                    isFoundSpeed = true;
                    break;
                }
            }
            
            if(!isFoundSpeed)
            {
                c.setCurrentSpeed(0);//偵錯用，正常來說是不會使用到此函式
            }
            
        }
        else if(c.getWorkingJob()!=null)//有job卻找不到速度<----偵錯用，正常來說是不會使用到此函式
        {
            c.setCurrentSpeed(0);
        }
        
    }

    @Override
    public void jobFirstExecuteAction(Job j) 
    {
        Processor p = j.getCurrentCore().getParentProcessor();
        hightSpeed hs = new hightSpeed();
        switch(p.getSchedulingAlgorithm().getClass().getSimpleName())
            {
                case "RMS":
                    
                case "EDF":
                    long nC_i= j.getParentTask().getComputationAmount() - j.getParentTask().getTotalCriticalSectionTime();
                    SRP srp = (SRP)p.getController().getConcurrencyControlProtocol();
                    long B_i = srp.getBlockingTime(j.getParentTask());
                    long actualBeBlockedTime = j.getBeBlockedTime();
                    
                    hs.job = j;
                    hs.speed = (S_b * nC_i)/(B_i - S_b*(actualBeBlockedTime) + nC_i) * j.getParentTask().ParentTaskSet().getProcessingSpeed();
                    
                    this.hightSpeedArrays.add(hs);
                break;

                default:
                    
            }
        
    }

    @Override
    public void jobEveryExecuteAction(Job j) {
    }

    @Override
    public void jobLockAction(Job j, SharedResource r) {
    }

    @Override
    public void jobUnlockAction(Job j, SharedResource r) {
    }

    @Override
    public void jobCompleteAction(Job j) 
    {
        for(hightSpeed hs : this.hightSpeedArrays)
        {
            if(hs.job == j)
            {
                this.hightSpeedArrays.remove(hs);
                break;
            }
        }
    }
    
    @Override
    public void checkEndSystemTimeAction(long systemTime) 
    {
        
    }

    @Override
    public void jobMissDeadlineAction(Job j) 
    {
        this.jobCompleteAction(j);
    }

    @Override
    public void jobBlockedAction(Job blockedJob, SharedResource blockingRes) 
    {
        
        
    }
}
