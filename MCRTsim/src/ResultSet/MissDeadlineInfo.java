/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ResultSet;

import WorkLoad.Job;
import WorkLoad.Task;
import static mcrtsim.Definition.magnificationFactor;

/**
 *
 * @author ShiuJia
 */
public class MissDeadlineInfo
{
    private double missTime;
    private Job missJob;
    private Task missTask;
    
    public MissDeadlineInfo()
    {
        this.missTime = 0;
        this.missJob = null;
    }
    
    public MissDeadlineInfo(int t, Job j)
    {
        this.missTime = t;
        this.missJob = j;
        this.missTask = j.getParentTask();
    }
    
    public double getMissTime()
    {
        return this.missTime / magnificationFactor;
    }
    
    public Job getMissJob()
    {
        return this.missJob;
    }
    
    public Task getMissTask()
    {
        return this.missTask;
    }
}
