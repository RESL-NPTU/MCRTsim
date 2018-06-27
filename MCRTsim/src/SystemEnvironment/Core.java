/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemEnvironment;

import ResultSet.SchedulingInfo;
import WorkLoad.Cost;
import WorkLoad.Job;
import WorkLoad.Task;
import WorkLoadSet.CoreSet;
import WorkLoadSet.JobQueue;
import WorkLoadSet.TaskSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import mcrtsim.Definition.CoreStatus;
import mcrtsim.Definition.JobStatus;
import mcrtsim.Definition.PriorityType;
import mcrtsim.Definition.SchedulingType;
import static mcrtsim.Definition.magnificationFactor;
import static mcrtsim.MCRTsim.*;
import mcrtsim.MCRTsimMath;
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
    private Queue<Cost> costQueue;
    private CoreStatus status;
    private Job workingJob;
    private double currentSpeed;
    private SchedulingInfo previousSchedulingInfo;
    private long currentTime;
    private Vector<SchedulingInfo> schedulingInfoSet;
    public boolean isPreemption;
    public boolean isChangeLock;//
    public boolean isChangeSpeed;//
    private long powerConsumption;
    private int contactSwitchCount = 0;
    private int migrationCount = 0;
    
    public Core()
    {
        this.parentCoreSet = null;
        this.parentProcessor = null;
        
        this.localScheduler = new Scheduler();
        this.localScheduler.setParentCore(this);
        this.taskSet = new TaskSet();
        this.localReadyQueue = new JobQueue();
        this.costQueue = new LinkedList<Cost>();
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
        
        for(Task t : this.taskSet)
        {
            println("~Core ID:"+this.ID+", Task ID:"+t.getID()+", Priority:"+t.getPriority().getValue());
        }
    }
    
    private void schedulerCalculatePriorityForDynamic()
    {
        if(this.localScheduler.getSchedAlgorithm().getPriorityType() == PriorityType.Dynamic)
        {
            this.localReadyQueue = this.localScheduler.calculatePriority(this.localReadyQueue);
        }
    }
    
    public void JobToCore(Job j)
    {
        //DVSAction
        this.parentProcessor.getDynamicVoltageRegulator().checkJobArrivesCore(j, this);
        if(this.getParentProcessor().getSchedulingAlgorithm().getSchedulingType() == SchedulingType.Global)
        {
            if(!this.localReadyQueue.isEmpty())
            {
                this.parentProcessor.getGlobalReadyQueue().add(this.localReadyQueue.poll());
            }
            j.setCurrentCore(this);
            this.localReadyQueue.add(j);
            
        }
        else// 如果是單核心或partition才進入以下程式
        {
            if(j.getOriginCore() == null)
            {
                j.setOriginCore(this);
            }
            j.setCurrentCore(this);
            
            this.localReadyQueue.add(j);
            //防止SRP在jobArrivesAction發生繼承問題(工作一進來就要給定優先權並排序)
            this.schedulerCalculatePriorityForDynamic();
        }
    }
    
    public void chooseExecuteJob()
    {
        if(this.status != CoreStatus.STOP )//&& this.status != CoreStatus.CONTEXTSWITCH)
        {
            
            if(this.getParentProcessor().getSchedulingAlgorithm().getSchedulingType() != SchedulingType.Global)
            {
                if(this.localScheduler.getSchedAlgorithm() != null)
                {
                    if(this.localScheduler.getSchedAlgorithm().getPriorityType() == PriorityType.Dynamic)
                    {
                        this.schedulerCalculatePriorityForDynamic();
                    }
                }
            }
            
            
            if(this.isPreemption && this.costQueue.isEmpty())//若為'可搶先'而且沒有Cost需要執行，則開始進行workingJob的切換成新進Job的判斷
            {             
//                if(this.getParentProcessor().getSchedulingAlgorithm().getSchedulingType() == SchedulingType.Global
//                   && !this.parentProcessor.getGlobalReadyQueue().isEmpty() && this.localReadyQueue.isEmpty())//
//                {
//                    this.JobToCore(this.getParentProcessor().getGlobalReadyQueue().poll());
//                }
                
                if(this.workingJob != this.localReadyQueue.peek())//若this.workingJob != this.localReadyQueue.peek()，則this.localReadyQueue.peek()為新進的Job(較高的優先權 or  NULL)
                {
                    if(this.localReadyQueue.peek() != null)//這時候新進的Job不應該是'COMPLETED'或是'MISSDEADLINE'的狀態
                    {    
                        if(this.localReadyQueue.peek().getStatus() == JobStatus.COMPUTING)//判斷新進來的Job是否需要恢復，若需要恢復則要加入Context switch cost;
                        {
                            this.setContextSwitchCost(this, this.localReadyQueue.peek());//setContextSwitchCost函式已考慮 Context switch cost 為0的情況
                        }
                        //以下新進的Job不需要'恢復'，只需考慮是否搶先。
                        else if(this.workingJob != null)//這裡的workingJob為被搶先的Job
                        {
                            if(this.workingJob.getStatus() == JobStatus.NONCOMPUTE//若workingJob是'NONCOMPUTE'的狀態，則新進的Job無搶先workingJob，因此不需要Context switch cost
                               || this.workingJob.getStatus() == JobStatus.COMPLETED //若workingJob是已經'COMPLETED'的狀態，則新進的Job無搶先workingJob，因此不需要Context switch cost
                               || this.workingJob.getStatus() == JobStatus.MISSDEADLINE)//若workingJob是已經'MISSDEADLINE'的狀態，則新進的Job無搶先workingJob，因此不需要Context switch cost
                            {
                                this.setWorkingJob(this.localReadyQueue.peek());
                            }
                            else if(this.workingJob.getStatus() == JobStatus.COMPUTING)//若workingJob是'COMPUTING'的狀態，則新進的Job搶先workingJob，因此需要Context switch cost
                            {
                                this.setContextSwitchCost(this, this.localReadyQueue.peek());//setContextSwitchCost函式已考慮 Context switch cost 為0的情況
                            }
                            
                            //需要加入migration cost之後“恢復”的判斷
                        }
                        else if(this.workingJob == null) //若this.workingJob == null就表示當前的Core是IDLE狀態，因此不需要Context switch cost
                        {
                            this.setWorkingJob(this.localReadyQueue.peek());
                        }
                    }
                    else if(this.localReadyQueue.peek() == null)//新進的Job若為null就表示目前無任何工作，CoreStatus將進入IDLE狀態
                    {
                        this.setWorkingJob(this.localReadyQueue.peek());
                    }
                }
                else if(this.workingJob == this.localReadyQueue.peek())
                {       //若當前的workingJob == this.localReadyQueue.peek()，則需要判斷是否需要加入ContextSwitchCost
                        //但不需要重新setWorkingJob因此不會有同一個Job搶先自己的問題
                    
                    if(!this.schedulingInfoSet.isEmpty() && this.schedulingInfoSet.lastElement().getJob() != this.localReadyQueue.peek()
                       && this.localReadyQueue.peek().getStatus() == JobStatus.COMPUTING)//this.localReadyQueue.peek()是'需要恢復'的Job才加入ContextSwitchCost
                    {
                        this.setContextSwitchCost(this, workingJob);
                    }
                }
            }
        }
    }
    
    public void readyRun()
    {
        boolean isReady = false;
        
        while( (!isReady) && this.costQueue.isEmpty())//若正在執行Cost 則不執行此段程式
        {
            if(this.status != CoreStatus.STOP)
            {
                if(this.workingJob != null)
                {
                    //ControllerAction
                    if(this.parentProcessor.getController().checkFirstExecuteAction(this.workingJob))
                    {
                        if(this.parentProcessor.getController().checkJobLock(this.workingJob))//IF不可合併
                        {
                            this.status = CoreStatus.EXECUTION;
                            
                            if(this.workingJob.getProgressAmount() == 0)
                            {
                                this.parentProcessor.getController().JobFirstExecuteAction(workingJob);                        
                                this.parentProcessor.getDynamicVoltageRegulator().JobFirstExecuteAction(workingJob);
                            }

                            this.parentProcessor.getDynamicVoltageRegulator().checkJobEveryExecute(workingJob);

                            isReady = true;
                        }
                    }
                    
                    if(!isReady)//isReady == false
                    {
                        if(this.isPreemption)
                        {
                            if(this.parentProcessor.getSchedulingAlgorithm().getSchedulingType() == SchedulingType.Global)
                            {
                                if(this.status == CoreStatus.WAIT)
                                {
                                    if(this.parentProcessor.getGlobalReadyQueue().peek() != null &&
                                       this.parentProcessor.getGlobalReadyQueue().peek().getCurrentProiority().isHigher(this.localReadyQueue.peek().getCurrentProiority()))
                                    {
                                        this.JobToCore(this.parentProcessor.getGlobalReadyQueue().poll());
                                        isReady = false;
                                    }
                                    else
                                    {
                                        isReady = true;
                                    }
                                }
                                else 
                                {
                                    //Global架構中，只要有Job被阻擋，而且不是waitting阻擋，就必須要把工作丟回去Global queue中在重新取得新工作。
                                    //若在此形成無窮迴圈，則代表被阻擋的工作被阻擋的當下沒有使用PIP or Suspension 機制
                                    if(!this.localReadyQueue.isEmpty())
                                    {
                                        this.parentProcessor.getGlobalReadyQueue().add(this.localReadyQueue.poll());
                                    }
                                    
                                    
                                    if(this.parentProcessor.getGlobalReadyQueue().peek() != null)
                                    {
                                        this.JobToCore(this.parentProcessor.getGlobalReadyQueue().poll());
                                    }
                                    
                                    isReady = false;
                                }
                            }
                            else // if not Global
                            {
                                if(this.status == CoreStatus.WAIT && this.workingJob == this.localReadyQueue.peek())
                                {
                                    isReady = true;
                                }
                                else
                                {
                                    isReady = false;
                                }
                            }
                        }
                        else
                        {
                            isReady = true;
                        }

                        if(!isReady)
                        {
                            this.chooseExecuteJob();
                            isReady = false;
                            println("NO!!!!!NO!!!!!");
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
        }
        
        this.parentProcessor.getDynamicVoltageRegulator().checkCoreExecute(this);
    }
    
    public void checkCost()
    {   
        if(!this.costQueue.isEmpty())
        {
            this.status = this.costQueue.peek().getStatus();
        }
    } 
    
    public void run(long processedTime)
    {
        double t = this.currentTime + processedTime;

        while(this.currentTime < t)
        {
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
                if(this.costQueue.peek().getStatus() == CoreStatus.CONTEXTSWITCH)
                {
                    this.costQueue.peek().execution(processedTime);
                }
            }
            else if(this.status == CoreStatus.MIGRATION)
            {
                this.record();
                this.currentTime += processedTime;
                this.powerConsumption += this.getParentCoreSet().getPowerConsumption() * processedTime;
                
                if(this.costQueue.peek().getStatus() == CoreStatus.MIGRATION)
                {
                    this.costQueue.peek().execution(processedTime);
                }
            }
        }
        
    }
    
    private void runJob(long processedTime)
    {
        if(((this.workingJob.getTargetAmount() - this.workingJob.getProgressAmount()) * this.workingJob.getMaxProcessingSpeed() ) >= processedTime * this.parentCoreSet.getCurrentSpeed())
        {   
            this.workingJob.execute(MCRTsimMath.mul(processedTime , MCRTsimMath.div(this.getParentCoreSet().getCurrentSpeed() , this.workingJob.getMaxProcessingSpeed())),this.currentTime);
            
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
    
    public void checkJobisCompleted()//檢查Job是否完成工作(201707)
    {
        if(this.workingJob != null)
        {
            if(this.workingJob.getProgressAmount() >= this.workingJob.getTargetAmount() //Job的工作量以完成
            && this.workingJob.getStatus() != JobStatus.COMPLETED && this.workingJob.getStatus() != JobStatus.MISSDEADLINE)//因為this.workingJob並不會因為'COMPLETED'or'MISSDEADLINE'而改變Job物件
            {                                                                                                              //為此需要加入此判斷必免重複執行此函式
                println("@~0workingJob ="+this.workingJob.getStatus());
                this.workingJob.setStatus(JobStatus.COMPLETED, this.currentTime);//將Job的狀態改為Completed
                println("@~1workingJob ="+this.workingJob.getStatus());
                //CortrollerAction
                this.parentProcessor.getController().jobCompletedAction(workingJob);
                //DVSAction
                this.parentProcessor.getDynamicVoltageRegulator().checkJobComplete(workingJob);
                println("0 "+ this.localReadyQueue.contains(this.workingJob));
                this.localReadyQueue.remove(this.workingJob);
                println("1 "+ this.localReadyQueue.contains(this.workingJob));
                if(this.workingJob.getCurrentCore().getLocalReadyQueue().contains(this.workingJob))//確保在migration期間也能確實remove已完成的Job
                {   
                    this.workingJob.getCurrentCore().getLocalReadyQueue().remove(this.workingJob);
                }
            }
        }
    }
    
    public void lastCheckCost()//在本次執行的最後檢查是否有Job已經完成or超出截止時間，有的話把該Job造成的Cost中斷
    {
        if(!this.costQueue.isEmpty())
        {
            int count = this.costQueue.size();
            for(int i = 0 ; i< count;i++)
            {
                Cost cost = this.costQueue.poll();
                
                if(cost.getRequestJob().getStatus() != JobStatus.COMPLETED  
                  && cost.getRequestJob().getStatus() != JobStatus.MISSDEADLINE)//在cost內的RequestJob不應該是'NONCOMPUTE'狀態
                {
                    if(!cost.checkIsCompleted())//若Cost.checkIsCompleted回傳true則，在Cost.checkIsCompleted的函式中就會配置Job;
                    {
                        this.costQueue.add(cost);
                    }
                }
            }
            
            if(this.costQueue.isEmpty())
            {
                this.status = CoreStatus.IDLE;
            }
            else
            {
                this.status = this.costQueue.peek().getStatus();
            }
        }
    }
    
//    
    
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
                println("this.isChangeSpeed = true");
            }
            
            if(this.status == CoreStatus.EXECUTION)
            {
                if(this.isChangeLock)
                {
                    println("Core(" + this.ID + ") : " + (double)this.currentTime/magnificationFactor + ": E : Job(" + this.workingJob.getParentTask().getID() + "," + this.workingJob.getID() + ") : " + this.getParentCoreSet().getCurrentSpeed());

                    if(!this.workingJob.getEnteredCriticalSectionSet().empty())
                    {
                        print("    Use Resource:");
                        for(int i = 0; i < this.workingJob.getEnteredCriticalSectionSet().size(); i++)
                        {
                            print(this.workingJob.getEnteredCriticalSectionSet().get(i).getUseSharedResource().getID() + ". ");
                        }
                        println();
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
                        println("Core(" + this.ID + ") : " + (double)this.currentTime/magnificationFactor + ": E : Job(" + this.workingJob.getParentTask().getID() + "," + this.workingJob.getID() + ") : " + this.getParentCoreSet().getCurrentSpeed());

                        if(!this.workingJob.getEnteredCriticalSectionSet().empty())
                        {
                            print("    Use Resource:");
                            for(int i = 0; i < this.workingJob.getEnteredCriticalSectionSet().size(); i++)
                            {
                                print(this.workingJob.getEnteredCriticalSectionSet().get(i).getUseSharedResource().getID() + ". ");
                            }
                            println();
                        }

                        this.newRecording();
                    }
                    else if(this.previousSchedulingInfo != null && ((this.previousSchedulingInfo.getCoreStatus() != CoreStatus.EXECUTION || (this.previousSchedulingInfo.getCoreStatus() == CoreStatus.EXECUTION && this.previousSchedulingInfo.getJob() != this.workingJob)) || this.isChangeSpeed))
                    {
                        println("Core(" + this.ID + ") : " + (double)this.currentTime/magnificationFactor + ": E : Job(" + this.workingJob.getParentTask().getID() + "," + this.workingJob.getID() + ") : " + this.getParentCoreSet().getCurrentSpeed());

                        if(!this.workingJob.getEnteredCriticalSectionSet().empty())
                        {
                            print("    Use Resource:");
                            for(int i = 0; i < this.workingJob.getEnteredCriticalSectionSet().size(); i++)
                            {
                                print(this.workingJob.getEnteredCriticalSectionSet().get(i).getUseSharedResource().getID() + ". ");
                            }
                            println();
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
                    println("Core(" + this.ID + ") : " + (double)this.currentTime/magnificationFactor + " : I : " + this.getParentCoreSet().getCurrentSpeed());
                    
                    this.newRecording();
                }
                else if(this.previousSchedulingInfo != null && (this.previousSchedulingInfo.getCoreStatus() != CoreStatus.IDLE || this.isChangeSpeed))
                {
                    println("Core(" + this.ID + ") : " + (double)this.currentTime/magnificationFactor + " : I : " + this.getParentCoreSet().getCurrentSpeed());

                    this.previousSchedulingInfo.setEndTime(currentTime);
                    this.previousSchedulingInfo.setTotalPowerConsumption(powerConsumption);
                    
                    this.newRecording();
                }
            }
            else if(this.status == CoreStatus.WAIT)
            {
                if(this.previousSchedulingInfo == null)//this.previousResult==null 代表第一次紀錄點
                {
                    println("Core(" + this.ID + ") : " + (double)this.currentTime/magnificationFactor + " : W : " + this.getParentCoreSet().getCurrentSpeed() );
                    
                    this.newRecording();
                }
                else if(this.previousSchedulingInfo != null && ((this.previousSchedulingInfo.getCoreStatus() != CoreStatus.WAIT || (this.previousSchedulingInfo.getCoreStatus() == CoreStatus.WAIT && this.previousSchedulingInfo.getJob() != this.workingJob))|| this.isChangeSpeed))
                {
                    println("Core(" + this.ID + ") : " + (double)this.currentTime/magnificationFactor + " : W : Job(" + this.workingJob.getParentTask().getID() + "," + this.workingJob.getID() + ") :" + this.getParentCoreSet().getCurrentSpeed());
                    
                    this.previousSchedulingInfo.setEndTime(currentTime);
                    this.previousSchedulingInfo.setTotalPowerConsumption(powerConsumption);
                    this.newRecording();
                }
            }
            else if(this.status == CoreStatus.CONTEXTSWITCH)
            {
                if(this.previousSchedulingInfo == null)//this.previousResult== null 代表整個Core的第一次紀錄點
                {
                    println("Core(" + this.ID + ") : " + (double)this.currentTime/magnificationFactor + " : C : " + this.getParentCoreSet().getCurrentSpeed() );
                    
                    this.newRecording();
                }
                else if(this.previousSchedulingInfo != null && ((this.previousSchedulingInfo.getCoreStatus() != CoreStatus.CONTEXTSWITCH || (this.previousSchedulingInfo.getCoreStatus() == CoreStatus.CONTEXTSWITCH && this.previousSchedulingInfo.getJob() != this.costQueue.peek().getRequestJob())) || this.isChangeSpeed))
                {
                    println("Core(" + this.ID + ") : " + (double)this.currentTime/magnificationFactor + " : C : Job(" + this.costQueue.peek().getRequestJob().getParentTask().getID() + "," + this.costQueue.peek().getRequestJob().getID() + ") :" + this.getParentCoreSet().getCurrentSpeed());
                    
                    this.previousSchedulingInfo.setEndTime(currentTime);
                    this.previousSchedulingInfo.setTotalPowerConsumption(powerConsumption);
                    this.newRecording();
                }
            }
            else if(this.status == CoreStatus.MIGRATION)
            {
                if(this.previousSchedulingInfo == null)//this.previousResult== null 代表整個Core的第一次紀錄點
                {
                    println("Core(" + this.ID + ") : " + (double)this.currentTime/magnificationFactor + " : M : " + this.getParentCoreSet().getCurrentSpeed() );
                    
                    this.newRecording();
                }
                else if(this.previousSchedulingInfo != null && ((this.previousSchedulingInfo.getCoreStatus() != CoreStatus.MIGRATION || (this.previousSchedulingInfo.getCoreStatus() == CoreStatus.MIGRATION && this.previousSchedulingInfo.getJob() != this.costQueue.peek().getRequestJob()))|| this.isChangeSpeed))
                {
                    println("Core(" + this.ID + ") : " + (double)this.currentTime/magnificationFactor + " : M : Job(" + this.costQueue.peek().getRequestJob().getParentTask().getID() + "," + this.costQueue.peek().getRequestJob().getID() + ") :" + this.getParentCoreSet().getCurrentSpeed());
                    
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
        if(this.status == CoreStatus.CONTEXTSWITCH || this.status == CoreStatus.MIGRATION)
        {
            newInfo.setJob(this.costQueue.peek().getRequestJob());
        }
        else
        {
            newInfo.setJob(this.workingJob);
        }
        newInfo.setStartTime(this.currentTime);
        newInfo.setUseSpeed(this.getParentCoreSet().getCurrentSpeed(),this.getParentCoreSet().getNormalizationOfSpeed());
        
        this.schedulingInfoSet.add(newInfo);
        this.previousSchedulingInfo = newInfo;
        this.isChangeSpeed = false;
    }
    
    /*SetValue*/
    public void setWorkingJob(Job j)
    {
        if(j != null && this.workingJob != null && this.workingJob.getStatus() == JobStatus.COMPUTING)//符合這個情況 代表進入了搶先
        {
            println("C"+this.ID+" ,j"+j.getParentTask().getID()+",CurrentProiority() = "+j.getCurrentProiority().getValue());
            println("Preemption");
            println("C"+this.ID+" ,j"+this.workingJob.getParentTask().getID()+",CurrentProiority() = "+this.workingJob.getCurrentProiority().getValue());
            this.parentProcessor.getController().jobPreemptedAction(workingJob, j);
        }
        
        this.workingJob = j;
    }
    
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
    
    public void setContextSwitchCost(Core requestCore, Job requestJob)
    {
        this.contactSwitchCount += 1;
        if(this.parentProcessor.getParentSimulator().getContextSwitchTime() > 0)
        {
            Cost cost = new Cost(this, requestCore, requestJob, CoreStatus.CONTEXTSWITCH);
            this.costQueue.add(cost);
        }
        else
        {
            this.setWorkingJob(requestJob);
        }
    }
    
    public void setMigrationCost(Core requestCore, Job requestJob)//ex:Core1 Migrate to Core2 , 這時一定是由Core1可以 '進行 Migration(Core的CostQueue內無Cost)' 的狀態下 
    {                                                              //才會在 Core1 and Core2 內產生 Migration cost;
        
        Cost contextSwitchCost = null;
        Cost migrationCost = null;
        
        this.contactSwitchCount += 1;
        if(this.parentProcessor.getParentSimulator().getContextSwitchTime() >0)//在Migration之前一定會觸發ContextSwitch
        {
            contextSwitchCost = new Cost(this, requestCore, requestJob, CoreStatus.CONTEXTSWITCH);
            this.costQueue.add(contextSwitchCost);
        }
        
        this.migrationCount +=1;
        if(this.parentProcessor.getParentSimulator().getMigrationTime() > 0)
        {
            migrationCost = new Cost(this, requestCore, requestJob, CoreStatus.MIGRATION);
            this.costQueue.add(migrationCost);
        }
        
        if(contextSwitchCost != null)
        {
            if(migrationCost != null)
            {
                contextSwitchCost.setNextCost(migrationCost);
            }
            else
            {
                migrationCost = new Cost(this, requestCore, requestJob, CoreStatus.MIGRATION);
                migrationCost.setCostTime(0);
                contextSwitchCost.setNextCost(migrationCost);
            }
        }
        else if(contextSwitchCost == null && migrationCost == null && this == requestCore)
        {
            requestCore.getLocalReadyQueue().add(requestJob);
            requestJob.setCurrentCore(requestCore);
        }
    }
    
    public void setbeBlockedTimeOfJobByLocalQueue()
    {
        if(this.status == CoreStatus.EXECUTION || this.status == CoreStatus.WAIT)
        {  
            this.localReadyQueue.setBlockingTime(this.workingJob);
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
        return this.powerConsumption;
    }
    
    public int getContextSwitchCount()
    {
        return this.contactSwitchCount;
    }
    
    public long getMigrationCount()
    {
        return this.migrationCount;
    }
    
    public Queue<Cost> getCostQueue()
    {
        return this.costQueue;
    }
    
}
