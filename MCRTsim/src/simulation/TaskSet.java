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


public class TaskSet extends Vector<Task>
{
    public long getScheduleTimeForTaskSet()//取得排程所需的時間
    {
        long lcmPeriod = this.getLcmOfPeriodForTaskSet();
        
        long biggestEnterTime = getBiggestEnterTime();
        
        return biggestEnterTime == 0 ? lcmPeriod : 200*100000;
        
    }
    
    private long getBiggestEnterTime()
    {
        long biggestEnterTime = 0;
        for(Task task : this)
        {
            biggestEnterTime = biggestEnterTime > task.getEnterTime() ? biggestEnterTime : task.getEnterTime();
        }
        return biggestEnterTime;
    }
    
    private long getLcmOfPeriodForTaskSet() // 取得TaskSet中所有工作的週期之最小公倍數
    {
        Equation e = new Equation();
        long lcm = this.get(0).getPeriod();
        for(int i = 1; i < this.size(); i++)
        {
            lcm = e.Math_lcm(lcm, this.get(i).getPeriod());
        }
        
        return lcm;
    }
    
    private long getLcmOfDeadlineForTaskSet() // 取得TaskSet中所有工作的Deadline之最小公倍數
    {
        Equation e = new Equation();
        long lcm = this.get(0).getRelativeDeadline();
        for(int i = 1; i < this.size(); i++)
        {
            lcm = e.Math_lcm(lcm, this.get(i).getRelativeDeadline());
        }
        
        return lcm;
    }
    
    public Task getTask(int i)
    {
        return this.get(i);
    }
    
    public double getUtilization()
    {
        double u = 0;
        for(Task t : this)
        {
            u += this.U(t);
        }
        return u;
    }
    
    private double U(Task task)
    {
        return ((double)task.getComputationAmount() + (double)this.B(task)) / (double)task.getPeriod();
    }
    
    private int B(Task task)
    {
        int maxBlock = 0;
        for(Task t : this)
        {
            if(t != task && task.isPriorityHigher(t.getPriority()) > 0)
            {
                for(CriticalSection cs : t.getCriticalSectionSet())
                {
                    if(cs.getResources().isPriorityHigher(task.getPriority()) >= 0)
                    {
                        maxBlock = (int)(cs.getEndTime() - cs.getStartTime());
                    }
                }
            }
        }
        return maxBlock;
    }
}
