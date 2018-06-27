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
import dynamicVoltageAndFrequencyScalingMethod.DynamicVoltageAndFrequencyScalingMethod;
import java.util.Stack;
import mcrtsim.Definition.CoreStatus;
import static mcrtsim.MCRTsim.println;

/**
 *
 * @author tung-i
 */
public class BATS extends DynamicVoltageAndFrequencyScalingMethod 
{
    
    private double lowSpeed = 0;
    private Stack<hightSpeed> hightSpeedSet = new Stack<hightSpeed>();
    
    public class hightSpeed
    {
        double ti_deadlineTime;
        double speed;
    }
    
    
    public BATS()
    {
        this.setName("Blocking-Aware Two Speed");
    }
    
    
    @Override
    public void definedSpeed(Processor p) 
    {
        
        if(p.getController().getConcurrencyControlProtocol().getClass().getSimpleName().equals("SRP"))
        {
            switch(p.getSchedulingAlgorithm().getClass().getSimpleName())
            {
                case "RMS":
                    double n = p.getTaskSet().size(); // Task Amount
                    lowSpeed=(p.getTaskSet().getTotalUtilization()/(n*((Math.pow(2,1/n))-1))) * p.getTaskSet().getProcessingSpeed(); 
                    // (n*((Math.pow(2,1/n))-1)) is utilization of tasks which RMS schedulability analysis
                    println("S_b = " + (p.getTaskSet().getTotalUtilization()/(n*((Math.pow(2,1/n))-1))));
                
                    println("LowSpeed = " + lowSpeed);
                break;
                    
                case "EDF":
                    lowSpeed=p.getTaskSet().getTotalUtilization() * p.getTaskSet().getProcessingSpeed();
                break;

                default:
                    lowSpeed = 0;
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
        if (!this.hightSpeedSet.isEmpty() && c.getStatus() == CoreStatus.IDLE)
        {
            this.hightSpeedSet.removeAllElements();
        }       
        
        if(this.hightSpeedSet.isEmpty())
        {
            c.setCurrentSpeed(lowSpeed);
        }
        else
        {
            c.setCurrentSpeed(this.hightSpeedSet.peek().speed);
        }
        
    }

    @Override
    public void jobFirstExecuteAction(Job j) {
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
    public void jobCompleteAction(Job j) {
    }
    
    @Override
    public void checkEndSystemTimeAction(long systemTime) 
    {
        while(!this.hightSpeedSet.isEmpty())
        {
            if(systemTime == this.hightSpeedSet.peek().speed)
            {
                this.hightSpeedSet.pop();
            }
            else
            {
                break;
            }
        }
    }

    @Override
    public void jobMissDeadlineAction(Job j) {
        
    }

    @Override
    public void jobBlockedAction(Job blockedJob, SharedResource blockingRes) 
    {
        Processor p = blockedJob.getCurrentCore().getParentProcessor();
        
        if(p.getController().getConcurrencyControlProtocol().getClass().getSimpleName().equals("SRP"))
        {

            double speed=0;
            double rpt=0;//response time
            double b=0;//real blocking time
            Job blockingJob = blockingRes.getWhoLockedLastResource(blockedJob);

            b = blockingJob.getCriticalSection(blockingRes).getRelativeEndTime() - blockingJob.getProgressAmount();
            
            switch(p.getSchedulingAlgorithm().getClass().getSimpleName())
            {
                case "RMS":
                    for (Task t : blockedJob.getParentTask().ParentTaskSet())
                    {
                        if(blockedJob.getParentTask().getPeriod() <= t.getPeriod())
                        {
                            rpt+= Math.ceil(blockedJob.getParentTask().getPeriod()/t.getPeriod())*t.getComputationAmount();
                        }
                    }
                break;
                    
                case "EDF":
                    for (Task t : blockedJob.getParentTask().ParentTaskSet())
                    {
                        if(blockedJob.getParentTask().getPeriod() <= t.getPeriod())
                        {
                            rpt+= Math.floor(blockedJob.getParentTask().getPeriod()/t.getPeriod())*t.getComputationAmount();
                        }
                    }
                break;

                default:
                    rpt = 0;
            }
            
            speed = (b+rpt)/blockedJob.getParentTask().getPeriod()*blockedJob.getParentTask().ParentTaskSet().getProcessingSpeed();

            if(speed < lowSpeed)
            {
                speed = lowSpeed;
            }

            if(this.hightSpeedSet.isEmpty() || speed > this.hightSpeedSet.peek().speed)
            {
                hightSpeed hs = new hightSpeed();
                hs.speed = speed;
                hs.ti_deadlineTime = blockedJob.getAbsoluteDeadline();
                this.hightSpeedSet.add(hs);
            }
        }
    }
}
