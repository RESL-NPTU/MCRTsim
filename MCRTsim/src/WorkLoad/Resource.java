/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WorkLoad;

/**
 *
 * @author ShiuJia
 */
public class Resource
{
    private int ID;
    private SharedResource parentResource;
    private Job lockedBy;
    private double relativeEndTime;
    
    public Resource()
    {
        this.ID = 0;
        this.parentResource = null;
        this.lockedBy = null;
        this.relativeEndTime = 0;
    }
    
    /*Operating*/
    public void setLockedBy(Job j)
    {
        this.lockedBy = j;
    }
    
    public void unlock()
    {
        this.lockedBy = null;
        this.relativeEndTime = 0;
    }
    
    public Job whoLocked()
    {
        return this.lockedBy;
    }
    
    /*SetValue*/
    public void setID(int id)
    {
        this.ID = id;
    }
    
    public void setParentResource(SharedResource r)
    {
        this.parentResource = r;
    }
    
    public void setRelativeEndTime(double t)
    {
        this.relativeEndTime = t;
    }
    
    /*GetValue*/
    public int getID()
    {
        return this.ID;
    }
    
    public SharedResource getParentResource()
    {
        return this.parentResource;
    }
    
    public double getRelativeEndTime()
    {
        return this.relativeEndTime;
    }
}
