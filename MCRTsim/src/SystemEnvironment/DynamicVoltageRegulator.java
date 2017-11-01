/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemEnvironment;

import WorkLoad.CoreSpeed;
import WorkLoad.Job;
import WorkLoad.SharedResource;
import WorkLoadSet.CoreSet;
import WorkLoadSet.CoreSpeedSet;
import dynamicVoltageAndFrequencyScalingMethod.DynamicVoltageAndFrequencyScalingMethod;
import java.util.Vector;
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
        
        //System.out.println("Regulator:DefinedSpeed");
    }
    
    public void checkJobArrivesProcessor(Job j, Processor p)
    {
        //System.out.println("Regulator:JobArrivesProcessor");
        this.method.jobArrivesProcessorAction(j, p);
    }
    
    public void checkJobArrivesCore(Job j, Core c)
    {
        //System.out.println("Regulator:JobArrivesCore");
        this.method.jobArrivesCoreAction(j, c);
    }
    
    public void checkCoresExecute()
    {
        //System.out.println("Regulator:CoresExecute");
        this.method.coresExecuteAction();
    }
    
    public void checkCoreExecute(Core c)
    {
        //System.out.println("Regulator:CoreExecute");
        this.method.coreExecuteAction(c);
    }
    
    public void checkJobFirstExecute(Job j)
    {
        this.method.jobFirstExecuteAction(j);
    }
    
    public void checkJobEveryExecute(Job j)
    {
        this.method.jobEveryExecuteAction(j);
    }
    
    public void checkJobLock(Job j, SharedResource r)
    {
        //System.out.println("Regulator:JobLock");
        this.method.jobLockAction(j, r);
    }
    
    public void checkJobUnlock(Job j, SharedResource r)
    {
        //System.out.println("Regulator:JobUnlock");
        this.method.jobUnlockAction(j, r);
    }
    
    public void checkJobComplete(Job j)
    {
        //System.out.println("Regulator:JobComplete");
        this.method.jobCompleteAction(j);
    }
    
    public void checkJobDeadline(Job j)
    {
        //System.out.println("Regulator:JobDeadline");
        this.method.jobDeadlineAction(j);
    }
    
    public void checkBlockAction(Job blockedJob, SharedResource blockingRes)
    {
        //System.out.println("Regulator:Block");
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
    
//    public void setCoreType(String s)
//    {
//        if(s.equals("Ideal"))
//        {
//            this.isIdeal = true;
//        }
//    }
//    
//    public void setAlphaValue(double a)
//    {
//        this.alpha = a;
//    }
//    
//    public void setBetaValue(double b)
//    {
//        this.beta = b;
//    }
//    
//    public void setGammaValue(double r)
//    {
//        this.gamma = r;
//    }
    
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
