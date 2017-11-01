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
public class BaseSpeed extends DynamicVoltageAndFrequencyScalingMethod
{
    Vector<Priority> preemptionLevelRes;
    Vector<Priority> preemptionLevelJob;
    
    Vector<Job> blockedJob = new Vector<Job>();
    Vector<Long> blockedTime = new Vector<Long>();
    
    //Vector<CoreSpeed> baseSpeedSet = new Vector<CoreSpeed>();
    
    Vector<Double> worstBlocked = new Vector<Double>();
    
   // CoreSpeed baseSpeedForFC;
    
    public BaseSpeed()
    {
        this.setName("BaseSpeed");
    }
    
    @Override
    public void definedSpeed(Processor p)
    {
        //**MSRP  意味著只能使用MSRP才能使用此速度調節
        preemptionLevelRes = ((MSRP)p.getController().getConcurrencyControlProtocol()).preemptionLevelForRes;
        preemptionLevelJob = ((MSRP)p.getController().getConcurrencyControlProtocol()).preemptionLevelForJob;
        
        for(int i = 0; i < p.getTaskSet().size(); i++)
        {
            this.worstBlocked.add(0.0);
        }
        
        CoreSpeed tempS = p.getCoresSet(0).getCoreSpeedSet().getMinSpeed();

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
                    //System.out.println("Task=" + t.getID() + ":" + (((double)(t.getComputationAmount() + this.spin(t.getLocalCore(), t)) / t.getPeriod()) + ((double)this.block(t) / t.getPeriod())));
                    //System.out.println("  spin^k_j =" + this.spin(t.getLocalCore(), t));
                    //System.out.println("  block_j  =" + this.block(t));
                }
            }

            c.setCurrentSpeed(tempU * c.getParentProcessor().getTaskSet().getMaxProcessingSpeed());//tempU =計算出來的值
                
//System.out.println("DUAL : Core U =" + tempU);
               // this.baseSpeedSet.add(this.utilizationToSpeed(p.getCore(0).getParentCoreSet().getCoreSpeedSet(), tempU));
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
    public void jobDeadlineAction(Job j)
    {
        
    }

    @Override
    public void jobBlockedAction(Job blockedJob, SharedResource blockingRes)
    {
        this.blockedJob.add(blockedJob);
        this.blockedTime.add(blockedJob.getCurrentCore().getCurrentTime());
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
    

    
    private long block(Task task)
    {
        TaskSet ts = this.getParentRegulator().getParentProcessor().getTaskSet();
        long csTime = 0;
        
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
                                long spinTime = this.spin(t.getLocalCore(), c.getUseSharedResource());
                                spinTime += c.getRelativeEndTime() - c.getRelativeStartTime();
                                
                                if(spinTime > csTime)
                                {
                                    csTime = spinTime;
                                }
                                //System.out.println("  Global=" + csTime/magnificationFactor);
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
                                //System.out.println("  Local=" + csTime/magnificationFactor);
                            }
                        }
                    }
                }
            }
        }
        return csTime;
    }
    
    //計算 Spin^k(r_g)
    private long spin(Core k, SharedResource rg)
    {
        long spinTime = 0;
                                
        for(Core tempC : this.getParentRegulator().getParentProcessor().getAllCore())
        {
            if(tempC != k)
            {
                long spinCore = 0;

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
    private long spin(Core c, Task t)
    {
        long spinTime = 0;
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
}
