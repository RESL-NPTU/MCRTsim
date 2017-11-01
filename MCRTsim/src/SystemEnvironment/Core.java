/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemEnvironment;

import ResultSet.SchedulingInfo;
import WorkLoad.Job;
import WorkLoad.Task;
import WorkLoadSet.CoreSet;
import WorkLoadSet.JobQueue;
import WorkLoadSet.TaskSet;
import java.util.Vector;
import mcrtsim.Definition.CoreStatus;
import mcrtsim.Definition.JobStatus;
import mcrtsim.Definition.PriorityType;
import mcrtsim.Definition.SchedulingType;
import static mcrtsim.Definition.magnificationFactor;
import schedulingAlgorithm.PriorityDrivenSchedulingAlgorithm;

/**
 *
 * @author ShiuJia
 */
public class Core
{
    private int ID;
    private CoreSet parentCoreSet;
    private Processor parentProcessor;
    private Scheduler localScheduler;
    private TaskSet taskSet;
    private JobQueue localReadyQueue;
    private CoreStatus status;
    private Job workingJob;
    private double currentSpeed;
    private SchedulingInfo previousSchedulingInfo;
    private long currentTime;
    private Vector<SchedulingInfo> schedulingInfoSet;
    public boolean isPreemption;
    public boolean isChangeLock;
    public boolean isChangeSpeed;
    private long powerConsumption;
    private long contactSwitchCost = 0;
    private int contactSwitchCount = 0;
    private long migrationCost = 0;
    private int migrationCount = 0;
    
    public Core()
    {
        this.parentCoreSet = null;
        this.parentProcessor = null;
        
        this.localScheduler = new Scheduler();
        this.localScheduler.setParentCore(this);
        this.taskSet = new TaskSet();
        this.localReadyQueue = new JobQueue();
        this.status = CoreStatus.IDLE;
        this.workingJob = null;
        this.isPreemption = true;
        this.schedulingInfoSet = new Vector<SchedulingInfo>();
        this.currentSpeed = 0;
        this.previousSchedulingInfo = null;
        this.currentTime = 0;
        this.powerConsumption = 0;
        
        this.isChangeLock = false;
        this.isChangeSpeed = false;
    }
    
    /*Operating*/
    public void addTask(Task t)
    {
        this.taskSet.add(t);
        t.setLocalCore(this);
    }
    
    public void schedulerCalculatePriorityForFixed()
    {
        
        this.localScheduler.calculatePriority(this.taskSet);
        System.out.println("4");
        
        for(Task t : this.taskSet)
        {
            System.out.println("5");
            System.out.println("~Core ID:"+this.ID+", Task ID:"+t.getID()+", Priority:"+t.getPriority().getValue());
        }
    }
    
    private void schedulerCalculatePriorityForDynamic()
    {
        if(this.localScheduler.getSchedAlgorithm().getPriorityType() == PriorityType.Dynamic)
        {
            this.localReadyQueue = this.localScheduler.calculatePriority(this.localReadyQueue);
        }
    }
    
    public boolean JobToCore(Job j)//Job成功進入core回傳true; 失敗則回傳false; 
    {
        //DVSAction
        this.parentProcessor.getDynamicVoltageRegulator().checkJobArrivesCore(j, this);
        if(this.getParentProcessor().getSchedulingAlgorithm().getSchedulingType() == SchedulingType.Global)
        {
            this.localReadyQueue.add(j);
            Job firstJob = this.localReadyQueue.poll();
            Job tempJob;
            while((tempJob = this.localReadyQueue.poll()) != null)
            {
                tempJob.setCurrentCore(null);
                tempJob.setPreviousCore(this);
                this.parentProcessor.getGlobalReadyQueue().add(tempJob);
            }

            if(firstJob.getOriginCore() == null)
            {
                firstJob.setOriginCore(this);
            }
            firstJob.setCurrentCore(this);
            this.localReadyQueue.add(firstJob);
            
            System.out.println("~~~~~Job: "+ j.getCurrentProiority().getValue() + " -- firstJob: " +firstJob.getCurrentProiority().getValue() + " -- Core: "+ this.ID);
            
            if(j != firstJob)
            {
                return false;
            }
        }
        else// 如果是單核心或partition才進入以下程式
        {
            if(j.getOriginCore() == null)
            {
                j.setOriginCore(this);
            }
            j.setCurrentCore(this);
            
            this.localReadyQueue.add(j);
            //防止SRP到達繼承問題
            this.schedulerCalculatePriorityForDynamic();
        }
        return true;
    }
    
    public void chooseExecuteJob()
    {
        if(this.status != CoreStatus.STOP && this.status != CoreStatus.CONTEXTSWITCH)//把進入CONTEXTSWITCH的狀態加入this.isPreemption = false;
        {
            if(this.localScheduler != null)
            {
                //if(this.localScheduler.getSchedAlgorithm() instanceof DynamicPrioritySchedulingAlgorithm)
                if(this.localScheduler.getSchedAlgorithm() != null)
                {
                    if(this.localScheduler.getSchedAlgorithm().getPriorityType() == PriorityType.Dynamic)
                    {
                        this.schedulerCalculatePriorityForDynamic();
                    }
                }
            }
        
            if(this.isPreemption)
            {
                this.getParentProcessor().getController().jobPreemptedAction(this);
                this.workingJob = this.localReadyQueue.peek();
            }
        }
    }
    
    public void readyRun()
    {
        boolean isReady = false;         //若是已經是CONTEXTSWITCH or MIGRATION狀態 則不執行此段程式
        while( (!isReady) && this.status != CoreStatus.CONTEXTSWITCH && this.status != CoreStatus.MIGRATION)
        {
            if(this.status != CoreStatus.STOP)
            {
                if(this.workingJob != null)
                {
                    //ControllerAction
                    if(this.getParentProcessor().getController().checkJobLock(this.workingJob))
                    {
                        this.status = CoreStatus.EXECUTION;
                        
                        if(this.workingJob.getProgressAmount() == 0)
                        {
                            this.parentProcessor.getController().checkJobExecute(workingJob);
                            this.parentProcessor.getDynamicVoltageRegulator().checkJobFirstExecute(workingJob);
                        }
                        
                        this.parentProcessor.getDynamicVoltageRegulator().checkJobEveryExecute(workingJob);
                        
                        isReady = true;
                    }
                    else
                    {
                        if(this.status == CoreStatus.WAIT && this.workingJob == this.localReadyQueue.peek())
                        {
                            isReady = true;
                        }
                        else
                        {
//                            if(this.getParentProcessor().getSchedulingAlgorithm().getSchedulingType() == SchedulingType.Global)
//                            {
//                                if(this.parentProcessor.getGlobalReadyQueue().size() > 0)
//                                {
//                                    this.JobToCore(this.parentProcessor.getGlobalReadyQueue().poll());
//                                }
//                            }
                            
                            this.chooseExecuteJob();
                            isReady = false;
                        }
                    }
                }
                else
                {
                    this.status = CoreStatus.IDLE;
                    isReady = true;
                }
            }
            else
            {
                isReady = true;
            }
            
            if(isReady)//進入context switch的條件式
            {
                if(!this.schedulingInfoSet.isEmpty())//isEmpty()是否為空值;空值為ture,反之為false
                {
                    Job previousJob = this.schedulingInfoSet.lastElement().getJob();
                    if(this.workingJob != null && previousJob != null && previousJob.getStatus() == JobStatus.COMPUTING && this.workingJob != previousJob)//判斷是否搶先
                    {
                        this.addContextSwitchCost(this.parentProcessor.getParentSimulator().getContextSwitchTime());//加入使用者輸入
                    }
                    else if(this.workingJob != null && this.workingJob != previousJob && this.workingJob.getStatus() == JobStatus.COMPUTING)//判斷計算中且未完成的JOB是否回復
                    {
                        this.addContextSwitchCost(this.parentProcessor.getParentSimulator().getContextSwitchTime());//加入使用者輸入
                    }
                }
            }
        }
        
        this.parentProcessor.getDynamicVoltageRegulator().checkCoreExecute(this);
    }
    
    public void run(long processedTime)
    {
        double t = this.currentTime + processedTime;

        while(this.currentTime < t)
        {
            //System.out.println("Those" + this.currentTime);
            if(this.status == CoreStatus.EXECUTION)
            {
                this.record();
                this.runJob(processedTime);
            }
            else if(this.status == CoreStatus.IDLE)
            {
                this.record();
                this.currentTime += processedTime;
                this.powerConsumption += this.parentCoreSet.getPowerConsumption() * processedTime;
            }
            else if(this.status == CoreStatus.WAIT)//Core處於等待時間的紀錄
            {  
                this.record();
                this.currentTime += processedTime;
                this.powerConsumption += this.getParentCoreSet().getPowerConsumption() * processedTime;
            }
            else if(this.status == CoreStatus.CONTEXTSWITCH)
            {
                this.record();
                this.currentTime += processedTime;
                this.powerConsumption += this.getParentCoreSet().getPowerConsumption() * processedTime;
                this.contactSwitchCost -= processedTime;
            }
            else if(this.status == CoreStatus.MIGRATION)
            {
                this.record();
                this.currentTime += processedTime;
                this.powerConsumption += this.getParentCoreSet().getPowerConsumption() * processedTime;
                this.contactSwitchCost -= processedTime;
            }
        }
        //System.out.println("  " + this.currentTime/magnificationFactor + "PowerConsumption=" + this.powerConsumption/magnificationFactor + "mHz");
    }
    
    private void runJob(long processedTime)
    {
        if((Double.valueOf((this.workingJob.getTargetAmount() - this.workingJob.getProgressAmount()) * this.workingJob.getMaxProcessingSpeed()) / this.parentCoreSet.getCurrentSpeed()) >= processedTime)
        {    
            this.workingJob.execute(processedTime * this.getParentCoreSet().getCurrentSpeed() / this.workingJob.getMaxProcessingSpeed(),this.currentTime);
            
            this.powerConsumption += this.getParentCoreSet().getPowerConsumption() * processedTime;
            //CortrollerAction
            this.parentProcessor.getController().checkJobUnlock(workingJob);
            this.currentTime += processedTime;
        }
        else
        {
            this.workingJob.finalExecute();
            this.powerConsumption += this.getParentCoreSet().getPowerConsumption() * processedTime;

            //CortrollerAction
            this.parentProcessor.getController().checkJobUnlock(workingJob);
            this.currentTime += processedTime;
        }
        this.workingJob.setStatus(JobStatus.COMPUTING, this.currentTime);
    }
    
    public void afterRun()
    {
        if(this.workingJob != null)
        {
            this.checkJobisCompleted();
        }
        
        if(this.status == CoreStatus.CONTEXTSWITCH && this.contactSwitchCost == 0)
        {
            this.status = CoreStatus.IDLE;
        }
        
        if(this.status == CoreStatus.MIGRATION && this.migrationCost == 0)
        {
            this.status = CoreStatus.IDLE;
        }
    }
    
    public void checkJobisCompleted()//檢查Job是否完成工作(201707)
    {
        if(this.workingJob.getProgressAmount() >= this.workingJob.getTargetAmount())
        {
            //CortrollerAction
            this.parentProcessor.getController().jobCompletedAction(workingJob);
            //DVSAction
            this.parentProcessor.getDynamicVoltageRegulator().checkJobComplete(workingJob);
            this.localReadyQueue.remove(this.workingJob);
            this.workingJob.setStatus(JobStatus.COMPLETED, this.currentTime);//將Job的狀態改為Completed
        }
    }
    
    public void finalRecording()
    {
        this.previousSchedulingInfo.setTotalPowerConsumption(powerConsumption);
    }
    
    public void record()
    {
        if((this.currentTime <= this.parentProcessor.getParentSimulator().getSimulationTime()) || this.isChangeSpeed)
        {
            if(this.isChangeSpeed)
            {
                System.out.println("this.isChangeSpeed = true");
            }
            
            if(this.status == CoreStatus.EXECUTION)
            {
                if(this.isChangeLock)
                {
                    System.out.println("Core(" + this.ID + ") : " + this.currentTime/magnificationFactor + " : Job(" + this.workingJob.getParentTask().getID() + "," + this.workingJob.getID() + ") : " + this.getParentCoreSet().getCurrentSpeed());

                    if(!this.workingJob.getEnteredCriticalSectionSet().empty())
                    {
                        System.out.print("    Use Resource:");
                        for(int i = 0; i < this.workingJob.getEnteredCriticalSectionSet().size(); i++)
                        {
                            System.out.print(this.workingJob.getEnteredCriticalSectionSet().get(i).getUseSharedResource().getID() + ". ");
                        }
                        System.out.println();
                    }

                    if(this.previousSchedulingInfo != null)
                    {
                        this.previousSchedulingInfo.setEndTime(currentTime);
                        this.previousSchedulingInfo.setTotalPowerConsumption(powerConsumption);
                    }
                    
                    this.newRecording();
                    
                    this.isChangeLock = false;
                }
                else
                {
                    if(this.previousSchedulingInfo == null)//this.previousResult==null 代表第一次紀錄點
                    {
                        System.out.println("Core(" + this.ID + ") : " + this.currentTime/magnificationFactor + " : Job(" + this.workingJob.getParentTask().getID() + "," + this.workingJob.getID() + ") : " + this.getParentCoreSet().getCurrentSpeed());

                        if(!this.workingJob.getEnteredCriticalSectionSet().empty())
                        {
                            System.out.print("    Use Resource:");
                            for(int i = 0; i < this.workingJob.getEnteredCriticalSectionSet().size(); i++)
                            {
                                System.out.print(this.workingJob.getEnteredCriticalSectionSet().get(i).getUseSharedResource().getID() + ". ");
                            }
                            System.out.println();
                        }

                        this.newRecording();
                    }
                    else if(this.previousSchedulingInfo != null && ((this.previousSchedulingInfo.getCoreStatus() != CoreStatus.EXECUTION || (this.previousSchedulingInfo.getCoreStatus() == CoreStatus.EXECUTION && this.previousSchedulingInfo.getJob() != this.workingJob)) || this.isChangeSpeed))
                    {
                        System.out.println("Core(" + this.ID + ") : " + this.currentTime/magnificationFactor + " : Job(" + this.workingJob.getParentTask().getID() + "," + this.workingJob.getID() + ") : " + this.getParentCoreSet().getCurrentSpeed());

                        if(!this.workingJob.getEnteredCriticalSectionSet().empty())
                        {
                            System.out.print("    Use Resource:");
                            for(int i = 0; i < this.workingJob.getEnteredCriticalSectionSet().size(); i++)
                            {
                                System.out.print(this.workingJob.getEnteredCriticalSectionSet().get(i).getUseSharedResource().getID() + ". ");
                            }
                            System.out.println();
                        }   
                        
                        this.previousSchedulingInfo.setEndTime(currentTime);
                        this.previousSchedulingInfo.setTotalPowerConsumption(powerConsumption);
                        
                        this.newRecording();
                    }
                }
            }
            else if(this.status == CoreStatus.IDLE)
            {
                if(this.previousSchedulingInfo == null)//this.previousResult==null 代表第一次紀錄點
                {
                    System.out.println("Core(" + this.ID + ") : " + this.currentTime/magnificationFactor + " : I : " + this.getParentCoreSet().getCurrentSpeed());
                    
                    this.newRecording();
                }
                else if(this.previousSchedulingInfo != null && (this.previousSchedulingInfo.getCoreStatus() != CoreStatus.IDLE || this.isChangeSpeed))
                {
                    System.out.println("Core(" + this.ID + ") : " + this.currentTime/magnificationFactor + " : E : " + this.getParentCoreSet().getCurrentSpeed());

                    this.previousSchedulingInfo.setEndTime(currentTime);
                    this.previousSchedulingInfo.setTotalPowerConsumption(powerConsumption);
                    
                    this.newRecording();
                }
            }
            else if(this.status == CoreStatus.WAIT)
            {
                if(this.previousSchedulingInfo == null)//this.previousResult==null 代表第一次紀錄點
                {
                    System.out.println("Core(" + this.ID + ") : " + this.currentTime/magnificationFactor + " : W : " + this.getParentCoreSet().getCurrentSpeed() );
                    
                    this.newRecording();
                }
                else if(this.previousSchedulingInfo != null && ((this.previousSchedulingInfo.getCoreStatus() != CoreStatus.WAIT || (this.previousSchedulingInfo.getCoreStatus() == CoreStatus.WAIT && this.previousSchedulingInfo.getJob() != this.workingJob))|| this.isChangeSpeed))
                {
                    System.out.println("Core(" + this.ID + ") : " + this.currentTime/magnificationFactor + " : W : Job(" + this.workingJob.getParentTask().getID() + "," + this.workingJob.getID() + ") :" + this.getParentCoreSet().getCurrentSpeed());
                    
                    this.previousSchedulingInfo.setEndTime(currentTime);
                    this.previousSchedulingInfo.setTotalPowerConsumption(powerConsumption);
                    this.newRecording();
                }
            }
            else if(this.status == CoreStatus.CONTEXTSWITCH)
            {
                if(this.previousSchedulingInfo == null)//this.previousResult== null 代表整個Core的第一次紀錄點
                {
                    System.out.println("Core(" + this.ID + ") : " + this.currentTime/magnificationFactor + " : C : " + this.getParentCoreSet().getCurrentSpeed() );
                    
                    this.newRecording();
                }
                else if(this.previousSchedulingInfo != null && ((this.previousSchedulingInfo.getCoreStatus() != CoreStatus.CONTEXTSWITCH || (this.previousSchedulingInfo.getCoreStatus() == CoreStatus.CONTEXTSWITCH && this.previousSchedulingInfo.getJob() != this.workingJob))|| this.isChangeSpeed))
                {
                    System.out.println("Core(" + this.ID + ") : " + this.currentTime/magnificationFactor + " : C : Job(" + this.workingJob.getParentTask().getID() + "," + this.workingJob.getID() + ") :" + this.getParentCoreSet().getCurrentSpeed());
                    
                    this.previousSchedulingInfo.setEndTime(currentTime);
                    this.previousSchedulingInfo.setTotalPowerConsumption(powerConsumption);
                    this.newRecording();
                }
            }
            else if(this.status == CoreStatus.MIGRATION)
            {
                if(this.previousSchedulingInfo == null)//this.previousResult== null 代表整個Core的第一次紀錄點
                {
                    System.out.println("Core(" + this.ID + ") : " + this.currentTime/magnificationFactor + " : M : " + this.getParentCoreSet().getCurrentSpeed() );
                    
                    this.newRecording();
                }
                else if(this.previousSchedulingInfo != null && ((this.previousSchedulingInfo.getCoreStatus() != CoreStatus.MIGRATION || (this.previousSchedulingInfo.getCoreStatus() == CoreStatus.MIGRATION && this.previousSchedulingInfo.getJob() != this.workingJob))|| this.isChangeSpeed))
                {
                    System.out.println("Core(" + this.ID + ") : " + this.currentTime/magnificationFactor + " : M : Job(" + this.workingJob.getParentTask().getID() + "," + this.workingJob.getID() + ") :" + this.getParentCoreSet().getCurrentSpeed());
                    
                    this.previousSchedulingInfo.setEndTime(currentTime);
                    this.previousSchedulingInfo.setTotalPowerConsumption(powerConsumption);
                    this.newRecording();
                }
            }
        }
    }
    
    private void newRecording()
    {
        SchedulingInfo newInfo = new SchedulingInfo();
        newInfo.setCore(this);
        newInfo.setCoreStatus(this.status);
        newInfo.setJob(this.workingJob);
        newInfo.setStartTime(this.currentTime);
        newInfo.setUseSpeed(this.getParentCoreSet().getCurrentSpeed(),this.getParentCoreSet().getNormalizationOfSpeed());

        this.schedulingInfoSet.add(newInfo);
        this.previousSchedulingInfo = newInfo;
        this.isChangeSpeed = false;
    }
    
    /*SetValue*/
    public void setID(int id)
    {
        this.ID = id;
    }
    

    public void setParentCoreSet(CoreSet coreSet)
    {
        this.parentCoreSet = coreSet; 
    }
    
    public void setParentProcessor(Processor p)
    {
        this.parentProcessor = p;
    }
    
    public void setLocalSchedAlgorithm(PriorityDrivenSchedulingAlgorithm a)
    {
        this.localScheduler = new Scheduler();
        this.localScheduler.setSchedAlgorithm(a);
    }
    
    public void setTaskSet(TaskSet ts)
    {
        this.taskSet = ts;
    }
    
    public void setLocalReadyQueue(JobQueue jq)
    {
        this.localReadyQueue = jq;
    }
    
    public void setCurrentSpeed(double s)
    {
        this.currentSpeed = s;
    }
    
    public void setCoreStatus(CoreStatus c)
    {
        this.status = c;
    }
    
    public void addContextSwitchCost(long cost)
    {
        this.contactSwitchCount += 1;
        if(cost>0)
        {
            contactSwitchCost += cost;
            this.status = CoreStatus.CONTEXTSWITCH;
        }
    }
    
    public void addMigrationCost(long cost)
    {
        this.migrationCount +=1;
        if(cost>0)
        {
            this.migrationCost += cost;
            this.status = CoreStatus.MIGRATION;
        }
    }
    
    /*GetValue*/
    public int getID()
    {
        return this.ID;
    }
    
    public CoreSet getParentCoreSet()
    {
        return this.parentCoreSet;
    }
    
    public Processor getParentProcessor()
    {
        return this.parentProcessor;
    }
    
    public Scheduler getLocalScheduler()
    {
        return this.localScheduler;
    }
    
    public JobQueue getLocalReadyQueue()
    {
        return this.localReadyQueue;
    }
    
    public Job getWorkingJob()
    {
        return this.workingJob;
    }
    
    public TaskSet getTaskSet()
    {
        return this.taskSet;
    }
    
    public CoreStatus getStatus()
    {
        return this.status;
    }

    public Task getTask(int i) 
    {
        return this.taskSet.get(i);
    }
   
    public Vector<SchedulingInfo> getSchedulingInfoSet()
    {
        return this.schedulingInfoSet;
    }
    
    public double getCurrentSpeed()
    {
        return this.currentSpeed;
    }
    
    public long getCurrentTime()
    {
        return this.currentTime;
    }
    
    public long getPowerConsumption()
    {
        //return Double.parseDouble(df.format(this.powerConsumption));
        return this.powerConsumption;
    }
    
    public long getContextSwitchCost()
    {
        return this.contactSwitchCost;
    }
    
    public long getMigrationCost()
    {
        return this.migrationCost;
    }
    
    public int getContextSwitchCount()
    {
        return this.contactSwitchCount;
    }
    
    public long getMigrationCount()
    {
        return this.migrationCount;
    }
    
}
