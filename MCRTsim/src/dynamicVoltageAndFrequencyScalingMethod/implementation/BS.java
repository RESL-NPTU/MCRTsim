/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicVoltageAndFrequencyScalingMethod.implementation;

import SystemEnvironment.Core;
import SystemEnvironment.Processor;
import WorkLoad.CoreSpeed;
import WorkLoad.CriticalSection;
import WorkLoad.Job;
import WorkLoad.Priority;
import WorkLoad.SharedResource;
import WorkLoad.Task;
import WorkLoadSet.TaskSet;
import concurrencyControlProtocol.implementation.MSRP;
import dynamicVoltageAndFrequencyScalingMethod.DynamicVoltageAndFrequencyScalingMethod;
import java.util.Vector;

/**
 *
 * @author ShiuJia
 */
public class BS extends DynamicVoltageAndFrequencyScalingMethod
{
    Vector<Priority> preemptionLevelRes;
    Vector<Priority> preemptionLevelJob;
    
    
    Vector<Double> baseSpeedSet = new Vector<Double>();
    
    Vector<Double> worstBlocked = new Vector<Double>();
    
    CoreSpeed baseSpeedForFC;
    
    public BS()
    {
        this.setName("Base Speed");
        //thesis name = Energy-Efficient Task Scheduling and Synchronization for Multicore Real-Time Systems
        //此方法為多核心的速度節能方法但此方法有BUG(論文本身的BUG)
    }
    
    @Override
    public void definedSpeed(Processor p)
    {
        preemptionLevelRes = ((MSRP)p.getController().getConcurrencyControlProtocol()).preemptionLevelForRes;
        preemptionLevelJob = ((MSRP)p.getController().getConcurrencyControlProtocol()).preemptionLevelForTask;
        
        for(int i = 0; i < p.getTaskSet().size(); i++)
        {
            this.worstBlocked.add(0.0);
        }
        
        for(Core c : p.getAllCore())
        {
            double tempU = 0;
            for(Task t : p.getTaskSet())
            {
                if(t.getLocalCore() == c)
                {
                    double tempWU = this.worstCaseUtilization(t, p.getTaskSet());
                    if(tempU < tempWU)
                    {
                        tempU = tempWU;
                    }
                }
            }
            this.baseSpeedSet.add(tempU * c.getParentProcessor().getTaskSet().getProcessingSpeed());
            
        }
        
    }

    @Override
    public void jobArrivesProcessorAction(Job j, Processor p)
    {
        
    }

    @Override
    public void jobArrivesCoreAction(Job j, Core c)
    {
        
    }

    @Override
    public void coresExecuteAction()
    {
        
    }

    @Override
    public void coreExecuteAction(Core c)
    {
        c.setCurrentSpeed(this.baseSpeedSet.get(c.getID() - 1));
    }

    @Override
    public void jobLockAction(Job j, SharedResource r)
    {
        
    }

    @Override
    public void jobUnlockAction(Job j, SharedResource r)
    {
        
    }

    @Override
    public void jobCompleteAction(Job j)
    {
        
    }

    @Override
    public void jobBlockedAction(Job blockedJob, SharedResource blockingRes)
    {
    }
    
    private double worstCaseUtilization(Task t, TaskSet ts)
    {
        double tempWorst = 0;
        for(Task tt : ts)
        {
            if(tt.getLocalCore() == t.getLocalCore() && tt.getPeriod() >= t.getPeriod() && tt.getID() != t.getID())
            {
                tempWorst += ((double)(tt.getComputationAmount() + this.spin(tt.getLocalCore(), tt))) / tt.getPeriod();
            }
        }
        
        this.worstBlocked.set(t.getID() - 1, tempWorst);
        tempWorst += ((double)(t.getComputationAmount() + this.spin(t.getLocalCore(), t) + this.block(t))) / t.getPeriod();
        return tempWorst;
    }
    
    private double block(Task task)
    {
        TaskSet ts = this.getParentRegulator().getParentProcessor().getTaskSet();
        double csTime = 0;
        
        for(Task t : ts)
        {
            if(t != task && t.getLocalCore() == task.getLocalCore())
            {
                if(this.preemptionLevelJob.get(task.getID() - 1).isHigher(this.preemptionLevelJob.get(t.getID() - 1)))
                {
                    if(t.getCriticalSectionSet().size() > 0)
                    {
                        for(CriticalSection c : t.getCriticalSectionSet())
                        {
                            //計算 B^g_i
                            if(c.getUseSharedResource().isGlobal())
                            {
                                double spinTime = this.spin(t.getLocalCore(), c.getUseSharedResource());
                                spinTime += c.getRelativeEndTime() - c.getRelativeStartTime();
                                
                                if(spinTime > csTime)
                                {
                                    csTime = spinTime;
                                }
                                //System.out.println("  Global=" + csTime/100000);
                            }
                            //計算B^l_i
                            else
                            {
                                if(this.preemptionLevelRes.get(c.getUseSharedResource().getID() - 1).isHigher(this.preemptionLevelJob.get(task.getID() - 1)))
                                {
                                    if(c.getRelativeEndTime() - c.getRelativeStartTime() > csTime)
                                    {
                                        csTime = c.getRelativeEndTime() - c.getRelativeStartTime();
                                    }
                                }
                                //System.out.println("  Local=" + csTime/100000);
                            }
                        }
                    }
                }
            }
        }
        return csTime;
    }
    
    //計算 Spin^k(r_g)
    private double spin(Core k, SharedResource rg)
    {
        double spinTime = 0;
                                
        for(Core tempC : this.getParentRegulator().getParentProcessor().getAllCore())
        {
            if(tempC != k)
            {
                double spinCore = 0;

                for(Task tempT : this.getParentRegulator().getParentProcessor().getTaskSet())
                {
                    if(tempT.getLocalCore() == tempC)
                    {
                        if(tempT.getCriticalSectionSet().size() > 0)
                        {
                            for(CriticalSection cs : tempT.getCriticalSectionSet())
                            {
                                if(cs.getUseSharedResource() == rg)
                                {
                                    if(cs.getRelativeEndTime() - cs.getRelativeStartTime() > spinCore)
                                    {
                                        spinCore = cs.getRelativeEndTime() - cs.getRelativeStartTime();
                                    }
                                }
                            }
                        }
                    }
                }
                
                spinTime += spinCore;
            }
        }
        
        return spinTime;
    }
    
    //計算spin^c_t
    private double spin(Core c, Task t)
    {
        double spinTime = 0;
        if(t.getCriticalSectionSet().size() > 0)
        {
            for(CriticalSection cs : t.getCriticalSectionSet())
            {
                if(cs.getUseSharedResource().isGlobal())
                {
                    spinTime += this.spin(t.getLocalCore(), cs.getUseSharedResource());
                }
            }
        }
        return spinTime;
    }

    @Override
    public void jobFirstExecuteAction(Job j)
    {
        
    }

    @Override
    public void jobEveryExecuteAction(Job j)
    {
    }

    @Override
    public void checkEndSystemTimeAction(long systemTime) {
    }

    @Override
    public void jobMissDeadlineAction(Job j) {
    }
}
