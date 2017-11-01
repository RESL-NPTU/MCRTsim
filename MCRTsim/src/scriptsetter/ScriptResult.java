/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scriptsetter;

import java.util.Vector;

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
    private double totalPendingTime;
    private double totalResponseTime;
            
    public ScriptResult(Script s)
    {
        this.parent = s;
        this.ID = s.getScriptResultSet().size()+1;
        this.isSchedulable = true;
        this.powerConsumptions = new Vector<>();
        this.totalJobCompeletedCount = 0;
        this.totalJobMissDeadlineCount = 0;
        this.totalPendingTime = 0;
        this.totalResponseTime = 0;
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
    
    public void setTotalPendingTime(double d)
    {
        this.totalPendingTime = d;
    }
    
    public void setTotalResponseTime(double d)
    {
        this.totalResponseTime = d;
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
    
    public double getTotalPendingTime()
    {
        return this.totalPendingTime;
    }
    
    public double getTotalResponseTime()
    {
        return this.totalResponseTime;
    }
    
    public String outputResult()
    {
        String str = ""+this.workloadFile;
        for(int i = 0 ; i < this.powerConsumptions.size();i++)
        {
            str+=" "+this.powerConsumptions.get(i);
        }
        
        return str;
    }
    
    public void showInfo()
    {
        //System.out.println("!!");
        System.out.println(this.parent.parent.getGroupID()+" - "+this.parent.getID()+" - "+this.ID);
        System.out.println("  WorkloadFile : "+this.workloadFile+".xml");
        System.out.println("  ProcessorFile : "+this.processorFile+".xml");
        
        for(int i = 0 ; i < this.powerConsumptions.size();i++)
        {
            //System.out.println("    Core("+(i+1)+") : "+this.powerConsumptions.get(i));
            System.out.println(""+this.powerConsumptions.get(i));
        }
    }
}
