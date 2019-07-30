/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ResultSet;

import SystemEnvironment.Core;
import WorkLoad.CriticalSection;
import WorkLoad.Job;
import java.util.Stack;
import java.util.Vector;
import mcrtsim.Definition.CoreStatus;
import static mcrtsim.Definition.magnificationFactor;

/**
 *
 * @author ShiuJia
 */
public class SchedulingInfo
{
    private Core core;
    private CoreStatus status;
    private Job job;
    private double startTime;
    private double endTime;
    private double useSpeed;
    private double normalizationOfSpeed;
    private long totalPowerConsumption;
    private Stack<CriticalSection> enteredCriticalSectionSet;
    
    private int jobMissDeadlineNum;
    private int jobCompletedNum;
    public SchedulingInfo()
    {
        this.core = null;
        this.status = null;
        this.job = null;
        this.startTime = 0;
        this.endTime = 0;
        this.useSpeed = 0;
        this.normalizationOfSpeed = 0;
        this.totalPowerConsumption = 0;
        this.enteredCriticalSectionSet = new Stack<>();
        this.jobMissDeadlineNum = 0;
        this.jobCompletedNum = 0;
    }
    
    /*SetValue*/
    public void setCore(Core c)
    {
        this.core = c;
    }
    
    public void setCoreStatus(CoreStatus s)
    {
        this.status = s;
    }
    
    public void setJob(Job j)
    {
        if(j != null)
        {
            this.job = j;
            this.setEnteredCriticalSectionSet(j.getEnteredCriticalSectionSet());

            this.jobMissDeadlineNum = j.getParentTask().getJobMissDeadlineCount();
            this.jobCompletedNum = j.getParentTask().getJobCompletedCount();
            j.addSchedulingInfo(this);
        }
    }
    
    public void setStartTime(double t)
    {
        this.startTime = t;
    }
    
    public void setEndTime(double t)
    {
        this.endTime = t;
    }
    
    public void setUseSpeed(double f,double n)
    {
        this.useSpeed = f;
        this.setNormalizationOfSpeed(n);
    }
    
    public void setNormalizationOfSpeed(double n)
    {
        this.normalizationOfSpeed = n;
    }
    
    public void setTotalPowerConsumption(long d)
    {
        this.totalPowerConsumption = d;
    }
    
    public void setEnteredCriticalSectionSet(Stack<CriticalSection> criticalSectionSet)
    {
        if(this.status.equals(CoreStatus.EXECUTION))
        {
            this.enteredCriticalSectionSet.addAll(criticalSectionSet);
        }
    }
    
    public void setJobMissDeadlineNum(int num)
    {
        this.jobMissDeadlineNum = num;
    }
    
    public void setJobCompletedNum(int num)
    {
        this.jobCompletedNum = num;
    }
    /*GetValue*/
    public Core getCore()
    {
        return this.core;
    }
    
    public CoreStatus getCoreStatus()
    {
        return this.status;
    }
    
    public Job getJob()
    {
        return this.job;
    }
    
    public double getStartTime()
    {
        return this.startTime / magnificationFactor;
    }
    
    public double getEndTime()
    {
        return this.endTime / magnificationFactor;
    }
    
    public double getUseSpeed()
    {
        return this.useSpeed;
    }
    
    public Double getNormalizationOfSpeed()
    {
        return this.normalizationOfSpeed;
    }
    
    public double getTotalPowerConsumption()
    {
        return (double)this.totalPowerConsumption / magnificationFactor;
    }
    
    public double getAveragePowerConsumption()
    {
        return (double)this.totalPowerConsumption / (double)this.endTime;
    }
    
    public Stack<CriticalSection> getEnteredCriticalSectionSet()
    {
        return this.enteredCriticalSectionSet;
    }
    
    public int getJobMissDeadlineNum()
    {
        return this.jobMissDeadlineNum;
    }
    
    public int getJobCompletedNum()
    {
        return this.jobCompletedNum;
    }
}
