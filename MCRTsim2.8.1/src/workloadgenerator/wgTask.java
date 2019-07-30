/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workloadgenerator;

import java.util.Vector;
import static mcrtsim.MCRTsim.*;

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
    private long arrivalTime = 0;
    private final String periodHeader = "period";
    private long period = 0;
    private final String relativeDeadlineHeader = "relativeDeadline";
    private long relativeDeadline = 0;
    private final String computationAmountHeader = "computationAmount";
    private long computationAmount = 0;
    private long computationAmountForCriticalSection = 0;
    
    private wgCriticalSectionSet criticalSectionSet = new wgCriticalSectionSet(this);
    private double utilization = 0;
    
    public wgTask(wgTaskSet p)
    {
        this.parent = p;
    }
    
    
    public void creatCriticalSection()
    {
        println("Task ID = " + this.ID);
        println("    ComputationAmount = "+this.computationAmount);
        println("    ComputationAmountForCriticalSection = "+this.computationAmountForCriticalSection);
        
        
        this.criticalSectionSet.setMaxCriticalSectionTime((long) wgMath.mul(this.computationAmountForCriticalSection, this.parent.parent.getMaxCriticalSectionRatio()));
        this.criticalSectionSet.setMinCriticalSectionTime((long) wgMath.mul(this.computationAmountForCriticalSection, this.parent.parent.getMinCriticalSectionRatio()));
        
        println("    MaxCriticalSectionTime = "+ this.criticalSectionSet.getMaxCriticalSectionTime());
        
        if(this.criticalSectionSet.getMaxCriticalSectionTime() == 0)
        {
            return;
        }
        
        int accessedResourcesNumber = this.parent.parent.getRandomAccessedResourceNum();
        println("    AccessedResourcesNumber = " + accessedResourcesNumber);
        
        if(accessedResourcesNumber == 0)return;
        
        
        for(int i = 0 ; i < accessedResourcesNumber ; i++)
        {
            println("");
            wgCriticalSection cs = new wgCriticalSection(this.criticalSectionSet);
            cs.setResources(cs.parent.getUnusedResources());
            println("    CS(" + (i+1) +"):R("+cs.getResources().getID()+")");
            
            long maximunEndTime = 0;
            
            do
            {
                
                cs.setStartTime(wgMath.rangeRandom(0,this.computationAmountForCriticalSection-1));
                println("V        StartTime = " + cs.getStartTime());

                maximunEndTime = this.getCriticalSectionMaximunEndTime(cs.getStartTime());
                println("^        FinalMaximunEndTime = " + maximunEndTime);
                
            }while(cs.getStartTime() == maximunEndTime || maximunEndTime == 0);
            
            
            long endTime = wgMath.rangeRandom(cs.getStartTime()+1,maximunEndTime);
    println("        Original EndTime = "+endTime);
            
            
            /*csSet為與cs同階層之CriticalSectionSet*/
            Vector<wgCriticalSection> csSet = this.criticalSectionSet.getCriticalSectionSetOn
                           (this.criticalSectionSet.getCriticalSectionUnder(cs.getStartTime(),"S"));
    
    print("      Original cs同階層之csSet:");
    for(wgCriticalSection csss :csSet)
    {
        print("cs:R("+csss.getResources().getID()+"), ");
    }
    println();
    
            
            /*在csSet中移除非巢狀的CriticalSection*/
    
    println("          在csSet中移除非巢狀的cs:");
    print("          Remove: ");
            for(int j = 0 ; j < csSet.size() ;j++)
            {
                if(csSet.get(j).getEndTime() <= cs.getStartTime() || maximunEndTime <= csSet.get(j).getStartTime())
                {
    print("cs:R("+csSet.get(j).getResources().getID()+"), ");
                    csSet.remove(csSet.get(j));
                    j--;
                }
            }
    println("");
    
    print("      cs同階層之csSet:");
    for(wgCriticalSection csss :csSet)
    {
        print("cs:R("+csss.getResources().getID()+"), ");
    }
    println();
    

//    
//    if(this.criticalSectionSet.getCriticalSectionUnder(cs.getStartTime(),"S")!=null)
//    {
//        println("Start csID = "+this.criticalSectionSet.getCriticalSectionUnder(cs.getStartTime(),"S").getResources().getID());
//        println("             "+ this.criticalSectionSet.getCriticalSectionUnder(cs.getStartTime(),"S").getStartTime()+ " , "+ this.criticalSectionSet.getCriticalSectionUnder(cs.getStartTime(),"S").getEndTime());
//    }
//    else
//    {
//        println("Start csID = null");
//    }   
//    if(this.criticalSectionSet.getCriticalSectionUnder(endTime,"E")!=null)
//    {
//        println("End csID = "+this.criticalSectionSet.getCriticalSectionUnder(endTime,"E").getResources().getID());
//        println("           "+ this.criticalSectionSet.getCriticalSectionUnder(endTime,"E").getStartTime()+ " , "+ this.criticalSectionSet.getCriticalSectionUnder(endTime,"E").getEndTime());
//
//    }
//    else
//    {
//        println("End csID = null");
//    }
    
            if(this.criticalSectionSet.getCriticalSectionUnder(cs.getStartTime(),"S") == this.criticalSectionSet.getCriticalSectionUnder(endTime,"E"))
            {
                cs.setEndTime(endTime);
    println("0, S"+cs.getStartTime()+" , E"+cs.getEndTime()+", ME"+maximunEndTime+" , "+ this.criticalSectionSet.getMaxCriticalSectionTime());
            
            }
            else
            {
                println("0.5, S"+ cs.getStartTime()+" , E"+endTime+" , ME"+maximunEndTime+" , "+ this.criticalSectionSet.getMaxCriticalSectionTime());
                
                wgCriticalSection cs2 = this.criticalSectionSet.getCriticalSectionUnder(endTime,"E",csSet);
                
                
                if(cs2 == null)
                {
                    println("cs2 = null");
                }
                
                if((cs2.getEndTime() - endTime) <= (endTime - cs2.getStartTime()))
                {
                    if(cs2 == csSet.lastElement())
                    {
                        cs.setEndTime(wgMath.rangeRandom(cs2.getEndTime(),maximunEndTime));
                        println("1, "+cs.getStartTime()+" , "+cs.getEndTime());
                    }
                    else
                    {
                        cs.setEndTime(wgMath.rangeRandom(cs2.getEndTime(),csSet.get(csSet.indexOf(cs2)+1).getStartTime()));
                        println("2, "+cs.getStartTime()+" , "+cs.getEndTime());
                    }
                }
                else
                {
                    if(cs2 == csSet.firstElement())
                    {
                        cs.setEndTime(wgMath.rangeRandom(cs.getStartTime()+1,cs2.getStartTime()));
                        println("3, "+cs.getStartTime()+" , "+cs.getEndTime());
                    }
                    else
                    {
                        cs.setEndTime(wgMath.rangeRandom(csSet.get(csSet.indexOf(cs2)-1).getEndTime(),cs2.getStartTime()));
                        println("4,"+cs.getEndTime());
                    }
                }
            }
            
            this.criticalSectionSet.addCriticalSection(cs);
        }
        
        this.criticalSectionSet.zoomInCriticalSection();
        
        
        println("TaskID = "+ this.getID());
        println("ComputationAmount = "+ this.computationAmount);
        
        println("TotalCriticalSectionTime = " + this.criticalSectionSet.getTotalCriticalSectionTime());
        
        println("CSR = " + ((double)this.criticalSectionSet.getTotalCriticalSectionTime())/((double)this.computationAmountForCriticalSection));
        println("ALLcs = ");
        for(wgCriticalSection csss: this.criticalSectionSet)
        {
            println("cs:R("+csss.getResources().getID()+"), "+csss.getStartTime()+", "+csss.getEndTime()+", "+csss.getCriticalSectionTime());
        }
        println("--");
        println("");
    }
    
    
    /**
     * 取得 新CriticalSection的最大EndTime
    */
    private long getCriticalSectionMaximunEndTime(long csStartTime)
    {
        long maximunEndTime = 0;
        
        wgCriticalSection cs2 = this.criticalSectionSet.getCriticalSectionUnder(csStartTime,"S");
        
        if(cs2 != null)
        {
            maximunEndTime = cs2.getEndTime();
            println("        OriginalMaximunEndTime = " + maximunEndTime+" , cs: R("+cs2.getResources().getID()+")");
        }
        else//進到這裡代表csStartTime在最底層
        {
            Vector<wgCriticalSection> csSet = this.criticalSectionSet.getCriticalSectionSetOn(null);
            maximunEndTime = csStartTime + this.criticalSectionSet.getMaxCriticalSectionTime();
            
            if(maximunEndTime == csStartTime)//若相等，直接跳出此函式，並重新產生start time
            {
          println("");
          println("--reCreat--");
                return maximunEndTime;
            }
            println("        OriginalMaximunEndTime = " + maximunEndTime+" , cs: null");
    print("          csSet :");
    for(wgCriticalSection csss :csSet)
    {
        print("cs: R("+csss.getResources().getID()+"),");
    }
    println();
            println("        第一次調整:");
            
            for(int i = 0 ; i < csSet.size() ;i++)
            {
                if(csSet.get(i).getEndTime() <= csStartTime)
                {
                    maximunEndTime-=csSet.get(i).getCriticalSectionTime();
                    println("            cs:R("+csSet.get(i).getResources().getID()+") ,"+csSet.get(i).getCriticalSectionTime());
                    csSet.remove(csSet.get(i));
                    i--;
    print("          csSet :");
    for(wgCriticalSection csss :csSet)
    {
        print("cs:R("+csss.getResources().getID()+"), ");
    }
    println();
                }
            }
            println("        1MaximunEndTime = " + maximunEndTime);
            
            
            println("        第二次調整:");
            if(this.computationAmountForCriticalSection < maximunEndTime)
            {
                maximunEndTime = this.computationAmountForCriticalSection;
            }
            println("        2MaximunEndTime = " + maximunEndTime);
            

            
            println("        第三次調整:");
            do
            {
                for(int i = 0 ; i < csSet.size() ;i++)
                {
                    if(csSet.get(i).getEndTime() > maximunEndTime)
                    {
                        maximunEndTime-=csSet.get(i).getCriticalSectionTime();
                        println("            cs:R("+csSet.get(i).getResources().getID()+") ,"+csSet.get(i).getCriticalSectionTime());
                        csSet.remove(csSet.get(i));
                        i--;
    print("          csSet :");
    for(wgCriticalSection csss :csSet)
    {
        print("cs:R("+csss.getResources().getID()+"), ");
    }
    println();
    
                        
                    }
                }
                
                if(csSet.isEmpty())break;
            }
            while(!(csSet.lastElement().getEndTime() <= maximunEndTime));
            println("        3MaximunEndTime = " + maximunEndTime);
        }
        
        return maximunEndTime;
    }
    
    
    
/*setValue*/
    public void setID(int id)
    {
        this.ID = id;
    }
    
    public void setArrivalTime(long arrivalTime)
    {
        this.arrivalTime = arrivalTime;
    }
    
    public void setPeriod(long Period)
    {
        this.period = Period;
    }
    
    public void setRelativeDeadline(long RelativeDeadline)
    {
        this.relativeDeadline = RelativeDeadline;
    }
    
    public void setComputationAmount(long ComputationAmount)
    {
        this.computationAmount = ComputationAmount;
        
        double zoomIn = wgMath.div(this.parent.parent.parent.criticalSectionAccuracy,this.parent.parent.parent.taskAccuracy);
        
        this.computationAmountForCriticalSection = (long)wgMath.mul(ComputationAmount,zoomIn);
    }
    
    public void setUtilization()
    {
        this.utilization =wgMath.div(this.computationAmount, this.period);
    }

/*getValue*/    
    public int getID()
    {
        return this.ID;
    }
    
    public long getEnterTime()
    {
        return this.arrivalTime;
    }
    
    public long getPeriod()
    {
        return this.period;
    }
    
    public long getRelativeDeadline()
    {
        return this.relativeDeadline;
    }
    
    public long getComputationAmount()
    {
        return this.computationAmount;
    }
    
    public long getComputationAmountForCriticalSection()
    {
        return this.computationAmountForCriticalSection;
    }
    
    public double exporeEnterTime()
    {
        return wgMath.div(this.arrivalTime , this.parent.parent.parent.taskAccuracy);
    }
    
    public double exporePeriod()
    {
        return wgMath.div(this.period , this.parent.parent.parent.taskAccuracy);
    }
    
    public double exporeRelativeDeadline()
    {
        return wgMath.div(this.relativeDeadline , this.parent.parent.parent.taskAccuracy);
    }
    
    public double exporeComputationAmount()
    {
        return wgMath.div(this.computationAmount, this.parent.parent.parent.taskAccuracy);
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
    
    public double getCriticalSectionRatio()
    {
        return wgMath.div(this.criticalSectionSet.getTotalCriticalSectionTime(),this.computationAmountForCriticalSection);
    }
    
    public wgCriticalSectionSet getCriticalSectionSet()
    {
        return this.criticalSectionSet;
    }
}
