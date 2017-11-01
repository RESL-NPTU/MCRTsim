/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PartitionAlgorithm.implementation;

import PartitionAlgorithm.PartitionAlgorithm;
import SystemEnvironment.Core;
import WorkLoad.Task;
import WorkLoadSet.TaskSet;
import java.util.Vector;

/**
 *
 * @author ShiuJia
 */
public class Order extends PartitionAlgorithm
{
    public Order()
    {
        this.setName("Order");
    }

    @Override
    public void taskToCore(Vector<Core> cores, TaskSet taskSet)
    {
        for(Task t : taskSet)
        {
            cores.get(t.getID() % cores.size()).addTask(t);
        }
    }
}
