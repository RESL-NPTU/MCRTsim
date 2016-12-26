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
public class Resources extends Vector<Resource>
{
    private int ID; //代碼
    private TaskSet accessSet; //所有將使用此資源的工作
    private Priority priorityCeiling;
    private Priority preemptionLevel;
    private Vector<Job> waitQueue; //被lockedBy所阻擋的Job
    private boolean isGlobal;
    private String status;
    
    public Resources()
    {
        super();
        this.accessSet = new TaskSet();
        this.priorityCeiling = new Priority();
        this.priorityCeiling = Final.Ohm;
        this.preemptionLevel = new Priority();
        this.preemptionLevel = Final.Ohm;
        this.waitQueue = new Vector<Job>();
        this.isGlobal = false;
        this.status = null;
    }
    
    public void setResourcesAmount(int amount)
    {
        for(int i = 0 ; i < amount ; i++)
        {
            this.add(new Resource(this , i+1));
        }
    }
    
    public int getResourcesAmount()
    {
        return this.size();
    }
    
    public void setID(int i)
    {
        this.ID = i;
    }
    
    public int getID()
    {
        return this.ID;
    }
    
    public TaskSet getAccessSet()
    {
        return this.accessSet;
    }
    
    public void setPriorityCeiling(Priority p)
    {
        this.priorityCeiling = p;
    }
    
    public Priority getPriorityCeiling()
    {
        return this.priorityCeiling;
    }
    
    public void setPreemptionLevelCeiling(Priority p)
    {
        this.preemptionLevel = p;
    }
    
    public Priority getPreemptionLevelCeiling()
    {
        return this.preemptionLevel;
    }
    
    public int isPriorityHigher(Priority p)
    {
        if(this.priorityCeiling.getValue() > p.getValue())
        {
            return 1;
        }
        else if(this.priorityCeiling.getValue() < p.getValue())
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
    
    public Resource getResource(int i)
    {
        return this.get(i);
    }
    
    public Resource getLockResource()
    {
        for(Resource res : this)
        {
            if(res.whoLocked() == null)
            {
                return res;
            }
        }
        return null;
    }
    
    public Resource checkWhoLockedResource(Job j)
    {
        for(Resource res : this)
        {
            if(res.whoLocked() == j)
            {
                return res;
            }
        }
        return null;
    }

    public Job getWhoLockedLastResource(Job j)
    {
        for (int i = this.getResourcesAmount()-1 ; i>=0 ; i--)
        {
            if(this.getResource(i).whoLocked() != null && this.getResource(i).whoLocked().getLocationCore() == j.getLocationCore())
            {
                return this.getResource(i).whoLocked();
            }
        }
        return null;
    }
            
    public int getLeftResourceAmount()
    {
        int i = 0;
        for(Resource res : this)
        {
            if(res.whoLocked() == null)
            {
                i += 1;
            }
        }
        return i ;
    }
    
    public void setWaitQueue(Vector<Job> jq)
    {
        this.waitQueue = jq;
    }
    
    public Vector<Job> getWaitQueue()
    {
        return this.waitQueue;
    }
    
    public void blocked(Job j)
    {
        this.waitQueue.add(j);
        j.getLocationCore().getLocalReadyQueue().remove(j);
        
        Job job = this.getWhoLockedLastResource(j);//相同Core才會return job
        if(job != null)
        {
            job.currentPriorityOfInheritOrRevert();
        }
    }
    
    public void setGlobal(boolean b)
    {
        this.isGlobal = b;
    }
    
    public void updateGlobal()
    {
        this.isGlobal = this.checkGlobal();
    }
    
    private boolean checkGlobal()
    {
        for(int i = 0; i < this.accessSet.size() - 1; i++)
        {
            for(int j = i + 1; j < this.accessSet.size(); j++)
            {
                if(this.accessSet.get(i).getCore() != this.accessSet.get(j).getCore())
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isGlobal()
    {
        return this.isGlobal;
    }
    
    //vvvvvv可能需要改成release Job to Processor
    
    public void releaseWaitQueueJob(Core c)
    {
        for(Job j : this.waitQueue)
        {
            if(j.getLocationCore() == c)
            {
                j.getLocationCore().addBlockedJob(j);
                this.waitQueue.remove(j);
            }
        }
        System.out.println("QQ");
    }
    
    public void releaseWaitQueueJob(Job j)
    {
        if(this.waitQueue.contains(j))
        {
            j.getLocationCore().addBlockedJob(j);
            this.waitQueue.remove(j);
        }
    }
    
    public void releaseWaitQueueAllJob()
    {
        for(int i = 0; i < this.waitQueue.size(); i++)
        {
            this.waitQueue.get(i).getLocationCore().addBlockedJob(this.waitQueue.get(i));
        }
        
        this.waitQueue.removeAllElements();
    }
    
    public void setStatus(String s)
    {
        this.status = s;
    }
    
    public String getStatus()
    {
        return this.status;
    }
}
