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
public class EDF extends DynamicPrioritySchedulingAlgorithm
{    
    public EDF()
    {
        this.setName("Earliest Deadline First Scheduling Algorithm");
    }
    
    @Override
    public void setPriority(Job j)
    {
        j.setOriginalPriority(new Priority(j.getAbsoluteDeadline()));
    }
}


