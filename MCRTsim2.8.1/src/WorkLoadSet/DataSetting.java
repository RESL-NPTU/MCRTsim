/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WorkLoadSet;

import SystemEnvironment.Processor;
import WorkLoad.SharedResource;
import WorkLoad.Task;

/**
 *
 * @author ShiuJia
 */
public class DataSetting
{
    Processor processor;
    TaskSet taskSet;
    SharedResourceSet sharedResourceSet;
    
    public DataSetting()
    {
        this.processor = new Processor();
        this.taskSet = new TaskSet();
        this.sharedResourceSet = new SharedResourceSet();
    }
    
    /*Operating*/
    public void addTask(Task t)
    {
        this.taskSet.add(t);
    }
    
    public void addSharedResource(SharedResource r)
    {
        this.sharedResourceSet.add(r);
    }
    
    /*GetValue*/
    public Processor getProcessor()
    {
        return this.processor;
    }
    
    public Task getTask(int i)
    {
        return this.taskSet.get(i);
    }
    
    public TaskSet getTaskSet()
    {
        return this.taskSet;
    }
    
    public SharedResource getSharedResource(int i)
    {
        return this.sharedResourceSet.get(i);
    }
    
    public SharedResourceSet getSharedResourceSet()
    {
        return this.sharedResourceSet;
    }
    
 
    
}
