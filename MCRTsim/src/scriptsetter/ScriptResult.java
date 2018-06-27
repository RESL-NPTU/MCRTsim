/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scriptsetter;

import java.util.Vector;
import static mcrtsim.MCRTsim.println;

/**
 *
 * @author YC
 */
public class ScriptResult 
{   
    private Script parent;
    private int ID;
    private String workloadFile,processorFile;
    public boolean isSchedulable;
    private Vector<Double> powerConsumptions;
    private int taskCount;
    private int totalJobCompeletedCount;
    private int totalJobMissDeadlineCount;
    private double completedRatio;
    private double deadlineMissRatio;
            
    private double averagePendingTime;
    private double averageResponseTime;
    private double beBlockedTimeRatio;
    private double maximumCriticalSectionRatio,actualCriticalSectionRatio,maximumUtilization,actualUtilization;
            
    public ScriptResult(Script s)
    {
        this.parent = s;
        this.ID = s.getScriptResultSet().size()+1;
        this.isSchedulable = true;
        this.powerConsumptions = new Vector<>();
        this.totalJobCompeletedCount = 0;
        this.totalJobMissDeadlineCount = 0;
        this.completedRatio=0;
        this.deadlineMissRatio=0;
        this.averagePendingTime = 0;
        this.averageResponseTime = 0;
    }
    
    
    public void setWorkloadFile(String str)
    {
        this.workloadFile = str;
    }
   
    public void setProcessorFile(String str)
    {
        this.processorFile = str;
    }
    
    public void setSchedulable(boolean b)
    {
        this.isSchedulable = b;
    }
    
    
    public void setMaximumCriticalSectionRatio(double ratio)
    {
        this.maximumCriticalSectionRatio = ratio;
    }
    
    public void setActualCriticalSectionRatio(double ratio)
    {
        this.actualCriticalSectionRatio = ratio;
    }
    
    public void setMaximumUtilization(double U)
    {
        this.maximumUtilization = U;
    }
    
    public void setActualUtilization(double U)
    {
        this.actualUtilization = U;
    }
    
    public void addPowerConsumption(double p)
    {
        this.powerConsumptions.add(p);
    }
    
    public void setTaskCount(int i)
    {
        this.taskCount = i;
    }
    
    public void setTotalJobCompeletedCount(int i)
    {
        this.totalJobCompeletedCount = i;
    }
    
    public void setTotalJobMissDeadlineCount(int i)
    {
        this.totalJobMissDeadlineCount = i;
        if(i>0)
        {
            this.isSchedulable = false;
        }
    }
    
    public void setCompletedRatio(double i)
    {
        this.completedRatio = i;
    }
    
    public void setDeadlineMissRatio(double i)
    {
        this.deadlineMissRatio = i;
    }
    
    public void setAveragePendingTime(double d)
    {
        this.averagePendingTime = d;
    }
    
    public void setAverageResponseTime(double d)
    {
        this.averageResponseTime = d;
    }
    
    public void setBeBlockedTimeRatio(double d)
    {
        this.beBlockedTimeRatio = d;
    }
    
    public double getMaximumCriticalSectionRatio()
    {
        return this.maximumCriticalSectionRatio;
    }
    
    public double getActualCriticalSectionRatio()
    {
        return this.actualCriticalSectionRatio;
    }
    
    public double getMaximumUtilization()
    {
        return this.maximumUtilization;
    }
    
    public double getActualUtilization()
    {
        return this.actualUtilization;
    }
    
    public String getWorkloadFile()
    {
        return this.workloadFile;
    }
    
    public String getProcessorFile()
    {
        return this.processorFile;
    }
    
    public Vector<Double> getPowerConsumptions()
    {
        return this.powerConsumptions;
    }
    
    public double getTotalPowerConsumption()
    {
        double p = 0;
        for(int i = 0 ; i<this.powerConsumptions.size() ; i++)
        {
            p +=this.powerConsumptions.get(i);
        }
        return p;
    }
    
    public int getTaskCount()
    {
        return this.taskCount;
    }
    
    public int getTotalJobCompeletedCount()
    {
        return this.totalJobCompeletedCount;
    }
    
    public int getTotalJobMissDeadlineCount()
    {
        return this.totalJobMissDeadlineCount;
    }
    
    public double getCompletedRatio()
    {
        return this.completedRatio;
    }
    
    public double getDeadlineMissRatio()
    {
        return this.deadlineMissRatio;
    }
    
    public double getAveragePendingTime()
    {
        return this.averagePendingTime;
    }
    
    public double getAverageResponseTime()
    {
        return this.averageResponseTime;
    }
    
    public double getBeBlockedTimeRatio()
    {
        return this.beBlockedTimeRatio;
    }
    
    
    public void showInfo()
    {
        //println("!!");
        println(this.parent.parent.getGroupID()+" - "+this.parent.getID()+" - "+this.ID);
        println("  WorkloadFile : "+this.workloadFile+".xml");
        println("  ProcessorFile : "+this.processorFile+".xml");
        
        for(int i = 0 ; i < this.powerConsumptions.size();i++)
        {
            //println("    Core("+(i+1)+") : "+this.powerConsumptions.get(i));
            println(""+this.powerConsumptions.get(i));
        }
    }
}
