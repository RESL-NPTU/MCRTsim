/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PartitionAlgorithm;

import SystemEnvironment.Core;
import SystemEnvironment.Processor;
import WorkLoadSet.TaskSet;
import java.util.Vector;

/**
 *
 * @author ShiuJia
 */
public abstract class PartitionAlgorithm
{
    private String name;
    
    public PartitionAlgorithm()
    {
        this.name = null;
    }
    
    public void setName(String n)
    {
        this.name = n;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public abstract void taskToCore(Vector<Core> cores, TaskSet taskSet);
}
