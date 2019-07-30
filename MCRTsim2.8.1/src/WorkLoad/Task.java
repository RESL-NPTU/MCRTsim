/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WorkLoad;

import SystemEnvironment.Core;
import WorkLoadSet.CriticalSectionSet;
import WorkLoadSet.TaskSet;
import java.util.PriorityQueue;
import java.util.Vector;
import mcrtsim.Definition.JobStatus;
import static mcrtsim.Definition.magnificationFactor;
import mcrtsim.MCRTsimMath;
import static mcrtsim.MCRTsim.println;

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
    private CriticalSectionSet criticalSectionSet; //執行過程中所需的CriticalSection
    private long totalCriticalSectionTime;//整體的CriticalSectionTime
    private TaskSet parentTaskSet; //所屬工作集合
    private Job curJob;
    private int jobMissDeadlineCount = 0;
    private int jobCompletedCount = 0;
    private Vector<Job> jobSet;
    
    private Vector<SharedResource> sharedResourceSet;
    
    private Vector<Nest> nestSet;
    
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
        this.sharedResourceSet = new Vector<SharedResource>();
        this.jobSet = new Vector<>();
        this.curJob = new Job();
        this.nestSet = new Vector<Nest>();
    }
    
    /*Operating*/
    public void addCriticalSection(CriticalSection cs)
    {
        this.criticalSectionSet.add(cs);
        this.sharedResourceSet.add(cs.getUseSharedResource());
        
        //排序用
        PriorityQueue<CriticalSection> PCS = new PriorityQueue<CriticalSection>();
        PCS.addAll(this.criticalSectionSet);
        this.criticalSectionSet.removeAllElements();
        while(!PCS.isEmpty())
        {
            this.criticalSectionSet.add(PCS.poll());
        }
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
        j.setMaxProcessingSpeed(this.ParentTaskSet().getProcessingSpeed());
        this.jobSet.add(j);
       // println("t("+ this.ID +","+ j.getID() +") :" + j.getReleaseTime() );
        return j;
    }
    
    public void showInfo()
    {
        println("Task(" + this.ID + "):");
        println("    EnterTime: " + this.enterTime);
        println("    RelativeDeadline: " + this.relativeDeadline);
        println("    ComputationAmount: " + this.computationAmount);
        println("    CriticalSection:");
        for(CriticalSection cs : this.criticalSectionSet)
        {
            println("        CriticalSection(R" + cs.getUseSharedResource().getID() + "):" + cs.getRelativeStartTime() + "/" + cs.getRelativeEndTime());
        }
        println();
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
    
    /*設定TotalCriticalSectionTime*/
    public void setTotalCriticalSectionTime()
    {
        CriticalSection criticalSection = null;
        for(CriticalSection cs : this.criticalSectionSet)
        {
            for(CriticalSection cs2 : this.criticalSectionSet)
            {
                if(cs != cs2 && cs2.getRelativeStartTime() <= cs.getRelativeStartTime() && cs.getRelativeEndTime() <= cs2.getRelativeEndTime())
                {
                    criticalSection = cs2;
                    break;
                }
            }
            
            if(criticalSection == null)
            {
                this.totalCriticalSectionTime += cs.getExecutionTime();
            }
            
            criticalSection = null;
        }   
    }
    
    public void addNest(Nest n)
    {
        this.nestSet.add(n);
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
    
    public TaskSet ParentTaskSet()
    {
        return this.parentTaskSet;
    }
    
    public Vector<SharedResource> getResourceSet()
    {
        return this.sharedResourceSet;
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
        return this.jobSet;
    }
        
    public int getJobCount()
    {
        return (this.jobCompletedCount + this.jobMissDeadlineCount);
    }
    
    public long getTotalCriticalSectionTime()
    {
        return this.totalCriticalSectionTime;
    }
    
    public double getAverageResponseTimeOfJob()
    {
        double time = 0;
        
        for(Job j : this.jobSet)
        {
            if(j.getStatus() == JobStatus.COMPLETED || j.getStatus() == JobStatus.MISSDEADLINE)
            {
                time = MCRTsimMath.add(time, j.getResponseTime());
            }
        }
        
        if(this.getJobCount() != 0)
        {
            return MCRTsimMath.div(time,this.getJobCount());
        }
        else
        {
            return 0;
        }
    }
    
    public double getAveragePendingTimeOfJob()
    {
        double time = 0;
        
        for(Job j : this.jobSet)
        {
            if(j.getStatus() == JobStatus.COMPLETED || j.getStatus() == JobStatus.MISSDEADLINE)
            {
                time = MCRTsimMath.add(time, j.getPendingTime());
            }
        }
        
        if(this.getJobCount() != 0)
        {
            return MCRTsimMath.div(time,this.getJobCount());
        }
        else
        {
            return 0;
        }
    }
    
    public double getAverageBeBlockedTimeRatioOfJob()
    {
        double ratio = 0;
        for(Job j : this.jobSet)
        {
            if(j.getStatus() == JobStatus.COMPLETED || j.getStatus() == JobStatus.MISSDEADLINE)
            {
                ratio = MCRTsimMath.add(ratio,j.getBeBlockedTimeRatio());
            }
        }
        
        if(this.getJobCount() != 0)
        {
            return MCRTsimMath.div(ratio,this.getJobCount());
        }
        else
        {
            return 0;
        }
    }
    
    public Vector<Nest> getNestSet()
    {
        return this.nestSet;
    }
    
    
}