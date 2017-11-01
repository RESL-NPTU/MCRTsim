/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PartitionAlgorithm.implementation;

import PartitionAlgorithm.PartitionAlgorithm;
import SystemEnvironment.Core;
import WorkLoad.SharedResource;
import WorkLoad.Task;
import WorkLoadSet.TaskSet;
import java.util.Vector;

/**
 *
 * @author ShiuJia
 */
public class SBP extends PartitionAlgorithm
{
    public class Similarity
    {
        public Vector<SharedResource> similarityRes = new Vector<SharedResource>();
        public TaskSet similarityTask = new TaskSet(); 
    }
    
    public Vector<Similarity> similarityForAllTask = new Vector<Similarity>();
    public Vector<Similarity> similarityForTemp;
    Vector<TaskSet> partitionTasks = new Vector<TaskSet>();
    
    public SBP()
    {
        this.setName("Similarity-Based Partitioning");
    }
    
    @Override
    public void taskToCore(Vector<Core> cores, TaskSet taskSet)
    {
        TaskSet allTs = new TaskSet();
        
        double u = 0;
        for(Task t : taskSet)
        {
            allTs.add(t);
            u +=  ((double)t.getComputationAmount() / t.getPeriod());
        }
        
        System.out.println("U = " + u);
        u = u / (cores.size());
        System.out.println("U*= " + u);
        
        for(int i = 0; i < taskSet.size() - 1; i++)
        {
            for(int j = i + 1; j < taskSet.size(); j++)
            {
                this.similarityForTwoTasks(taskSet.get(i), taskSet.get(j));
            }
        }
        
        for(int i = 0; i < cores.size() - 1; i++)
        {
            TaskSet ts = new TaskSet();
            double tempU = 0;
            this.similarityForTemp = new Vector<Similarity>();
            
            while(tempU <= u  && this.similarityForAllTask.size() > 0)
            {
                Similarity maxSim;
                if(this.similarityForAllTask.size() > 0)
                {
                    System.out.println("Size= " + this.similarityForTemp.size());
                    if(this.similarityForTemp.isEmpty())
                    {
                        System.out.println("Size= ET");
                        maxSim = this.findMaxSimilarityTasks(this.similarityForAllTask);
                    }
                    else
                    {
                        System.out.println("Size= FT");
                        maxSim = this.findMaxSimilarityTasks(this.similarityForTemp);
                        this.similarityForTemp.remove(maxSim);
                        if(maxSim.similarityRes.size() <= 0)
                        {
                            maxSim = this.findMaxSimilarityTasks(this.similarityForAllTask);
                        }
                    }
                    
                    this.similarityForAllTask.remove(maxSim);
                    
                    for(int x = 0 ; x < this.similarityForAllTask.size(); x++)
                    {
                        if(this.similarityForAllTask.get(x).similarityTask.contains(maxSim.similarityTask.get(0)))
                        {
                            System.out.println("SBP= X*(" + this.similarityForAllTask.get(x).similarityTask.get(0).getID() + ", " + this.similarityForAllTask.get(x).similarityTask.get(1).getID() + ")");
                            if(!this.similarityForTemp.contains(this.similarityForAllTask.get(x)))
                            {
                                this.similarityForTemp.add(this.similarityForAllTask.get(x));
                            }
                        }
                    }
                    
                    for(int x = 0 ; x < this.similarityForAllTask.size(); x++)
                    {
                        if(this.similarityForAllTask.get(x).similarityTask.contains(maxSim.similarityTask.get(1)))
                        {
                            System.out.println("SBP= X*(" + this.similarityForAllTask.get(x).similarityTask.get(0).getID() + ", " + this.similarityForAllTask.get(x).similarityTask.get(1).getID() + ")");
                            if(!this.similarityForTemp.contains(this.similarityForAllTask.get(x)))
                            {
                                this.similarityForTemp.add(this.similarityForAllTask.get(x));
                            }
                        }
                    }
                    
                    System.out.println("START===============================");
                    for(Task t :maxSim.similarityTask)
                    {
                        if(allTs.contains(t))
                        {
                            System.out.println("SBP= Core(" + i + 1 + ") <= T(" + t.getID() + ")");
                            //t.setLocalCore(cores.get(i));
                            cores.get(i).addTask(t);
                            tempU += (double)t.getComputationAmount() / t.getPeriod();
                            allTs.remove(t);
                            System.out.println("SBP= U(" + i + 1 + ") = " + tempU);
                        }
                    }
                    System.out.println("E N D===============================");
                    
                }
            }
        }
        
        if(this.similarityForAllTask.size() > 0)
        {
            for(int i = 0; i < this.similarityForAllTask.size(); i++)
            {
                for(Task t : this.similarityForAllTask.get(i).similarityTask)
                {
                    if(allTs.contains(t))
                    {
                        //t.setLocalCore(cores.get(cores.size() - 1));
                        cores.get(cores.size() - 1).addTask(t);
                        allTs.remove(t);
                    }
                }
            }
        }
        
        for(Task t : taskSet)
        {
            System.out.println("SBP= " + t.getID() + ":" + t.getLocalCore().getID());
        }
    }
    
    public Vector<SharedResource> similarityForTwoTasks(Task t1, Task t2)
    {
        Vector<SharedResource> sr = new Vector<SharedResource>();
        
        for(SharedResource r : t2.getResourceSet())
        {
            if(t1.getResourceSet().contains(r))
            {
                sr.add(r);
            }
        }
        
        Similarity sim = new Similarity();
        sim.similarityRes = sr;
        sim.similarityTask.add(t1);
        sim.similarityTask.add(t2);
        
        this.similarityForAllTask.add(sim);
        
        System.out.println("====================");
        System.out.println("Task" + t1.getID());
        for(SharedResource r : t1.getResourceSet())
        {
            System.out.println("  Resource" + r.getID());
        }
        
        System.out.println("Task" + t2.getID());
        for(SharedResource r : t2.getResourceSet())
        {
            System.out.println("  Resource" + r.getID());
        }
        
        System.out.println("Task" + t1.getID() + ":" + t2.getID());
        for(SharedResource r : sr)
        {
            System.out.println("  Resource" + r.getID());
        }
        System.out.println("====================");
        
        return sr;
    }
    
    public Similarity findMaxSimilarityTasks(Vector<Similarity> set)
    {
        Similarity tempSim;
        if(set.size() > 0)
        {
            tempSim = set.get(0);
            for(int j = 0; j < set.size(); j++)
            {
                if(set.get(j).similarityRes.size() > tempSim.similarityRes.size())
                {
                    tempSim = set.get(j);
                }
            }
        }
        else
        {
            return null;
        }
        
        return tempSim;
    }
}
