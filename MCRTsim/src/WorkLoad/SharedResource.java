/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WorkLoad;

import WorkLoadSet.TaskSet;
import java.util.Vector;

/**
 *
 * @author ShiuJia
 */
public class SharedResource extends Vector<Resource>
{
    private int ID;
    private TaskSet accessTaskSet;
    private Vector<Job> waitQueue;
    private int idleResourceNum;
    
    public SharedResource()
    {
        super();
        this.ID = 0;
        this.accessTaskSet = new TaskSet();
        this.waitQueue = new Vector<Job>();
        this.idleResourceNum = 0;
    }
    
    public void createResources(int n)
    {
        for(int i = 0; i < n; i++)
        {
            Resource r = new Resource();
            r.setID(i + 1);
            r.setParentResource(this);
            this.add(r);
        }
        this.idleResourceNum = n;
    }
    
    public void addAccessTask(Task t)
    {
        this.accessTaskSet.add(t);
    }
    
    public void setLock(Job j, CriticalSection cs)
    {
        for(Resource r : this)
        {
            if(r.whoLocked() == null)
            {
                r.setLockedBy(j);
                r.setRelativeEndTime(cs.getRelativeEndTime());
                cs.setResourceID(r.getID());
                this.idleResourceNum--;
                break;
            }
        }
    }
    
    public void setUnlock(Job j)
    {
        for(Resource r : this)
        {
            if(r.whoLocked() == j)
            {
                r.unlock();
                this.idleResourceNum++;
                this.releaseWaitQueueJob();
            }
        }
    }
    
    private void releaseWaitQueueJob()
    {
        for(Job j : this.waitQueue)
        {
            if(j.getCurrentCore() != null)
            {
                j.getCurrentCore().JobToCore(j);///???
            }
            else
            {
                j.getLocalProcessor().JobArrives(j);///???
            }
        }
        
        this.waitQueue.removeAllElements();
    }
    
    public Job getWhoLockedLastResource(Job j)
    {
        for (int i = this.getResourcesAmount()-1 ; i>=0 ; i--)
        {
            if(this.getResource(i).whoLocked() != null && this.getResource(i).whoLocked().getCurrentCore() == j.getCurrentCore())
            {
                return this.getResource(i).whoLocked();
            }
        }
        return null;
    }
    
    public void blockedJob(Job j)
    {
        this.waitQueue.add(j);
        if(j.getCurrentCore() != null && j.getCurrentCore().getLocalReadyQueue().contains(j))
        {
            j.getCurrentCore().getLocalReadyQueue().remove(j);
        }
        
        if(j.getLocalProcessor() != null && j.getLocalProcessor().getGlobalReadyQueue().contains(j))
        {
            j.getLocalProcessor().getGlobalReadyQueue().remove(j);
        }
    }
    
    public boolean isGlobal()
    {
        for(int i = 0; i < this.accessTaskSet.size() - 1; i++)
        {
            if(this.accessTaskSet.get(i).getLocalCore() != null)
            {
                for(int j = i + 1; j < this.accessTaskSet.size(); j++)
                {
                    if(this.accessTaskSet.get(j).getLocalCore() != null)
                    {
                        if(this.accessTaskSet.get(i).getLocalCore() != this.accessTaskSet.get(j).getLocalCore())
                        {
                            return true;
                        }
                    }
                    else
                    {
                        return true;
                    }
                }
            }
            else
            {
                return true;
            }
        }
        return false;
    }
    
    public void showInfo()
    {
        System.out.println("Resource(" + this.ID + "):");
        System.out.println("    AccessSet:");
        for(Task t : this.accessTaskSet)
        {
            System.out.println("        Task(" + t.getID() + ")");
        }
        System.out.println();
    }
    
    public void setID(int id)
    {
        this.ID = id;
    }
    
    public int getID()
    {
        return this.ID;
    }
    
    public Vector<Job> getWaitQueue()
    {
        return this.waitQueue;
    }
    
    public Job getWaitQueue(int i)
    {
        return this.waitQueue.get(i);
    }
    
    public Resource getResource(int i)
    {
        return this.get(i);
    }

    public int getResourcesAmount() 
    {
        return this.size();
    }
    
    public TaskSet getAccessTaskSet()
    {
        return this.accessTaskSet;
    }
    
    public int getIdleResourceNum()
    {
        return this.idleResourceNum;
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
}
