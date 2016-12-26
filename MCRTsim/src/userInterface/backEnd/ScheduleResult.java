/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface.backEnd;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;
import simulation.Core;
import simulation.DataSetting;
import simulation.Result;
import simulation.CoreStatus;
import simulation.Task;

/**
 *
 * @author ShiuJia
 */
public class ScheduleResult 
{
    public ResultViewer parent;
    
    public boolean isMultiCore;
    public boolean isCoreTimeLine;
    private int singleTaskGap = 200;
    private int MCTaskGap = 250;
    private double simulationTime; //排程時間
    private Dictionary<String, TaskTimeLine> taskTimeLines; //Dictionary<ID, Task時間軸>
    private Dictionary<String, CoreTimeLine> coreTimeLines; //Dictionary<ID, Core時間軸>
    private Result[][] atbSet;//儲存記錄佇列
    private int baseunit = 40; //比例尺
    private int accuracy = 100; //時間精準度
    
    public ScheduleResult(ResultViewer rv) 
    {
        this.parent = rv;
        this.isMultiCore = this.parent.getCores().size() > 1 ? true : false;
        this.taskTimeLines = new Hashtable<String, TaskTimeLine>();
        this.coreTimeLines = new Hashtable<String, CoreTimeLine>();
        this.simulationTime = this.parent.parent.getDataSetting().getProcessor().getSimulator().getSimulationTime() / 100000.0;
        
    }
    
    public void startCoreTimeLineSchedule()
    {
        this.isCoreTimeLine = true;
        DataSetting ds = this.parent.parent.getDataSetting();
        this.atbSet = new Result[this.parent.getCores().size()][ds.getTaskSet().size()];
        
        for(int i = 0; i < ds.getProcessor().getCores().size() ; i++)
        {
           String id = String.valueOf(ds.getProcessor().getCore(i).getID());
           coreTimeLines.put(id , new CoreTimeLine(this, i, id));
        }

        this.atbSet = new Result[ds.getTaskSet().size()][(int)(this.simulationTime * accuracy)+1];

        for(Core core : this.parent.getCores())
        {
            Vector<Result> record = core.getResult();

            for(Task task : core.getTaskSet())
            {
                for (int i=0;i<record.size()-1;i++)
                {
                    for(int j=Double.valueOf(record.get(i).getStartTime() * accuracy).intValue();j<Double.valueOf(record.get(i).getEndTime() * accuracy).intValue();j++)
                    {
                        this.atbSet[core.getID()-1][j] = record.get(i);
                    }
                }
                //addfinallyResult---VVVV
                for(int j = (int)(record.get(record.size()-1).getStartTime() * accuracy);j <= (int)(this.simulationTime * accuracy) ;j++)
                {
                    this.atbSet[core.getID()-1][j] = record.get(record.size()-1);
                }
            }
        }
        
        CoreTimeLine ctl;
        
        for(Core core : this.parent.getCores())
        {
            for(Result curResult : core.getResult())
            {  
                if(curResult.getStatus() == CoreStatus.EXECUTION )
                {
                    ctl = this.coreTimeLines.get(String.valueOf(core.getID()));
                    ctl.addExecution(new TaskExecution(curResult));
                }
                else if(curResult.getStatus() == CoreStatus.IDLE)
                {

                }
                else if(curResult.getStatus() == CoreStatus.WRONG)
                {
                    ctl = this.coreTimeLines.get(String.valueOf(core.getID()));
                    ctl.addExecution(new TaskExecution(curResult));
                }
                else if(curResult.getStatus() == CoreStatus.WAIT)
                {
                    ctl = this.coreTimeLines.get(String.valueOf(core.getID()));
                    ctl.addExecution(new TaskExecution(curResult));
                }
            }
        
        }
    }
    
    public void startTaskTimeLineSchedule()
    {
        this.isCoreTimeLine = false;
        DataSetting ds = this.parent.parent.getDataSetting();
        this.atbSet = new Result[this.parent.getCores().size()][ds.getTaskSet().size()];
        
        if(this.isMultiCore)
        {
            for(int i = 0; i < ds.getTaskSet().size() ; i++)
            {
               String id = String.valueOf(ds.getTaskSet(i).getID());
               taskTimeLines.put(id , new TaskTimeLine(this, i, id));
            }
            
            this.atbSet = new Result[ds.getTaskSet().size()][(int)(this.simulationTime * accuracy)+1];
            
            for(Core core : this.parent.getCores())
            {
                Vector<Result> record = core.getResult();
                
                for(Task task : core.getTaskSet())
                {
                    for (int i=0;i<record.size()-1;i++)
                    {
                        for(int j=Double.valueOf(record.get(i).getStartTime() * accuracy).intValue();j<Double.valueOf(record.get(i).getEndTime() * accuracy).intValue();j++)
                        {
                            this.atbSet[task.getID()-1][j] = record.get(i);
                        }
                    }
                    //addfinallyResult---VVVV
                    for(int j = (int)(record.get(record.size()-1).getStartTime() * accuracy);j <= (int)(this.simulationTime * accuracy) ;j++)
                    {
                        this.atbSet[task.getID()-1][j] = record.get(record.size()-1);
                    }
                }
            }
        }
        else
        {
            Core core = this.parent.getCore(0);
            Vector<Result> record = core.getResult();
            
            for(int i = 0;i < core.getTaskSet().size() ; i++)
            {
               String id = String.valueOf(core.getTaskSet(i).getID());
               taskTimeLines.put(id , new TaskTimeLine(this, i, id));
            }
            
            this.atbSet = new Result[1][(int)(this.simulationTime * accuracy)+1];
           
            for (int i=0;i<record.size()-1;i++)
            {
                for(int j=Double.valueOf(record.get(i).getStartTime() * accuracy).intValue();j<Double.valueOf(record.get(i).getEndTime() * accuracy).intValue();j++)
                {
                    this.atbSet[0][j] = record.get(i);
                }
            }
            //addfinallyResult---VVVV
            for(int j = (int)(record.get(record.size()-1).getStartTime() * accuracy);j <= (int)(this.simulationTime * accuracy) ;j++)
            {
                this.atbSet[0][j] = record.get(record.size()-1);
            }
        }
        
        TaskTimeLine ttl;
        
        
        for(Core core : this.parent.getCores())
        {
            for(Result curResult : core.getResult())
            {  
                if(curResult.getStatus() == CoreStatus.EXECUTION )
                {
                    ttl = this.taskTimeLines.get(String.valueOf(curResult.getJob().getTask().getID()));
                    ttl.addExecution(new TaskExecution(curResult));
                }
                else if(curResult.getStatus() == CoreStatus.IDLE)
                {

                }
                else if(curResult.getStatus() == CoreStatus.WRONG)
                {
                    ttl = this.taskTimeLines.get(String.valueOf(curResult.getJob().getTask().getID()));
                    ttl.addExecution(new TaskExecution(curResult));
                }
                else if(curResult.getStatus() == CoreStatus.WAIT)
                {
                    ttl = this.taskTimeLines.get(String.valueOf(curResult.getJob().getTask().getID()));
                    ttl.addExecution(new TaskExecution(curResult));
                }
            }
        }
    }
    
    public Result getAtbSet(int taskID , int time)
    {
        return this.atbSet[taskID][time];
    }
    
    public  double getFinalTime()
    {
        return this.simulationTime;
    }

    public  Dictionary<String, TaskTimeLine> getTaskTimeLines()
    {
        return this.taskTimeLines;
    }
    
    public  Dictionary<String, CoreTimeLine> getCoreTimeLines()
    {
        return this.coreTimeLines;
    }
    
    public int getBaseunit()
    {
        return this.baseunit;
    }
    
    public void setBaseunit(Double d)
    {
        this.baseunit = (int)(this.baseunit * d);
    }
    
    public int getAccuracy()
    {
        return this.accuracy;
    }
    
    public int getSingleTaskGap()
    {
        return this.singleTaskGap;
    }
    
    public int getMCTaskGap()
    {
        return this.MCTaskGap;
    }
    
    public int getTaskGap()
    {
        return this.isMultiCore ? this.MCTaskGap :this.singleTaskGap;
    }
}
