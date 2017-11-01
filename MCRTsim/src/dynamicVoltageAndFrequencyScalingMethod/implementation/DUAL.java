/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicVoltageAndFrequencyScalingMethod.implementation;

import SystemEnvironment.Core;
import SystemEnvironment.Processor;
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
public class DUAL extends DynamicVoltageAndFrequencyScalingMethod
{
    public class RemoteBlocking
    {
        Job blockedJob;
        SharedResource globalResource;
        double startBlock;
        double endBlock;
    }
    
    Vector<Priority> preemptionLevelRes;
    Vector<Priority> preemptionLevelJob;
    
    Vector<Job> blockedJob = new Vector<Job>();
    Vector<Long> blockedTime = new Vector<Long>();
    
    Vector<Double> worstBlocked = new Vector<Double>();
    Vector<Double> worstSpin = new Vector<Double>();
    Vector<Double> worstBlock = new Vector<Double>();
    
    Vector<Double> acBlock = new Vector<Double>();
    Vector<Double> acSpin = new Vector<Double>();
    Vector<RemoteBlocking> remoteBlocking = new Vector<RemoteBlocking>();
    boolean isSpin = false;
    
    public DUAL()
    {
        this.setName("DUAL");
    }
    
    @Override
    public void definedSpeed(Processor p)
    {
        preemptionLevelRes = ((MSRP)p.getController().getConcurrencyControlProtocol()).preemptionLevelForRes;
        preemptionLevelJob = ((MSRP)p.getController().getConcurrencyControlProtocol()).preemptionLevelForJob;
        
        for(int i = 0; i < p.getTaskSet().size(); i++)
        {
            this.worstBlocked.add(0.0);
            this.worstSpin.add(0.0);
            this.worstBlock.add(0.0);
            this.acBlock.add(0.0);
            this.acSpin.add(0.0);
        }
        
            for(Core c : p.getAllCore())
            {
                this.acBlock.add(0.0);
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
                //System.out.println("DUAL : Core U =" + tempU);
                //System.out.println("DUAL = " + c.getID() + "  DFS =" + tempU);
               // this.baseSpeedSet.add(this.utilizationToSpeed(c.getParentCoreSet().getCoreSpeedSet(), tempU));
                c.setCurrentSpeed(tempU * tempU * c.getParentProcessor().getTaskSet().getMaxProcessingSpeed());
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
        
        if(r.isGlobal())
        {
            double block = 0;
            if(this.remoteBlocking.size() > 0)
            {
                for(int i = 0; i < this.remoteBlocking.size(); i++)
                {
                    if(this.remoteBlocking.get(i).blockedJob == j && this.remoteBlocking.get(i).globalResource == r)
                    {
                        block += (j.getCurrentCore().getCurrentTime() - this.remoteBlocking.get(i).startBlock);
                        this.remoteBlocking.remove(i);
                    }
                }
            }
            
            double tempU = 0;
            TaskSet ts = this.getParentRegulator().getParentProcessor().getTaskSet();
            for(Task t : ts)
            {
                if(t.getLocalCore() == j.getCurrentCore())
                {
                    double tempWU = this.actualSpinUtilization(t, ts, j, r, block);

                    if(tempU < tempWU)
                    {
                        tempU = tempWU;
                    }
                }
            }
                    Core c = j.getCurrentCore();
                    c.setCurrentSpeed(tempU * c.getParentProcessor().getTaskSet().getMaxProcessingSpeed());

        }
    }

    @Override
    public void jobUnlockAction(Job j, SharedResource r)
    {
        if(r.isGlobal())
        {
            for(RemoteBlocking rb : this.remoteBlocking)
            {
                if(r == rb.globalResource && j == rb.blockedJob)
                {
                    rb.endBlock = j.getCurrentCore().getCurrentTime();
                }
            }
        }
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
        if(blockingRes.isGlobal())
        {
            RemoteBlocking rb = new RemoteBlocking();
            rb.blockedJob = blockedJob;
            rb.globalResource = blockingRes;
            rb.startBlock = blockedJob.getCurrentCore().getCurrentTime();
            this.remoteBlocking.add(rb);
        }
        else
        {
            this.blockedJob.add(blockedJob);
            this.blockedTime.add(blockedJob.getCurrentCore().getCurrentTime());
        }
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
        this.worstSpin.set(t.getID() - 1, this.spin(t.getLocalCore(), t));
        this.worstBlock.set(t.getID() - 1, this.block(t));
        tempWorst += ((double)(t.getComputationAmount() + this.worstSpin.get(t.getID() - 1) + this.worstBlock.get(t.getID() - 1))) / t.getPeriod();
        return tempWorst;
    }
    
    private double actualBlockUtilization(Task t, TaskSet ts, Job j, double blockTime)
    {
        double tempWorst = this.worstBlocked.get(t.getID() - 1);
        if(t == j.getParentTask())
        {
            tempWorst += ((double)(t.getComputationAmount() + this.worstSpin.get(t.getID() - 1) + blockTime)) / t.getPeriod();
        }
        else
        {
            tempWorst += ((double)(t.getComputationAmount() + this.worstSpin.get(t.getID() - 1) + this.worstBlock.get(t.getID() - 1))) / t.getPeriod();
        }
        return tempWorst;
    }
    
    private double actualSpinUtilization(Task t, TaskSet ts, Job j, SharedResource r, double blockTime)
    {
        double gap = this.spin(j.getCurrentCore(), r) - blockTime;
        double newAcSpin = this.acSpin.get(j.getParentTask().getID() - 1) - gap;
        this.acSpin.set(j.getParentTask().getID() - 1, newAcSpin);
        
        double tempWorst = 0;
        for(Task tt : ts)
        {
            if(tt.getLocalCore() == t.getLocalCore() && tt.getPeriod() >= t.getPeriod() && tt.getID() != t.getID())
            {
                if(tt == j.getParentTask())
                {
                    
                    tempWorst += ((double)(tt.getComputationAmount() + this.acSpin.get(j.getParentTask().getID() - 1))) / tt.getPeriod();
                }
                else
                {
                    tempWorst += ((double)(tt.getComputationAmount() + this.worstSpin.get(tt.getID() - 1))) / tt.getPeriod();
                }
            }
        }
        
        if(t == j.getParentTask())
        {
            tempWorst += ((double)(t.getComputationAmount() + this.acSpin.get(j.getParentTask().getID() - 1) + this.acBlock.get(j.getParentTask().getID() - 1))) / t.getPeriod();
        }
        else
        {
            tempWorst += ((double)(t.getComputationAmount() + this.worstSpin.get(t.getID() - 1) + this.worstBlock.get(t.getID() - 1))) / t.getPeriod();
        }
        
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
        double block = 0;
        if(this.blockedJob.size() > 0)
        {
            for(int i = 0; i < this.blockedJob.size(); i++)
            {
                if(this.blockedJob.get(i) == j)
                {
                    block = j.getCurrentCore().getCurrentTime() - this.blockedTime.get(i);
                    this.blockedJob.remove(i);
                    this.blockedTime.remove(i);
                }
            }
        }
        
        this.acBlock.set(j.getParentTask().getID() - 1, block);
        this.acSpin.set(j.getParentTask().getID() - 1, this.worstSpin.get(j.getParentTask().getID() - 1));

        
        double tempU = 0;

        
        TaskSet ts = this.getParentRegulator().getParentProcessor().getTaskSet();
        for(Task t : ts)
        {
            if(t.getLocalCore() == j.getCurrentCore())
            {
                double tempWU = this.actualBlockUtilization(t, ts, j, block);

                if(tempU < tempWU)
                {
                    tempU = tempWU;
                }
            }
        }
        

        Core c = j.getCurrentCore();
        c.setCurrentSpeed(tempU * c.getParentProcessor().getTaskSet().getMaxProcessingSpeed());
    }

    @Override
    public void jobEveryExecuteAction(Job j)
    {

    }
}