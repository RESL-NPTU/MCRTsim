/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WorkLoad;

import java.util.Vector;


/**
 *
 * @author ShiuJia
 */
public class CriticalSection implements Comparable
{
    private SharedResource useSharedResource; //使用資源
    private long relativeStartTime; //開始時間
    private long relativeEndTime; //結束時間
    private long executionTime; //執行時間

    private int resourceID;
    
    private Nest nest = null;
    private CriticalSection outsideCriticalSection=null;
    private Vector<CriticalSection> innerCriticalSection = new Vector<CriticalSection>();
    
    public CriticalSection()
    {
        this.useSharedResource = null;
        this.relativeStartTime = 0;
        this.relativeEndTime = 0;
        this.resourceID = 0;
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
        else
        {
            if(this.relativeEndTime < cs.relativeEndTime)
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }
    }
    
    /*SetValue*/
    public void setUseSharedResource(SharedResource r)
    {
        this.useSharedResource = r;
    }
    
    public void setRelativeStartTime(long t)
    {
        this.relativeStartTime = t;
    }
    
    public void setRelativeEndTime(long t)
    {
        this.relativeEndTime = t;
        
        this.executionTime = this.relativeEndTime - this.relativeStartTime;
    }
    
    public void setResourceID(int rID)
    {
        this.resourceID = rID;
    }
    
    public void setNests(Nest n)
    {
        this.nest=n;
    }
    
    public void addInnerCriticalSection(CriticalSection c)
    {
        innerCriticalSection.add(c);
    }
    
    public void setOutsideCriticalSection(CriticalSection c)
    {
        outsideCriticalSection = c;
    }
    
    /*GetValue*/
    public SharedResource getUseSharedResource()
    {
        return this.useSharedResource;
    }
    
    public long getRelativeStartTime()
    {
        return this.relativeStartTime;
    }
    
    public long getRelativeEndTime()
    {
        return this.relativeEndTime;
    }
    
    public long getExecutionTime()
    {
        return this.executionTime;
    }
    
    public int getResourceID()
    {
        return this.resourceID;
    }
    
    public Nest getNsets()
    {
        return this.nest;
    }
    
    public CriticalSection getInnerCriticalSection(int i)
    {
        return innerCriticalSection.get(i);
    }
    
    public CriticalSection getOutsideCriticalSection()
    {
        return outsideCriticalSection;
    }
}
