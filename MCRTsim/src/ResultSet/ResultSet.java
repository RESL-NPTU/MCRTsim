/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ResultSet;

import SystemEnvironment.Core;
import java.util.Vector;

/**
 *
 * @author ShiuJia
 */
public class ResultSet
{
    private Vector<Core> cores;
    private Vector<MissDeadlineInfo> missDeadlineInfoSet;
    
    public ResultSet()
    {
        this.cores = new Vector<Core>();
        this.missDeadlineInfoSet = new Vector<MissDeadlineInfo>();
    }
    
    //public void addSchedulingInfo(Vector<SchedulingInfo> s)
    public void addCoreInfo(Core c)
    {
        this.cores.add(c);
    }
    
    public void addMissDeadlineInfo(MissDeadlineInfo md)
    {
        this.missDeadlineInfoSet.add(md);
    }
    
    //public SchedulingInfo getSchedulingInfo(int n)
    public Core getCoreInfo(int n)
    {
        return this.cores.get(n);
    }
    
    public Vector<MissDeadlineInfo> getMissDeadlineInfoSet()
    {
        return this.missDeadlineInfoSet;
    }
    
    /*public SchedulingInfo getLastSchedulingInfo()
    {
        return this.coreLocalSchedulingInfoSet.lastElement();
    }
    
    public int getSchedulingInfoNum()
    {
        return this.coreLocalSchedulingInfoSet.size();
    }*/
}
