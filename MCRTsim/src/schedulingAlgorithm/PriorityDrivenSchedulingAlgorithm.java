/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingAlgorithm;

import WorkLoadSet.JobQueue;
import WorkLoadSet.TaskSet;
import mcrtsim.Definition.PriorityType;
import mcrtsim.Definition.SchedulingType;

/**
 *
 * @author ShiuJia
 */
public class PriorityDrivenSchedulingAlgorithm
{
    private String name;
    private SchedulingType schedulingType;
    private PriorityType priorityType;
    
    public PriorityDrivenSchedulingAlgorithm()
    {
        this.name = null;
        this.priorityType = null;
        this.schedulingType = null;
    }
    
    public void setName(String n)
    {
        this.name = n;
    }
    
    public void setSchedulingType(SchedulingType t)
    {
        this.schedulingType = t;
    }
    
    public void setPriorityType(PriorityType t)
    {
        this.priorityType = t;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public SchedulingType getSchedulingType()
    {
        return this.schedulingType;
    }
    
    public PriorityType getPriorityType()
    {
        return this.priorityType;
    }
    
    public void calculatePriority(TaskSet ts){}
    public JobQueue calculatePriority(JobQueue jq)
    {
        return null;
    }
}
