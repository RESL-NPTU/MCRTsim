/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import java.util.Stack;
import java.util.Vector;

/**
 *
 * @author ShiuJia
 */
public class Result
{
    private Core core;
    private double startTime, endTime;
    private double frequencyOfSpeed;
    private double normalizationOfSpeed;
    private double totalPowerConsumption;
    private CoreStatus status;
    private Job job;
    private Stack<LockInfo> lockedResource;
    private Vector<Integer> priorityCeiling;
    private Vector<Integer> preemptibleCeiling;
    private int systemCeiling;
    private int jobMissDeadlineNum;
    private int jobCompletedNum;
    
    public Result()
    {   
        this.jobMissDeadlineNum = 0;
        this.jobCompletedNum = 0;
        this.core = null;
        this.startTime = 0;
        this.endTime = 0;
        this.status = null;
        this.frequencyOfSpeed = 0;
        this.normalizationOfSpeed = 0;
        this.job = null;
        this.systemCeiling = 0;
        this.lockedResource = new Stack<>();
        this.priorityCeiling = new Vector<>();
        this.preemptibleCeiling = new Vector<>();
        this.totalPowerConsumption = 0;
    }
    
    public Result(double t, CoreStatus s, double sp, double n, Job j ,Core c)
    {
        this.core = c;
        this.startTime = t;
        this.status = s;
        this.frequencyOfSpeed = sp;
        this.normalizationOfSpeed = n;
        this.job = j;
        this.systemCeiling = c.getSystemTemp().getValue();
        this.lockedResource = new Stack<>();
        this.priorityCeiling = new Vector<>();
        this.preemptibleCeiling = new Vector<>();
        
        if(j!=null)
        {
            this.jobMissDeadlineNum = j.getTask().getJobMissDeadlineNum();
            this.jobCompletedNum = j.getTask().getJobCompletedNum();
            j.addResult(this);
        }
        
        if(s.equals(CoreStatus.EXECUTION))
        {
            this.lockedResource.addAll(j.getLockedResource());
        }
        for(int i = 0 ; i<lockedResource.size() ; i++)
        {
            this.priorityCeiling.add(j.getLockedResource().get(i).getResources().getPriorityCeiling().getValue());
            this.preemptibleCeiling.add(j.getLockedResource().get(i).getResources().getPreemptionLevelCeiling().getValue());       
        }
    }
    
    public int getSystemCeiling()
    {
        return this.systemCeiling;
    }
    
    public double getStartTime()
    {
        return this.startTime;
    }
    
    public void setEndTime(Double t)
    {
        this.endTime = t;
    }
    
    public double getEndTime()
    {
        return this.endTime;
    }
    
    public Double getFrequencyOfSpeed()
    {
        return this.frequencyOfSpeed;
    }
    
    public Double getNormalizationOfSpeed()
    {
        return this.normalizationOfSpeed;
    }
    
    public CoreStatus getStatus()
    {
        return this.status;
    }
    
    public Job getJob()
    {
        return this.job;
    }
    
    public Stack<LockInfo> getLockedResource()
    {
        return this.lockedResource;
    }
    
    public Integer getPriorityCeiling(int i)
    {
        return this.priorityCeiling.get(i);
    }
    public Integer getPreemptibleCeiling(int i)
    {
        return this.preemptibleCeiling.get(i);
    }
    
    public Core getCore()
    {
        return this.core;
    }
    
    public void setTotalPowerConsumption(double d)
    {
        this.totalPowerConsumption = d;
    }
    
    public double getTotalPowerConsumption()
    {
        return this.totalPowerConsumption / 100000;
    }
    
    public double getAveragePowerConsumption()
    {
        return this.getTotalPowerConsumption() / this.endTime;
    }
    
    public int getJobMissDeadlineNum()
    {
        return this.jobMissDeadlineNum;
    }
    
    public int getJobCompletedNum()
    {
        return this.jobCompletedNum;
    }
    
    public void setJobMissDeadlineNum(int num)
    {
        this.jobMissDeadlineNum = num;
    }
    
    public void setJobCompletedNum(int num)
    {
        this.jobCompletedNum = num;
    }
}
