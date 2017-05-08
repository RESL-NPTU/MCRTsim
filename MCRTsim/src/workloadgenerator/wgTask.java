/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workloadgenerator;

/**
 *
 * @author YC
 */
public class wgTask 
{
    public wgTaskSet parent;
    private final String taskHeader = "task";
    private final String IDHeader = "ID";
    private int ID = 0;
    private final String enterTimeHeader = "arrivalTime";
    private double arrivalTime = 0;
    private final String periodHeader = "period";
    private double period = 0;
    private final String relativeDeadlineHeader = "relativeDeadline";
    private double relativeDeadline = 0;
    private final String computationAmountHeader = "computationAmount";
    private double computationAmount = 0;
    private wgCriticalSectionSet criticalSectionSet = new wgCriticalSectionSet(this);
    private double utilization = 0;
    
    public wgTask(wgTaskSet p)
    {
        this.parent = p;
    }
    
/*setValue*/
    public void setID(int id)
    {
        this.ID = id;
    }
    
    public void setArrivalTime(double arrivalTime)
    {
        this.arrivalTime = arrivalTime;
    }
    
    public void setPeriod(double Period)
    {
        this.period = Period;
    }
    
    public void setRelativeDeadline(double RelativeDeadline)
    {
        this.relativeDeadline = RelativeDeadline;
    }
    
    public void setComputationAmount(double ComputationAmount)
    {
        this.computationAmount = ComputationAmount;
    }
    
    public void setUtilization()
    {
        this.utilization =wgMath.div(this.computationAmount, this.period,10);
    }

/*getValue*/    
    public int getID()
    {
        return this.ID;
    }
    
    public double getEnterTime()
    {
        return this.arrivalTime;
    }
    
    public double getPeriod()
    {
        return this.period;
    }
    
    public double getRelativeDeadline()
    {
        return this.relativeDeadline;
    }
    
    public double getComputationAmount()
    {
        return this.computationAmount;
    }
    
    public double exporeEnterTime()
    {
        return this.arrivalTime / this.parent.parent.parent.exportAccuracy;
    }
    
    public double exporePeriod()
    {
        return this.period / this.parent.parent.parent.exportAccuracy;
    }
    
    public double exporeRelativeDeadline()
    {
        return this.relativeDeadline / this.parent.parent.parent.exportAccuracy;
    }
    
    public double exporeComputationAmount()
    {
        System.out.println("~"+this.computationAmount+"~"+this.computationAmount / this.parent.parent.parent.exportAccuracy);
        //return this.computationAmount / WorkloadGenerator.accuracy;
        return wgMath.div(this.computationAmount, this.parent.parent.parent.exportAccuracy, 10);
    }
    
    public String getTaskHeader()
    {
        return this.taskHeader;
    }
    
    public String getIDHeader()
    {
        return this.IDHeader;
    }
    
    public String getEnterTimeHeader()
    {
        return this.enterTimeHeader;
    }
    
    public String getPeriodHeader()
    {
        return this.periodHeader;
    }
    
    public String getRelativeDeadlineHeader()
    {
        return this.relativeDeadlineHeader;
    }
    
    public String getComputationAmountHeader()
    {
        return this.computationAmountHeader;
    }
    
    public double getUtilization()
    {
        return this.utilization;
    }
    
    public wgCriticalSectionSet getCriticalSectionSet()
    {
        return this.criticalSectionSet;
    }
}
