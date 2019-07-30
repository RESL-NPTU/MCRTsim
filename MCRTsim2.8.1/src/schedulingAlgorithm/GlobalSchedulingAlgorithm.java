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
public abstract class GlobalSchedulingAlgorithm extends PriorityDrivenSchedulingAlgorithm
{
    public GlobalSchedulingAlgorithm()
    {
        this.setSchedulingType(Definition.SchedulingType.Global);
    }
    public abstract void calculatePriority(TaskSet ts);
    public abstract JobQueue calculatePriority(JobQueue jq);
}
