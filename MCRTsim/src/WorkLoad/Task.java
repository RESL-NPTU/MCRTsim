/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WorkLoad;

import SystemEnvironment.Core;
import WorkLoadSet.CriticalSectionSet;
import WorkLoadSet.TaskSet;
import java.util.Vector;
/**
 *
 * @author ShiuJia
 */
public class Task
{
    private int ID; //代碼
    private int JobCount; //產生的Job個數
    private long enterTime; //初次進入系統的時間
    private long period; //週期
    private long relativeDeadline; //相對截止時間
    private long computationAmount; //所需工作量
    private Priority priority; //使用於靜態優先權分配所需屬性
    private Core localCore; //使用於Partitioned Scheduling分配Core
    private CriticalSectionSet criticalSectionSet; //執行過程中的CriticalSection
    private TaskSet parentTaskSet; //所屬工作集合
    private Job curJob;
    private int jobMissDeadlineCount = 0;
    private int jobCompletedCount = 0;
    private Vector<Job> JobSet;
    
    private Vector<SharedResource> resourceSet;
    
    public Task()
    {
        this.ID = 0;
        this.JobCount = 0;
        this.enterTime = 0;
        this.period = 0;
        this.relativeDeadline = 0;
        this.computationAmount = 0;
        this.priority = new Priority(0);
        this.localCore = null;
        this.criticalSectionSet = new CriticalSectionSet();
        this.parentTaskSet = null;
        this.resourceSet = new Vector<SharedResource>();
        this.JobSet = new Vector<>();
        this.curJob = new Job();
    }
    
    /*Operating*/
    public void addCriticalSection(CriticalSection cs)
    {
        this.criticalSectionSet.add(cs);
        this.resourceSet.add(cs.getUseSharedResource());
    }
    
    public Job produceJob(long produceTime)
    {
        this.JobCount++;
        Job j = new Job();
        this.curJob = j;
        j.setID(this.JobCount);
        j.setParentTask(this);
        j.setReleaseTime(produceTime);
        j.setAbsoluteDeadline(produceTime + this.relativeDeadline);
        j.setTargetAmount(this.computationAmount);
        j.setOriginalPriority(this.priority);
        j.setCurrentProiority(this.priority);
        j.setCriticalSectionSet(this.criticalSectionSet);
        j.setMaxProcessingSpeed(this.getParentTaskSet().getMaxProcessingSpeed());
        this.JobSet.add(j);
       // System.out.println("t("+ this.ID +","+ j.getID() +") :" + j.getReleaseTime() );
        return j;
    }
    
    public void showInfo()
    {
        System.out.println("Task(" + this.ID + "):");
        System.out.println("    EnterTime: " + this.enterTime);
        System.out.println("    RelativeDeadline: " + this.relativeDeadline);
        System.out.println("    ComputationAmount: " + this.computationAmount);
        System.out.println("    CriticalSection:");
        for(CriticalSection cs : this.criticalSectionSet)
        {
            System.out.println("        CriticalSection(R" + cs.getUseSharedResource().getID() + "):" + cs.getRelativeStartTime() + "/" + cs.getRelativeEndTime());
        }
        System.out.println();
    }
    
    /*SetValue*/
    public void setID(int id)
    {
        this.ID = id;
    }
    
    public void setEnterTime(long t)
    {
        this.enterTime = t;
    }
    
    public void setPeriod(long p)
    {
        this.period = p;
    }
    
    public void setRelativeDeadline(long d)
    {
        this.relativeDeadline = d;
    }
    
    public void setComputationAmount(long c)
    {
        this.computationAmount = c;
    }
    
    public void setPriority(Priority p)
    {
        this.priority = p;
    }
    
    public void setLocalCore(Core c)
    {
        this.localCore = c;
    }
    
    public void setParentTaskSet(TaskSet ts)
    {
        this.parentTaskSet = ts;
    }
    
    /*GetValue*/
    public int getID()
    {
        return this.ID;
    }
    
    public long getEnterTime()
    {
        return this.enterTime;
    }
    
    public long getPeriod()
    {
        return this.period;
    }
    
    public long getRelativeDeadline()
    {
        return this.relativeDeadline;
    }
    
    public long getComputationAmount()
    {
        return this.computationAmount;
    }
    
    public Priority getPriority()
    {
        return this.priority;
    }
    
    public Core getLocalCore()
    {
        return this.localCore;
    }
    
    public TaskSet getParentTaskSet()
    {
        return this.parentTaskSet;
    }
    
    public Vector<SharedResource> getResourceSet()
    {
        return this.resourceSet;
    }
    
    public double getUtilization()
    {
        return (double)this.computationAmount / this.period;
    }
    
    public CriticalSectionSet getCriticalSectionSet()
    {
        return this.criticalSectionSet;
    }
    
    public Job getCurJob() 
    {
        return this.curJob;
    }
    
    public void addJobMissDeadlineCount()
    {
        this.jobMissDeadlineCount++;
    }
    
    public int getJobMissDeadlineCount()
    {
        return this.jobMissDeadlineCount;
    }
    
    public void addJobCompletedCount()
    {
        this.jobCompletedCount++;
    }
    
    public int getJobCompletedCount()
    {
        return this.jobCompletedCount;
    }
    
    public Vector<Job> getJobSet()
    {
        return this.JobSet;
    }
        
    public int getJobCount()
    {
        return (this.jobCompletedCount + this.jobMissDeadlineCount);
    }
    
}