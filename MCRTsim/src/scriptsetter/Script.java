/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scriptsetter;


import java.util.Vector;
import mcrtsim.MCRTsimMath;

/**
 *
 * @author YC
 */
public class Script 
{
    public ScriptTable parent;
    private String ID,workloadSite,processorSite,partitionAlgorithm,DVFSMethod,schedAlgorithm,CCProtocol,simulationTime;
            
    private Vector<ScriptResult> scriptResultSet;
    private  MCRTsimMath math = new MCRTsimMath();
    
    public Script(ScriptTable ST,ScriptPanel SP)
    {
        this.parent = ST;
        this.scriptResultSet = new Vector<>();
        this.ID = SP.getScriptID();
        this.workloadSite = SP.getWorkloadSite();
        this.processorSite = SP.getProcessorSite();
        this.partitionAlgorithm = SP.getPartitionMethod();
        this.DVFSMethod = SP.getDVFSMethod();
        this.schedAlgorithm = SP.getSchedAlorithm();
        this.CCProtocol = SP.getCCProtocol();
        this.simulationTime = SP.getSimulationTime();
    }

//setValue
    public void modifyScript(ScriptPanel SP)
    {
        this.scriptResultSet.removeAllElements();
        this.ID = SP.getScriptID();
        this.workloadSite = SP.getWorkloadSite();
        this.processorSite = SP.getProcessorSite();
        this.partitionAlgorithm = SP.getPartitionMethod();
        this.DVFSMethod = SP.getDVFSMethod();
        this.schedAlgorithm = SP.getSchedAlorithm();
        this.CCProtocol = SP.getCCProtocol();
        this.simulationTime = SP.getSimulationTime();
    }
    
    public void addScriptResult(ScriptResult SR)
    {
        this.scriptResultSet.add(SR);
    }
    
    public void removeAllScriptResult()
    {
        this.scriptResultSet.removeAllElements();
    }
    
    
    public void setWorkloadSite(String s)
    {
        this.workloadSite = s;
    }
    
    public void setProcessorSite(String s)
    {
        this.processorSite = s;
    }
    
    public void setPartitionAlgorithm(String s)
    {
        this.partitionAlgorithm = s;
    }
    
    public void setDVFSMethod(String s)
    {
        this.DVFSMethod = s;
    }
    
    public void setSchedulingAlgorithm(String s)
    {
        this.schedAlgorithm = s;
    }
    
    public void setCCProtocol(String s)
    {
        this.CCProtocol = s;
    }
    
    public void setSimulationTime(String s)
    {
        this.simulationTime = s;
    }
    
//getValue
    
    public String getID()
    {
        return this.ID;
    }
    
    public String getWorkloadSite()
    {
        return this.workloadSite;
    }
    
    public String getProcessorSite()
    {
        return this.processorSite;
    }
    
    public String getPartitionAlgorithm()
    {
        return this.partitionAlgorithm;
    }
    
    public String getDVFSMethod()
    {
        return this.DVFSMethod;
    }
    
    public String getSchedulingAlgorithm()
    {
        return this.schedAlgorithm;
    }
    
    public String getCCProtocol()
    {
        return this.CCProtocol;
    }
    
    public String getSimulationTime()
    {
        return this.simulationTime;
    }
    
    public Vector<ScriptResult> getScriptResultSet()
    {
        return this.scriptResultSet;
    }
    
    //get average result
    public int getWorkloadCount()
    {
        return this.scriptResultSet.size();
    }
    
    public int getSchedulableCount()
    {
        int n = 0;
        for(int i = 0 ; i<this.scriptResultSet.size() ; i++)
        {
            if(this.scriptResultSet.get(i).isSchedulable)
            {
                n += 1;
            }
        }
        return n;
    }
            
    public int getNonSchedulableCount()
    {
        int n = 0;
        for(int i = 0 ; i<this.scriptResultSet.size() ; i++)
        {
            if(!this.scriptResultSet.get(i).isSchedulable)
            {
                n += 1;
            }
        }
        return n;
    }
    
    public double getAverageTaskCount()
    {
        double p = 0;
        
        for(int i = 0 ; i<this.scriptResultSet.size() ; i++)
        {
            p = MCRTsimMath.add(p,this.scriptResultSet.get(i).getTaskCount());
        }
        
        return math.changeDecimalFormatFor5(MCRTsimMath.div(p,this.scriptResultSet.size()));
    }
    
    public double getAveragePowerConsumption()
    {
        double p = 0;
        
        for(int i = 0 ; i<this.scriptResultSet.size() ; i++)
        {
            p = MCRTsimMath.add(p,this.scriptResultSet.get(i).getTotalPowerConsumption());
        }
        
        return math.changeDecimalFormatFor5(MCRTsimMath.div(p,this.scriptResultSet.size()));                
    }
    
    public double getAverageJobCompeletedCount()
    {
        double p = 0;
        
        for(int i = 0 ; i<this.scriptResultSet.size() ; i++)
        {
            p = MCRTsimMath.add(p,this.scriptResultSet.get(i).getTotalJobCompeletedCount());
        }
        
        return math.changeDecimalFormatFor5(MCRTsimMath.div(p,this.scriptResultSet.size()));                
    }
    
    public double getAverageJobMissDeadlineCount()
    {
        double p = 0;
        
        for(int i = 0 ; i<this.scriptResultSet.size() ; i++)
        {
            p = MCRTsimMath.add(p,this.scriptResultSet.get(i).getTotalJobMissDeadlineCount());
        }
        
        return math.changeDecimalFormatFor5(MCRTsimMath.div(p,this.scriptResultSet.size()));                
    }
    
    public double getAverageCompletedRatio()
    {
        double p = 0;
        
        for(int i = 0 ; i<this.scriptResultSet.size() ; i++)
        {
            p = MCRTsimMath.add(p,this.scriptResultSet.get(i).getCompletedRatio());
        }
        
        return math.changeDecimalFormatFor5(MCRTsimMath.div(p,this.scriptResultSet.size()));                
    }
    
    public double getAverageDeadlineMissRatio()
    {
        double p = 0;
        
        for(int i = 0 ; i<this.scriptResultSet.size() ; i++)
        {
            p = MCRTsimMath.add(p,this.scriptResultSet.get(i).getDeadlineMissRatio());
        }
        
        return math.changeDecimalFormatFor5(MCRTsimMath.div(p,this.scriptResultSet.size()));                
    }
    
    
    public double getAveragePendingTime()
    {
        double p = 0;
        
        for(int i = 0 ; i<this.scriptResultSet.size() ; i++)
        {
            p = MCRTsimMath.add(p,this.scriptResultSet.get(i).getAveragePendingTime());
        }
        return math.changeDecimalFormatFor5(MCRTsimMath.div(p,this.scriptResultSet.size()));                
    }
    
    public double getAverageResponseTime()
    {
        double p = 0;
        
        for(int i = 0 ; i<this.scriptResultSet.size() ; i++)
        {
            p = MCRTsimMath.add(p,this.scriptResultSet.get(i).getAverageResponseTime());
        }
        
        return math.changeDecimalFormatFor5(MCRTsimMath.div(p,this.scriptResultSet.size()));                
    }
    
    public double getAverageMaximumCriticalSectionRatio()
    {
        double p = 0;
        
        for(int i = 0 ; i<this.scriptResultSet.size() ; i++)
        {
            p = MCRTsimMath.add(p,this.scriptResultSet.get(i).getMaximumCriticalSectionRatio());
        }
        
        return math.changeDecimalFormatFor5(MCRTsimMath.div(p,this.scriptResultSet.size()));    
    }
    
    public double getAverageActualCriticalSectionRatio()
    {
        double p = 0;
        
        for(int i = 0 ; i<this.scriptResultSet.size() ; i++)
        {
            p = MCRTsimMath.add(p,this.scriptResultSet.get(i).getActualCriticalSectionRatio());
        }
        
        return math.changeDecimalFormatFor5(MCRTsimMath.div(p,this.scriptResultSet.size()));    
    }
    
    public double getAverageMaximumUtilization()
    {
        double p = 0;
        
        for(int i = 0 ; i<this.scriptResultSet.size() ; i++)
        {
            p = MCRTsimMath.add(p,this.scriptResultSet.get(i).getMaximumUtilization());
        }
        
        return math.changeDecimalFormatFor5(MCRTsimMath.div(p,this.scriptResultSet.size())); 
    }
    
    public double getAverageActualUtilization()
    {
        double p = 0;
        
        for(int i = 0 ; i<this.scriptResultSet.size() ; i++)
        {
            p = MCRTsimMath.add(p,this.scriptResultSet.get(i).getActualUtilization());
        }
        
        return math.changeDecimalFormatFor5(MCRTsimMath.div(p,this.scriptResultSet.size()));    
    }
    
    public double getAverageActualBeBlockedTimeRatio()
    {
        double p = 0;
        
        for(int i = 0 ; i<this.scriptResultSet.size() ; i++)
        {
            p = MCRTsimMath.add(p,this.scriptResultSet.get(i).getBeBlockedTimeRatio());
        }
        
        return math.changeDecimalFormatFor5(MCRTsimMath.div(p,this.scriptResultSet.size()));  
    }
}
