/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingAlgorithm;

import simulation.Job;
import simulation.TaskSet;

/**
 *
 * @author ShiuJia
 */
public class PriorityDrivenSchedulingAlgorithm
{
    private String algorithmName;
    public boolean isGlobalScheduling = false;
    
    public PriorityDrivenSchedulingAlgorithm()
    {
        
    }
    
    public void setName(String name)
    {
        this.algorithmName = name;
    }
    
    public String getName()
    {
        return this.algorithmName;
    }
    
    public void setPriority(TaskSet ts)
    {
        
    }
    
    public void setPriority(Job j)
    {
        
    }
}

