/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemEnvironment;

import ResultSet.MissDeadlineInfo;
import ResultSet.ResultSet;
import WorkLoadSet.DataSetting;
import mcrtsim.Definition.SchedulingType;
import userInterface.frontEnd.SimulationViewer;

import static mcrtsim.MCRTsim.println;

/**
 *
 * @author ShiuJia
 */
public class Simulator
{
//    public SimulationViewer parentSimuationViewer;
    private Processor processor;
    //private TaskSet taskSet;
    //private SharedResourceSet sharedResourceSet;
    private long simulationTime;
    private long elapsedTime;
    private long contextSwitchTime = 0;
    private long migrationTime = 0;
    private ResultSet resultSet;
    
//    public Simulator(SimulationViewer sv)
    public Simulator()
    {
//        this.parentSimuationViewer = sv;
        this.processor = null;
        //this.taskSet = null;
        //this.sharedResourceSet = null;
        this.simulationTime = 0;
        this.elapsedTime = 0;
        this.resultSet = new ResultSet();
    }
    
    /*Operating*/
    public void loadDataSetting(DataSetting ds)
    {
        this.processor = ds.getProcessor();
        this.processor.setParentSimulator(this);
        this.processor.loadTaskSet(ds.getTaskSet());
        this.processor.loadResourceSet(ds.getSharedResourceSet());
        //this.taskSet = ds.getTaskSet();
        //this.sharedResourceSet = ds.getSharedResourceSet();
    }
    
    //public void addSchedulingInfo(SchedulingInfo s)
    public void addCoreInfo(Core c)
    {
        //this.resultSet.addSchedulingInfo(s);
        this.resultSet.addCoreInfo(c);
    }
    
    public void addMissDeadlineInfo(MissDeadlineInfo md)
    {
        this.resultSet.addMissDeadlineInfo(md);
    }
    
    public void start()
    {
        long time1,time2;
        time1 = System.currentTimeMillis();
        println(("Start"));
        
        this.processor.partitionTasks();
        
        this.processor.schedulerCalculatePriorityForFixed();
        
        //ControllerAction
        this.processor.getController().preAction();
        
        //DVSAction
        this.processor.getDynamicVoltageRegulator().definedSpeed();
        
        while(this.elapsedTime < this.simulationTime)
        {
            if(this.processor.getSchedulingAlgorithm().getSchedulingType()== SchedulingType.Global)
            {
                this.processor.globalExecute(1);
            }
            else
            {
                this.processor.execute(1);
            }
            this.elapsedTime += 1;
        }
        
        for(Core c : this.processor.getAllCore())//finalRecording
        {
            //println("Sim= " + this.simulationTime);
            c.getSchedulingInfoSet().get(c.getSchedulingInfoSet().size() - 1).setEndTime(this.simulationTime);
            c.finalRecording();
            this.resultSet.addCoreInfo(c);
            
            println("PowerConsumption(" + c.getID() + ")= " + c.getPowerConsumption());
        }
        println("MissDeadline= " + this.resultSet.getMissDeadlineInfoSet().size());
        
        println("End");
        
        
        time2 = System.currentTimeMillis();
        System.out.println(("！！！！ Spend：" + (double)(time2-time1)/1000 + " second. ！！！！"));
    }
    
    /*SetValue*/
    
    public void setSimulationTime(long i)
    {
        this.simulationTime = i;
    }
    
    public void setContextSwitchTime(long i)
    {
        this.contextSwitchTime = i;
    }
    
    public void setMigrationTime(long i)
    {
        this.migrationTime = i;
    }
    /*GetValue*/
    public Processor getProcessor()
    {
        return this.processor;
    }
    
    public long getElapsedTime()
    {
        return this.elapsedTime;
    }
    
    public long getSimulationTime()
    {
        return this.simulationTime;
    }
    
    public long getContextSwitchTime()
    {
        return this.contextSwitchTime;
    }
    
    public long getMigrationTime()
    {
        return this.migrationTime;
    }
    
    public ResultSet getResultSet()
    {
        return this.resultSet;
    }
    
    /*public TaskSet getTaskSet()
    {
        return this.taskSet;
    }
    
    public SharedResourceSet getSharedResourceSet()
    {
        return this.sharedResourceSet;
    }*/
}
