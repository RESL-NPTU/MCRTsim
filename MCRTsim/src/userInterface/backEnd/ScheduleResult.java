/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface.backEnd;

import ResultSet.MissDeadlineInfo;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;
import SystemEnvironment.Core;
import WorkLoadSet.DataSetting;
import ResultSet.ResultSet;
import ResultSet.SchedulingInfo;
import mcrtsim.Definition;
import WorkLoad.Task;
import static mcrtsim.Definition.magnificationFactor;

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
    private SchedulingInfo[][] atbSet;//儲存記錄佇列
    private int baseunit = 40; //比例尺
    private int accuracy = 100; //時間精準度
    private ResultSet resultSet;
    
    public ScheduleResult(ResultViewer rv) 
    {
        this.parent = rv;
        this.isMultiCore = this.parent.getCores().size() > 1 ? true : false;
        this.taskTimeLines = new Hashtable<String, TaskTimeLine>();
        this.coreTimeLines = new Hashtable<String, CoreTimeLine>();
        this.resultSet = this.parent.parent.parent.getSimulationViewer().getSimulator().getResultSet();
        this.simulationTime = (double)this.parent.parent.parent.getSimulationViewer().getSimulationTime() / magnificationFactor;
    }
    
    public void startCoreTimeLineSchedule()
    {
        
        this.isCoreTimeLine = true;
        DataSetting ds = this.parent.parent.getDataSetting();
        
        for(int i = 0; i < ds.getProcessor().getAllCore().size() ; i++)
        {
           String id = String.valueOf(ds.getProcessor().getCore(i).getID());
           coreTimeLines.put(id , new CoreTimeLine(this, i, id));
        }

        this.atbSet = new SchedulingInfo[ds.getTaskSet().size()][(int)(this.simulationTime * accuracy)+1];

        for(Core core : this.parent.getCores())
        {
            Vector<SchedulingInfo> schedulingInfoSet = core.getSchedulingInfoSet();

            for (int i=0;i<schedulingInfoSet.size()-1;i++)
            {
                for(int j=Double.valueOf(schedulingInfoSet.get(i).getStartTime() * accuracy).intValue();j<Double.valueOf(schedulingInfoSet.get(i).getEndTime() * accuracy).intValue();j++)
                {
                    this.atbSet[core.getID()-1][j] = schedulingInfoSet.get(i);
                }
            }
            //addfinallyResult---VVVV
            for(int j = (int)(schedulingInfoSet.get(schedulingInfoSet.size()-1).getStartTime() * accuracy);j <= (int)(this.simulationTime * accuracy) ;j++)
            {
                this.atbSet[core.getID()-1][j] = schedulingInfoSet.get(schedulingInfoSet.size()-1);
            }
            
        }
        
        CoreTimeLine ctl;
        
        for(Core core : this.parent.getCores())
        {
            for(SchedulingInfo curResult : core.getSchedulingInfoSet())
            {  
                
                if(curResult.getCoreStatus() == Definition.CoreStatus.EXECUTION )
                {
                    ctl = this.coreTimeLines.get(String.valueOf(core.getID()));
                    ctl.addExecution(new TaskExecution(curResult));
                }
                else if(curResult.getCoreStatus() == Definition.CoreStatus.IDLE)
                {

                }
                else if(curResult.getCoreStatus() == Definition.CoreStatus.WAIT)
                {
                    ctl = this.coreTimeLines.get(String.valueOf(core.getID()));
                    ctl.addExecution(new TaskExecution(curResult));
                }
                else if(curResult.getCoreStatus() == Definition.CoreStatus.CONTEXTSWITCH)
                {
                    ctl = this.coreTimeLines.get(String.valueOf(core.getID()));
                    ctl.addExecution(new TaskExecution(curResult));
                }
                else if(curResult.getCoreStatus() == Definition.CoreStatus.MIGRATION)
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
       
        if(this.isMultiCore)
        {
            for(int i = 0; i < ds.getTaskSet().size() ; i++)
            {
               String id = String.valueOf(ds.getTask(i).getID());
               taskTimeLines.put(id , new TaskTimeLine(this, i, id));
            }
            
            this.atbSet = new SchedulingInfo[ds.getTaskSet().size()][(int)(this.simulationTime * accuracy)+1];
            
            SchedulingInfo nullSInfo = new SchedulingInfo();
            for(Task task : ds.getTaskSet())
            {
                
                for(int j=0;j<Double.valueOf(this.simulationTime * accuracy).intValue()+1;j++)
                {
                    this.atbSet[task.getID()-1][j] = nullSInfo;
                }
            }
            
            
            for(Core core : this.parent.getCores())
            {
                Vector<SchedulingInfo> schedulingInfoSet = core.getSchedulingInfoSet();
                

                for (int i=0;i<schedulingInfoSet.size()-1;i++)
                {
                    if(schedulingInfoSet.get(i).getJob() != null)
                    {
                        for(int j=Double.valueOf(schedulingInfoSet.get(i).getStartTime() * accuracy).intValue();j<Double.valueOf(schedulingInfoSet.get(i).getEndTime() * accuracy).intValue();j++)
                        {
                            this.atbSet[schedulingInfoSet.get(i).getJob().getParentTask().getID()-1][j] = schedulingInfoSet.get(i);
                        }
                    }
                }
                //addfinallyResult---VVVV
                for(int j = (int)(schedulingInfoSet.get(schedulingInfoSet.size()-1).getStartTime() * accuracy);j <= (int)(this.simulationTime * accuracy) ;j++)
                {
                    if(schedulingInfoSet.get(schedulingInfoSet.size()-1).getJob() != null)
                    {
                        this.atbSet[schedulingInfoSet.get(schedulingInfoSet.size()-1).getJob().getParentTask().getID()-1][j] = schedulingInfoSet.get(schedulingInfoSet.size()-1);
                    }
                }
                
            }
        }
        else
        {
            Core core = this.parent.getCore(0);
            Vector<SchedulingInfo> record = core.getSchedulingInfoSet();
            
            for(int i = 0; i < ds.getTaskSet().size() ; i++)
            {
               String id = String.valueOf(ds.getTask(i).getID());
               taskTimeLines.put(id , new TaskTimeLine(this, i, id));
            }
            
            this.atbSet = new SchedulingInfo[1][(int)(this.simulationTime * accuracy)+1];
           
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
            for(SchedulingInfo curResult : core.getSchedulingInfoSet())
            {  
                if(curResult.getCoreStatus() == Definition.CoreStatus.EXECUTION )
                {
                    ttl = this.taskTimeLines.get(String.valueOf(curResult.getJob().getParentTask().getID()));
                    ttl.addExecution(new TaskExecution(curResult));
                }
                else if(curResult.getCoreStatus() == Definition.CoreStatus.IDLE)
                {
                
                }
                else if(curResult.getCoreStatus() == Definition.CoreStatus.WAIT)
                {
                    ttl = this.taskTimeLines.get(String.valueOf(curResult.getJob().getParentTask().getID()));
                    ttl.addExecution(new TaskExecution(curResult));
                }
                else if(curResult.getCoreStatus() == Definition.CoreStatus.CONTEXTSWITCH)
                {
                    ttl = this.taskTimeLines.get(String.valueOf(curResult.getJob().getParentTask().getID()));
                    ttl.addExecution(new TaskExecution(curResult));
                }
                else if(curResult.getCoreStatus() == Definition.CoreStatus.MIGRATION)
                {
                    ttl = this.taskTimeLines.get(String.valueOf(curResult.getJob().getParentTask().getID()));
                    ttl.addExecution(new TaskExecution(curResult));
                }
            }
        }
        
        for(MissDeadlineInfo missDeadlineInfo : this.resultSet.getMissDeadlineInfoSet())
        {
            ttl = this.taskTimeLines.get(String.valueOf(missDeadlineInfo.getMissTask().getID()));
            ttl.addExecution(new TaskExecution(missDeadlineInfo));
        }
        
        this.removeNeedlessTaskTimeLine();
    }
    
    private void removeNeedlessTaskTimeLine()
    {
        DataSetting ds = this.parent.parent.getDataSetting();
        for(int i = 0; i < ds.getTaskSet().size() ; i++)
        {
            String id = String.valueOf(ds.getTask(i).getID());
           
            if(taskTimeLines.get(id).getExecutionNumber() < 1)
            {
                this.taskTimeLines.remove(taskTimeLines.get(id));
            }
        }
    }
    
    public SchedulingInfo getAtbSet(int taskID , int time)
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
