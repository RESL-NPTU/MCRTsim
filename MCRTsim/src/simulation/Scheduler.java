/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import schedulingAlgorithm.PriorityDrivenSchedulingAlgorithm;

/**
 *
 * @author ShiuJia
 */
public class Scheduler
{
    private PriorityDrivenSchedulingAlgorithm scheduling;

    public Scheduler()
    {
        
    }
    
    public void setSchedAlgorithm(PriorityDrivenSchedulingAlgorithm algorithm)
    {
        this.scheduling = algorithm;
    }
    
    public PriorityDrivenSchedulingAlgorithm getSchedulingAlgorithm()
    {
        return this.scheduling;
    } 
    
    public void setPriority(TaskSet ts)
    {
        this.scheduling.setPriority(ts);
    }
    
    public JobQueue setPriority(JobQueue JQ)
    {
        JobQueue newJQ = new JobQueue();
        Job j = new Job();
        
        while((j = JQ.poll()) != null)
        {
            this.scheduling.setPriority(j);
            newJQ.offer(j);
        }
        
        return newJQ;
    }
}