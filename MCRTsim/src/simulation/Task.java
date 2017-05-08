/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import java.util.Vector;

/**
 *
 * @author ShiuJia
 */
public class Task
{
    
    private int ID; // 代碼
    private int numJob = 0; // number of jobs that have been created
    private int period; // 週期
    private int enterTime = 0; //週期進來的時間 
    private int relativeDeadline; // 相對截止時間
    private int computationAmount; // 以最高速度運作所需工作量(the value is assigned for the task running at maxProcessingSpeed)
    private double maxProcessingSpeed; // 單位=MHz
    private Priority priority; // 使用於固定式優先權分配(only for fixed-priority assignment)
    private CriticalSectionSet criticalSectionSet; // 所有使用資源的時段
    private Priority preemptionLevel;
    private Core localCore;
    private Double utilization;
    private Double blockingTime;
    private Job curJob;
    private int jobMissDeadlineNum = 0;
    private int jobCompletedNum = 0;
    
    private Vector<Job> JobSet;
    
    public Task()
    {
        this.JobSet = new Vector<>();
        this.priority = new Priority();
        this.preemptionLevel = new Priority();
        this.blockingTime = -1.0;
        this.curJob = new Job();
    }
    
    public void setEnterTime(int i)
    {
        this.enterTime = i;
    }
    
    public int getEnterTime()
    {
        return this.enterTime;
    }
    
    public void setID(int i)
    {
        this.ID = i;
    }
    
    public int getID()
    {
        return this.ID;
    }
    
    public int getNumJob()
    {
        return this.numJob;
    }
    
    public void setPeriod(int p)
    {
        this.period = p;
    }
    
    public int getPeriod()
    {
        return this.period;
    }
    
    public void setRelativeDeadline(int r)
    {
        this.relativeDeadline = r;
    }
    
    public int getRelativeDeadline()
    {
        return this.relativeDeadline;
    }
    
    public void setComputationAmount(int c)
    {
        this.computationAmount = c;
    }
    
    public int getComputationAmount()
    {
        return this.computationAmount;
    }
    
    public void setMaxProcessingSpeed(double mps)
    {
        this.maxProcessingSpeed = mps;
    }
    
    public double getMaxProcessingSpeed()
    {
        return this.maxProcessingSpeed;
    }
    
    public void setPriority(Priority p)
    {
        this.priority.clonePriority(p);
    }
    
    public Priority getPriority()
    {
        return this.priority;
    }
    
    public void setPreemptionLevel(Priority p)
    {
        this.preemptionLevel = p;
    }
    
    public Priority getPreemptionLevel()
    {
        return this.preemptionLevel;
    }
    
    public void setCriticalSectionSet(CriticalSectionSet css)
    {
        this.criticalSectionSet = css;
    }
    
    public CriticalSectionSet getCriticalSectionSet()
    {
        return this.criticalSectionSet;
    }
    
    public void setCore(Core c)
    {
        this.localCore = c;
    }
    
    public Core getCore()
    {
        return this.localCore;
    }
    
    public Job newJob(int newJobTime) //產生新Job
    {
        this.numJob++;
        Job j = new Job();
        this.curJob = j;
        j.setTask(this);
        j.setReleaseTime(newJobTime);
        j.setID(this.numJob);
        j.setAbsoluteDeadline((j.getReleaseTime()) + this.relativeDeadline);
        j.setProgressAmount(0);
        j.setTargetAmount(this.computationAmount);
        j.setOriginalPriority(this.priority);
        j.setLocationCore(this.localCore);
        j.setCriticalSectionSet();
        j.setPreemptionLevel(this.preemptionLevel);
        this.JobSet.add(j);
        return j;
    }
    
    public int isPriorityHigher(Priority p)
    {
        if(this.priority.getValue() > p.getValue())
        {
            return 1;
        }
        else if(this.priority.getValue() < p.getValue())
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
    
    public int isPreemptionLevelHigher(Priority p)
    {
        if(this.preemptionLevel.getValue() > p.getValue())
        {
            return 1;
        }
        else if(this.preemptionLevel.getValue() < p.getValue())
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
    
    public Double getUtilization()
    {
        return this.utilization;
    }
    
    public void setUtilization()
    {
        this.utilization = (double)this.computationAmount / (double)this.period;
        System.out.println("Task" + this.ID + ": U= " + this.utilization);
    }
    
    
    public void setBlockingTime(double bt)
    {
        this.blockingTime = bt;
    }
    
    public Double getBlockingTime()
    {
        return this.blockingTime;
    }

    public Job getCurJob() 
    {
        return this.curJob;
    }
    
    public void addJobMissDeadlineNum()
    {
        this.jobMissDeadlineNum++;
    }
    
    public int getJobMissDeadlineNum()
    {
        return this.jobMissDeadlineNum;
    }
    
    public void addJobCompletedNum()
    {
        this.jobCompletedNum++;
    }
    
    public int getJobCompletedNum()
    {
        return this.jobCompletedNum;
    }
    
    public Vector<Job> getJobSet()
    {
        return this.JobSet;
    }
    
}