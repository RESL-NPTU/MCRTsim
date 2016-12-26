/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import java.util.Comparator;
import java.util.Stack;
import java.util.Vector;
import schedulingAlgorithm.DynamicPrioritySchedulingAlgorithm;
import schedulingAlgorithm.PriorityDrivenSchedulingAlgorithm;

/**
 *
 * @author ShiuJia
 */
public class Core
{
    private int ID;
    private Processor processor;
    private DynamicVoltageRegulator dynamicVoltageRegulator;
    private Scheduler localScheduler;
    private Controller controller;
    private double currentTime;
    private double checkTime;
    private double totalPowerConsumption;
    private double totalUtilization;
    private JobQueue localReadyQueue;
    private Job workingJob, prevJob;
    private Vector<Result> resultSet;
    private TaskSet taskSet;
    private Priority systemPriorityCeiling;
    private Priority systemPreemptionLevel;
    private Priority systemTempCeiling;
    private Stack<Priority> systemTemp;
    private Stack<Resources> systemTempResources;
    private String status;
    private boolean preemptible;
    private boolean resourceChange;
    
    public Core()
    {
        this.ID = 0;
        this.processor = new Processor();
        this.dynamicVoltageRegulator = new DynamicVoltageRegulator();
        this.localScheduler = new Scheduler();
        this.controller = new Controller();
        this.localReadyQueue = new JobQueue();
        this.workingJob = null;
        this.prevJob = null;
        this.resourceChange = false;
        this.resultSet = new Vector<Result>();
        this.totalPowerConsumption = 0.0;
        this.preemptible = true;
        this.taskSet = new TaskSet();
        this.systemPreemptionLevel = Final.Ohm;
        this.systemPriorityCeiling = Final.Ohm;
        this.systemTempCeiling = Final.Ohm;
        this.systemTemp = new Stack<Priority>();
        this.systemTempResources = new Stack<Resources>();
        this.currentTime = 0;
        this.checkTime = -1;
        this.totalUtilization = 0.0;
        this.status = "start";

    }
    
    public String operationCheckWorkStatusForGlobal()
    {
        while(true)
        {
            if(this.status == "start")
            {
                if(this.processor.getGlobalReadyQueue().peek() != null)
                {
                    if(this.localReadyQueue.peek() == null || (this.preemptible && this.localReadyQueue.peek().compareTo(this.processor.getGlobalReadyQueue().peek())==1))
                    {
                        this.processor.assignJobToCore(this);
                    }
                }
                this.workingJob = this.localReadyQueue.peek();

                if(this.workingJob != null)
                {
                    //System.out.println("TTT" + this.workingJob.getProgressAmount() + "/" + this.currentTime);
                    if(this.controller.lockControl(this.workingJob))
                    {
                        return "E";
                    }
                }
                else
                {
                    return "I";
                }
            }
            else if(this.status == "wait")
            {
                return "W";
            }
        }
    }
    
    public String operationCheckWorkStatusForPartition()
    {
        while(true)
        {
            if(this.status == "start")
            {
                if(this.localScheduler.getSchedulingAlgorithm() instanceof DynamicPrioritySchedulingAlgorithm) //動態優先權分配
                {
                    this.localReadyQueue = this.localScheduler.setPriority(this.localReadyQueue);
                }

                if(this.preemptible || this.workingJob == null)
                {
                    this.workingJob = this.localReadyQueue.peek();
                }

                if(this.workingJob != null)
                {
                    //System.out.println("TTT" + this.workingJob.getProgressAmount() + "/" + this.currentTime);
                    if(this.controller.lockControl(this.workingJob))
                    {
                        return "E";
                    }
                }
                else
                {
                    return "I";
                }
            }
            else if(this.status == "wait")
            {
                return "W";
            }
        }
    }
    
    public void operationRun(int processedTime, String status)
    {
        double targetTime = this.currentTime + processedTime;
        
        switch(status)
        {
            case "E":
                this.processing(targetTime - this.currentTime);
                break;
                
            case "I":
                if(this.prevJob != null || this.currentTime == 0)
                {
                    System.out.println(this.ID + " ," + this.currentTime / 100000 + ":I:" + this.dynamicVoltageRegulator.getCurrentSpeed());
                    if(this.resultSet.size() > 0)
                    {
                        if(!this.resultSet.get(this.resultSet.size() - 1).getStatus().equals(CoreStatus.WRONG))
                        {
                            this.resultSet.get(this.resultSet.size() - 1).setEndTime(this.currentTime/100000);
                            this.resultSet.get(this.resultSet.size() - 1).setTotalPowerConsumption(this.totalPowerConsumption);
                        }
                    }
                    this.resultSet.add(new Result(this.currentTime / 100000, CoreStatus.IDLE, this.dynamicVoltageRegulator.getCurrentSpeed(),this.dynamicVoltageRegulator.getNormalizationOfSpeed(), null ,this));
                    this.prevJob = null;
                }
                this.currentTime += ((int)(Math.ceil((targetTime - this.currentTime) * 100000))) / 100000.0;
                break;
                
            case "W":
                if(this.currentTime == 0 || this.resultSet.get(this.resultSet.size() - 1).getStatus() != CoreStatus.WAIT)
                {
                    System.out.println(this.ID + " ," +this.currentTime / 100000 + ":W:" + this.dynamicVoltageRegulator.getCurrentSpeed() + ":" + this.workingJob.getTask().getID()+ ","+ this.workingJob.getID());
                    if(this.resultSet.size() > 0)
                    {
                        if(!this.resultSet.get(this.resultSet.size() - 1).getStatus().equals(CoreStatus.WRONG))
                        {
                            this.resultSet.get(this.resultSet.size() - 1).setEndTime(this.currentTime/100000);
                            this.resultSet.get(this.resultSet.size() - 1).setTotalPowerConsumption(this.totalPowerConsumption);
                        }
                    }
                    this.resultSet.add(new Result(this.currentTime / 100000, CoreStatus.WAIT, this.dynamicVoltageRegulator.getCurrentSpeed(),this.dynamicVoltageRegulator.getNormalizationOfSpeed(), this.workingJob ,this));
                    this.prevJob = null;
                }
                this.currentTime += ((int)(Math.ceil((targetTime - this.currentTime) * 100000))) / 100000.0;
                break;
                
            default :
                ;
        }
    }
    

    
    private void processing(double processingTime)
    {
        if(this.resourceChange == true)
        {
            this.outPrint();
            this.resourceChange = false;
        }
        
        if(this.prevJob != this.workingJob)
        {
            this.outPrint();
        }

        if((this.workingJob.getTargetAmount() - this.workingJob.getProgressAmount()) >= processingTime * (this.dynamicVoltageRegulator.getCurrentSpeed() / this.workingJob.getTask().getMaxProcessingSpeed()))
        {
            this.currentTime += ((int)(Math.ceil(processingTime)));
            this.totalPowerConsumption += processingTime * this.dynamicVoltageRegulator.getPowerConsumption();
            this.workingJob.processed((processingTime * (this.dynamicVoltageRegulator.getCurrentSpeed() / this.workingJob.getTask().getMaxProcessingSpeed())));
        }
        else
        {
            this.currentTime += ((int)(Math.ceil(((this.workingJob.getTargetAmount() - this.workingJob.getProgressAmount()) / (this.dynamicVoltageRegulator.getCurrentSpeed() / this.workingJob.getTask().getMaxProcessingSpeed())))));
            this.totalPowerConsumption += ((this.workingJob.getTargetAmount() - this.workingJob.getProgressAmount()) / (this.dynamicVoltageRegulator.getCurrentSpeed() / this.workingJob.getTask().getMaxProcessingSpeed())) * this.dynamicVoltageRegulator.getPowerConsumption();
            this.workingJob.processed(this.workingJob.getTargetAmount() - this.workingJob.getProgressAmount());
        }
    }
    
    public void outPrint()
    {
        boolean isPrint = false;
        System.out.printf(this.ID + " ," +this.currentTime / 100000 + ":E:" + this.dynamicVoltageRegulator.getCurrentSpeed() + ":" + this.workingJob.getTask().getID() + ","+ this.workingJob.getID()+ "|");
        for(int i = 0; i < this.workingJob.getLockedResource().size(); i++)
        {
            if(isPrint == true)
            {
                System.out.printf(",");
            }
            System.out.printf(""+this.workingJob.getLockedResource().get(i).getResources().getID());
            isPrint = true;
        }
        if(isPrint == false)
        {
            System.out.println("0");
        }
        else
        {
            System.out.println();
        }
        
        
        if(this.resultSet.size() > 0)
        {
            if(!this.resultSet.get(this.resultSet.size() - 1).getStatus().equals(CoreStatus.WRONG))
            {
                this.resultSet.get(this.resultSet.size() - 1).setEndTime(this.currentTime/100000);
                this.resultSet.get(this.resultSet.size() - 1).setTotalPowerConsumption(this.totalPowerConsumption);
            }
            
            for(int i = this.resultSet.size()-1 ; i>=0 ; i--)
            {
                Result result = this.resultSet.get(i);
                if(result.getStatus().equals(CoreStatus.EXECUTION) )
                {
                    if(result.getJob() == this.prevJob)
                    {
                        result.setEndTime(this.currentTime/100000);
                        result.setTotalPowerConsumption(this.totalPowerConsumption);
                        //break;
                    }
                    break;
                }
            }
        }
        
        this.resultSet.add(new Result(this.currentTime / 100000, CoreStatus.EXECUTION, this.dynamicVoltageRegulator.getCurrentSpeed(), this.dynamicVoltageRegulator.getNormalizationOfSpeed(), this.workingJob,this));
        //System.out.println("="+this.result.get(this.result.size()-1).getJob().getLockedResource().size());
        this.prevJob = this.workingJob;
    }
    
    
    
    public void checkTaskPeriodForPartition()
    {
        if(this.checkTime != this.currentTime)
        {
            for(Task t : this.taskSet)
            {
                if(this.currentTime % t.getPeriod() == t.getEnterTime())
                {
                    System.out.print("@~");
                    
                    JobQueue newJQ = new JobQueue();
                    {//等同於this.localReadyQueue.reSort();
                        newJQ = new JobQueue();
                        while(this.localReadyQueue.peek()!=null)
                        {
                            System.out.print("J"+this.localReadyQueue.peek().getTask().getID()+","
                                    +this.localReadyQueue.peek().getID() +" P:"
                                    +this.localReadyQueue.peek().getCurrentPriority().getValue()+"<");
                            newJQ.add(this.localReadyQueue.poll());
                        }
                        System.out.println();
                        this.localReadyQueue.addAll(newJQ);
                    }
                    
                    this.localReadyQueue.add(t.newJob((int)this.currentTime));
                    //System.out.println("This AA:" + t.getID() +","+ t.getNumJob());
                    
                    {//等同於this.localReadyQueue.reSort();
                        newJQ = new JobQueue();
                        System.out.print("#~");
                        while(this.localReadyQueue.peek()!=null)
                        {
                            System.out.print("J"+this.localReadyQueue.peek().getTask().getID()+","
                                    +this.localReadyQueue.peek().getID()+" P:"
                                    +this.localReadyQueue.peek().getCurrentPriority().getValue()+"<");
                            newJQ.add(this.localReadyQueue.poll());
                        }
                        System.out.println();
                        this.localReadyQueue.addAll(newJQ);
                    }
                }
            }
            this.checkTime = this.currentTime;
            
            
        }
    }
    
    public void operationRunEnd()
    {
        if(this.workingJob != null)
        {
            this.controller.unlockControl(this.workingJob);//解鎖資源

            if(this.workingJob.getProgressAmount() == this.workingJob.getTargetAmount())//判斷ＪＯＢ是否完成
            {
//                while(this.workingJob.getLockedResource().size() > 0 && this.workingJob.getLockedResource().peek() != null)
//                {
//                    System.out.println("Error!!!!!!!!");
//                   
//                }
                this.localReadyQueue.remove(this.workingJob);
            }
        }
    }
    
    public void checkJobMissDeadlineForPartition()
    {
        boolean isMissDeadline = false;
        JobQueue newJQ = new JobQueue();
        Job j;

        while((j = this.localReadyQueue.poll()) != null)
        {
            if(j.getAbsoluteDeadline() <= this.currentTime) //Job j MissDeadline
            {
                isMissDeadline = true;
                if(j.getLocationCore().getResult().size() > 0)
                {
                    if(!j.getLocationCore().getResult().get(j.getLocationCore().getResult().size() - 1).getStatus().equals(CoreStatus.WRONG))
                    {
                        j.getLocationCore().getResult().get(j.getLocationCore().getResult().size() - 1).setEndTime((double)this.currentTime / 100000);
                        j.getLocationCore().getResult().get(j.getLocationCore().getResult().size() - 1).setTotalPowerConsumption(this.totalPowerConsumption);
                    }
                }
                j.getLocationCore().getResult().add(new Result((double)this.currentTime / 100000, CoreStatus.WRONG, 0,0, j,this));
                System.out.println(this.ID + " ," +(double)this.currentTime / 100000 + ":X:" + j.getTask().getID()+ ","+ j.getID()+"!!!");
                this.controller.unlockControlForMissDeadline(j);
                if(j.getLocationCore().getWorkingJob() == j)
                {
                    j.getLocationCore().changeStatus("start");
                    j.getLocationCore().setPreemptible(true);
                }
            }
            else
            {
                newJQ.offer(j);
            }
        }
        this.localReadyQueue = newJQ;
        
        
        for(int x = 0; x < this.systemTempResources.size(); x++)
        {
            for(int y = 0; y < this.systemTempResources.get(x).getWaitQueue().size(); y++)
            {
                j = this.systemTempResources.get(x).getWaitQueue().get(y);
                if(j.getAbsoluteDeadline() <= this.currentTime) //Job j MissDeadline
                {
                    isMissDeadline = true;
                    if(j.getLocationCore().getResult().size() > 0)
                    {
                        if(!j.getLocationCore().getResult().get(j.getLocationCore().getResult().size() - 1).getStatus().equals(CoreStatus.WRONG))
                        {
                            j.getLocationCore().getResult().get(j.getLocationCore().getResult().size() - 1).setEndTime((double)this.currentTime / 100000);
                            j.getLocationCore().getResult().get(j.getLocationCore().getResult().size() - 1).setTotalPowerConsumption(this.totalPowerConsumption);
                        }
                    }
                    j.getLocationCore().getResult().add(new Result((double)this.currentTime / 100000, CoreStatus.WRONG, 0,0, j,j.getLocationCore()));
                    System.out.println(this.ID + " ," +(double)this.currentTime / 100000 + ":X:" + j.getTask().getID() + ","+ j.getID());
                    
                    this.controller.unlockControlForMissDeadline(j);
                    this.systemTempResources.get(x).getWaitQueue().remove(j);
                    if(j.getLocationCore().getWorkingJob() == j)
                    {
                        j.getLocationCore().changeStatus("start");
                        j.getLocationCore().setPreemptible(true);
                    }
                }
            }
        }
        if(isMissDeadline)
        {
            this.localReadyQueue.jobPriorityOfInheritOrRevert();
        }
    }
    
    public void setID(int i)
    {
        this.ID = i;
    }
    
    public int getID()
    {
        return this.ID;
    }
    
    public void setProcessor(Processor p)
    {
        this.processor = p;
    }
    
    public Processor getProcessor()
    {
        return this.processor;
    }
    
    public void setDynamicVoltageRegulator(DynamicVoltageRegulator ss)
    {
        ss.addCore(this);
        this.dynamicVoltageRegulator = ss;
    }
    
    public DynamicVoltageRegulator getDynamicVoltageRegulator()
    {
        return this.dynamicVoltageRegulator;
    }
    
    public void setLocalSchedulingAlgorithm(PriorityDrivenSchedulingAlgorithm algorithm)
    {
        this.localScheduler.setSchedAlgorithm(algorithm);
    }
    
    public Scheduler getLocalScheduler()
    {
        return this.localScheduler;
    }
    
    public void setController(Controller c)
    {
        this.controller = c;
    }
    
    public void setLocalReadyQueue(JobQueue jq)
    {
        this.localReadyQueue = jq;
    }
    
    public JobQueue getLocalReadyQueue()
    {
        return this.localReadyQueue;
    }
    
    public Job getWorkingJob()
    {
        return this.workingJob;
    }
    
    public Job getPrevJob()
    {
        return this.prevJob;
    }
    
    public void setResourceChange()
    {
        this.resourceChange = true;
    }
    
    public Vector<Result> getResult()
    {
        return this.resultSet;
    }
    
    public double getTotalPowerConsumption()
    {
        return this.totalPowerConsumption;
    }
    
    public void setPreemptible(boolean b)
    {
        this.preemptible = b;
    }
    
    public boolean getPreemptible()
    {
        return this.preemptible;
    }
    
    public void addTask(Task t)
    {
        double scale = t.getMaxProcessingSpeed()/this.getDynamicVoltageRegulator().getMaxFrequencyOfSpeed();
        
        this.totalUtilization += t.getUtilization()*scale;
        t.setCore(this);
        this.taskSet.add(t);
        
        this.taskSet.sort
        (
            new Comparator<Task>()
            {
                public int compare(Task t1, Task t2)
                {
                    if(t1.getID() < t2.getID())
                    {
                        return -1;
                    }
                    else if(t1.getID() > t2.getID())
                    {
                        return 1;
                    }
                    return 0;
                }
            }
        );
    }
    
    public void addTaskSet(TaskSet ts)
    {
        this.totalUtilization = 0;
                
        for(Task t : ts)
        {
            t.setCore(this);
        }
        this.taskSet = ts;
        
        this.taskSet.sort
        (
            new Comparator<Task>()
            {
                public int compare(Task t1, Task t2)
                {
                    if(t1.getID() < t2.getID())
                    {
                        return -1;
                    }
                    else if(t1.getID() > t2.getID())
                    {
                        return 1;
                    }
                    return 0;
                }
            }
        );
        
        for(Task t : this.taskSet)
        {
            double scale = t.getMaxProcessingSpeed()/this.getDynamicVoltageRegulator().getMaxFrequencyOfSpeed();
            this.totalUtilization += t.getUtilization()*scale;
        }
    }
    
    public TaskSet getTaskSet()
    {
        return this.taskSet;
    }
    
    public Task getTaskSet(int i)
    {
        return this.taskSet.get(i);
    }
    
    public void setSystemPriorityCeiling(Resources r)
    {
        Priority p = r.getPriorityCeiling();
        if(p.getValue() > this.systemTempCeiling.getValue())
        {
            this.systemTemp.add(p);
            this.systemTempResources.add(r);
            this.systemTempCeiling = p;
        }
        else
        {
            this.systemTemp.add(p);
            this.systemTempResources.add(this.systemTempResources.peek());
        }
    }
    
    public Priority getSystemPriorityCeiling(Job j)
    {
        if(this.systemTempResources.size() > 0 && this.systemTempResources.peek().getWhoLockedLastResource(j) != j)
        {
            return this.systemTempCeiling;
        }
        else
        {
            return Final.Ohm;
        }
    }
    
    public Resources getResourcesOfSystemPriorityCeiling()
    {
        return this.systemTempResources.peek();
    }
    
    public void setSystemPreemptionLevel(Resources r)
    {
        Priority p = r.getPreemptionLevelCeiling();
        if(p.getValue() > this.systemTempCeiling.getValue())
        {
            this.systemTemp.add(p);
            this.systemTempResources.add(r);
            this.systemTempCeiling = p;
        }
        else
        {
            this.systemTemp.add(p);
            this.systemTempResources.add(this.systemTempResources.peek());
        }
    }
    
    public Priority getSystemPreemptionLevel(Job j)
    {
        if(this.systemTempResources.size() > 0 && this.systemTempResources.peek().getWhoLockedLastResource(j) != j)
        {
            return this.systemTempCeiling;
        }
        else
        {
            return Final.Ohm;
        }
    }
    
    public Resources getResourceOfSystemPreemptionLevel()
    {
        return this.systemTempResources.peek();

    }
    
    
    public Priority getSystemTemp()
    {
        return this.systemTempCeiling;

    }
    
    public void restoreSystemTempCeiling()
    {
        //System.out.println("THISRST=" + this.systemTemp.size());
        this.systemTempCeiling = Final.Ohm;
        //System.out.println("this.systemTempCeiling1 = "+this.systemTempCeiling.getValue());
        if(this.systemTemp.size() > 0)
        {
            this.systemTemp.pop();
            for(Priority p : this.systemTemp)
            {
                this.systemTempCeiling = this.systemTempCeiling.getValue() > p.getValue() ? this.systemTempCeiling : p;
            }
        }
        this.systemTempResources.pop();
        //System.out.println("this.systemTempCeiling2 = "+this.systemTempCeiling.getValue());
        
    }
    
    public void changeStatus(String s)
    {
        this.status = s;
    }
    
    public String getCoreStatus()
    {
        return this.status;
    }

    public Double getTotalUtilization()
    {
        return this.totalUtilization;
    }
    
    public Double getLeftUtilization()
    {
        return 1 - this.totalUtilization;
    }
    
    public void setCurrentSpeed(Double s)
    {
        this.dynamicVoltageRegulator.setCurrentSpeed(s);
    }
    
    public double getCurrentSpeed()
    {
        return this.dynamicVoltageRegulator.getCurrentSpeed();
    }
    
    public void addBlockedJob(Job j)
    {
        JobQueue newJQ = new JobQueue();
        newJQ.add(j);
        while(this.localReadyQueue.peek()!=null)
        {
            newJQ.add(this.localReadyQueue.poll());
        }
        this.localReadyQueue = newJQ;
    }
    
    public int leftUtilizationCompareTo(Task t)
    {
        double scale = t.getMaxProcessingSpeed()/this.getDynamicVoltageRegulator().getMaxFrequencyOfSpeed();
        if(this.getLeftUtilization() > (t.getUtilization() * scale))
        {
            return 1;
        }
        else if(this.getLeftUtilization() < (t.getUtilization() * scale))
        {
            return -1;
        } 
        else
        {
            return 0;
        }
    }
    
    public int leftUtilizationCompareTo(Core c)
    {
        if(this.getLeftUtilization() > c.getLeftUtilization())
        {
            return 1;
        }
        else if(this.getLeftUtilization() < c.getLeftUtilization())
        {
            return -1;
        } 
        else
        {
            return 0;
        }
    }
}
