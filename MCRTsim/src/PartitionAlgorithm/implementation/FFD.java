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
public class FFD extends PartitionAlgorithm
{
    public FFD()
    {
        this.setName("First-Fit Decreasing");
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
        
        decreasingTaskSet.sort
        (
            new Comparator<Task>()
            {
                @Override
                public int compare(Task t1, Task t2)
                {
                    if(t1.getUtilization() > t2.getUtilization())
                    {
                        return -1;
                    }
                    else if(t1.getUtilization() <= t2.getUtilization())
                    {
                        return 1;
                    }
                    return 0;
                }
            }
        );
        
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
