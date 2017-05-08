/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import java.text.DecimalFormat;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.Vector;

/**
 *
 * @author ShiuJia
 */
public class Job implements Comparable
{
    private int ID; //代碼，J{task.ID, ID}
    private Task task; //所屬何Task
    private int releaseTime; // 被產生之時間
    private int absoluteDeadline; // 絕對截止時間
    private double progressAmount; //工作進度量
    private double targetAmount; //目標工作量
    private Priority originalPriority; //最初的優先權值
    private Priority currentPriority; //當前的優先權值
    private double pendingTime;//待機時間
    private double responseTime;//回應時間
    public boolean isInherit;
    private PriorityQueue<CriticalSection> lockResource; //尚未Lock之所需資源
    private Core locationCore;
    private Stack<LockInfo> lockedResources; //已Lock之所需資源
    private Priority preemptionLevel;
    private Vector<Result> resultSet;
    
    private int status = 0; // 0未完成; 1完成 ; -1MissDeadline
    private double timeOfStatus = 0;//改變狀態的當前時間
    
    public Job()
    {
        this.isInherit = false;
        this.lockResource = new PriorityQueue<CriticalSection>();
        this.lockedResources = new Stack<LockInfo>();
        this.originalPriority = new Priority();
        this.currentPriority = this.originalPriority;
        this.preemptionLevel = new Priority();
        this.resultSet = new Vector<Result>();
    }

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
            if(this.isInherit )
            {
                return -1;
            }
            
            return 1;
        }
        
        return 0;
    }
    
    public void setID(int i)
    {
        this.ID = i;
    }
    
    public int getID()
    {
        return this.ID;
    }
    
    public void setTask(Task t)
    {
        this.task = t;
    }
    
    public Task getTask()
    {
        return this.task;
    }
    
    public void setReleaseTime(int rt)
    {
        this.releaseTime = rt;
    }
    
    public int getReleaseTime()
    {
        return this.releaseTime;
    }
    
    public void setAbsoluteDeadline(int d)
    {
        this.absoluteDeadline = d;
        
        //設置PendingTime,ResponseTime初始值
        this.setPendingTime(this.absoluteDeadline - this.releaseTime);
        this.setResponseTime(this.absoluteDeadline - this.releaseTime);
    }
    
    public int getAbsoluteDeadline()
    {
        return this.absoluteDeadline;
    }
    
    public void setProgressAmount(double p)
    {
        this.progressAmount = p;
    }
    
    public double getProgressAmount()
    {
        return this.progressAmount;
    }
    
    public void setTargetAmount(double t)
    {
        this.targetAmount = t;
    }
    
    public double getTargetAmount()
    {
        return this.targetAmount;
    }

    public void setOriginalPriority(Priority p)
    {
        this.originalPriority.clonePriority(p);
    }
    
    public Priority getOriginalPriority()
    {
        return this.originalPriority;
    }

  
    public void currentPriorityOfInheritOrRevert()
    {
        System.out.println("@Job"+this.task.getID()+","+this.ID +" TP:" + this.task.getPriority().getValue()+ " JP:" + this.getCurrentPriority().getValue());
        System.out.println("ˇˇˇˇˇˇˇˇˇˇˇˇˇ");
        if(this.lockedResources.size() > 0)
        {
            Priority p = Final.Ohm;
            for(LockInfo l : this.lockedResources)
            {
                for(Job job : l.getResources().getWaitQueue())
                {
                    if(job.getLocationCore() == this.getLocationCore())
                    {
                        p = job.getCurrentPriority().getValue() > p.getValue() ? job.getCurrentPriority() : p;
                    }
                }
            }
            
            if(p != Final.Ohm)
            {
                this.currentPriority = p;
                this.isInherit = true;
            }
            else
            {
                this.currentPriority = this.originalPriority;
                this.isInherit = false;
            }
        }
        else
        {
            this.currentPriority = this.originalPriority;
            this.isInherit = false;
        }
        
        this.locationCore.getLocalReadyQueue().reSort();
        
        System.out.println("#Job"+this.task.getID()+","+this.ID +" TP:" + this.task.getPriority().getValue()+ " JP:" + this.getCurrentPriority().getValue());
    }

    public Priority getCurrentPriority()
    {
        return this.currentPriority;
    }
    
    public void setCriticalSectionSet()
    {
        for(int i = 0; i < this.getTask().getCriticalSectionSet().size(); i++)
        {
            CriticalSection newCriticalSection = new CriticalSection();
            //Clone
            newCriticalSection.setResources(this.getTask().getCriticalSectionSet().get(i).getResources());
            newCriticalSection.setStartTime(this.getTask().getCriticalSectionSet().get(i).getStartTime());
            newCriticalSection.setEndTime(this.getTask().getCriticalSectionSet().get(i).getEndTime());
            this.lockResource.offer(newCriticalSection);
        }
    }
    
    public CriticalSectionSet getCriticalSectionSet()
    {
        return this.getTask().getCriticalSectionSet();
    }
    
    public void setLocationCore(Core c)
    {
        this.locationCore = c;
    }
    
    public Core getLocationCore()
    {
        return this.locationCore;
    }
    
    public PriorityQueue<CriticalSection> getLockResource()
    {
        return this.lockResource;
    }
    
    public Stack<LockInfo> getLockedResource()
    {
        return this.lockedResources;
    }
    
    public void processed(double processedTime, double curTime)
    {
        if(this.progressAmount == 0)
        {
            this.setPendingTime(curTime-1 - this.releaseTime);
        }
        
        this.progressAmount += processedTime;
    }
    
    public void lock(Resources resources)
    {
        //Resources resources = cs.getResources();
        Resource resource = resources.getLockResource();
        resource.setLockedBy(this);
        this.locationCore.setResourceChange();
        this.lockedResources.push(new LockInfo(resources, resource.getID(), this.getLockResource().peek().getEndTime(), this.getCurrentPriority()));
    }
    
    public void unLock(Resources r)
    {
        r.checkWhoLockedResource(this).unlock();
        this.locationCore.setResourceChange();
    }
    
    public void setPreemptionLevel(Priority p)
    {
        this.preemptionLevel.clonePriority(p);
    }
    
    public Priority getPreemptionLevel()
    {
        return this.preemptionLevel;
    }
    
    public int isPriorityHigher(Priority p)
    {
        if(this.currentPriority.getValue() > p.getValue())
        {
            return 1;
        }
        else if(this.currentPriority.getValue() < p.getValue())
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
    
    public void setStatus(int sta , double time)
    {
        this.status = sta;
        
        switch(sta)
        {
            case -1:
                this.task.addJobMissDeadlineNum();
                break;
            case 0:
                break;
            case 1:
                this.task.addJobCompletedNum();
                break;
            default:
        }
        
        for(Result result: this.resultSet)
        {
            result.setJobCompletedNum(this.task.getJobCompletedNum());
            result.setJobMissDeadlineNum(this.task.getJobMissDeadlineNum());
        }
        
        this.setTimeOfStatus(time);
        this.setResponseTime(this.timeOfStatus - this.releaseTime);
    }
    
    public String getStatus()
    {
        switch(this.status)
        {
            case -1:
                return " MissDeadline ";
            case 0:
                return " Non Completed ";
            case 1:
                return " Completed ";
            default:
                return " Error!!! ";
        }
    }
    
    public void setTimeOfStatus(double time)
    {
        this.timeOfStatus = time;
        
    }
    
    public String getTimeOfStatus()
    {
        DecimalFormat df = new DecimalFormat("##.00000");
        
        if(this.timeOfStatus != 0)
        {
            return ""+Double.parseDouble(df.format(this.timeOfStatus/100000));
        }
        else
        {
            return ""+this.resultSet.get(this.resultSet.size()-1).getEndTime();
        }
    }
    
    public void setResponseTime(double time)
    {
        this.responseTime = time;
    }
    
    public double getResponseTime()
    {
        return this.responseTime;
    }
    
    public void setPendingTime(double time)
    {
        this.pendingTime = time;
    }
    
    public double getPendingTime()
    {
        return this.pendingTime;
    }
    
    public void addResult(Result result)
    {
        this.resultSet.add(result);
    }
    
    public Vector<Result> getResultSet()
    {
        return this.resultSet;
    }
}