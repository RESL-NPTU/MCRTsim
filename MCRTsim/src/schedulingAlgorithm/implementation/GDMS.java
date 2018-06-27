/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingAlgorithm.implementation;

import WorkLoad.Priority;
import WorkLoad.Task;
import WorkLoadSet.JobQueue;
import WorkLoadSet.TaskSet;
import mcrtsim.Definition;
import schedulingAlgorithm.GlobalSchedulingAlgorithm;

/**
 *
 * @author ShiuJia
 */
public class GDMS extends GlobalSchedulingAlgorithm
{
    public GDMS()
    {
        this.setName("Global Deadline Monotonic Scheduling Algorithm");
        this.setPriorityType(Definition.PriorityType.Fixed);
    }

    @Override
    public void calculatePriority(TaskSet ts)
    {
        for(Task t : ts)
        {
            t.setPriority(new Priority(t.getRelativeDeadline())); 
        }
    }

    @Override
    public JobQueue calculatePriority(JobQueue jq) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
