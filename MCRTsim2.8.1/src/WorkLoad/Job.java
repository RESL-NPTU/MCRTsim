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
import mcrtsim.Definition;
import mcrtsim.Definition.JobStatus;
import static mcrtsim.Definition.magnificationFactor;
import mcrtsim.MCRTsimMath;
import static mcrtsim.MCRTsim.println;
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
    private long beBlockedTime;//被阻擋的時間
    private double targetAmount; //目標工作量
    private double progressAmount; //目前工作量
    private Priority originalPriority; //最初的優先權(來自Task)
    private Priority currentPriority; //目前的優先權
    private Priority inheritPriority;
    private Core originalCore;//第一次分配的Core
    private Core currentCore;//當前的分配的Core
    private Processor localProcessor;
    private Vector<CriticalSection> criticalSectionSet;
    private PriorityQueue<CriticalSection> notEnteredCriticalSectionSet;
    private Stack<CriticalSection> enteredCriticalSectionSet;
    private double maxProcessingSpeed;
    private Vector<SharedResource> resourceSet;
    private Vector<SchedulingInfo> schedulingInfoSet;
    private JobStatus status = JobStatus.NONCOMPUTE;
    private long timeOfStatus = 0;//改變狀態的當前時間
    
    /**
     * 以這個系統來說，同一個時間點Job只會因為一個資源而被阻擋
     * @blockingResource 直到此job被執行(指的是progressAmount增加)才會變成null
     */
    private SharedResource blockingResource = null;
            
    public boolean isInherit;
    public boolean isSuspended;
    
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
        this.criticalSectionSet = new Vector<CriticalSection>();
        this.notEnteredCriticalSectionSet = new PriorityQueue<CriticalSection>();
        this.enteredCriticalSectionSet = new Stack<CriticalSection>();
        this.maxProcessingSpeed = 0;
        this.schedulingInfoSet = new Vector<SchedulingInfo>();
        this.resourceSet = new Vector<SharedResource>();
        
        this.isInherit = false;
        this.inheritPriority = null;
        
        this.isSuspended = false;
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
        }
        
        this.setBlockingResource(null);
        
        this.progressAmount = MCRTsimMath.add(this.progressAmount,executionTime);//每次執行executionTime的時間單位 (2017/7/22)
    }
    
    public void finalExecute()//當剩餘的工作量小於executionTime時被呼叫，意味著在最後一次執行的時候被呼叫 (2017/7/22)
    {
        this.setBlockingResource(null);
        this.progressAmount = this.targetAmount;
    }
    
    public void lockSharedResource(SharedResource sr)
    {
        CriticalSection enterCS = this.notEnteredCriticalSectionSet.poll();
        sr.setLock(this, enterCS);
        this.enteredCriticalSectionSet.add(enterCS);
    }
    
    public void unLockSharedResource(SharedResource sr)
    {
        sr.setUnlock(this);
        this.enteredCriticalSectionSet.pop();
    }
    
    public void raisePriority(Priority p, int i)
    {
        Priority priority = new Priority(-p.getValue()-i);
        this.setCurrentProiority(priority);
    }
    
    public void inheritPriority(Priority p)
    {
        Priority priority = new Priority((-p.getValue())-1);
        if(!this.isInherit || priority.isHigher(this.currentPriority))
        {
            this.isInherit = true;
            this.inheritPriority = priority;
            if(!this.isSuspended)
            {
                this.setCurrentProiority(this.inheritPriority);
            }
        }
    }
    
    public void endInheritance()
    {
        /*檢查是否還有使用資源，若有，則進一步檢查是否有阻擋其他Job*/
        if(this.enteredCriticalSectionSet.isEmpty())
        {
            this.isInherit = false;
            this.inheritPriority = null;
            this.setCurrentProiority(this.getOriginalPriority());//還原優先權的部分
        }
    }
    
    public void recoverInheritance()
    {
        this.isInherit = false;
        if(!this.enteredCriticalSectionSet.isEmpty())
        {
            Object[] css = this.enteredCriticalSectionSet.toArray();
            Priority p = Definition.Ohm;
            
            for(int i = 0 ; i < css.length ; i++)
            {
                Vector<Job> waittingJobs = ((CriticalSection)css[i]).getUseSharedResource().getPIPQueue();
                
                for(Job j : waittingJobs)
                {
                    if(j.isInherit)
                    {
                        if(this.currentCore.equals(j.getCurrentCore()) && j.getInheritPriority().isHigher(p))
                        {
                            p = j.getInheritPriority();
                        }
                    }
                    else
                    {
                        if(this.currentCore.equals(j.getCurrentCore()) && j.getOriginalPriority().isHigher(p))
                        {
                            p = j.getOriginalPriority();
                        }
                    }
                }
            }
            
            if(p.equals(Definition.Ohm))
            {
                this.inheritPriority = null;
                this.setCurrentProiority(this.getOriginalPriority());
            }
            else
            {
                this.inheritPriority(p);
            }
        }
    }
    
    public void showInfo()
    {
        println("Job(" + this.parentTask.getID() + ", " + this.ID + "):");
        println("    ReleaseTime: " + this.releaseTime);
        println("    AbsoluteDeadline: " + this.absoluteDeadline);
        println("    TargetAmount: " + this.targetAmount);
        println("    OriginalPriority: " + this.originalPriority);
        println("    CriticalSection:");
        for(CriticalSection cs : this.notEnteredCriticalSectionSet)
        {
            println("        CriticalSection(" + cs.getUseSharedResource() + "):" + cs.getRelativeStartTime() + "/" + cs.getRelativeEndTime());
        }
        println();
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
        //println("1Job("+this.ID+"): "+" pendingTime = "+this.pendingTime);
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
        
        if(this.currentCore != null && this.currentCore.getLocalReadyQueue().contains(this))
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
    
    public void setLocalProcessor(Processor p)
    {
        this.localProcessor = p;
    }
    
    public void setCriticalSectionSet(CriticalSectionSet css)
    {
        for(CriticalSection cs : css)
        {
            this.criticalSectionSet.add(cs);
            this.notEnteredCriticalSectionSet.add(cs);
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
    
    /*找出cs下面一個的CriticalSection*/

    
    public Processor getLocalProcessor()
    {
        return this.localProcessor;
    }
    
    public CriticalSection getCriticalSection(SharedResource r)
    {
        for(CriticalSection cs : this.criticalSectionSet)
        {
            if(cs.getUseSharedResource().equals(r))
            {
                return cs;
            }
        }
        return null;
    }
    
    public Vector<CriticalSection> getCriticalSectionSet()
    {
        return this.criticalSectionSet;
    }
    
    public PriorityQueue<CriticalSection> getNotEnteredCriticalSectionSet()
    {
        return this.notEnteredCriticalSectionSet;
    }
    
    public Vector<CriticalSection> getNotEnteredCriticalSectionArray()
    {
        Vector<CriticalSection> newCS = new Vector<CriticalSection>();
        PriorityQueue<CriticalSection> tempCS = new PriorityQueue<CriticalSection>();
        
        while(this.getNotEnteredCriticalSectionSet().size() != 0)
        {
            CriticalSection cs = this.getNotEnteredCriticalSectionSet().poll();
            newCS.add(cs);
            tempCS.add(cs);
        }
        this.notEnteredCriticalSectionSet = tempCS;
        
        return newCS;
    }
    
    public Stack<CriticalSection> getEnteredCriticalSectionSet()
    {
        return this.enteredCriticalSectionSet;
    }
    
    public Vector<CriticalSection> getEnteredCriticalSectionArray()
    {
        Vector<CriticalSection> newCSs = new Vector<CriticalSection>();
        
        Object[] css = this.getEnteredCriticalSectionSet().toArray();
        for(int i = 0 ; i< css.length ; i++)
        {
            newCSs.add((CriticalSection)css[i]);
        }
        
        return newCSs;
    }
    
    public double getMaxProcessingSpeed()
    {
        return this.maxProcessingSpeed;
    }
    
    public Priority getInheritPriority()
    {
        return this.inheritPriority;
    }
    
    public Vector<SharedResource> getResourceSet()
    {
        return this.resourceSet;
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
//                if(this.status != JobStatus.COMPLETED)
//                {
                    this.parentTask.addJobCompletedCount();
                    println("@@COMPLETED  --  Job("+this.parentTask.getID()+","+this.getID()+")");
//                }
                break;
            case MISSDEADLINE:
//                if(this.status != JobStatus.MISSDEADLINE)
//                {    
                    println("@@MISSDEADLINE  --  Job("+this.parentTask.getID()+","+this.getID()+")");
                    this.parentTask.addJobMissDeadlineCount();
//                }
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
    
    public boolean migrateTo(Core nextCore)//只能在Core run之前被呼叫
    {
        if(this.currentCore.getCostQueue().isEmpty() 
                && this.status != JobStatus.COMPLETED 
                && this.status != JobStatus.MISSDEADLINE)
        {
            println("!!");
            println("currentCore:"+currentCore.getCurrentTime()+", getLocalReadyQueue: "+currentCore.getLocalReadyQueue().peek().getParentTask().getID()+","+currentCore.getLocalReadyQueue().peek().getID());
            println("nextCore:"+nextCore.getCurrentTime()+", getLocalReadyQueue: "+currentCore.getLocalReadyQueue().peek().getParentTask().getID()+","+currentCore.getLocalReadyQueue().peek().getID());
            println("Migration ~~~~~ :"+"Job("+this.parentTask.getID()+","+this.getID()+")"+", Core: "+this.currentCore.getID()+" to "+ nextCore.getID());
            println("!!");
            
            this.currentCore.getLocalReadyQueue().remove(this);
            this.currentCore.setMigrationCost(nextCore,this);
            nextCore.setMigrationCost(nextCore,this);
            
            this.setCurrentCore(nextCore);//需要等migration cost完成之後才加入nextCore LocalReadyQueue
            return true;
        }
        return false;
    }
    
    public void setBeBlockedTime(long l)
    {
        this.beBlockedTime += l;
    }
    
    public long getBeBlockedTime()
    {
        
        return this.beBlockedTime;
    }
    
    public double getBeBlockedTimeRatio()
    {
        println("T"+this.parentTask.getID()+" J"+this.ID+", beBlockedTime="+this.beBlockedTime);
        println("T"+this.parentTask.getID()+" J"+this.ID+", beBlockedTimeRatio="+MCRTsimMath.div(this.beBlockedTime,this.parentTask.getPeriod()) );
        
        return MCRTsimMath.div(this.beBlockedTime,this.parentTask.getPeriod()) ;
    }
    
    public void setSuspended(boolean b)
    {
        if(this.status != JobStatus.MISSDEADLINE || this.status != JobStatus.COMPLETED)
        {
            this.isSuspended = b;

            if(b)
            {   
                this.setCurrentProiority(Definition.Ohm);
            }
            else
            {
                if(this.isInherit)
                {
                    this.setCurrentProiority(this.inheritPriority);
                }
                else
                {
                    this.setCurrentProiority(this.originalPriority);
                }
            }
        }
    }
    
    public SharedResource getBlockingResource()
    {
        return this.blockingResource;
    }
    
    public void setBlockingResource(SharedResource r)
    {
        this.blockingResource = r;
    }
}
