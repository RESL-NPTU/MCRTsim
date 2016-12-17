/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingAlgorithm;

import simulation.TaskSet;

/**
 *
 * @author ShiuJia
 */
public abstract class FixedPrioritySchedulingAlgorithm extends PriorityDrivenSchedulingAlgorithm
{
    public abstract void setPriority(TaskSet ts);
}
