/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingAlgorithm;

import SystemEnvironment.Core;
import WorkLoadSet.JobQueue;
import WorkLoadSet.TaskSet;
import java.util.Vector;
import mcrtsim.Definition;

/**
 *
 * @author ShiuJia
 */
public abstract class PartitionedSchedulingAlgorithm extends PriorityDrivenSchedulingAlgorithm
{
    public PartitionedSchedulingAlgorithm()
    {
        this.setSchedulingType(Definition.SchedulingType.Partition);
    }
    public abstract void setCoresLocalSchedulingAlgorithm(Vector<Core> cores);
}
