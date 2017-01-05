/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import concurrencyControlProtocol.ConcurrencyControlProtocol;
import dynamicVoltageAndFrequencyScaling.DynamicVoltageAndFrequencyScalingMethod;
import schedulingAlgorithm.FixedPrioritySchedulingAlgorithm;
import schedulingAlgorithm.PriorityDrivenSchedulingAlgorithm;
import taskToCore.TaskToCore;
import userInterface.frontEnd.SimulationViewer;

/**
 *
 * @author ShiuJia
 */
public class Simulator 
{

    public SimulationViewer parent;
    private int systemTime;
    private Controller controller;
    private Processor processor;
    private TaskSet taskSet;
    private ResourcesSet resourcesSet;
    private long simulationTime;
    private Assignment assignment;

    public Simulator(SimulationViewer sv) 
    {
        this.parent = sv;
        this.systemTime = 0;
        this.controller = new Controller();
        this.taskSet = new TaskSet();
        this.resourcesSet = new ResourcesSet();
        this.processor = new Processor();
        this.assignment = new Assignment();
    }

    public void setBlockTimeOfTasks() 
    {
        for (Task t : this.taskSet) 
        {
            t.setBlockingTime(this.controller.getCCProtocol().getBlockingTime(this.taskSet,t));
        }
    }

    public String showBlockTimeOfTasks() 
    {
        String s = "Worst Case Blocking Time :";
        for (Task t : this.taskSet) 
        {
            s = s + "\n    Task " + t.getID() + " = " + t.getBlockingTime();
        }
        return s;
    }

    public void start() 
    {
        while (this.systemTime < this.simulationTime) 
        {
            this.processor.run(1);
            this.systemTime += 1;
        }
        for (Core core : this.processor.getCores()) 
        {
            core.getResult().get(core.getResult().size() - 1).setEndTime((double) this.systemTime / 100000);
            core.getResult().get(core.getResult().size() - 1).setTotalPowerConsumption(core.getTotalPowerConsumption());
        }
        System.out.println("End");
    }
    
    public void runInit() 
    {
        this.processor.run(1);
        this.systemTime += 1;
    }

    public int getSystemTime() 
    {
        return this.systemTime;
    }

    public void setTaskToCore(TaskToCore t) 
    {
        this.assignment.setTaskToCore(t);
        this.assignment.assign();
        for (int i = 0; i < this.processor.getCores().size(); i++) 
        {
            if (this.getProcessor().getCore(i).getTaskSet().size() == 0) 
            {
                this.getProcessor().getCore(i).getDynamicVoltageRegulator().getCores().remove(this.getProcessor().getCore(i));
                this.getProcessor().getCores().remove(this.processor.getCore(i));
                i--;
            }
        }

        for (Resources r : this.resourcesSet) 
        {
            r.updateGlobal();
            System.out.println("Res" + r.getID() + ":" + r.isGlobal());
        }
    }

    public void setTaskToProcessor() 
    {
        this.processor.setGlobalTaskSet(taskSet);
        for (Resources r : this.resourcesSet) 
        {
            r.setGlobal(true);
            System.out.println("Res" + r.getID() + ":" + r.isGlobal());
        }
    }

    public TaskToCore getTaskToCore() 
    {
        return this.assignment.getTaskToCore();
    }

    public void setSchedAlgorithm(PriorityDrivenSchedulingAlgorithm algorithm) 
    {
        if (!this.processor.isGlobalScheduling) 
        {
            for (Core core : this.processor.getCores()) 
            {
                if (core.getCoreStatus() != "stop") 
                {
                    core.setLocalSchedulingAlgorithm(algorithm);
                }
            }

            for (Core core : this.processor.getCores()) 
            {
                if (core.getCoreStatus() != "stop") 
                {
                    if (core.getLocalScheduler().getSchedulingAlgorithm() instanceof FixedPrioritySchedulingAlgorithm) //靜態優先權分配
                    {
                        core.getLocalScheduler().setPriority(core.getTaskSet());
                    }
                }
            }
        } 
        else 
        {
            this.processor.setGlobalSchedulingAlgorithm(algorithm);

            if (this.processor.getGlobalScheduler().getSchedulingAlgorithm() instanceof FixedPrioritySchedulingAlgorithm) //靜態優先權分配
            {
                this.processor.getGlobalScheduler().setPriority(this.processor.getGlobalTaskSet());
            }
        }
    }

    public void setCCProtocol(ConcurrencyControlProtocol cc) 
    {
        this.controller.setCCProtocol(cc);
        for (Core core : this.processor.getCores()) 
        {
            if (core.getCoreStatus() != "stop") 
            {
                core.setController(this.controller);
            }
        }
    }

    public Controller getController() 
    {
        return this.controller;
    }

    public void setDVFSMethod(DynamicVoltageAndFrequencyScalingMethod method) throws CloneNotSupportedException 
    {
        for (DynamicVoltageRegulator DVR : this.processor.getDynamicVoltageRegulators()) 
        {
            if (DVR.getCores().size() > 0) 
            {
                DVR.setDVFSMethod((DynamicVoltageAndFrequencyScalingMethod) method.clone());
            }
        }
    }

    public void loadDataSetting(DataSetting ds) 
    {
        this.processor = ds.getProcessor();
        this.processor.setSimulator(this);

        this.taskSet = ds.getTaskSet();
        this.resourcesSet = ds.getResourceSet();
    }

    public Processor getProcessor() 
    {
        return this.processor;
    }

    public TaskSet getTaskSet() 
    {
        return this.taskSet;
    }

    public ResourcesSet getResourcesSet() 
    {
        return this.resourcesSet;
    }

    public void setSimulationTime(long i) 
    {
        this.simulationTime = i;
    }

    public long getSimulationTime() 
    {
        return this.simulationTime;
    }
}
