/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingAlgorithm.implementation;

import WorkLoad.Job;
import WorkLoad.Priority;
import WorkLoadSet.JobQueue;
import WorkLoadSet.TaskSet;
import mcrtsim.Definition;
import schedulingAlgorithm.GlobalSchedulingAlgorithm;

/**
 *
 * @author ShiuJia
 */
public class GEDF extends GlobalSchedulingAlgorithm
{
    public GEDF()
    {
        this.setName("Global Earliest Deadline First Scheduling Algorithm");
        this.setPriorityType(Definition.PriorityType.Dynamic);
    }

    @Override
    public JobQueue calculatePriority(JobQueue jq)
    {
        JobQueue newJQ = new JobQueue();
        Job j;
        while((j = jq.poll()) != null)
        {
            j.setCurrentProiority(new Priority(j.getAbsoluteDeadline()));
            newJQ.add(j);
        }
        return newJQ;
    }

    @Override
    public void calculatePriority(TaskSet ts) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
