/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemEnvironment;

import PartitionAlgorithm.PartitionAlgorithm;

/**
 *
 * @author ShiuJia
 */
public class PartitionDistributor
{
    private PartitionAlgorithm algorithm;
    private Processor parentProcessor;
    
    public PartitionDistributor()
    {
        this.algorithm = null;
        this.parentProcessor = null;
    }
    
    /*Operating*/
    public void split()
    {
        this.algorithm.taskToCore(this.parentProcessor.getAllCore(), this.parentProcessor.getTaskSet());
    }
    
    /*SetValue*/
    public void setPartitionAlgorithm(PartitionAlgorithm a)
    {
        this.algorithm = a;
    }
    
    public void setParentProcessor(Processor p)
    {
        this.parentProcessor = p;
    }
    
    /*GetValue*/
    public PartitionAlgorithm getSPartitionAlgorithm()
    {
        return this.algorithm;
    }
    
    public Processor getParentProcessor()
    {
        return this.parentProcessor;
    }
}
