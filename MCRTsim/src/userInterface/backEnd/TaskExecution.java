/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface.backEnd;

import ResultSet.MissDeadlineInfo;
import ResultSet.SchedulingInfo;
import WorkLoad.CriticalSection;
import java.util.ArrayList;
import mcrtsim.Definition.CoreStatus;

/**
 *
 * @author ShiuJia
 */
public class TaskExecution
{
    private int coreID;
    private int taskID;
    private int jobID;
    private double executionTime;
    private double startTime;
    private double endTime;
    private double speed;
    private CoreStatus status;
    private ArrayList<ResourcePanel> resourcePanels;

    public TaskExecution()
    {
        
    }
    
    public TaskExecution(SchedulingInfo schedulingInfo)
    {
        this.coreID = schedulingInfo.getCore().getID();
        this.taskID = schedulingInfo.getJob().getParentTask().getID();
        this.jobID = schedulingInfo.getJob().getID();
        this.startTime = schedulingInfo.getStartTime();
        this.endTime= schedulingInfo.getEndTime();
        
        if(schedulingInfo.getCoreStatus() == CoreStatus.EXECUTION)
        {
            status = schedulingInfo.getCoreStatus();
            speed = schedulingInfo.getUseSpeed();
        }
        else if(schedulingInfo.getCoreStatus() == CoreStatus.WAIT)
        {
            status = schedulingInfo.getCoreStatus();
            speed = schedulingInfo.getUseSpeed();
        }
        else if(schedulingInfo.getCoreStatus() == CoreStatus.CONTEXTSWITCH)
        {
            status = schedulingInfo.getCoreStatus();
            speed = schedulingInfo.getUseSpeed();
        }
        else if(schedulingInfo.getCoreStatus() == CoreStatus.MIGRATION)
        {
            status = schedulingInfo.getCoreStatus();
            speed = schedulingInfo.getUseSpeed();
        }
        
        resourcePanels = new ArrayList<ResourcePanel>();
        executionTime=endTime-startTime;
        
        for(CriticalSection cs : schedulingInfo.getEnteredCriticalSectionSet())
        {
            resourcePanels.add(new ResourcePanel(cs));
        }
    }
    
    public TaskExecution(MissDeadlineInfo missDeadlineInfo)
    {
        this.coreID = 0;
        this.taskID = missDeadlineInfo.getMissTask().getID();
        this.jobID = missDeadlineInfo.getMissJob().getID();
        this.startTime = missDeadlineInfo.getMissTime();
        this.endTime = this.startTime;
        this.status = CoreStatus.WRONG;
        this.speed = 0;
        resourcePanels = new ArrayList<ResourcePanel>();
        executionTime=endTime-startTime;
        
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
    
    public CoreStatus getStatus()
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
    
    public int getJobID()
    {
        return this.jobID;
    }
    
    public ArrayList<ResourcePanel> getResourcePanels()
    {
        return this.resourcePanels;
    }
}
