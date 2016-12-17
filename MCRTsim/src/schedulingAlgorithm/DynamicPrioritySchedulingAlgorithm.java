/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingAlgorithm;

import simulation.Job;

/**
 *
 * @author ShiuJia
 */
public abstract class DynamicPrioritySchedulingAlgorithm extends PriorityDrivenSchedulingAlgorithm
{
    public abstract void setPriority(Job j);
}
