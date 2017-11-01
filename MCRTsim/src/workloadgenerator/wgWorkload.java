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

public class wgWorkload 
{
    public WorkloadGenerator parent;
    private final String XMLVersion = "1.0";
    private final String XMLEncoding = "UTF-8";
    private final String  workloadHeader = "workload";
    private final String utilizationHeader = "Utilization";
    private final String  frequencyHeader = "baseSpeed";
    private int frequency;//單位:MHZ
    
/*Task*/    
    private double utilization = 0;
    private final String  taskNumberHeader = "numTask";
    private int minNumOfTask = 0;
    private int maxNumOfTask = 0;
    private int taskNumber = 0;
    private wgTaskSet taskSet = new wgTaskSet(this);
    private double MintaskPeriod = 0;
    private double maxTaskPeriod = 0;
    private double minTaskComputationAmount = 0;
    private double maxTaskComputationAmount = 0;
    private int MinaccessedResourcesNumber = 0;
    private int MaxaccessedResourcesNumber = 0;
    private double criticalSectionRatio = 0;
    private double extraTaskU=0;
    
/*Resources*/
    private final String  resourcesNumberHeader = "numResource";
    private int MinNumOfresources = 0;
    private int MaxNumOfresources = 0;
    private int resourcesNumber = 0;
    private wgResourcesSet resourcesSet = new wgResourcesSet(this);
    
    
    
    public  wgWorkload(WorkloadGenerator p)
    {
        
        this.parent = p;
        
    }
    
    public void showInitInfo()
    {
        System.out.println("!!!!!!!!!!!!!!InitInfo!!!!!!!!!!!!!!!");
        
        System.out.println("    TaskPeriodMax = " + this.getTaskPeriodMax());
        System.out.println("    TaskPeriodMin = " + this.getTaskPeriodMin());
        System.out.println("    ComputationAmountMax = " + this.getTaskComputationAmountMax());
        System.out.println("    ComputationAmountMin = " + this.getTaskComputationAmountMin());
      
        System.out.println("!!!!!!!!!!!!!!InitInfo!!!!!!!!!!!!!!!");
        
    }
    
    public void showInfo()
    {
        System.out.println("!!!!!!!!!!!!!!Workload!!!!!!!!!!!!!!!");
        System.out.println("    ResourcesNumber = " + this.resourcesSet.size());
        System.out.println("    ResourceNumber = " + this.resourcesSet.getResources(0).size());
        System.out.println("    TaskNumber = " + this.taskSet.size());
        
        for(wgTask t : this.taskSet)
        {
            System.out.println("    TaskID = " + t.getID());
            System.out.println("        Period = " + t.getPeriod());
            System.out.println("        ComputationAmount = " + t.getComputationAmount());
            System.out.println("        CriticalSectionNumber = " + t.getCriticalSectionSet().size());
            
            for(wgCriticalSection cs : t.getCriticalSectionSet())
            {
                System.out.println("        ResourcesID = " + cs.getResources().getID());
                System.out.println("            StartTime = " + cs.getStartTime());
                System.out.println("            EndTime = " + cs.getEndTime());
                System.out.println("            CriticalSectionTime = " + cs.getCriticalSectionTime());
            }
            System.out.println("        CriticalSectionRatio = " + t.getCriticalSectionSet().getCriticalSectionRatio());
            System.out.println("        Utilization = "+ t.getUtilization());
            
        }
        System.out.println("TotalUtilization = "+ this.taskSet.getTotalUtilization());
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
        System.out.println("    ResourcesNumber = " + resourcesNumber);
    }

    public void creatTask()
    {
        System.out.println("TaskNuber = " + taskNumber);
        System.out.println("Utilization = " + utilization);
        
        for(int num = 0; num < this.taskNumber ; num++)
        {
            wgTask task = new wgTask(this.taskSet); 
            task.setArrivalTime(0);
            task.setPeriod(wgMath.rangeRandom(this.MintaskPeriod, this.maxTaskPeriod));
            task.setRelativeDeadline(task.getPeriod());
            task.setComputationAmount(wgMath.rangeRandom(this.minTaskComputationAmount, this.maxTaskComputationAmount < task.getPeriod() ? this.maxTaskComputationAmount : task.getPeriod()));
            task.setUtilization();
            
            System.out.println("xxxxx"+task.getUtilization());
            
            this.taskSet.addTask(task);
            
            
            if(this.taskSet.getTotalUtilization() > this.utilization)
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
        
        extraTaskU=wgMath.sub(this.utilization,this.taskSet.getTotalUtilization());
        if(extraTaskU > 0.02)
        {
            wgTask task = new wgTask(this.taskSet); 
            task.setArrivalTime(0);
            task.setPeriod(wgMath.rangeRandom(this.MintaskPeriod, this.maxTaskPeriod));
            task.setRelativeDeadline(task.getPeriod());
            task.setComputationAmount((int)wgMath.mul(extraTaskU,task.getPeriod()));
            task.setUtilization();
            this.taskSet.addTask(task);

//            System.out.println("complement="+extraTaskU+" task.getPeriod()="+task.getPeriod()
//            +" task.getUtilization()="+task.getUtilization());
//            System.out.println("UUUU"+utilization); 
//            System.out.println("ttttttt"+taskSet.getTotalUtilization());
        }
    }
    
    public void creatCriticalSection() 
    {
        int[] cAll = new int[this.resourcesNumber];
        int[] c1 = new int[this.resourcesNumber / 4];
        int[] c2 = new int[this.resourcesNumber / 4];
        int[] c3 = new int[this.resourcesNumber / 4];
        int[] c4 = new int[this.resourcesNumber - c1.length - c2.length - c3.length];
        
        int[] tAll = new int[this.taskSet.size()];
        for(int i = 0; i < this.taskSet.size() / 4; i++)
        {
            tAll[i] = 0;
        }
        for(int i = 0; i < this.taskSet.size() / 4; i++)
        {
            tAll[i + (this.taskSet.size() / 4)] = 1;
        }
        for(int i = 0; i < this.taskSet.size() / 4; i++)
        {
            tAll[i + (this.taskSet.size() / 4)*2] = 2;
        }
        for(int i = 0; i < this.taskSet.size() / 4; i++)
        {
            tAll[i + (this.taskSet.size() / 4)*3] = 3;
        }
        
        for(int i = 0; i < tAll.length; i++)
        {
            int r=(int)(Math.random() * tAll.length);
            int temp = tAll[i];
            tAll[i] = tAll[r];
            tAll[r] = temp;
        }
        
        for(int i = 0; i < this.resourcesNumber; i++)
        {
            cAll[i] = i;
        }
        
        for(int i = 0; i < cAll.length; i++)
        {
            int r=(int)(Math.random() * cAll.length);
            int temp = cAll[i];
            cAll[i] = cAll[r];
            cAll[r] = temp;
        }
        
        for(int i = 0; i < c1.length; i++)
        {
            c1[i] = cAll[i];
        }
        
        for(int i = 0; i < c2.length; i++)
        {
            c2[i] = cAll[i + c1.length];
        }
        
        for(int i = 0; i < c3.length; i++)
        {
            c3[i] = cAll[i + c1.length + c2.length];
        }
        
        for(int i = 0; i < c4.length; i++)
        {
            c4[i] = cAll[i + c1.length + c2.length + c3.length];
        }
        
        for(wgTask t : this.taskSet)
        {
            int accessedResourcesNumber = 0;
            int[] tempArray = null;
            
            //switch((int)(Math.random()*4))
            switch(tAll[t.getID()-1])
            {
                case 0:
                {
                    accessedResourcesNumber =  wgMath.rangeRandom(this.MinaccessedResourcesNumber, this.MaxaccessedResourcesNumber < c1.length ? this.MaxaccessedResourcesNumber : c1.length);
                
                    for(int ii = 0; ii<c1.length;ii++)
                    {
                        int r=(int)(Math.random()*c1.length);
                        int temp = c1[ii];
                        c1[ii] = c1[r];
                        c1[r] = temp;
                    }
                    tempArray = c1;
                    break;
                }
                case 1:
                {
                    accessedResourcesNumber =  wgMath.rangeRandom(this.MinaccessedResourcesNumber, this.MaxaccessedResourcesNumber < c2.length ? this.MaxaccessedResourcesNumber : c2.length);
                
                    for(int ii = 0; ii<c2.length;ii++)
                    {
                        int r=(int)(Math.random()*c2.length);
                        int temp = c2[ii];
                        c2[ii] = c2[r];
                        c2[r] = temp;
                    }
                    tempArray = c2;
                    break;
                }
                case 2:
                {
                    accessedResourcesNumber =  wgMath.rangeRandom(this.MinaccessedResourcesNumber, this.MaxaccessedResourcesNumber < c3.length ? this.MaxaccessedResourcesNumber : c3.length);
                
                    for(int ii = 0; ii<c3.length;ii++)
                    {
                        int r=(int)(Math.random()*c3.length);
                        int temp = c3[ii];
                        c3[ii] = c3[r];
                        c3[r] = temp;
                    }
                    tempArray = c3;
                    break;
                }
                case 3:
                {
                    accessedResourcesNumber =  wgMath.rangeRandom(this.MinaccessedResourcesNumber, this.MaxaccessedResourcesNumber < c4.length ? this.MaxaccessedResourcesNumber : c4.length);
                
                    for(int ii = 0; ii<c4.length;ii++)
                    {
                        int r=(int)(Math.random()*c4.length);
                        int temp = c4[ii];
                        c4[ii] = c4[r];
                        c4[r] = temp;
                    }
                    tempArray = c4;
                    break;
                }
            }
            
            
            for(int i = 0 ; i < accessedResourcesNumber ; i++)
            {
                wgCriticalSection cs = new wgCriticalSection(t.getCriticalSectionSet());
                
                cs.setResources(cs.parent.parent.parent.parent.getResourcesSet().getResources(tempArray[i]));

                cs.setStartTime(wgMath.rangeRandom(0,t.getComputationAmount()));

                wgCriticalSection cs2 = t.getCriticalSectionSet().getCriticalSectionFor(cs);

                if(cs2 == null)
                {
                    double maximunEndTime = cs.getStartTime() + t.getComputationAmount() * (this.getCriticalSectionRatio() - t.getCriticalSectionSet().getCriticalSectionRatio());

                    if(maximunEndTime > t.getComputationAmount())
                    {
                        maximunEndTime = t.getComputationAmount();
                    }

                    do
                    {
                        cs.setEndTime(wgMath.rangeRandom(cs.getStartTime(), maximunEndTime));
                    }while(!(t.getCriticalSectionSet().getCriticalSectionFor(cs.getEndTime()) == null));
                }
                else
                {
                    do
                    {
                        cs.setEndTime(wgMath.rangeRandom(cs.getStartTime(), cs2.getEndTime()));
                    }while(!(t.getCriticalSectionSet().getCriticalSectionFor(cs.getEndTime()) == cs2));
                }

                if(cs.getCriticalSectionTime() == 0)//Remove Needless Critical Section
                {
                    i--;
                }
                else
                {
                    t.getCriticalSectionSet().addCriticalSection(cs);
                }
            }
        }
        
/*        
        for(wgTask t : this.taskSet)
        {
            int accessedResourcesNumber = wgMath.rangeRandom(this.MinaccessedResourcesNumber, this.MaxaccessedResourcesNumber < this.resourcesNumber ? this.MaxaccessedResourcesNumber : this.resourcesNumber);
            
            for(int i = 0 ; i < accessedResourcesNumber ; i++)
            {
                wgCriticalSection cs = new wgCriticalSection(t.getCriticalSectionSet());
                cs.setResources(cs.parent.getUnusedResources());
                cs.setStartTime(wgMath.rangeRandom(0,t.getComputationAmount()));

                wgCriticalSection cs2 = t.getCriticalSectionSet().getCriticalSectionFor(cs);
                
                if(cs2 == null)
                {
                    double maximunEndTime = wgMath.add(cs.getStartTime(),wgMath.mul(t.getComputationAmount(),
                           wgMath.sub(this.getCriticalSectionRatio(), t.getCriticalSectionSet().getCriticalSectionRatio())));
//                    System.out.println("StartTime ="+cs.getStartTime()+", "+"getComputationAmount() ="+t.getComputationAmount()+
//                            ", "+"this.getCriticalSectionRatio() ="+this.getCriticalSectionRatio()+", "+"t.getCriticalSectionSet().getCriticalSectionRatio() ="+t.getCriticalSectionSet().getCriticalSectionRatio());
//                    
//                    System.out.println("sub ="+wgMath.sub(this.getCriticalSectionRatio(), t.getCriticalSectionSet().getCriticalSectionRatio())+", "+"mul ="+wgMath.mul(t.getComputationAmount(),
//                           wgMath.sub(this.getCriticalSectionRatio(), t.getCriticalSectionSet().getCriticalSectionRatio())));
//                    
                    if(maximunEndTime > t.getComputationAmount())
                    {
                        maximunEndTime = t.getComputationAmount();
                    }
                    
//                    System.out.println("maximunEndTime2 =" +maximunEndTime + "ComputationAmount()2 ="+t.getComputationAmount());
                    
                    do
                    {
                        cs.setEndTime(wgMath.rangeRandom(cs.getStartTime(), maximunEndTime));
//                        System.out.println("StartTime2 ="+cs.getStartTime()+", "+"EndTime =" +cs.getEndTime());
//                        System.out.println("1-1");
                    }while(!(t.getCriticalSectionSet().getCriticalSectionFor(cs.getEndTime()) == null));
                }
                else
                {
                    do
                    {
                        cs.setEndTime(wgMath.rangeRandom(cs.getStartTime(), cs2.getEndTime()));
                        System.out.println("1-2");
                    }while(!(t.getCriticalSectionSet().getCriticalSectionFor(cs.getEndTime()) == cs2));
                }
                
                if(cs.getCriticalSectionTime() == 0)//Remove Needless Critical Section
                {
                    i--;//重新產生新的Critical Section
                }
                else
                {
                    t.getCriticalSectionSet().addCriticalSection(cs);//add Critical Section
                    
                    if(t.getCriticalSectionSet().getCriticalSectionRatio()>this.getCriticalSectionRatio()) //如果使用率大於設定值則Remove 最後的 Critical Section
                    {
                        i--;//重新產生新的Critical Section
                        t.getCriticalSectionSet().removeCriticalSection(cs);
                    }
                }
                System.out.println("123");
            }
            System.out.println("1234");
        }
*/
    }
    
    public String checkQuality()
    {
        String str = "<html>";
                
        if( 0 <= this.taskSet.getTotalUtilization() && this.taskSet.getTotalUtilization() <= this.utilization)
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
        
        if( this.MinNumOfresources <= this.resourcesSet.size() && this.taskNumber <= this.MaxNumOfresources)
        {
            str += "<text>The Number of Resources = " + this.resourcesSet.size() +"<text><br>";
        }
        else
        {
            str += "<text>The Number of Resources = <font color='red'>" + this.resourcesSet.size() +"</font><text><br>";
        }
        
        return str;
    }
    
/*setValue*/
    public void setUtilization(double u)
    {
        this.utilization = u;
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
          
        int maximumTaskNum = (int)Math.floor(utilization / (minTaskComputationAmount / maxTaskPeriod));
        System.out.println("MaximumTaskNum = " + maximumTaskNum);
        
        if(maximumTaskNum < minNumOfTask)
        {
            this.taskNumber = maximumTaskNum;
        }       
        else
        {
            this.taskNumber = maxNumOfTask;
        }
    }
    
    public void setTaskPeriodMin(double minimun) 
    {
        this.MintaskPeriod = minimun;
    }
    
    public void setTaskPeriodMax(double maximun) 
    {
        this.maxTaskPeriod = maximun;
    }
    
    public void setTaskComputationTimeMin(double minimun) 
    {
        this.minTaskComputationAmount = minimun;
    }

    public void setTaskComputationTimeMax(double maximun) 
    {
        this.maxTaskComputationAmount = maximun;
    }
    
    public void setResourcesNumbermin(int number) 
    {
        this.MinNumOfresources = number;
    }

    public void setResourcesNumbermax(int number) 
    {
        this.MaxNumOfresources = number;
    }
    
    public void setResourcesNumber()
    {
        this.resourcesNumber = wgMath.rangeRandom(MinNumOfresources, MaxNumOfresources);
    }
    
    public void setFrequency(int MHZ)
    {
        this.frequency = MHZ;
    }
    
    public void setAccessedResourceNumberMin(int Number)
    {
        this.MinaccessedResourcesNumber = Number;
    }
    
    public void setAccessedResourceNumberMax(int Number)
    {
        this.MaxaccessedResourcesNumber = Number;
    }
    
    public void setCriticalSectionRatio(double ratio)
    {
        this.criticalSectionRatio = ratio;
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
    
    public double getUtilization()
    {
        return this.taskSet.getTotalUtilization();
        //return this.utilization;
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
    
    public double getTaskPeriodMin() 
    {
        return this.MintaskPeriod;
    }
    
    public double getTaskPeriodMax() 
    {
        return this.maxTaskPeriod;
    }
    
    public double getTaskComputationAmountMin() 
    {
        return this.minTaskComputationAmount;
    }

    public double getTaskComputationAmountMax() 
    {
        return this.maxTaskComputationAmount;
    }
    
    public int getResourceNumbermin() 
    {
        return this.MinNumOfresources;
    }

    public int getResourceNumbermax() 
    {
        return this.MaxNumOfresources;
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
    
    public String getUtilizationHeader()
    {
        return this.utilizationHeader;
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
        return wgMath.rangeRandom(MinaccessedResourcesNumber, MaxaccessedResourcesNumber);
    }
    
    public double getCriticalSectionRatio()
    {
        return this.criticalSectionRatio;
    }
}
