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
public class BF extends PartitionAlgorithm
{
    public BF()
    {
        this.setName("Best-Fit");
    }
    
    @Override
    public void taskToCore(Vector<Core> cores, TaskSet taskSet)
    {
        TaskSet decreasingTaskSet = new TaskSet();
        Vector<Core> non_decreasingCores = new Vector<Core>();
        double[] coreU = new double[cores.size()];
        for(int i = 0; i < cores.size(); i++)
        {
            non_decreasingCores.add(cores.get(i));
            
            coreU[i] = cores.get(i).getParentCoreSet().getCoreSpeedSet().getMaxFrequencyOfSpeed()
                     / taskSet.getProcessingSpeed();
        }
        
        
        for(Task t : taskSet)
        {
            decreasingTaskSet.add(t);
        }
        
        while(decreasingTaskSet.size() > 0)
        {
            non_decreasingCores.sort
            (
                new Comparator<Core>()
                {
                    @Override
                    public int compare(Core c1, Core c2) 
                    {
                        if(coreU[c1.getID()-1] > coreU[c2.getID()-1])
                        {
                            
                            return 1;
                        }
                        else if(coreU[c1.getID()-1] < coreU[c2.getID()-1])
                        {
                            return -1;
                        }
                        return 0;
                        
                    }
                }
            );
            
            
            for(int i = 0; i < coreU.length; i++)
            {
                if(coreU[i] > decreasingTaskSet.get(0).getUtilization())
                {
                    cores.get(i).addTask(decreasingTaskSet.get(0));
                    coreU[i] -= decreasingTaskSet.get(0).getUtilization();
                    decreasingTaskSet.remove(0);
                    break;
                }
            }
        }
    }
}
