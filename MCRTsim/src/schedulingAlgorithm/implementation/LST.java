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
import schedulingAlgorithm.SingleCoreSchedulingAlgorithm;

/**
 *
 * @author ShiuJia
 */
public class LST extends SingleCoreSchedulingAlgorithm
{
    public LST()
    {
        this.setName("Least Slack Time Scheduling Algorithm");
        this.setPriorityType(Definition.PriorityType.Dynamic);
    }

    @Override
    public JobQueue calculatePriority(JobQueue jq)
    {
        JobQueue newJQ = new JobQueue();
        Job j;
        while((j = jq.poll()) != null)
        {
            long lastAmount = (long) Math.ceil(j.getTargetAmount() - j.getProgressAmount());
            j.setCurrentProiority(new Priority(j.getAbsoluteDeadline() - j.getCurrentCore().getCurrentTime() - lastAmount));
            newJQ.add(j);
        }
        return newJQ;
    }

    @Override
    public void calculatePriority(TaskSet ts) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
