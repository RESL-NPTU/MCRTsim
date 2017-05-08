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
public class wgCriticalSection 
{
    public wgCriticalSectionSet parent;
    private final String criticalSectionHeader = "criticalSection";
    private final String resourceIDHeader = "resourceID";
    private final String startTimeHeader = "startTime";
    private double startTime = 0;
    private final String endTimeHeader = "endTime";
    private double endTime = 0;
    private wgResources resources;
    
    public wgCriticalSection(wgCriticalSectionSet p)
    {
        this.parent = p;
    }
    
/*setValue*/
    public void setStartTime(double StartTime)
    {
        this.startTime = StartTime;
    }
    
    public void setEndTime(double EndTime)
    {
        this.endTime = EndTime;
    }
    
    public void setResources(wgResources Resources)
    {
        this.resources = Resources;
    }
    
/*getValue*/    
    public double getStartTime()
    {
        return this.startTime;
    }
    
    public double getEndTime()
    {
        return this.endTime;
    }
    
    public double getCriticalSectionTime()
    {
        return (this.endTime - this.startTime);
    }
    
    public double exporeStartTime()
    {
        return this.startTime / this.parent.parent.parent.parent.parent.exportAccuracy;
    }
    
    public double exporeEndTime()
    {
        return this.endTime / this.parent.parent.parent.parent.parent.exportAccuracy;
    }
    
    public double exporeCriticalSectionTime()
    {
        return (this.endTime - this.startTime) / this.parent.parent.parent.parent.parent.exportAccuracy;
    }
    
    public wgResources getResources()
    {
        return this.resources;
    }
    
    public String getCriticalSectionHeader()
    {
        return this.criticalSectionHeader;
    }
    
    public String getResourceIDHeader()
    {
        return this.resourceIDHeader;
    }
    
    public String getStartTimeHeader()
    {
        return this.startTimeHeader;
    }
    
    public String getEndTimeHeader()
    {
        return this.endTimeHeader;
    }
}
