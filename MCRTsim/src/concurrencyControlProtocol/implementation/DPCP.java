/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencyControlProtocol.implementation;

import SystemEnvironment.Processor;
import WorkLoad.CriticalSection;
import WorkLoad.Job;
import WorkLoad.Priority;
import WorkLoad.SharedResource;
import WorkLoad.Task;
import java.util.Vector;
import mcrtsim.Definition;

/**
 *
 * @author YC
 */
public class DPCP extends PCP
{
    public DPCP()
    {
        this.setName("Dynamic Priority Ceiling Protocol");
    }
    
    @Override
    public void preAction(Processor p)
    {
        //DPCP 設定Resource的初始值方法如下：
        //挑出使用特定Resource的所有Task中最緊急的Deadline，設定給此Resource
        for(int i = 0; i < p.getSharedResourceSet().size(); i++)
        {
            this.ceilingRes.add(Definition.Ohm);
            
            long Highest_Priority_Deadline=Integer.MAX_VALUE;
            
            for(Task t : p.getSharedResourceSet().get(i).getAccessTaskSet())
            {
                if(t.getRelativeDeadline() < Highest_Priority_Deadline)
                {
                    Highest_Priority_Deadline = t.getRelativeDeadline();
                }
            }
            
            this.ceilingRes.set(i, new Priority(Highest_Priority_Deadline));
        }
        
    }
    
    @Override
    public void jobCompletedAction(Job j)
    {
        //在資源解鎖時設定下一輪的Resources Ceiling
        Vector<Long> curDeadlineSet = new Vector<>();
        
        CriticalSection criticalSection = null;
        for(int i=0;i<j.getCriticalSectionSet().size();i++)//設定下一輪的Resources Ceiling
        {
            criticalSection = j.getCriticalSectionSet().poll();
        
            for(int k=0;k<criticalSection.getUseSharedResource().getAccessTaskSet().size();k++) //該Resource的所有Tasks
            {
                long tempDeadLine = criticalSection.getUseSharedResource().getAccessTaskSet().getTask(k).getCurJob().getAbsoluteDeadline(); //找出當前Job的DeadLine
                /*避免同一個Job的DeadLine被記錄*/
                if(criticalSection.getUseSharedResource().getAccessTaskSet().getTask(k).getCurJob() != j)
                {
                    curDeadlineSet.add(tempDeadLine); //儲存所有的Task DeadLine
                }
                
                //找出當前Job的下一個Job的DeadLine
                long nextDeadLine = tempDeadLine + criticalSection.getUseSharedResource().getAccessTaskSet().getTask(k).getRelativeDeadline();
                
                curDeadlineSet.add(nextDeadLine); //儲存所有的Task的下一個Job的DeadLine
            }
            
            long earliestDeadline = Long.MAX_VALUE;//紀錄目前最緊急的DeadLine
        
            for(long Deadline :  curDeadlineSet)
            {
                if(Deadline < earliestDeadline && Deadline > j.getAbsoluteDeadline())
                {
                    earliestDeadline = Deadline;
                }
            }
            this.ceilingRes.get(criticalSection.getUseSharedResource().getID() - 1).setValue(earliestDeadline);
        }
        
    }
    
}
