/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencyControlProtocol.implementation;

import java.util.Vector;
import simulation.DataSetting;
import simulation.Final;
import simulation.Job;
import simulation.LockInfo;
import simulation.Priority;
import simulation.Task;

/**
 *
 * @author USER
 */

public class DPCP extends PCP {
    
    public DPCP(DataSetting ds) 
    {
        super(ds);
        this.setName("Dynamic Priority Ceiling Protocol");
        
        //設定所有的Resources Ceiling的初始值
        for(int i = 0; i < this.getDataSetting().getResourceSet().size(); i++) //抓所有的Resources
        {
            int Highest_Priority_Deadline=Integer.MAX_VALUE;
            
            //抓出該Resource的所有Tasks
            
            for(int j = 0; j < this.getDataSetting().getResourceSet().getResources(i).getAccessSet().size(); j++)
            {
                Task task = this.getDataSetting().getResourceSet().getResources(i).getAccessSet().getTask(j);
                
                if(task.getRelativeDeadline() < Highest_Priority_Deadline)
                {
                    Highest_Priority_Deadline = task.getRelativeDeadline();
                }
            }
            this.getDataSetting().getResourceSet().getResources(i).setPriorityCeiling(new Priority(Highest_Priority_Deadline));
        }
    }
    
    public void jobCompleted(Job j, LockInfo l) 
    {
        //在資源解鎖時設定下一輪的Resources Ceiling
        Vector<Integer> curDeadlineSet = new Vector<>();
        for(int i=0;i<j.getCriticalSectionSet().size();i++)//設定下一輪的Resources Ceiling
        {
            for(int k=0;k<j.getCriticalSectionSet().get(i).getResources().getAccessSet().size();k++) //該Resource的所有Tasks
            {
                int tempDeadLine = j.getCriticalSectionSet().get(i).getResources()
                        .getAccessSet().getTask(k).getCurJob().getAbsoluteDeadline(); //找出當前Job的DeadLine
                /*避免同一個Job的DeadLine被記錄*/
                if(j.getCriticalSectionSet().get(i).getResources().getAccessSet().getTask(k).getCurJob() != j)
                {
                    curDeadlineSet.add(tempDeadLine); //儲存所有的Task DeadLine
                }
                
                int nextDeadLine = tempDeadLine + j.getCriticalSectionSet().get(i).getResources()
                        .getAccessSet().getTask(k).getRelativeDeadline();//找出當前Job的下一個Job的DeadLine
                curDeadlineSet.add(nextDeadLine); //儲存所有的Task的下一個Job的DeadLine
            }
            
            int earliestDeadline = Integer.MAX_VALUE;//紀錄目前最緊急的DeadLine
        
            for(int Deadline :  curDeadlineSet)
            {
                if(Deadline < earliestDeadline && Deadline > j.getAbsoluteDeadline())
                {
                    earliestDeadline = Deadline;
                }
            }
            j.getCriticalSectionSet().get(i).getResources().getPriorityCeiling().setValue(earliestDeadline);
        }
    }
}
