/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workloadgenerator;

import java.util.Comparator;
import java.util.Random;
import java.util.Vector;
import static mcrtsim.MCRTsim.println;

/**
 *
 * @author YC
 */
public class wgCriticalSectionSet extends Vector<wgCriticalSection>
{
    private String criticalSectionSetHeader = "criticalSections";
    public wgTask parent;
    private long maxCriticalSectionTime = 0;//最大的maxCriticalSectionTime ＝ 使用者設定之csr * Job之計算時間
    private long minCriticalSectionTime = 0;
    private long totalCriticalSectionTime = 0;//當前累計之CriticalSectionTime
//    private double csr = 0;
    private Vector<wgResources> userdResources = new Vector<>();
    
    public wgCriticalSectionSet(wgTask p)
    {
        super();
        this.parent = p;
    }
    
    /*找出time點下方一層的一個CriticalSection並回傳，回傳null代表此time在最底層*/
    /*timeisSorE 輸入 S or E 字串，讓此函式辨別輸入之time為StartTime or EndTime*/
    public wgCriticalSection getCriticalSectionUnder(double time, String timeisSorE)
    {
        return this.getCriticalSectionUnder(time, timeisSorE, this);
    }
    
    /*在csSet中找出time點下方一層的一個CriticalSection並回傳，回傳null代表此time不在csSet內之所有cs的範圍中*/
    /*timeisSorE 輸入 S or E 字串，讓此函式辨別輸入之time為StartTime or EndTime*/
    public wgCriticalSection getCriticalSectionUnder(double time, String timeisSorE, Vector<wgCriticalSection> csSet)
    {
        wgCriticalSection criticalSection = null;
        for(wgCriticalSection cs : csSet)
        {
            switch(timeisSorE)
            {
                case "S":
                    if(cs.getStartTime() <= time && time < cs.getEndTime())
                    {
                        if(criticalSection == null || cs.getCriticalSectionTime() < criticalSection.getCriticalSectionTime())
                        {
                            criticalSection = cs;
                        }
                    }
                break;
                case "E":
                    if(cs.getStartTime() < time && time <= cs.getEndTime())
                    {
                        if(criticalSection == null || cs.getCriticalSectionTime() < criticalSection.getCriticalSectionTime())
                        {
                            criticalSection = cs;
                        }
                    }
                break;
            }
        }
        return criticalSection;
    }
    
    /**
     * 找出wgCS下方一層的一個CriticalSection並回傳
     * @param wgCS 不可為null
     * @return null代表wgCS在最底層
     */
    public wgCriticalSection getCriticalSectionUnder(wgCriticalSection wgCS)
    {
        wgCriticalSection criticalSection = null;
        for(wgCriticalSection cs : this)
        {
            /*避免cs 與 wgCS同時使用時，互相選到對方 */
            if(wgCS != cs && ((cs.getStartTime() <= wgCS.getStartTime() && wgCS.getEndTime() < cs.getEndTime())
                    ||(cs.getStartTime() < wgCS.getStartTime() && wgCS.getEndTime() <= cs.getEndTime())))
            {
                if(criticalSection == null || cs.getCriticalSectionTime() < criticalSection.getCriticalSectionTime())
                {
                    criticalSection = cs;
                }
            }
        }
        return criticalSection;
    }
    
    /**
     * 找出criticalSection上方一層的所有cs，並回傳csSet。
     * @param criticalSection 可以是null，這時代表找出最底層的所有cs
     * @return csSet 為 criticalSection上方一層的所有cscriticalSection上方一層的所有cs
     */
    public Vector<wgCriticalSection> getCriticalSectionSetOn(wgCriticalSection criticalSection)
    {
        Vector<wgCriticalSection> csSet = new Vector<wgCriticalSection>();
        
        
        for(wgCriticalSection cs : this)
        {
            if(cs != criticalSection && criticalSection == this.getCriticalSectionUnder(cs))
            {
                csSet.add(cs);
            }
        }
        
        /*csSet依照其Start Time遞增排序*/
        csSet.sort
        (
            new Comparator<wgCriticalSection>()
            {
                @Override
                public int compare(wgCriticalSection cs1, wgCriticalSection cs2)
                {
                    if(cs1.getStartTime() < cs2.getStartTime())
                    {
                        return -1;
                    }
                    else if(cs1.getStartTime() > cs2.getStartTime())
                    {
                        return 1;
                    }
                    else if(cs2.getCriticalSectionTime() > cs1.getCriticalSectionTime())
                    {
                        return -1;
                    }
                    return 0;
                }
            }
        );
        
        /*過濾掉同時間開始與結束的criticalSection，僅保留一個*/
        for(int i=0 ; i<csSet.size() ; i++)
        {
            for(int j=i+1 ; j < csSet.size() ; j++)
            {
                if(csSet.get(i).getStartTime() == csSet.get(j).getStartTime() && csSet.get(i).getEndTime() == csSet.get(j).getEndTime())
                {
                    csSet.remove(csSet.get(j));
                    j--;
                }
                else
                {
                    break;
                }
            }
        }
        
        return csSet;
    }
    
    public void zoomInCriticalSection()
    {
        Random ran = new Random();
        Vector<wgCriticalSection> csSet = this.getCriticalSectionSetOn(null);
        
        while(this.totalCriticalSectionTime < this.minCriticalSectionTime)
        {
            long gapTime = this.maxCriticalSectionTime - this.totalCriticalSectionTime;
            
            wgCriticalSection cs = csSet.get(ran.nextInt(csSet.size()));//取得要更改的CS
            
            if(ran.nextInt(2) == 0)//改變startTime
            {
                if(cs == csSet.firstElement())
                {
                    long time = cs.getStartTime() < gapTime ? cs.getStartTime() : gapTime;
                    cs.setStartTime(cs.getStartTime()-wgMath.rangeRandom(0, time));
                
                }
                else
                {
                    wgCriticalSection previousCS = csSet.get(csSet.indexOf(cs)-1);
                    long time = cs.getStartTime()-previousCS.getEndTime() < gapTime 
                              ? cs.getStartTime()-previousCS.getEndTime() : gapTime;
                    
                    cs.setStartTime(cs.getStartTime()-wgMath.rangeRandom(0, time));
                }
            }
            else//改變EndTime
            {
                if(cs == csSet.lastElement())
                {
                    
                    long time = this.parent.getComputationAmountForCriticalSection()-cs.getEndTime() < gapTime
                              ? this.parent.getComputationAmountForCriticalSection()-cs.getEndTime() : gapTime;
                    cs.setEndTime(cs.getEndTime()+wgMath.rangeRandom(0, time));
                }
                else
                {
                    wgCriticalSection nextCS = csSet.get(csSet.indexOf(cs)+1);
                    
                    long time = nextCS.getStartTime()-cs.getEndTime() < gapTime
                              ? nextCS.getStartTime()-cs.getEndTime() : gapTime;
                    cs.setStartTime(cs.getEndTime()+wgMath.rangeRandom(0, time));
                }
            }
            
            this.setTotalCriticalSectionTime();
        }
    }
    
/*setValue*/ 
    public void addCriticalSection(wgCriticalSection cs)
    {
        this.add(cs);
        this.userdResources.add(cs.getResources());
        this.setTotalCriticalSectionTime();
    }
    
    public void removeCriticalSection(wgCriticalSection cs)
    {
        this.remove(cs);
        this.userdResources.remove(cs.getResources());
        this.setTotalCriticalSectionTime();
    }
    
    public void setMaxCriticalSectionTime(long time)
    {
        this.maxCriticalSectionTime = time;
    }
    
    public void setMinCriticalSectionTime(long time)
    {
        this.minCriticalSectionTime = time;
    }

    private void setTotalCriticalSectionTime()
    {
        Vector<wgCriticalSection> csSet = this.getCriticalSectionSetOn(null);
        this.totalCriticalSectionTime = 0;
        for(wgCriticalSection cs : csSet)
        {
            this.totalCriticalSectionTime += cs.getCriticalSectionTime();
            println("!!!!!CriticalSectionTime = "+cs.getCriticalSectionTime());
        }
        
        println("!!!!!totalCriticalSectionTime = "+totalCriticalSectionTime);
    }
/*getValue*/
    public wgCriticalSection getCriticalSection(int i)
    {
        return this.get(i);
    }
    
    public wgResources getUnusedResources()
    {
        wgResourcesSet rsSet = this.parent.parent.parent.getResourcesSet();
        wgResourcesSet rsSet_Unused = new wgResourcesSet();
        rsSet_Unused.addAll(rsSet);
        rsSet_Unused.removeAll(this.userdResources);
        
        if(rsSet_Unused.size() == 0)
        {
            return null;
        }
        
        return rsSet_Unused.getResources((int)wgMath.rangeRandom(0, rsSet_Unused.size()-1));
    }
    
    public String getCriticalSectionSetHeader()
    {
        return this.criticalSectionSetHeader;
    }
    
    public long getMaxCriticalSectionTime()
    {
        return this.maxCriticalSectionTime;
    }
    
    public long getMinCriticalSectionTime()
    {
        return this.minCriticalSectionTime;
    }
    
    public long getTotalCriticalSectionTime()
    {
        return this.totalCriticalSectionTime;
    }
}
