/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import java.util.PriorityQueue;
import java.util.Stack;

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
    public boolean isInherit;
    private PriorityQueue<CriticalSection> lockResource; //尚未Lock之所需資源
    private Core locationCore;
    private Stack<LockInfo> lockedResources; //已Lock之所需資源
    private Priority preemptionLevel;
    
    public Job()
    {
        this.isInherit = false;
        this.lockResource = new PriorityQueue<CriticalSection>();
        this.lockedResources = new Stack<LockInfo>();
        this.originalPriority = new Priority();
        this.currentPriority = this.originalPriority;
        this.preemptionLevel = new Priority();
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
            if(this.isInherit)
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
    
    public void processed(double processedTime)
    {
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
}