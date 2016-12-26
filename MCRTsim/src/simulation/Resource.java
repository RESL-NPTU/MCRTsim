/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

/**
 *
 * @author ShiuJia
 */
public class Resource
{
    private Resources parent;
    private int ID;
    private Job lockedBy; //鎖定使用權的Job
   
    
    public Resource(Resources r,int i)
    {
        this.parent = r;
        this.ID = i;
        this.lockedBy = null;
    }
    
    public void setID(int i)
    {
        this.ID = i;
    }
    
    public int getID()
    {
        return this.ID;
    }
    
    public Job whoLocked()
    {
        return this.lockedBy;
    }
    
    public void setLockedBy(Job j)
    {
        this.lockedBy = j;
    }
    
    public void unlock()
    {
        this.lockedBy = null;
    }
   
    public Resources getParent()
    {
        return this.parent;
    }
}
