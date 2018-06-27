/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemEnvironment;


import WorkLoad.Job;
import WorkLoad.SharedResource;
import dynamicVoltageAndFrequencyScalingMethod.DynamicVoltageAndFrequencyScalingMethod;
import mcrtsim.Definition.DVFSType;

/**
 *
 * @author ShiuJia
 */
public class DynamicVoltageRegulator
{
    private DynamicVoltageAndFrequencyScalingMethod method;
    private Processor parentProcessor;
    private DVFSType type;
    

    
    
    public DynamicVoltageRegulator()
    {
        this.method = null;
        this.parentProcessor = null;
        this.type = null;
    }
    
    public void definedSpeed()
    {
        this.method.definedSpeed(this.parentProcessor);
        
        //println("Regulator:DefinedSpeed");
    }
    
    public void checkJobArrivesProcessor(Job j, Processor p)
    {
        //println("Regulator:JobArrivesProcessor");
        this.method.jobArrivesProcessorAction(j, p);
    }
    
    public void checkJobArrivesCore(Job j, Core c)
    {
        //println("Regulator:JobArrivesCore");
        this.method.jobArrivesCoreAction(j, c);
    }
    
    public void checkCoresExecute()
    {
        //println("Regulator:CoresExecute");
        this.method.coresExecuteAction();
    }
    
    public void checkCoreExecute(Core c)
    {
        //println("Regulator:CoreExecute");
        this.method.coreExecuteAction(c);
    }
    
    public void JobFirstExecuteAction(Job j)
    {
        this.method.jobFirstExecuteAction(j);
    }
    
    public void checkJobEveryExecute(Job j)
    {
        this.method.jobEveryExecuteAction(j);
    }
    
    public void checkJobLock(Job j, SharedResource r)
    {
        //println("Regulator:JobLock");
        this.method.jobLockAction(j, r);
    }
    
    public void checkJobUnlock(Job j, SharedResource r)
    {
        //println("Regulator:JobUnlock");
        this.method.jobUnlockAction(j, r);
    }
    
    public void checkJobComplete(Job j)
    {
        //println("Regulator:JobComplete");
        this.method.jobCompleteAction(j);
    }
    
    public void checkEndSystemTimeAction(long systemTime)
    {
        this.method.checkEndSystemTimeAction(systemTime);
    }
    
    public void checkJobMissDeadline(Job j)
    {
        //println("Regulator:JobDeadline");
        this.method.jobMissDeadlineAction(j);
    }
    
    public void checkBlockAction(Job blockedJob, SharedResource blockingRes)
    {
        //println("Regulator:Block");
        this.method.jobBlockedAction(blockedJob, blockingRes);
    }
    
    /*SetValue*/
    public void setDynamicVoltageAndFrequencyScalingMethod(DynamicVoltageAndFrequencyScalingMethod m)
    {
        this.method = m;
        this.method.setParentRegulator(this);
    }
    
    public void setParentProcessor(Processor p)
    {
        this.parentProcessor = p;
    }
    
    public void setDVFSType(String s)
    {
        switch(s)
        {
            case "full-chip":
            {
                this.type = DVFSType.FullChip;
                break;
            }
            
            case "per-core":
            {
                this.type = DVFSType.PerCore;
                break;
            }
            
            case "VFI":
            {
                this.type = DVFSType.VFI;
                break;
            }
        }
    }

    
    /*GetValue*/
    public DynamicVoltageAndFrequencyScalingMethod getDynamicVoltageAndFrequencyScalingMethod()
    {
        return this.method;
    }
    
    public Processor getParentProcessor()
    {
        return this.parentProcessor;
    }
    
    public DVFSType getDVFSType()
    {
        return this.type;
    }
    
}
