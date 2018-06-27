/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WorkLoadSet;


import WorkLoad.Job;
import WorkLoad.Nest;
import WorkLoad.Task;
import java.util.Vector;
import mcrtsim.Definition;
import mcrtsim.Definition.JobStatus;
import static mcrtsim.Definition.magnificationFactor;
import static mcrtsim.MCRTsim.print;
import static mcrtsim.MCRTsim.println;
import mcrtsim.MCRTsimMath;

/**
 *
 * @author ShiuJia
 */
public class TaskSet extends Vector<Task>
{
    private double maxProcessingSpeed; // 單位=MHz
    private double maximumCriticalSectionRatio,actualCriticalSectionRatio,maximumUtilization,actualUtilization;
    
    public TaskSet()
    {
        super();
        this.maxProcessingSpeed = 0;
    }
    
    public void setProcessingSpeed(double s)
    {
        this.maxProcessingSpeed = s;
    }
    
    public void setMaximumCriticalSectionRatio(double ratio)
    {
        this.maximumCriticalSectionRatio = ratio;
    }
    
    public void setActualCriticalSectionRatio(double ratio)
    {
        this.actualCriticalSectionRatio = ratio;
    }
    
    public void setMaximumUtilization(double U)
    {
        this.maximumUtilization = U;
    }
    
    public void setActualUtilization(double U)
    {
        this.actualUtilization = U;
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
    
    public double getProcessingSpeed()
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
    
    public double getJobCompletedRatio()
    {
        if(this.getTotalJobNumber() !=0)
        {
            return MCRTsimMath.div(this.getTotalJobCompletedNumber(), this.getTotalJobNumber());
        }
        else
        {
            return 0;
        }
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
    
    public double getJobMissDeadlineRatio()
    {
        if(this.getTotalJobNumber() !=0)
        {
            return MCRTsimMath.div(this.getTotalJobMissDeadlineNumber(), this.getTotalJobNumber());
        }
        else
        {
            return 0;
        }
    }
    
    public double getAveragePendingTimeOfTask()
    {
        double time = 0;
        for(Task t : this)
        {
            time = MCRTsimMath.add(time,t.getAveragePendingTimeOfJob());
        }
        
        return MCRTsimMath.div(MCRTsimMath.div(time, this.size()),magnificationFactor);
    }
    
    public double getAverageResponseTimeOfTask()
    {
        double time = 0;
        for(Task t : this)
        {
            time = MCRTsimMath.add(time,t.getAverageResponseTimeOfJob());
        }
        
        return MCRTsimMath.div(MCRTsimMath.div(time, this.size()),magnificationFactor);
    }
    
    public double getAverageBeBlockedTimeRatioOfTask()
    {
        double ratio = 0;
        for(Task t : this)
        {
            ratio = MCRTsimMath.add(ratio,t.getAverageBeBlockedTimeRatioOfJob());  
        }
        
        return MCRTsimMath.div(MCRTsimMath.div(ratio, this.size()),magnificationFactor);
    }
    
    public double getTotalUtilization()
    {
        double U = 0;
        
        for(Task t : this)
        {
            U+=t.getUtilization();
        }
        
        return U;
    }
    
    public double getMaximumCriticalSectionRatio()
    {
        return this.maximumCriticalSectionRatio;
    }
    
    public double getActualCriticalSectionRatio()
    {
        return this.actualCriticalSectionRatio;
    }
    
    public double getMaximumUtilization()
    {
        return this.maximumUtilization;
    }
    
    public double getActualUtilization()
    {
        return this.actualUtilization;
    }
    
    public void setNestSetForTask()
    {
        for(int i=0; i<this.size() ; i++)
        {
            Nest nest = null;
            for(int j=0;j<this.getTask(i).getCriticalSectionSet().size();j++)
            {
                if(j!=0)
                {
                    if(this.getTask(i).getCriticalSectionSet().get(j-1).getRelativeStartTime()<=this.getTask(i).getCriticalSectionSet().get(j).getRelativeStartTime())
                    {
                        if(this.getTask(i).getCriticalSectionSet().get(j-1).getRelativeEndTime()>=this.getTask(i).getCriticalSectionSet().get(j).getRelativeEndTime())
                        {
                            this.getTask(i).getCriticalSectionSet().get(j-1).addInnerCriticalSection(this.getTask(i).getCriticalSectionSet().get(j));
                            this.getTask(i).getCriticalSectionSet().get(j).setOutsideCriticalSection(this.getTask(i).getCriticalSectionSet().get(j-1));
                            if(this.getTask(i).getCriticalSectionSet().get(j).getOutsideCriticalSection()==null)
                            {
                                nest = new Nest(getTask(i));
                                nest.addCriticalSection(this.getTask(i).getCriticalSectionSet().get(j));
                                this.getTask(i).addNest(nest);
                            }
                            else 
                            {
                                nest.addCriticalSection(this.getTask(i).getCriticalSectionSet().get(j));
                            }
                        }
                        else
                        {
                            int temp=2;
                            boolean run=true;
                            
                            while(j-temp>=0 && run)
                            {
                                
                                if(this.getTask(i).getCriticalSectionSet().get(j-temp).getRelativeEndTime() >= this.getTask(i).getCriticalSectionSet().get(j).getRelativeEndTime())
                                {
                                    
                                    this.getTask(i).getCriticalSectionSet().get(j-temp).addInnerCriticalSection(this.getTask(i).getCriticalSectionSet().get(j));
                                    this.getTask(i).getCriticalSectionSet().get(j).setOutsideCriticalSection(this.getTask(i).getCriticalSectionSet().get(j-temp));
                                    if(this.getTask(i).getCriticalSectionSet().get(j).getOutsideCriticalSection()==null)
                                    {
                                        nest = new Nest(this.getTask(i));
                                        nest.addCriticalSection(this.getTask(i).getCriticalSectionSet().get(j));
                                        this.getTask(i).addNest(nest);
                                        run=false;
                                    }
                                    else
                                    {
                                        nest.addCriticalSection(this.getTask(i).getCriticalSectionSet().get(j));
                                        run=false;
                                    }
                                }
                                temp++;
                            }
                            if(run)
                            {
                                nest = new Nest(this.getTask(i));
                                nest.addCriticalSection(this.getTask(i).getCriticalSectionSet().get(j));
                                this.getTask(i).addNest(nest);
                            }
                        }
                    }
                    
                }
                else
                {
                    nest = new Nest(this.getTask(i));
                    nest.addCriticalSection(this.getTask(i).getCriticalSectionSet().get(j));
                    this.getTask(i).addNest(nest);
                } 
            } 
        }
        
        
        for(int i=0;i<this.size();i++)
        {
            print("Task"+this.get(i).getID());
            for(int j=0;j<this.get(i).getNestSet().size();j++)
            {
                print(" have "+this.get(i).getNestSet().size()+" :");
                for(int k=0;k<this.get(i).getNestSet().get(j).size();k++)
                {
                    print(" R"+this.get(i).getNestSet().get(j).get(k).getUseSharedResource().getID());
                }
            }
            println("");
        } 
        println("=======================");
        for(int i=0;i<this.size();i++)
        {
            print("Task"+this.get(i).getID());
            for(int j=0;j<this.get(i).getNestSet().size();j++)
            {
                print(" have "+this.get(i).getNestSet().size()+" :");
                for(int k=0;k<this.get(i).getNestSet().get(j).size();k++)
                {
                    print(" R"+this.get(i).getNestSet().get(j).get(k).getUseSharedResource().getID());
                    if(this.get(i).getNestSet().get(j).get(k).getOutsideCriticalSection()!=null)
                    {
                        print("( R"+this.get(i).getNestSet().get(j).get(k).getUseSharedResource().getID());
                        print(" is inner R"+this.get(i).getNestSet().get(j).get(k).getOutsideCriticalSection().getUseSharedResource().getID()+")");
                    }
                }
            }
            println("");
        } 
    }
    
    
    
}
