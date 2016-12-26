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
public class CriticalSection implements Comparable
{
    private Resources resources; //所使用的資源
    private double relativeStartTime; //開始使用資源的相對時間
    private double relativeEndTime; //結束使用資源的相對時間
    
    public CriticalSection()
    {
        
    }
    
    @Override
    public int compareTo(Object o)
    {
        CriticalSection cs = (CriticalSection)o;
        if(this.relativeStartTime < cs.relativeStartTime)
        {
            return -1;
        }
        else if(this.relativeStartTime > cs.relativeStartTime)
        {
            return 1;
        }
        else if(this.relativeStartTime == cs.relativeStartTime)
        {
            if(this.relativeEndTime < cs.relativeEndTime)
            {
                return 1;
            }
            else if(this.relativeEndTime >= cs.relativeEndTime)
            {
                return -1;
            }
        }    
        return 0;
    }
    
    public void setResources(Resources r)
    {
        this.resources = r;
    }
    
    public Resources getResources()
    {
        return this.resources;
    }
    
    public void setStartTime(double st)
    {
        this.relativeStartTime = st;
    }
    
    public double getStartTime()
    {
        return this.relativeStartTime;
    }
    
    public void setEndTime(double et)
    {
        this.relativeEndTime = et;
    }
    
    public double getEndTime()
    {
        return this.relativeEndTime;
    }
}
