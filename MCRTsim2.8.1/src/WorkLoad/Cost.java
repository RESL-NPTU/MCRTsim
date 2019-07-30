/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WorkLoad;

import SystemEnvironment.Core;
import mcrtsim.Definition.CoreStatus;

/**
 *
 * @author YC
 */
public class Cost 
{
    private Core curCore = null; //當前的Core
    private Core requestCore = null; //發出請求造成Cost的Core
    private Job requestJob = null;//造成Cost的Job
    private CoreStatus status = null;//屬於哪一種Cost狀態
    private Cost nextCost = null;
    private long costTime = 0;
    
    public Cost(Core curCore, Core requestCore, Job requestJob, CoreStatus status)
    {
        this.curCore = curCore;
        this.requestCore = requestCore;
        this.requestJob = requestJob;
        this.status = status;

        switch(this.status)
        {
            case CONTEXTSWITCH:
                this.costTime = this.curCore.getParentProcessor().getParentSimulator().getContextSwitchTime();
            break;
                
            case MIGRATION:
                this.costTime = this.curCore.getParentProcessor().getParentSimulator().getMigrationTime();
            break;
                
            default:
        }  
    }
    
    public void execution(long time)
    {
        this.costTime -= time;
    }
    
    public void setNextCost(Cost migrationCost)//做給migration cost使用的函式，在發生migration cost之前必定會產生 context switch cost
    {
        this.nextCost = migrationCost;
    }
    
    public Core getCurrentCore()
    {
        return this.curCore;
    }
    
    public Core getRequestCore()
    {
        return this.requestCore;
    }
    
    public Job getRequestJob()
    {
        return this.requestJob;
    }
    
    public CoreStatus getStatus()
    {
        return this.status;
    }
    
    public void setCostTime(long time)
    {
        this.costTime = time;
    }
    
    public boolean checkIsCompleted()//只要成功完成之後，此Cost就會被剔除CostQuequ
    {
        if(this.costTime == 0)
        {
            if(this.status == CoreStatus.CONTEXTSWITCH)
            {
                if(this.nextCost == null)
                {
                    if(this.requestJob.getCurrentCore().getWorkingJob() != requestJob)//若 requestJob == this.requestJob.getCurrentCore().getWorkingJob()
                    {                                                                 //則不需要重新setWorkingJob
                        this.requestJob.getCurrentCore().setWorkingJob(requestJob);
                    }
                }
                else if(this.nextCost.checkIsCompleted())//避免migration cost == 0時忽略執行Job在 requestCore.getLocalReadyQueue的轉移
                {
                    //不需要加東西
                }
            }
            else if(this.status == CoreStatus.MIGRATION && this.requestCore == this.curCore)
            {
                this.requestCore.getLocalReadyQueue().add(requestJob);
                requestJob.setCurrentCore(this.requestCore);
            }
            return true;
        }
        return false;
    }
    
}
