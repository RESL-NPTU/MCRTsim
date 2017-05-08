/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import java.util.Comparator;
import java.util.Vector;
import schedulingAlgorithm.DynamicPrioritySchedulingAlgorithm;
import schedulingAlgorithm.PriorityDrivenSchedulingAlgorithm;

/**
 *
 * @author ShiuJia
 */
public class Processor
{
    private Simulator simulator;
    private String model;
    private Vector<Core> cores;
    private Vector<DynamicVoltageRegulator> dynamicVoltageRegulators;
    private DataSetting dataSetting;
    private JobQueue globalReadyQueue;
    public boolean isPerCore;
    public boolean isGlobalScheduling;
    private Scheduler globalScheduler;
    private double currentTime;
    private double checkTime;
    private TaskSet globalTaskSet;
    
    public Processor()
    {

        this.isGlobalScheduling = false;
                                    //true;
        this.dynamicVoltageRegulators = new Vector<DynamicVoltageRegulator>();
        this.cores = new Vector<Core>();
        this.isPerCore = true;
        this.globalReadyQueue = new JobQueue();
        this.globalScheduler = new Scheduler();
        this.checkTime = -1;
        this.currentTime = 0;
        this.globalTaskSet = new TaskSet(); 
    }
    
    public void setProcessorModel(String m)
    {
        this.model = m;
    }
    
    public String getProcessorModel()
    {
        return this.model;
    }
    
    public void setSimulator(Simulator sim)
    {
        this.simulator = sim;
    }
    
    public Simulator getSimulator()
    {
        return this.simulator;
    }
    
    public void addCore(Core c)
    {
        c.setID(this.cores.size()+1);
        c.setProcessor(this);
        this.cores.add(c);
    }
    
    public void addCores(Vector<Core> cores)
    {
        for (Core c : cores)
        {
            c.setID(this.cores.size()+1);
            c.setProcessor(this);
            this.cores.add(c);
        }
    }
    
    public Vector<Core> getCores()
    {
        return this.cores;
    }
    
    public double getTotalPowerConsumption()
    {
        double TPC = 0;
        for(Core c : this.cores)
        {
            TPC += c.getTotalPowerConsumption();
        }
        return TPC;
    }
    
    public Core getCore(int i)
    {
        return this.cores.get(i);
    }
    
    public void addDynamicVoltageRegulator(DynamicVoltageRegulator DVR)
    {
        this.dynamicVoltageRegulators.add(DVR);
    }
    
    public Vector<DynamicVoltageRegulator> getDynamicVoltageRegulators()
    {
        return this.dynamicVoltageRegulators;
    }
    
    public void setGlobalTaskSet(TaskSet ts)
    {
        
        this.globalTaskSet = ts;
        
        this.globalTaskSet.sort
        (
            new Comparator<Task>()
            {
                public int compare(Task t1, Task t2)
                {
                    if(t1.getID() < t2.getID())
                    {
                        return -1;
                    }
                    else if(t1.getID() > t2.getID())
                    {
                        return 1;
                    }
                    return 0;
                }
            }
        );
    }
    
    public TaskSet getGlobalTaskSet()
    {
        return this.globalTaskSet;
    }
    
    public Task getGlobalTaskSet(int i)
    {
        return this.globalTaskSet.get(i);
    }
    
    public void setDataSetting(DataSetting ds)
    {
        this.dataSetting = ds;
    }
    
    public DataSetting getDataSetting()
    {
        return this.dataSetting;
    }
    
    public void setDVFSType(String str)
    {
        switch(str)
        {
            case "per-core":
                this.isPerCore = true;
                break;
                
            case "full-chip":
            case "VFI":
                this.isPerCore = false;
                break;
                
            default :
                this.isPerCore = true;
        }
    }
    
    public void setGlobalReadyQueue(JobQueue jq)
    {
        this.globalReadyQueue = jq;
    }
    
    public JobQueue getGlobalReadyQueue()
    {
        return this.globalReadyQueue;
    }
    
    public Scheduler getGlobalScheduler()
    {
        return this.globalScheduler;
    }
    
    public void setGlobalSchedulingAlgorithm(PriorityDrivenSchedulingAlgorithm algorithm)
    {
        this.globalScheduler.setSchedAlgorithm(algorithm);
    }
    
    public void globalScheduling(int processedTime)
    {
        if(this.checkTime != this.currentTime) //Check Period Of Task
        {
            for(Task t : this.globalTaskSet)
            {
                if(this.currentTime % t.getPeriod() == t.getEnterTime())
                {
                    Job newJ = t.newJob((int)this.currentTime);
                    System.out.println("Task:"+t.getID()+" create Job:"+newJ.getID());
                    this.globalReadyQueue.add(newJ);
                }
            }
            if(this.currentTime == 0)
            {
                System.out.println("GlobalReadyQueue Job:");
                for(Job j : this.globalReadyQueue)
                {
                    System.out.println("("+j.getTask().getID()+","+j.getID()+")");
                }
            }
            this.checkTime = this.currentTime;   
        }
        
        if(this.globalScheduler.getSchedulingAlgorithm() instanceof DynamicPrioritySchedulingAlgorithm) //動態優先權分配
        {
            this.globalReadyQueue = this.globalScheduler.setPriority(this.globalReadyQueue);
            
            if(this.currentTime == 0)
            {
                System.out.println("!!!GlobalReadyQueue Job:");
                for(Job j : this.globalReadyQueue)
                {
                    System.out.println("("+j.getTask().getID()+","+j.getID()+")");
                }
            }
        }
        
        while(this.assignJobToCore()){};
        
        
        
        Vector<String> coreWorkStatus = new Vector<String>();
        for(int i = 0; i < this.cores.size(); i++)
        {
            if(this.getCore(i).getCoreStatus() != "stop")
            {
                coreWorkStatus.add(this.getCore(i).operationCheckWorkStatusForGlobal());
            }
        }
        
        for(DynamicVoltageRegulator DVR : this.dynamicVoltageRegulators)
        {
            if(DVR.getCores().size()>0)
            {
                DVR.scalingVoltage(); //設定每個DynamicVoltageRegulator當前速度
            }
        }
        
        for(int i = 0; i < this.cores.size(); i++)
        {
            if(this.getCore(i).getCoreStatus() != "stop")
            {
                this.getCore(i).operationRun(processedTime, coreWorkStatus.get(i));
            }
        }
        
        for(int i = 0; i < this.cores.size(); i++)
        {
            if(this.getCore(i).getCoreStatus() != "stop")
            {
                this.getCore(i).operationRunEnd();
                this.getCore(i).checkJobMissDeadlineForPartition();
            }
        }
    }
    
    //public void assignJobToCore(Core c)
    public boolean assignJobToCore()
    {
        if(this.globalReadyQueue.peek() != null)
        {
            Core c = null;
            for(Core core : this.cores)
            {
                if(core.getCoreStatus()== "start")
                {
                    c = core;
                    break;
                }
            }
            
            for(Core core : this.cores)//找出當前優先權最小的Core
            {
                if(core.getLocalReadyQueue().peek() == null)
                {
                    c = core;
                    break;
                }
                else if(c.getLocalReadyQueue().peek().compareTo(core.getLocalReadyQueue().peek())==-1)// cc優先權大於Core
                {
                    if(core.getCoreStatus()== "start")
                    {
                        c = core;
                    }
                }
            }
        
            if(c.getLocalReadyQueue().peek() == null || (c.preemptible && this.globalReadyQueue.peek().compareTo(c.getLocalReadyQueue().peek())==-1))
            {        
                Job localJob = c.getLocalReadyQueue().poll();
                c.getLocalReadyQueue().add(this.globalReadyQueue.poll());
                if(localJob != null)
                {
                    JobQueue newJQ = new JobQueue();
                    newJQ.add(localJob);
                    for(Job j : this.globalReadyQueue)
                    {
                        newJQ.add(j);
                    }
                    this.globalReadyQueue = newJQ;
                    return true;
                }
            }
        }
        return false;
    }
    
    public void partitionScheduling(int processedTime)
    {
        Vector<String> coreWorkStatus = new Vector<String>();
        
        for(int i = 0; i < this.cores.size(); i++)
        {
            if(this.getCore(i).getCoreStatus() != "stop")
            {
                this.getCore(i).checkTaskPeriodForPartition();
                coreWorkStatus.add(this.getCore(i).operationCheckWorkStatusForPartition());
            }
        }
        
        for(DynamicVoltageRegulator DVR : this.dynamicVoltageRegulators)
        {
            if(DVR.getCores().size()>0)
            {
                DVR.scalingVoltage(); //設定每個DynamicVoltageRegulator當前速度
            }
        }
        
        for(int i = 0; i < this.cores.size(); i++)
        {
            if(this.getCore(i).getCoreStatus() != "stop")
            {
                this.getCore(i).operationRun(processedTime, coreWorkStatus.get(i));
            }
        }
        
        for(int i = 0; i < this.cores.size(); i++)
        {
            if(this.getCore(i).getCoreStatus() != "stop")
            {
                this.getCore(i).operationRunEnd();
                this.getCore(i).checkJobMissDeadlineForPartition();
            }
        }
    }
    
    public void run(int processedTime)
    {
        if(!this.isGlobalScheduling)
        {
            this.partitionScheduling(processedTime);
        }
        else
        {
            this.globalScheduling(processedTime);
        }
        
        this.currentTime += processedTime;
    }
}
