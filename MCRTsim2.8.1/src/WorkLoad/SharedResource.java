/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WorkLoad;

import WorkLoadSet.TaskSet;
import java.util.Vector;
import static mcrtsim.MCRTsim.println;

/**
 *
 * @author ShiuJia
 */
public class SharedResource extends Vector<Resource>
{
    private int ID;
    private TaskSet accessTaskSet;
    private Vector<Job> PIPQueue;
    private int idleResourceNum;
    private boolean isGlobal = false;//for MSRP
    
    public SharedResource()
    {
        super();
        this.ID = 0;
        this.accessTaskSet = new TaskSet();
        this.PIPQueue = new Vector<Job>();
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
            }
        }
    }
    
    public void releasePIPQueueJob()
    {   
        this.PIPQueue.removeAllElements();
    }
    
    public void addJob2PIPQueue(Job j)
    {
        this.PIPQueue.add(j);
    }

    public Job getWhoLockedLastResource(Job j)//between local and global resource 會有問題
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
    
    
    public void setIsGlobal()//for MSRP
    {
        //isGlobal預設是false
        for(int i = 0; i < this.accessTaskSet.size()-1; i++)
        {
            if(this.accessTaskSet.get(i).getLocalCore() != null)
            {
                for(int j = i + 1; j < this.accessTaskSet.size(); j++)
                {
                    if(this.accessTaskSet.get(j).getLocalCore() == null 
                    || this.accessTaskSet.get(i).getLocalCore() != this.accessTaskSet.get(j).getLocalCore())
                    {
                        isGlobal = true;
                        break;
                    }
                }
            }
            else
            {
                isGlobal = true;
                break;
            }
        }
        
    }
    public boolean isGlobal()//for MSRP
    {
        return this.isGlobal;
//        for(int i = 0; i < this.accessTaskSet.size() - 1; i++)
//        {
//            if(this.accessTaskSet.get(i).getLocalCore() != null)
//            {
//                for(int j = i + 1; j < this.accessTaskSet.size(); j++)
//                {
//                    if(this.accessTaskSet.get(j).getLocalCore() != null)
//                    {
//                        if(this.accessTaskSet.get(i).getLocalCore() != this.accessTaskSet.get(j).getLocalCore())
//                        {
//                            return true;
//                        }
//                    }
//                    else
//                    {
//                        return true;
//                    }
//                }
//            }
//            else
//            {
//                return true;
//            }
//        }
//        return false;
    }
    
    public void showInfo()
    {
        println("Resource(" + this.ID + "):");
        println("    AccessSet:");
        for(Task t : this.accessTaskSet)
        {
            println("        Task(" + t.getID() + ")");
        }
        println();
    }
    
    public void setID(int id)
    {
        this.ID = id;
    }
    
    public int getID()
    {
        return this.ID;
    }
    
    public Vector<Job> getPIPQueue()
    {
        return this.PIPQueue;
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
