/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PartitionAlgorithm.implementation;

import PartitionAlgorithm.PartitionAlgorithm;
import SystemEnvironment.Core;
import WorkLoad.Task;
import WorkLoadSet.TaskSet;
import java.util.Comparator;
import java.util.Vector;

/**
 *
 * @author ShiuJia
 */
public class FF extends PartitionAlgorithm
{
    public FF()
    {
        this.setName("First-Fit");
    }
    
    @Override
    public void taskToCore(Vector<Core> cores, TaskSet taskSet)
    {
        TaskSet decreasingTaskSet = new TaskSet();
        double[] coreU = new double[cores.size()];
        for(int i = 0; i < cores.size(); i++)
        {
            coreU[i] = cores.get(i).getParentCoreSet().getCoreSpeedSet().getMaxFrequencyOfSpeed()
                     / taskSet.getProcessingSpeed();
        }
        
        for(Task t : taskSet)
        {
            decreasingTaskSet.add(t);
        }
        
        while(decreasingTaskSet.size() > 0)
        {
            boolean isAssigned = false;
            for(int i = 0; i < coreU.length; i++)
            {
                if(coreU[i] > decreasingTaskSet.get(0).getUtilization())
                {
                    cores.get(i).addTask(decreasingTaskSet.get(0));
                    coreU[i] -= decreasingTaskSet.get(0).getUtilization();
                    decreasingTaskSet.remove(0);
                    isAssigned = true;
                    break;
                }
            }
            
            if(!isAssigned)
            {
                cores.get(0).addTask(decreasingTaskSet.get(0));
                coreU[0] -= decreasingTaskSet.get(0).getUtilization();
                decreasingTaskSet.remove(0);
                isAssigned = true;
            }
        }
    }
}
