/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import taskToCore.TaskToCore;

/**
 *
 * @author ShiuJia
 */
public class Assignment
{
    private TaskToCore taskToCore;
    
    public Assignment()
    {
        
    }
    
    public void setTaskToCore(TaskToCore t)
    {
        this.taskToCore = t;
    }
    
    public TaskToCore getTaskToCore()
    {
        return this.taskToCore;
    }
    
    public void assign()
    {
        this.taskToCore.assign();
    }
}
