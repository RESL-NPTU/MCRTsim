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
public class LockInfo
{
    private Resources resources; //鎖定的Resource
    private double relativeEndTime;
    private int resourceID;
    
    public LockInfo(Resources r , int reID, double rt, Priority p)
    {
        this.resources = r;
        this.relativeEndTime = rt;
        
        this.resourceID = reID;
    }
    
    public Resources getResources()
    {
        return this.resources;
    }
    
    public Resource getResource()
    {
        return this.resources.getResource(resourceID-1);
    }
    
    public double getEndTime()
    {
        return this.relativeEndTime;
    }
}