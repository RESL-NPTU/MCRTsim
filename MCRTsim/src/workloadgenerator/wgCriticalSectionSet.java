/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workloadgenerator;

import java.util.Vector;

/**
 *
 * @author YC
 */
public class wgCriticalSectionSet extends Vector<wgCriticalSection>
{
    private String criticalSectionSetHeader = "criticalSections";
    public wgTask parent;
    private double csr = 0;
    private Vector<wgResources> userdResources = new Vector<>();
    
    public wgCriticalSectionSet(wgTask p)
    {
        super();
        this.parent = p;
    }
    
    /*取得包含Time之最上面的CriticalSection*/
    public wgCriticalSection getCriticalSectionFor(wgCriticalSection cs)
    {
        wgCriticalSection criticalSection = null;
        for(wgCriticalSection cs2 : this)
        {
            if(cs != cs2 && cs2.getStartTime()<= cs.getStartTime() && cs.getStartTime() <= cs2.getEndTime())
            {
                if(criticalSection == null || cs2.getCriticalSectionTime() < criticalSection.getCriticalSectionTime())
                {
                    criticalSection = cs2;
                }
            }
        }
        return criticalSection;
    }
    
    public wgCriticalSection getCriticalSectionFor(double time)
    {
        wgCriticalSection criticalSection = null;
        for(wgCriticalSection cs : this)
        {
            if(cs.getStartTime()<= time && time <= cs.getEndTime())
            {
                if(criticalSection == null || cs.getCriticalSectionTime() < criticalSection.getCriticalSectionTime())
                {
                    criticalSection = cs;
                }
            }
//            System.out.println("1");
        }
        return criticalSection;
    }
    
/*setValue*/ 
    public void addCriticalSection(wgCriticalSection cs)
    {
        this.add(cs);
        this.userdResources.add(cs.getResources());
        this.setCriticalSectionRatio();
    }
    
    public void removeCriticalSection(wgCriticalSection cs)
    {
        this.remove(cs);
        this.userdResources.remove(cs.getResources());
        this.setCriticalSectionRatio();
    }
    
    public void setCriticalSectionRatio()
    {
        this.csr = 0;
        
        for(wgCriticalSection cs : this)
        {
            if(this.getCriticalSectionFor(cs) == null)
            {
                this.csr = wgMath.add(this.csr, wgMath.div(cs.getCriticalSectionTime(), this.parent.getComputationAmount(), 10));
            }
            System.out.println("12");
        }
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
        
        return rsSet_Unused.getResources(wgMath.rangeRandom(0, rsSet_Unused.size()-1));
    }
    
    public double getCriticalSectionRatio()
    {
        return this.csr;
    }
    
    public String getCriticalSectionSetHeader()
    {
        return this.criticalSectionSetHeader;
    }
}
