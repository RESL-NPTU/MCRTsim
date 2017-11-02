/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WorkLoadSet;

import WorkLoad.CriticalSection;
import WorkLoad.Job;
import WorkLoad.Task;
import java.util.Vector;
import static mcrtsim.Definition.magnificationFactor;
import mcrtsim.MCRTsimMath;

/**
 *
 * @author ShiuJia
 */
public class TaskSet extends Vector<Task>
{
    private double maxProcessingSpeed; // 單位=MHz
    
    public TaskSet()
    {
        super();
        this.maxProcessingSpeed = 0;
    }
    
    public void setMaxProcessingSpeed(double s)
    {
        this.maxProcessingSpeed = s;
    }
    
    public Task getTask(int i)
    {
        return this.get(i);
    }
    
    public long getScheduleTimeForTaskSet()//取得排程所需的時間
    {
        long lcmPeriod = this.getLcmOfPeriodForTaskSet();
        
        long biggestEnterTime = getBiggestEnterTime();
        
        return biggestEnterTime == 0 ? lcmPeriod : 200*magnificationFactor;
        
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
        MCRTsimMath e = new MCRTsimMath();
        long lcm = this.get(0).getPeriod();
        for(int i = 1; i < this.size(); i++)
        {
            lcm = e.Math_lcm(lcm, this.get(i).getPeriod());
        }
        
        return lcm;
    }
    
    private long getLcmOfDeadlineForTaskSet() // 取得TaskSet中所有工作的Deadline之最小公倍數
    {
        MCRTsimMath e = new MCRTsimMath();
        long lcm = this.get(0).getRelativeDeadline();
        for(int i = 1; i < this.size(); i++)
        {
            lcm = e.Math_lcm(lcm, this.get(i).getRelativeDeadline());
        }
        
        return lcm;
    }
    
    public double getMaxProcessingSpeed()
    {
        return this.maxProcessingSpeed;
    }
    
    public int getTotalJobNumber()
    {
        int num = 0;
        for(Task t : this)
        {
            num += t.getJobCount();
        }
        return num;
    }
    
    public int getTotalJobCompletedNumber()
    {
        int num = 0;
        for(Task t : this)
        {
            num += t.getJobCompletedCount();
        }
        return num;
    }
    
    public int getTotalJobMissDeadlineNumber()
    {
        int num = 0;
        for(Task t : this)
        {
            num += t.getJobMissDeadlineCount();
        }
        return num;
    }
    
    public double getTotalJobPendingTime()
    {
        double time = 0;
        for(Task t : this)
        {
            for(int i = 0 ; i < t.getJobCount() ; i++)
            {
                time += t.getJobSet().get(i).getPendingTime();
            }
        }
        return time/magnificationFactor;
    }
    
    public double getTotalAverageJobPendingTime()
    {
        if(this.getTotalJobNumber()!=0)
        {
            return this.getTotalJobPendingTime() / this.getTotalJobNumber();
        }
        else
        {
            return -1;
        }
    }
    
    public double getTotalJobResponseTime()
    {
        double time = 0;
        for(Task t : this)
        {
            for(int i = 0 ; i < t.getJobCount() ; i++)
            {
                time += t.getJobSet().get(i).getResponseTime();
            }
        }
        return time/magnificationFactor;
    }
    
    public double getTotalAverageJobResponseTime()
    {
        return this.getTotalJobResponseTime() / this.getTotalJobNumber();
    }
}
