/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingAlgorithm;

import WorkLoadSet.JobQueue;
import WorkLoadSet.TaskSet;
import mcrtsim.Definition;

/**
 *
 * @author ShiuJia
 */
public abstract class SingleCoreSchedulingAlgorithm extends PriorityDrivenSchedulingAlgorithm
{
    public SingleCoreSchedulingAlgorithm()
    {
        this.setSchedulingType(Definition.SchedulingType.SingleCore);
    }
    public abstract void calculatePriority(TaskSet ts);
    public abstract JobQueue calculatePriority(JobQueue jq);
}
