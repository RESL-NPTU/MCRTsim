/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface.backEnd;

import java.util.ArrayList;
import simulation.LockInfo;
import simulation.Result;
import simulation.CoreStatus;

/**
 *
 * @author ShiuJia
 */
public class TaskExecution
{
    private int coreID;
    private int taskID;
    private double executionTime;
    private double startTime;
    private double endTime;
    private double speed;
    private String status;
    private ArrayList<ResourcePanel> resourcePanels;

    public TaskExecution()
    {
        
    }
    
    public TaskExecution(Result result)
    {
        this.coreID = result.getCore().getID();
        this.taskID = result.getJob().getTask().getID();
        this.startTime = result.getStartTime();
        this.endTime= result.getEndTime();
        
        if(result.getStatus() == CoreStatus.EXECUTION)
        {
            status = "E";
            speed = result.getFrequencyOfSpeed();
        }
        else if(result.getStatus() == CoreStatus.WRONG)
        {
            status = "X";
            speed = 0;
        }
        else if(result.getStatus() == CoreStatus.WAIT)
        {
            status = "W";
            speed = result.getFrequencyOfSpeed();
        }
        
        resourcePanels = new ArrayList<ResourcePanel>();
        executionTime=endTime-startTime;
        
        for(LockInfo lockInfo : result.getLockedResource())
        {
            resourcePanels.add(new ResourcePanel(lockInfo));
        }
    }
    
    public double getExecutionTime()
    {
        return this.executionTime;
    }
    
    public double getStartTime()
    {
        return this.startTime;
    }
            
    public  double getEndTime()
    {
        return this.endTime;
    }
    
    public String getStatus()
    {
        return this.status;
    }
    
    public double getSpeed()
    {
        return this.speed;
    }
    
    public int getCoreID()
    {
        return this.coreID;
    }
    
    public int getTaskID()
    {
        return this.taskID;
    }
    
    public ArrayList<ResourcePanel> getResourcePanels()
    {
        return this.resourcePanels;
    }
}
