/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WorkLoad;

import ResultSet.SchedulingInfo;
import SystemEnvironment.Core;
import SystemEnvironment.Processor;
import WorkLoadSet.CriticalSectionSet;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.Vector;
import mcrtsim.Definition.JobStatus;
import static mcrtsim.Definition.magnificationFactor;
import mcrtsim.MCRTsimMath;

/**
 *
 * @author ShiuJia
 */
public class Job implements Comparable
{
    private int ID; //代碼，J{parentTask.ID , ID}
    private Task parentTask; //所屬Task
    private long releaseTime; //被產生的時間
    private long absoluteDeadline; //絕對截止時間
    private long pendingTime;//待機時間
    private long responseTime;//回應時間
    private double targetAmount; //目標工作量
    private double progressAmount; //目前工作量
    private Priority originalPriority; //最初的優先權(來自Task)
    private Priority currentPriority; //目前的優先權
    private Priority inheritPriority;
    private Core originalCore;//第一次分配的Core
    private Core currentCore;//當前的分配的Core
    private Core previousCore;//上一個分配的Core，在每次migration時更新。
    private Processor localProcessor;
    private PriorityQueue<CriticalSection> criticalSectionSet;
    private Stack<CriticalSection> enteredCriticalSectionSet;
    private double maxProcessingSpeed;
    private Vector<SharedResource> resourceSet;
    private Vector<SchedulingInfo> schedulingInfoSet;
    private JobStatus status = JobStatus.NONCOMPUTE;
    private long timeOfStatus = 0;//改變狀態的當前時間
    
    private boolean isInherit;
    
    
    public Job()
    {
        this.ID = 0;
        this.parentTask = null;
        this.releaseTime = 0;
        this.absoluteDeadline = 0;
        this.targetAmount = 0;
        this.progressAmount = 0;
        this.originalPriority = null;
        this.currentPriority = null;
        this.originalCore = null;
        this.currentCore = null;
        this.localProcessor = null;
        this.criticalSectionSet = new PriorityQueue<CriticalSection>();
        this.enteredCriticalSectionSet = new Stack<CriticalSection>();
        this.maxProcessingSpeed = 0;
        this.schedulingInfoSet = new Vector<SchedulingInfo>();
        this.resourceSet = new Vector<SharedResource>();
        
        this.isInherit = false;
        this.inheritPriority = null;
    }

    /*Operating*/
    @Override
    public int compareTo(Object o)
    {
        Job j = (Job)o;
        
        if(this.currentPriority.getValue() > j.currentPriority.getValue())
        {
            return -1;
        }
        else if(this.currentPriority.getValue() < j.currentPriority.getValue())
        {
            return 1;
        }
        else if(this.currentPriority.getValue() == j.currentPriority.getValue())
        {
            if(this.isInherit)
            {
                return -1;
            }
            else if(this.getReleaseTime() < j.getReleaseTime())
            {
                return -1;
            }
            return 1;
        }
        
        return 0;
    }
    
    public void execute(double executionTime, long curTime)//當剩餘的工作量大於executionTime時被呼叫 (2017/7/22)
    {                                                        //目前每次執行的時間單位為executionTime = 1  (2017/7/22)
        if(this.progressAmount == 0)
        {
            this.setPendingTime(curTime - this.releaseTime);
       //     System.out.println("2Job("+this.ID+"): "+" pendingTime = "+this.pendingTime);
        }
        
        this.progressAmount += executionTime;//每次執行executionTime的時間單位 (2017/7/22)
      //  System.out.println(""+ this.progressAmount+","+executionTime);
        
    }
    
    public void finalExecute()//當剩餘的工作量小於executionTime時被呼叫，意味著在最後一次執行的時候被呼叫 (2017/7/22)
    {
        this.progressAmount = this.targetAmount;
    }
    
    public void lockSharedResource(SharedResource sr)
    {
        CriticalSection enterCS = this.criticalSectionSet.poll();
        sr.setLock(this, enterCS);
        this.enteredCriticalSectionSet.add(enterCS);
    }
    
    public void unLockSharedResource(SharedResource sr)
    {
        sr.setUnlock(this);
        this.enteredCriticalSectionSet.pop();
    }
    
    public void inheritBlockedJobPriority(Job j)
    {
        this.isInherit = true;
        this.inheritPriority = j.getCurrentProiority();
        this.setCurrentProiority(this.inheritPriority);
    }
    
    public void inheritPriority(Priority p)
    {
        this.isInherit = true;
        this.inheritPriority = p;
        this.setCurrentProiority(this.inheritPriority);
    }
    
    public void endInheritance()
    {
        this.isInherit = false;
        this.inheritPriority = null;
        this.setCurrentProiority(this.getOriginalPriority());
    }
    
    public void showInfo()
    {
        System.out.println("Job(" + this.parentTask.getID() + ", " + this.ID + "):");
        System.out.println("    ReleaseTime: " + this.releaseTime);
        System.out.println("    AbsoluteDeadline: " + this.absoluteDeadline);
        System.out.println("    TargetAmount: " + this.targetAmount);
        System.out.println("    OriginalPriority: " + this.originalPriority);
        System.out.println("    CriticalSection:");
        for(CriticalSection cs : this.criticalSectionSet)
        {
            System.out.println("        CriticalSection(" + cs.getUseSharedResource() + "):" + cs.getRelativeStartTime() + "/" + cs.getRelativeEndTime());
        }
        System.out.println();
    }
    
    public void migration(Core c)
    {
        this.previousCore = this.getCurrentCore();
        this.getCurrentCore().getLocalReadyQueue().remove(this);
        this.setCurrentCore(c);
        c.getLocalReadyQueue().add(this);
    }
    
    /*SetValue*/
    public void setID(int id)
    {
        this.ID = id;
    }
    
    public void setParentTask(Task t)
    {
        this.parentTask = t;
    }
    
    public void setReleaseTime(long t)
    {
        this.releaseTime = t;
        this.setTimeOfStatus(t);//初始狀態的時間為產生之時間
    }
    
    public void setAbsoluteDeadline(long d)
    {
        this.absoluteDeadline = d;
        
        //設置PendingTime,ResponseTime初始值
        this.setPendingTime(this.absoluteDeadline - this.releaseTime);
        this.setResponseTime(this.absoluteDeadline - this.releaseTime);
        //System.out.println("1Job("+this.ID+"): "+" pendingTime = "+this.pendingTime);
    }
    
    public void setTargetAmount(long a)
    {
        this.targetAmount = a;
    }
    
    public void setOriginalPriority(Priority p)
    {
        this.originalPriority = p;
    }
    
    public void setCurrentProiority(Priority p)
    {
        this.currentPriority = p;
        
        if(this.localProcessor != null && this.localProcessor.getGlobalReadyQueue().contains(this))
        {
            this.localProcessor.getGlobalReadyQueue().remove(this);
            this.localProcessor.getGlobalReadyQueue().add(this);
        }
        else if(this.currentCore != null && this.currentCore.getLocalReadyQueue().contains(this))
        {
            this.currentCore.getLocalReadyQueue().remove(this);
            this.currentCore.getLocalReadyQueue().add(this);
        }
    }
    
    public void setOriginCore(Core c)
    {
        this.originalCore = c;
    }
    
    public void setCurrentCore(Core c)
    {
        this.currentCore = c;
    }
    
    public void setPreviousCore(Core c)
    {
        this.previousCore = c;
    }
    
    public void setLocalProcessor(Processor p)
    {
        this.localProcessor = p;
    }
    
    public void setCriticalSectionSet(CriticalSectionSet css)
    {
        for(CriticalSection cs : css)
        {
            this.criticalSectionSet.add(cs);
            this.resourceSet.add(cs.getUseSharedResource());
        }
    }
    
    public void setMaxProcessingSpeed(double s)
    {
        this.maxProcessingSpeed = s;
    }
    
    public void addSchedulingInfo(SchedulingInfo s)
    {
        this.schedulingInfoSet.add(s);
    }
    
    /*GetValue*/
    public int getID()
    {
        return this.ID;
    }
    
    public Task getParentTask()
    {
        return this.parentTask;
    }
    
    public long getReleaseTime()
    {
        return this.releaseTime;
    }
    
    public long getAbsoluteDeadline()
    {
        return this.absoluteDeadline;
    }
    
    public double getTargetAmount()
    {
        return this.targetAmount;
    }
    
    public double getProgressAmount()
    {
        return this.progressAmount;
    }
    
    public Priority getOriginalPriority()
    {
        return this.originalPriority;
    }
    
    public Priority getCurrentProiority()
    {
        return this.currentPriority;
    }
    
    public Core getOriginCore()
    {
        return this.originalCore;
    }
    
    public Core getCurrentCore()
    {
        return this.currentCore;
    }
    
    public Core getPreviousCore()
    {
        return this.previousCore;
    }
    
    public Processor getLocalProcessor()
    {
        return this.localProcessor;
    }
    
    public PriorityQueue<CriticalSection> getCriticalSectionSet()
    {
        return this.criticalSectionSet;
    }
    
    public Stack<CriticalSection> getEnteredCriticalSectionSet()
    {
        return this.enteredCriticalSectionSet;
    }
    
    public double getMaxProcessingSpeed()
    {
        return this.maxProcessingSpeed;
    }
    
    public boolean isInherit()
    {
        return this.isInherit;
    }
    
    public Priority getInheritPriority()
    {
        return this.inheritPriority;
    }
    
    public Vector<SharedResource> getResourceSet()
    {
        return this.resourceSet;
    }
    
    public Vector<CriticalSection> getCriticalSectionArray()
    {
        Vector<CriticalSection> newCS = new Vector<CriticalSection>();
        PriorityQueue<CriticalSection> tempCS = new PriorityQueue<CriticalSection>();
        
        while(this.getCriticalSectionSet().size() != 0)
        {
            CriticalSection cs = this.getCriticalSectionSet().poll();
            newCS.add(cs);
            tempCS.add(cs);
        }
        this.criticalSectionSet = tempCS;
        
        return newCS;
    }
    
    public void setStatus(JobStatus sta , long time)
    {
        this.status = sta;
        
        switch(sta)
        {
            case NONCOMPUTE:
                //" NonCompleted"
                break;
            case COMPUTING:
                //" Computing"
                break;
            case COMPLETED:
                this.parentTask.addJobCompletedCount();
                break;
            case MISSDEADLINE:
                this.parentTask.addJobMissDeadlineCount();
                break;
            default:
        }
        
        for(SchedulingInfo schedulingInfo: this.schedulingInfoSet)
        {
            schedulingInfo.setJobCompletedNum(this.parentTask.getJobCompletedCount());
            schedulingInfo.setJobMissDeadlineNum(this.parentTask.getJobMissDeadlineCount());
        }
        
        this.setTimeOfStatus(time);
        this.setResponseTime(this.timeOfStatus - this.releaseTime);
    }
    
    public String getStatusString()
    {
        switch(this.status)
        { 
            case NONCOMPUTE:
                return " NonCompute ";
            case COMPUTING:
                return " Completed ";
            case COMPLETED:
                return " Completed ";
            case MISSDEADLINE:
                return " MissDeadline ";
            default:
                return " Error!!! ";
        }
    }
    
    public JobStatus getStatus()
    {
        return this.status;
    }
    
    public void setTimeOfStatus(long time)
    {
        this.timeOfStatus = time;
    }
    
    public String getTimeOfStatus()
    {
        
        MCRTsimMath math = new MCRTsimMath();
        if(this.timeOfStatus != 0)
        {
            return ""+ math.changeDecimalFormat((double)this.timeOfStatus/magnificationFactor);
        }
        else
        {
            return ""+this.schedulingInfoSet.lastElement().getEndTime();
        }
    }
    
    public void setResponseTime(long time)
    {
        this.responseTime = time;
    }
    
    public long getResponseTime()
    {
        return this.responseTime;
    }
    
    public void setPendingTime(long time)
    {
        this.pendingTime = time;
    }
    
    public long getPendingTime()
    {
        return this.pendingTime;
    }
    
}
