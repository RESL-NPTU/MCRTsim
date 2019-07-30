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

import static mcrtsim.MCRTsim.println;


public class wgWorkload 
{
    public WorkloadGenerator parent;
    private final String XMLVersion = "1.0";
    private final String XMLEncoding = "UTF-8";
    private final String workloadHeader = "workload";
    private final String maximumUtilizationHeader = "maximumUtilization";
    private final String actualUtilizationHeader = "actualUtilization";
    private final String maximumCriticalSectionRatioHeader = "maximumCriticalSectionRatio";
    private final String actualCriticalSectionRatioHeader = "actualCriticalSectionRatio";
    private final String frequencyHeader = "baseSpeed";
    private int frequency;//單位:MHZ
    
/*Task*/    
    private double maximumUtilization = 0;
    private final String  taskNumberHeader = "numTask";
    private int minNumOfTask = 0;
    private int maxNumOfTask = 0;
    private int taskNumber = 0;
    private wgTaskSet taskSet = new wgTaskSet(this);
    private long mintaskPeriod = 0;
    private long maxTaskPeriod = 0;
    private long minTaskComputationAmount = 0;
    private long maxTaskComputationAmount = 0;
    private int minAccessedResourcesNumber = 0;
    private int maxAccessedResourcesNumber = 0;
    private double minCriticalSectionRatio = 0;
    private double maxCriticalSectionRatio = 0;
    private double extraTaskU=0;
    
/*Resources*/
    private final String  resourcesNumberHeader = "numResource";
    private int minNumOfresources = 0;
    private int maxNumOfresources = 0;
    private int resourcesNumber = 0;
    private wgResourcesSet resourcesSet = new wgResourcesSet(this);
    
    
    
    public  wgWorkload(WorkloadGenerator p)
    {
        
        this.parent = p;
        
    }
    
    public void showInitInfo()
    {
        println("!!!!!!!!!!!!!!InitInfo!!!!!!!!!!!!!!!");
        
        println("    TaskPeriodMax = " + this.getTaskPeriodMax());
        println("    TaskPeriodMin = " + this.getTaskPeriodMin());
        println("    ComputationAmountMax = " + this.getTaskComputationAmountMax());
        println("    ComputationAmountMin = " + this.getTaskComputationAmountMin());
      
        println("!!!!!!!!!!!!!!InitInfo!!!!!!!!!!!!!!!");
        
    }
    
    public void showInfo()
    {
        println("!!!!!!!!!!!!!!Workload!!!!!!!!!!!!!!!");
        println("    ResourcesNumber = " + this.resourcesSet.size());
        println("    ResourceNumber = " + this.resourcesSet.getResources(0).size());
        println("    TaskNumber = " + this.taskSet.size());
        
        for(wgTask t : this.taskSet)
        {
            println("    TaskID = " + t.getID());
            println("        Period = " + t.getPeriod());
            println("        ComputationAmount = " + t.getComputationAmount());
            println("        CriticalSectionNumber = " + t.getCriticalSectionSet().size());
            
            for(wgCriticalSection cs : t.getCriticalSectionSet())
            {
                println("        ResourcesID = " + cs.getResources().getID());
                println("            StartTime = " + cs.getStartTime());
                println("            EndTime = " + cs.getEndTime());
                println("            CriticalSectionTime = " + cs.getCriticalSectionTime());
            }
//            println("        CriticalSectionRatio = " + t.getCriticalSectionSet().getCriticalSectionRatio());
            println("        Utilization = "+ t.getUtilization());
            
        }
        println("TotalUtilization = "+ this.taskSet.getTotalUtilization());
    }
    
    public void creatResources() 
    {
    /*creatResources*/
        for(int num = 0; num < this.resourcesNumber ; num++)
        {
            wgResources r = new wgResources(this.resourcesSet);
        /*creatResource目前預設一個*/   
            for(int n = 0; n < 1 ; n++)
            {
                r.addResource(new wgResource(r));
            }
            
            this.resourcesSet.addResources(r);
        }
        println("    ResourcesNumber = " + resourcesNumber);
    }

    public void creatTask()
    {
        println("TaskNuber = " + taskNumber);
        println("MaximumUtilization = " + maximumUtilization);
        
        for(int num = 0; num < this.taskNumber ; num++)
        {
            wgTask task = new wgTask(this.taskSet); 
            task.setArrivalTime(0);
            task.setPeriod(wgMath.rangeRandom(this.mintaskPeriod, this.maxTaskPeriod));
            task.setRelativeDeadline(task.getPeriod());
            task.setComputationAmount(wgMath.rangeRandom(this.minTaskComputationAmount, this.maxTaskComputationAmount < task.getPeriod() ? this.maxTaskComputationAmount : task.getPeriod()));
            task.setUtilization();
            
            println("Utilization"+task.getUtilization());
            
            this.taskSet.addTask(task);
            
            
            if(this.taskSet.getTotalUtilization() > this.maximumUtilization)
            {
                this.taskSet.removeTask(task);
                if(parent.isExtraTask()) 
                {    
                    this.creatExtraTask();
                }
                break;
            }
        }
        
        this.taskNumber = taskSet.size();
    }
    
    public void creatExtraTask()
    {//做補數
        
        extraTaskU=wgMath.sub(this.maximumUtilization,this.taskSet.getTotalUtilization());
        if(extraTaskU > 0.02)
        {
            wgTask task = new wgTask(this.taskSet); 
            task.setArrivalTime(0);
            task.setPeriod(wgMath.rangeRandom(this.mintaskPeriod, this.maxTaskPeriod));
            task.setRelativeDeadline(task.getPeriod());
            task.setComputationAmount((int)wgMath.mul(extraTaskU,task.getPeriod()));
            task.setUtilization();
            this.taskSet.addTask(task);

//            println("complement="+extraTaskU+" task.getPeriod()="+task.getPeriod()
//            +" task.getUtilization()="+task.getUtilization());
//            println("UUUU"+utilization); 
//            println("ttttttt"+taskSet.getTotalUtilization());
        }
    }
    
    public void creatCriticalSection()
    {
        for(wgTask t : this.taskSet)
        {
            t.creatCriticalSection();
        }
    }
    
    public boolean checkQuality()
    {
        return 
        (
            (this.maximumUtilization-0.02 <= this.taskSet.getTotalUtilization() && this.taskSet.getTotalUtilization() <= this.maximumUtilization)
            &&(this.minNumOfTask <= this.taskNumber && this.taskNumber <= this.maxNumOfTask)
            &&(this.minNumOfresources <= this.resourcesSet.size() && this.resourcesSet.size() <= this.maxNumOfresources)
        );
        
    }
    
    public String showQuality()
    {
        String str = "<html>";
        str+= "<text> The generated workload has the following attributes : <text><br><br>";
                
        if( this.maximumUtilization-0.02 <= this.taskSet.getTotalUtilization() && this.taskSet.getTotalUtilization() <= this.maximumUtilization)
        { 
                str += "<text>Utilization = " + this.taskSet.getTotalUtilization() +"<text><br>";
        }
        else
        {
            str += "<text>Utilization = <font color='red'>" + this.taskSet.getTotalUtilization() +"</font><text><br>";
        }
            
        if( this.minNumOfTask <= this.taskNumber && this.taskNumber <= this.maxNumOfTask)
        {
            str += "<text>The Number of Task = " + this.taskNumber +"<text><br>";
        }
        else
        {
            str += "<text>The Number of Task = <font color='red'>" + this.taskNumber +"</font><text><br>";
        }
        
        if( this.minNumOfresources <= this.resourcesSet.size() && this.resourcesSet.size() <= this.maxNumOfresources)
        {
            str += "<text>The Number of Resources = " + this.resourcesSet.size() +"<text><br>";
        }
        else
        {
            str += "<text>The Number of Resources = <font color='red'>" + this.resourcesSet.size() +"</font><text><br>";
        }
        
        str+= "<br><text> Re-generate it or Save ? <text><br>";
        
        return str;
    }
    
/*setValue*/
    public void setUtilization(double u)
    {
        this.maximumUtilization = u;
    }
    
    public void setTaskNumberMin(int number) 
    {
        this.minNumOfTask = number;
    }

    public void setTaskNumberMax(int number) 
    {
        this.maxNumOfTask = number;
    }
    
    public void setTaskNumber()
    {
        int maximumTaskNum = (int)Math.floor(maximumUtilization / (minTaskComputationAmount / maxTaskPeriod));
        println("MaximumTaskNum = " + maximumTaskNum);
        
        if(maximumTaskNum < minNumOfTask)
        {
            this.taskNumber = maximumTaskNum;
        }       
        else
        {
            this.taskNumber = maxNumOfTask;
        }
    }
    
    public void setTaskPeriodMin(long minimun) 
    {
        this.mintaskPeriod = minimun;
    }
    
    public void setTaskPeriodMax(long maximun) 
    {
        this.maxTaskPeriod = maximun;
    }
    
    public void setTaskComputationTimeMin(long minimun) 
    {
        this.minTaskComputationAmount = minimun;
    }

    public void setTaskComputationTimeMax(long maximun) 
    {
        this.maxTaskComputationAmount = maximun;
    }
    
    public void setResourcesNumbermin(int number) 
    {
        this.minNumOfresources = number;
    }

    public void setResourcesNumbermax(int number) 
    {
        this.maxNumOfresources = number;
    }
    
    public void setResourcesNumber()
    {
        this.resourcesNumber = (int)wgMath.rangeRandom(minNumOfresources, maxNumOfresources);
    }
    
    public void setFrequency(int MHZ)
    {
        this.frequency = MHZ;
    }
    
    public void setAccessedResourceNumberMin(int Number)
    {
        this.minAccessedResourcesNumber = Number;
    }
    
    public void setAccessedResourceNumberMax(int Number)
    {
        if(Number <= this.resourcesNumber)
        {
            this.maxAccessedResourcesNumber = Number;
        }
        else
        {
            this.maxAccessedResourcesNumber = this.resourcesNumber;
        }
    }
    
    public void setMinCriticalSectionRatio(double ratio)
    {
        this.minCriticalSectionRatio = ratio;
    }
    
    public void setMaxCriticalSectionRatio(double ratio)
    {
        this.maxCriticalSectionRatio = ratio;
    }
    
    
    
/*getValue*/
    public String getXMLVersion()
    {
        return this.XMLVersion;
    }
    
    public String getXMLEncoding()
    {
        return this.XMLEncoding;
    }
    
    public double getMaximumUtilization()
    {
        return this.maximumUtilization;
    }
    
    public double getActualUtilization()
    {
        return this.taskSet.getTotalUtilization();
    }
    
    public int getTaskNumberMin() 
    {
        return this.minNumOfTask;
    }

    public int getTaskNumberMax() 
    {
        return this.maxNumOfTask;
    }
    
    public int getTaskNumber()
    {
        return this.taskNumber;
    }
    
    public long getTaskPeriodMin() 
    {
        return this.mintaskPeriod;
    }
    
    public long getTaskPeriodMax() 
    {
        return this.maxTaskPeriod;
    }
    
    public long getTaskComputationAmountMin() 
    {
        return this.minTaskComputationAmount;
    }

    public long getTaskComputationAmountMax() 
    {
        return this.maxTaskComputationAmount;
    }
    
    public int getResourceNumbermin() 
    {
        return this.minNumOfresources;
    }

    public int getMaxResourceNumber() 
    {
        return this.maxNumOfresources;
    }
    
    public int getResourcesNumber()
    {
        return this.resourcesNumber;
    }
    
    public int getFrequency()
    {
        return this.frequency;
    }
    
    public String getWorkloadHeader()
    {
        return this.workloadHeader;
    }
    
    public String getMaximumUtilizationHeader()
    {
        return this.maximumUtilizationHeader;
    }
    
    public String getActualUtilizationHeader()
    {
        return this.actualUtilizationHeader;
    }
    
    public String getMaximumCriticalSectionRatioHeader()
    {
        return this.maximumCriticalSectionRatioHeader;
    }
    
    public String getActualCriticalSectionRatioHeader()
    {
        return this.actualCriticalSectionRatioHeader;
    }
    
    public String getTaskNumberHeader()
    {
        return this.taskNumberHeader;
    }
    
    public String getResourcesNumberHeader()
    {
        return this.resourcesNumberHeader;
    }
    
    public String getFrequencyHeader()
    {
        return this.frequencyHeader;
    }
    
    public wgTaskSet getTaskSet()
    {
        return this.taskSet;
    }
    
    public wgResourcesSet getResourcesSet()
    {
        return this.resourcesSet;
    }
    
    public int getRandomAccessedResourceNum()
    {
        return (int)wgMath.rangeRandom(minAccessedResourcesNumber, maxAccessedResourcesNumber);
    }
    
    public double getMinCriticalSectionRatio()
    {
        return this.minCriticalSectionRatio;
    }
    
    public double getMaxCriticalSectionRatio()
    {
        return this.maxCriticalSectionRatio;
    }
    
    public double getActualCriticalSectionRatio()
    {
        return this.taskSet.getTotalCriticalSectionRatio();
    }
}
