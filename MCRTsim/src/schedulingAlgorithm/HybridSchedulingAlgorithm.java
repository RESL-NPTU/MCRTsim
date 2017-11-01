/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingAlgorithm;

import SystemEnvironment.Core;
import SystemEnvironment.Processor;
import WorkLoadSet.JobQueue;
import WorkLoadSet.TaskSet;
import java.util.Vector;
import mcrtsim.Definition;

/**
 *
 * @author ShiuJia
 */
public abstract class HybridSchedulingAlgorithm extends PriorityDrivenSchedulingAlgorithm
{
    public HybridSchedulingAlgorithm()
    {
        this.setSchedulingType(Definition.SchedulingType.Hybrid);
    }
    public abstract void calculatePriority(TaskSet ts);
    public abstract JobQueue calculatePriority(JobQueue jq);
    public abstract void setProcessorGlobalSchedulingAlgorithm(Processor p);
    public abstract void setCoresLocalSchedulingAlgorithm(Vector<Core> cores);
}
