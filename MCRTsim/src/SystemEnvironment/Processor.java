/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemEnvironment;

import PartitionAlgorithm.PartitionAlgorithm;
import ResultSet.MissDeadlineInfo;
import WorkLoad.CoreSpeed;
import WorkLoad.CriticalSection;
import WorkLoad.Job;
import WorkLoad.SharedResource;
import WorkLoad.Task;
import WorkLoadSet.CoreSet;
import WorkLoadSet.JobQueue;
import WorkLoadSet.SharedResourceSet;
import WorkLoadSet.TaskSet;
import concurrencyControlProtocol.ConcurrencyControlProtocol;
import dynamicVoltageAndFrequencyScalingMethod.DynamicVoltageAndFrequencyScalingMethod;
import java.util.Vector;
import mcrtsim.Definition.CoreStatus;
import mcrtsim.Definition.JobStatus;
import mcrtsim.Definition.PriorityType;
import mcrtsim.Definition.SchedulingType;
import schedulingAlgorithm.HybridSchedulingAlgorithm;
import schedulingAlgorithm.PartitionedSchedulingAlgorithm;
import schedulingAlgorithm.PriorityDrivenSchedulingAlgorithm;
import schedulingAlgorithm.SingleCoreSchedulingAlgorithm;

/**
 *
 * @author ShiuJia
 */
public class Processor
{
    private String modelName;
    private Simulator parentSimlator;
    
    //--VVV
    private Vector<CoreSet> coreSets;//分群後之群組
    //---^^^
    private Vector<Core> allCore;
    
    private PriorityDrivenSchedulingAlgorithm schedulingAlgorithm = null;
    private Scheduler globalScheduler;
    private Controller controller;
    private DynamicVoltageRegulator regulator;
    private PartitionDistributor distributor;
    private JobQueue globalReadyQueue;
    private TaskSet taskSet;
    private SharedResourceSet sharedResourceSet;
    private long elapsedTime;
    
    public Processor()
    {
        this.coreSets = new Vector<>();
        this.allCore = new Vector<Core>();
        this.globalScheduler = new Scheduler();
        this.globalScheduler.setParentProcessor(this);
        this.controller = new Controller();
        this.controller.setParentProcessor(this);
        this.regulator = new DynamicVoltageRegulator();
        this.regulator.setParentProcessor(this);
        this.distributor = new PartitionDistributor();
        this.distributor.setParentProcessor(this);
        this.globalReadyQueue = new JobQueue();
        this.elapsedTime = 0;
    }
    
    /*Operating*/
    public void addCore(Core c)
    {
        this.allCore.add(c);
        c.setID(this.allCore.size());
    }
    
    public void addCoreSet(CoreSet coreSet)
    {
        this.coreSets.add(coreSet);
        coreSet.setGroupID(this.coreSets.size());
    }
    
    public void loadTaskSet(TaskSet ts)
    {
        this.taskSet = ts;
    }
    
    public void loadResourceSet(SharedResourceSet rs)
    {
        this.sharedResourceSet = rs;
    }
    
    public void partitionTasks()
    {
        
        if(schedulingAlgorithm.getSchedulingType() == SchedulingType.Partition || schedulingAlgorithm.getSchedulingType() == SchedulingType.SingleCore)
        {
            System.out.println("PartitionTasks = " + this.distributor.getSPartitionAlgorithm().getName());
            
            this.distributor.split();
            
            for(Task t : this.taskSet)
            {
                System.out.println("Task(" + t.getID() + ") to Core(" + t.getLocalCore().getID() + ")");
            }
        }
    }
    
    public void execute(long t)
    {
        this.checkArrivalSystemJob();
        
        if(this.schedulingAlgorithm.getSchedulingType() == SchedulingType.Global)
        {
            this.schedulerCalculatePriorityForDynamic();//使用到globalScheduler才有用到
        
            Core c = null;
            while((c = this.getLowerPriorityCore()) != null)
            {
                //若globalReadyQueue無Job則跳出迴圈，若有Job則判斷c.JobToCore是否成功(true)，失敗(false)則跳出迴圈
                if( this.globalReadyQueue.peek() == null || !c.JobToCore(this.globalReadyQueue.poll()))
                {
                    break;
                }
            }
            
        }
        else if(this.schedulingAlgorithm.getSchedulingType() == SchedulingType.Hybrid)
        {
            //Hybrid GlobalQueue -> LocalQueue
        }
        
        for(Core c : this.allCore)
        {   
            c.chooseExecuteJob();
        }
        
        //VVVVV 確保在可搶先的情況下，Core所選擇的是優先權最高的Job
        boolean quit = false;
        while(!quit)
        {
            quit = true;
            for(Core c : this.allCore)
            {
                if(c.isPreemption && c.getWorkingJob() != c.getLocalReadyQueue().peek() 
                        && c.getStatus() != CoreStatus.CONTEXTSWITCH && c.getStatus() != CoreStatus.MIGRATION)
                {
                    quit = false;
                    c.chooseExecuteJob();
                }
            }
        }
        
        for(Core c : this.allCore)
        {
            c.readyRun();
        }
        
        //DVSAction
        this.regulator.checkCoresExecute();
        
        //set CoreSet Speed
        for(CoreSet coreSet : this.coreSets )
        {
            coreSet.setCurrentSpeed();
        }
        
        for(Core c : this.allCore)
        {
            c.run(t);
        }
        
        this.elapsedTime += t;
        
        for(Core c : this.allCore)
        {
            c.afterRun();
        }
        this.checkMissDeadlineJob();
    }
    
    private void checkArrivalSystemJob()
    {
        for(Task t : this.taskSet)
        {
            if(this.elapsedTime >= t.getEnterTime())
            {
                //需注意到達時間及週期算法
                if(((this.elapsedTime - t.getEnterTime()) % t.getPeriod()) == 0 )
                {
                    Job j = t.produceJob(this.elapsedTime);
                    j.setLocalProcessor(this);

                    switch(schedulingAlgorithm.getSchedulingType())
                    {
                        case SingleCore:
                            t.getLocalCore().JobToCore(j);
                        break;

                        case Partition:
                            t.getLocalCore().JobToCore(j);
                        break;

                        case Global:
                            this.globalReadyQueue.add(j);
                            this.schedulerCalculatePriorityForDynamic();
                        break;

                        case Hybrid://尚未驗證2017/10/27
                            //待加入
                        break;

                        default:      
                    }
                    
//                    //防止SRP到達繼承問題
//                    this.schedulerCalculatePriorityForDynamic();//使用到globalScheduler才有用到

                    //CortrollerAction
                    this.controller.checkJobArrives(j);
                    
                    //DVSAction
                    this.regulator.checkJobArrivesProcessor(j, this);
                }
            }
        }
    }
    
    private void checkMissDeadlineJob()//
    {
        Job tempJob;
        JobQueue tempQueue = new JobQueue();
        /*Processor GlobalQueue*/
        while((tempJob = this.globalReadyQueue.poll()) != null)
        {
            if(tempJob.getAbsoluteDeadline() <= this.elapsedTime)
            {
                //CortrollerAction
                this.controller.checkJobDeadline(tempJob);
                
                //DVSAction
                this.regulator.checkJobDeadline(tempJob);
                
                System.out.println("XXXXXXXXXXXXXXXX " + this.elapsedTime + " : MissDeadline : (" + tempJob.getParentTask().getID() + "," + tempJob.getID() + ")= " + tempJob.getReleaseTime());
//                while(tempJob.getEnteredCriticalSectionSet().size() != 0)
//                {
//                    CriticalSection cs = tempJob.getEnteredCriticalSectionSet().peek();
//                    tempJob.unLockSharedResource(cs.getUseSharedResource());
//                }
                
                tempJob.setStatus(JobStatus.MISSDEADLINE, this.elapsedTime);//JOB的狀態改為MissDeadline
                MissDeadlineInfo md = new MissDeadlineInfo((int)this.elapsedTime, tempJob);
                this.parentSimlator.addMissDeadlineInfo(md);
            }
            else
            {
                tempQueue.add(tempJob);
            }
        }
        this.globalReadyQueue = tempQueue;
        
        /*Core LocalQueue*/
        for(Core c : this.allCore)
        {
            tempQueue = new JobQueue();
            while((tempJob = c.getLocalReadyQueue().poll()) != null)
            {
                if(tempJob.getAbsoluteDeadline() <= this.elapsedTime)
                {
                    //CortrollerAction
                    this.controller.checkJobDeadline(tempJob);

                    //DVSAction
                    this.regulator.checkJobDeadline(tempJob);

                    System.out.println("XXXXXXXXXXXXXXXX " + this.elapsedTime + " : MissDeadline : (" + tempJob.getParentTask().getID() + "," + tempJob.getID() + ")= " + tempJob.getReleaseTime());
                    while(tempJob.getEnteredCriticalSectionSet().size() != 0)
                    {
                        CriticalSection cs = tempJob.getEnteredCriticalSectionSet().peek();
                        tempJob.unLockSharedResource(cs.getUseSharedResource());
                    }
                
                    tempJob.setStatus(JobStatus.MISSDEADLINE, this.elapsedTime);//JOB的狀態改為MissDeadline
                
                    MissDeadlineInfo md = new MissDeadlineInfo((int)this.elapsedTime, tempJob);
                    this.parentSimlator.addMissDeadlineInfo(md);
                }
                else
                {
                    tempQueue.add(tempJob);
                }
            }
            c.setLocalReadyQueue(tempQueue);
        }
        
        /*SharedResource WaitQueue*/
        for(SharedResource r : this.sharedResourceSet)
        {
            for(int i = 0; i < r.getWaitQueue().size(); i++)
            {
                tempJob = r.getWaitQueue(i);
                if(tempJob.getAbsoluteDeadline() <= this.parentSimlator.getElapsedTime())
                {
                    //CortrollerAction
                    this.controller.checkJobDeadline(tempJob);

                    //DVSAction
                    this.regulator.checkJobDeadline(tempJob);
                    
                    System.out.println("XXXXXXXXXXXXXXXX " + this.elapsedTime + " : MissDeadline : (" + tempJob.getParentTask().getID() + "," + tempJob.getID() + ")= " + tempJob.getReleaseTime());
                    while(tempJob.getEnteredCriticalSectionSet().size() != 0)
                    {
                        CriticalSection cs = tempJob.getEnteredCriticalSectionSet().peek();
                        tempJob.unLockSharedResource(cs.getUseSharedResource());
                    }
                    
                    tempJob.setStatus(JobStatus.MISSDEADLINE, this.elapsedTime);//JOB的狀態改為-MissDeadline
                
                    
                    MissDeadlineInfo md = new MissDeadlineInfo((int)this.elapsedTime, tempJob);
                    this.parentSimlator.addMissDeadlineInfo(md);
                    r.getWaitQueue().remove(i);
                    i--;
                }
            }
        }
    }
    
    public void schedulerCalculatePriorityForFixed()
    {
        switch(schedulingAlgorithm.getSchedulingType())
        {
            case SingleCore:
                if(this.allCore.firstElement().getLocalScheduler().getSchedAlgorithm().getPriorityType() == PriorityType.Fixed)
                {
                    this.allCore.firstElement().schedulerCalculatePriorityForFixed();
                }
            break;
                
            case Partition:
                for(Core c : this.allCore)
                {
                    if(c.getLocalScheduler().getSchedAlgorithm() != null)
                    {
                        if(c.getLocalScheduler().getSchedAlgorithm().getPriorityType() == PriorityType.Fixed)
                        {
                            c.schedulerCalculatePriorityForFixed();
                        }
                    }
                }
            break;
                
            case Global:
                if(schedulingAlgorithm.getPriorityType() == PriorityType.Fixed)
                {
                    this.globalScheduler.calculatePriority(this.taskSet);
                }
            break;
                
            case Hybrid://尚未驗證2017/10/27
                //待加入
            break;
                
            default:      
        }
    }
    
    private void schedulerCalculatePriorityForDynamic()
    {
        if(this.globalScheduler.getSchedAlgorithm().getPriorityType() == PriorityType.Dynamic)
        {
            this.globalReadyQueue = this.globalScheduler.calculatePriority(this.globalReadyQueue);
        }
    }
    
    public void JobArrives(Job j)
    {
        this.globalReadyQueue.add(j);
    }
    
    public void showInfo()
    {
        for(CoreSet cSet : this.coreSets)
        {
            System.out.println("CoreSet(" + cSet.getGroupID() + "):");
            
            for(Core c : cSet)
            {
                System.out.println("    Core :" + c.getID());
            }
            
            System.out.println("        Alpha :" + cSet.getAlphaValue());
            System.out.println("        Beta :" + cSet.getBetaValue());
            System.out.println("        Gamma :" + cSet.getGammaValue());
            
            for(CoreSpeed cSpeed : cSet.getCoreSpeedSet())
            {
                System.out.println("        CoreSpeed :" + cSpeed.getSpeed());
                System.out.println("        PowerConsumption :" + cSpeed.getPowerConsumption()); 
            }
        }
        System.out.println();
    }
    
    /*SetValue*/
    public void setModelName(String s)
    {
        this.modelName = s;
    }
    
    public void setParentSimulator(Simulator s)
    {
        this.parentSimlator = s;
    }
    
    public void setSchedAlgorithm(PriorityDrivenSchedulingAlgorithm a)
    {
        schedulingAlgorithm = a;
       
        switch(schedulingAlgorithm.getSchedulingType())
        {
            case SingleCore:
                SingleCoreSchedulingAlgorithm sa = (SingleCoreSchedulingAlgorithm) a;
                this.allCore.firstElement().setLocalSchedAlgorithm(a);
                System.out.println("Core(" + this.allCore.firstElement().getID() + ") Scheduler=" + this.allCore.firstElement().getLocalScheduler().getSchedAlgorithm().getName());
            break;
                
            case Partition:
                PartitionedSchedulingAlgorithm psa = (PartitionedSchedulingAlgorithm) a;
                psa.setCoresLocalSchedulingAlgorithm(this.allCore);

                for(Core c : this.allCore)
                {
                    System.out.println("Core(" + c.getID() + ") Scheduler=" + c.getLocalScheduler().getSchedAlgorithm().getName());
                }
            break;
                
            case Global:
                this.globalScheduler.setSchedAlgorithm(a);
                System.out.println("Processor SchedAlgorithm=" + this.globalScheduler.getSchedAlgorithm().getName());
            break;
                
            case Hybrid://尚未驗證2017/10/27
                HybridSchedulingAlgorithm hsa = (HybridSchedulingAlgorithm) a;
                hsa.setProcessorGlobalSchedulingAlgorithm(this);
                hsa.setCoresLocalSchedulingAlgorithm(this.allCore);
            break;
                
            default:      
        }
    }
    
    public void setCCProtocol(ConcurrencyControlProtocol p)
    {
        this.controller.setConcurrencyControlProtocol(p);
        
        System.out.println("Processor ControlProtocol=" + this.controller.getConcurrencyControlProtocol().getName());
    }
    
    public void setDVFSMethod(DynamicVoltageAndFrequencyScalingMethod m)
    {
        this.regulator.setDynamicVoltageAndFrequencyScalingMethod(m);
        
        System.out.println("Processor DVFSMethod=" + this.regulator.getDynamicVoltageAndFrequencyScalingMethod().getName());
    }
    
    public void setPartitionAlgorithm(PartitionAlgorithm a)
    {
        this.distributor.setPartitionAlgorithm(a);
    }
    
    /*GetValue*/
    public String getModelName()
    {
        return this.modelName;
    }
    
    public Simulator getParentSimulator()
    {
        return this.parentSimlator;
    }
    
    public DynamicVoltageRegulator getDynamicVoltageRegulator()
    {
        return this.regulator;
    }
    
    public Scheduler getGlobalScheduler()
    {
        return this.globalScheduler;
    }
    
    public Controller getController()
    {
        return this.controller;
    }
    
    public PartitionDistributor getPartitionDistributor()
    {
        return this.distributor;
    }
    
    public Core getCore(int x)
    {
        return this.allCore.get(x);
    }
    
    public Vector<Core> getAllCore()
    {
        return this.allCore;
    }
    
    public CoreSet getCoresSet(int i)
    {
        return this.coreSets.get(i);
    }
    
    public Vector<CoreSet> getCoresSets()
    {
        return this.coreSets;
    }
    
    public JobQueue getGlobalReadyQueue()
    {
        return this.globalReadyQueue;
    }
    
    public TaskSet getTaskSet()
    {
        return this.taskSet;
    }
    
    public SharedResourceSet getSharedResourceSet()
    {
        return this.sharedResourceSet;
    }
    
    public double getTotalPowerConsumption()
    {
        double TPC = 0;
        for(Core c : this.allCore)
        {
            TPC += c.getPowerConsumption();
        }
        return TPC;
    }
    
    public PriorityDrivenSchedulingAlgorithm getSchedulingAlgorithm()
    {
        return this.schedulingAlgorithm;
    }
    
    public Core getLowerPriorityCore()
    {
        Core core = null;
        
        for(Core c : this.allCore)
        {
            Job workingJob = c.getLocalReadyQueue().peek();
            
            if(workingJob == null)
            {
                core = c;
                break;
            }
            else if((workingJob != null && c.isPreemption) && (core == null || !workingJob.getCurrentProiority().isHigher(core.getLocalReadyQueue().peek().getCurrentProiority())))
            {  //若c內有Job而且可被搶先，則近一步判斷後面的條件 core == null || workingJob.getCurrentProiority().isHigher(core.getLocalReadyQueue().peek().getCurrentProiority())
                core = c;
            }
        }
        return core;
    }   
}
