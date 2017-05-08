/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingAlgorithm.implementation;

import schedulingAlgorithm.DynamicPrioritySchedulingAlgorithm;
import simulation.Job;
import simulation.Priority;

/**
 *
 * @author ShiuJia
 */
public class PEDF extends DynamicPrioritySchedulingAlgorithm
{    
    public PEDF()
    {
        this.setName("Partitioned Earliest Deadline First Scheduling Algorithm");
        this.isGlobalScheduling = false;
    }
    
    @Override
    public void setPriority(Job j)
    {
        j.setOriginalPriority(new Priority(j.getAbsoluteDeadline()));
    }
}


