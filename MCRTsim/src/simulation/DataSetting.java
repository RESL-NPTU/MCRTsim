/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

/**
 *
 * @author ShiuJia
 */
public class DataSetting
{
    private TaskSet taskSet;
    private ResourcesSet resourcesSet;
    private Processor processor;
    
    public DataSetting()
    {
        this.taskSet = new TaskSet();
        this.resourcesSet = new ResourcesSet();
        this.processor = new Processor();
    }
    
    public void setTaskSet(TaskSet ts)
    {
        this.taskSet = ts;
    }
    
    public TaskSet getTaskSet()
    {
        return this.taskSet;
    }
    
    public Task getTaskSet(int i)
    {
        return this.taskSet.get(i);
    }
    
    public void setResourceSet(ResourcesSet rs)
    {
        this.resourcesSet = rs;
    }
    
    public ResourcesSet getResourceSet()
    {
        return this.resourcesSet;
    }
  
    public void setProcessor(Processor p)
    {
        this.processor = p;
    }
    
    public Processor getProcessor()
    {
        return this.processor;
    }
}

