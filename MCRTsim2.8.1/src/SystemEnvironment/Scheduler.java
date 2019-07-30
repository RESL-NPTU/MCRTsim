/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemEnvironment;

import WorkLoad.Job;
import WorkLoadSet.JobQueue;
import WorkLoadSet.TaskSet;
import mcrtsim.Definition;
import schedulingAlgorithm.PriorityDrivenSchedulingAlgorithm;

/**
 *
 * @author ShiuJia
 */
public class Scheduler
{
    private PriorityDrivenSchedulingAlgorithm algorithm;
    private Processor parentProcessor; //當此為一多核心Global Scheduler
    private Core parentCore; //當此為一多核心Local Scheduler或者單核心Scheduler
    
    public Scheduler()
    {
        this.algorithm = null;
        this.parentProcessor = null;
        this.parentCore = null;
    }
    
    /*Operating*/
    public void calculatePriority(TaskSet ts)
    {
        this.algorithm.calculatePriority(ts);
    }
    
    public JobQueue calculatePriority(JobQueue jp)//此方法只有在動態排程方法時被呼叫
    {
        JobQueue newJQ = this.algorithm.calculatePriority(jp);//動態的排程方法每個時間單位都會重新計算新的Proiority
        JobQueue inheritJQ = new JobQueue();
        Job tempJob;
        while((tempJob = newJQ.poll()) != null)
        {
            tempJob.setOriginalPriority(tempJob.getCurrentProiority());//因為是動態的排程方法，因此新的Proiority也需要更改到OriginalPriority
            
            if(tempJob.isInherit)
            {
                tempJob.setCurrentProiority(tempJob.getInheritPriority());
            }
            
            if(tempJob.isSuspended)
            {
                tempJob.setCurrentProiority(Definition.Ohm);
            }
            
            inheritJQ.add(tempJob);
        }
        return inheritJQ;
    }
    
    /*SetValue*/
    public void setSchedAlgorithm(PriorityDrivenSchedulingAlgorithm a)
    {
        this.algorithm = a;
    }
    
    public void setParentProcessor(Processor p)
    {
        this.parentProcessor = p;
    }
    
    public void setParentCore(Core c)
    {
        this.parentCore = c;
    }
    
    /*GetValue*/
    public PriorityDrivenSchedulingAlgorithm getSchedAlgorithm()
    {
        return this.algorithm;
    }
    
    public Processor getParentProcessor()
    {
        return this.parentProcessor;
    }
    
    public Core getParentCore()
    {
        return this.parentCore;
    }
}
