package concurrencyControlProtocol.implementation;


import SystemEnvironment.Processor;
import WorkLoad.CriticalSection;
import WorkLoad.Job;
import WorkLoad.Nest;
import WorkLoad.Priority;
import WorkLoad.SharedResource;
import WorkLoad.Task;
import concurrencyControlProtocol.ConcurrencyControlProtocol;
import concurrencyControlProtocol.implementation.G_FMLP.FMLP_Nest;
import java.util.Vector;
import mcrtsim.Definition;
import static mcrtsim.Definition.Ohm;
import static mcrtsim.MCRTsim.*;


public class G_FMLP extends ConcurrencyControlProtocol
{
    class Resource
    {
        boolean isShort = true;
        SharedResource R = null;
        ResourceGroup parentRG = null;
        boolean isgroup = false;
        public Resource(SharedResource r)
        {
            this.R = r;
        }
    }
    
    private class ResourceGroup extends Vector<Resource>
    {  
        Vector<Job> FIFOJobQueue = new Vector<Job>();
        Vector<Job> suspensionQueue = new Vector<Job>();
        
        public void addResource(Resource r)
        {
            this.add(r);
            r.parentRG = this;
        }
        
        public void addJobForFIFOJobQueue(Job j)
        {
            if(!this.FIFOJobQueue.contains(j))
            {
                this.FIFOJobQueue.add(j);
            }
        }
        
        public void removeJobForFIFOJobQueue(Job j)
        {
            this.FIFOJobQueue.remove(j);
        }

        public Job getFirstJobForFIFOJobQueue()
        {
            if(this.FIFOJobQueue.isEmpty())
            {
                return null;
            }
            return this.FIFOJobQueue.get(0);
        }
        
        public void addJobForSuspensionQueue(Job j)
        {   
            j.setSuspended(true);
            this.suspensionQueue.add(j);
        }
        
        public void releaseJobForSuspensionQueue(Job job)
        { 
            if(job != null)
            {
                job.setSuspended(false);
                this.suspensionQueue.remove(job);
                Priority p = Ohm;
                
                for(Job j : this.suspensionQueue)
                {
                    if(j.isInherit)
                    {
                        if(j.getInheritPriority().isHigher(p))
                        {
                            p = j.getInheritPriority();
                        }
                    }
                    else
                    {
                        if(j.getOriginalPriority().isHigher(p))
                        {
                            p = j.getOriginalPriority();
                        }
                    }
                }
                
                if(p.isHigher(Ohm) && p.isHigher(job.getCurrentProiority()))
                {
                    job.inheritPriority(p);
                }
            }
        } 

        public Job getJobForSuspensionQueue(int i)
        {
            return this.suspensionQueue.get(i);
        }
        
        public boolean isShort()
        {  
            return this.get(0).isShort;
        }
        
    }
    
    class FMLP_Nest
    {
        Nest nest = null;
        boolean isgroup = false;
        
        public FMLP_Nest(Nest n)
        {
            this.nest = n;
        }
    }
    
    public ResourceGroup searchResourceGroup(SharedResource s)
    {
        if(s.equals(allR.get(s.getID()-1).R))
        {
           return  allR.get(s.getID()-1).parentRG;
        }
        else 
        {
           return null;
        }
    }
    
    public SharedResource searchResourceForCriticalSectionArray(Job j,int i)
    {
        return j.getEnteredCriticalSectionArray().get(i).getUseSharedResource();
    }
    
    public  void setResourceStatus()
    {   
        proportion=0.5;
        
        for(int i=0;i<allR.size();i++)
        {
            if(i<allR.size()*proportion)
            {
                allR.get(i).isShort=true;
                println("R"+allR.get(i).R.getID()+" = "+allR.get(i).isShort);
            }
            else
            {
                allR.get(i).isShort=false;
                println("R"+allR.get(i).R.getID()+" = "+allR.get(i).isShort);
            }
        }
    }
    public void modifyResource(Processor p)
    {
        boolean change=true;
        int run=0;
        
        while(change)
        {    
            change=false;
            println("run:"+ ++run);
            for(int i=0;i<p.getTaskSet().size();i++)//task
            {
                for(int j=0;j<p.getTaskSet().getTask(i).getNestSet().size();j++)//task allnests
                {
                    int temp=0;
                    print("Task "+p.getTaskSet().getTask(i).getID()+" : ");
                    for(int k=0;k<p.getTaskSet().getTask(i).getNestSet().get(j).size();k++)//task allnests nests
                    {
                        print(""+p.getTaskSet().getTask(i).getNestSet().get(j).getCriticalSection(k).getResourceID());
                        if(p.getTaskSet().getTask(i).getNestSet().get(j).getCriticalSection(k).getOutsideCriticalSection()!=null)
                        {   
                            SharedResource rOut=p.getTaskSet().getTask(i).getNestSet().get(j).getCriticalSection(k).getOutsideCriticalSection().getUseSharedResource();
                            SharedResource r=p.getTaskSet().getTask(i).getNestSet().get(j).getCriticalSection(k).getUseSharedResource();
                            if(allR.get(rOut.getID()-1).isShort&& !allR.get(r.getID()-1).isShort)
                            {
                                allR.get(rOut.getID()-1).isShort=false;
                                change=true;
                            }
                        }
                    }
                }
            }
        }
        
        println("===============");
        
        for(int i=0;i<p.getTaskSet().size();i++)//設定Task狀態
        {
            int temp=0;
            SharedResource r = p.getTaskSet().getTask(i).getCriticalSectionSet().get(0).getUseSharedResource();
            
            for(int j=0;j<p.getTaskSet().getTask(i).getCriticalSectionSet().size();j++)
            {
                SharedResource rj = p.getTaskSet().getTask(i).getCriticalSectionSet().get(j).getUseSharedResource();
                if(allR.get(r.getID()-1).isShort==allR.get(rj.getID()-1).isShort)
                {
                    temp++;
                }
            }
            
            if(temp==p.getTaskSet().getTask(i).getCriticalSectionSet().size())
            {
                if(allR.get(r.getID()-1).isShort)
                {
                    println("Task"+p.getTaskSet().getTask(i).getID()+" isShort");
                    status.add(allIsShort);
                }
                else
                {
                    println("Task"+p.getTaskSet().getTask(i).getID()+" allIsLong");
                    status.add(allIsLong);
                }
            }
            else
            {
                println("Task"+p.getTaskSet().getTask(i).getID()+" complex");
                status.add(complex);
            }
        }
        
        println("===============");
        
        for(int i=0;i<p.getTaskSet().size();i++)
        {
            println("Task:"+p.getTaskSet().getTask(i).getID()+" status:"+status.get(i).toString());
        }
    }
    
    public void setResourceGroup(Processor p)
    {
        int rNum=0;
        int runtemp=0;
        println("size:"+allR.size());
        
        while(rNum<allR.size())
        {
            for(int i=0;i<allR.size();i++)
            {
                if(allR.get(i).parentRG!=null)
                {
                    print(" R"+allR.get(i).R.getID());
                }
            }
            
            println("");
            
            for(int i=0;i<p.getTaskSet().size();i++)
            {
                if(runtemp==0)
                {
                    if(status.get(i)==allIsShort||status.get(i)==allIsLong)
                    {
                        for(int j=0;j<p.getTaskSet().getTask(i).getNestSet().size();j++)
                        {
                            ResourceGroup rg = new ResourceGroup();
                            if(p.getTaskSet().getTask(i).getNestSet().get(j).size()>1 && !allNests.get(i).get(j).isgroup)
                            {
                                for(int k=0;k<p.getTaskSet().getTask(i).getNestSet().get(j).size();k++)
                                {   
                                    SharedResource r =p.getTaskSet().getTask(i).getNestSet().get(j).getCriticalSection(k).getUseSharedResource();
                                    if(allR.get(r.getID()-1).parentRG==null && !allR.get(r.getID()-1).isgroup)
                                    {
                                        rg.add(allR.get(r.getID()-1));
                                        allR.get(r.getID()-1).parentRG=rg;
                                        ++rNum;
                                        print("Rnum:"+rNum +"   Task"+p.getTaskSet().getTask(i).getID()+"  R"+allR.get(r.getID()-1).R.getID());
                                        println("("+status.get(i)+")");
                                        mark(allR.get(r.getID()-1).R);
                                    }
                                }
                                if(rg.size()>0)
                                {
                                    allRG.add(rg);
                                }
                            }
                        }
                    }
                }
                else if(runtemp==1)
                {
                    for(int j=0;j<p.getTaskSet().getTask(i).getNestSet().size();j++)
                    {
                        ResourceGroup rg = new ResourceGroup();
                        for(int k=0;k<p.getTaskSet().getTask(i).getNestSet().get(j).size();k++)
                        {
                            SharedResource r =p.getTaskSet().getTask(i).getNestSet().get(j).getCriticalSection(k).getUseSharedResource();
                            if(allR.get(r.getID()-1).parentRG==null)
                            {
                                rg.add(allR.get(r.getID()-1));
                                allR.get(r.getID()-1).parentRG=rg;
                                ++rNum;
                                print("Rnum:"+rNum +"   Task"+p.getTaskSet().getTask(i).getID()+"  R"+allR.get(r.getID()-1).R.getID());
                                println("("+status.get(i)+")");
                                mark(allR.get(r.getID()-1).R);
                            }
                        }
                        if(rg.size()>0)
                        {
                            allRG.add(rg);
                        }
                    }
                }
            }
            
            runtemp=1;
        }
        
        for(int i=0;i<allRG.size();i++)
        {
            print("RG"+i+" : ");
            for(int j=0;j<allRG.get(i).size();j++)
            {
                print("  R"+allRG.get(i).get(j).R.getID());
            }
            println("");
        }
    }
    public void setNests(Processor p)
    {
        for(int i = 0;i<p.getTaskSet().size();i++)
        {
            taskNests = new Vector<FMLP_Nest>();
            for(int j =0;j<p.getTaskSet().getTask(i).getNestSet().size();j++)
            {
                println("i"+i+" j"+j+"   "+p.getTaskSet().getTask(i).getNestSet().get(j));
                FMLP_Nest n = new FMLP_Nest(p.getTaskSet().getTask(i).getNestSet().get(j));
                taskNests.add(n);
            }
            allNests.add(taskNests);
        }
    }
    public void mark (SharedResource r)
    {
        if(!allR.get(r.getID()-1).isgroup)
        {    
            allR.get(r.getID()-1).isgroup=true;
            for(int i=0;i<allNests.size();i++)
            {
                for(int j=0 ;j<allNests.get(i).size();j++)
                {
                    for(int k=0;k<allNests.get(i).get(j).nest.size();k++)
                    {
                        if(r.equals(allNests.get(i).get(j).nest.getCriticalSection(k).getUseSharedResource()))
                        {
                            allNests.get(i).get(j).isgroup=true;
                        }
                    }
                }
            }
        }
    }
    
    Vector<Resource> allR = new Vector<Resource>();
    Vector<ResourceGroup> allRG = new Vector<ResourceGroup>();//只用於print
    Vector<String> status = new Vector<String>();
    Vector<FMLP_Nest> taskNests;
    Vector<Vector<FMLP_Nest>> allNests = new Vector<Vector<FMLP_Nest>>();
    String allIsShort="allIsShort",allIsLong="allIsLong" ,complex="complex";
    double proportion =0.0;
    
    public G_FMLP()
    {
        this.setName("Flexible Multiprocessor Locking Protocol under G_EDF"); 
    }
    
    @Override
    public void preAction(Processor p)
    {
        for(int i = 0;i<p.getSharedResourceSet().size();i++)
        {
            Resource R = new Resource(p.getSharedResourceSet().get(i));
            allR.add(R);
        }
        
        setResourceStatus();//設置資源長短狀態
        setNests(p);//設置巢狀資源
        modifyResource(p);//修正資源長短狀態
        setResourceGroup(p);//資源分群
    }

    @Override
    public void jobArrivesAction(Job j) 
    {
    }

    @Override
    public void jobPreemptedAction(Job preemptedJob, Job nextJob) 
    {
    }

    @Override
    public boolean checkJobFirstExecuteAction(Job j) 
    {
        return true;
    }

    @Override
    public void jobFirstExecuteAction(Job j) 
    {
    }

    @Override
    public SharedResource checkJobLockAction(Job j, SharedResource r) 
    {   
        ResourceGroup rg = searchResourceGroup(r);
        println("RG="+rg);
        rg.addJobForFIFOJobQueue(j);
        
        if(r.getIdleResourceNum() > 0 //判斷是否還有閒置的資源
           && j.equals(rg.getFirstJobForFIFOJobQueue()))//是否在FIFOQ裡為第一個
        {
            j.lockSharedResource(r);

            if(rg.isShort())
            {
                j.getCurrentCore().isPreemption = false;
            }
            println("J"+j.getParentTask().getID()+" lock R"+r.getID());
            return null;
        }   
        println("J"+j.getParentTask().getID()+" don't lock R"+r.getID());
        println("r.getIdleResourceNum() = "+r.getIdleResourceNum());
        println("j.equals(rg.getFirstJobForFIFOJobQueue()) = "+j.equals(rg.getFirstJobForFIFOJobQueue()));
        println("rg.getFirstJobForFIFOJobQueue() = "+rg.getFirstJobForFIFOJobQueue().getParentTask().getID());
        return r;
    }

    @Override
    public void jobBlockedAction(Job blockedJob, SharedResource blockingRes) 
    {
        ResourceGroup rg = searchResourceGroup(blockingRes);
        Job blockingJob = rg.getFirstJobForFIFOJobQueue();
        println("blockedJob="+blockedJob.getParentTask().getID());
        println("blockingRes="+blockingRes.getID());
        println("blockingJob="+blockingJob);
        
        if(rg.isShort())
        {//Busy Waiting
            blockedJob.getCurrentCore().isPreemption = false;
            blockedJob.getCurrentCore().setCoreStatus(Definition.CoreStatus.WAIT);
        }
        else
        {
            if(blockedJob!=null && blockedJob.getCurrentProiority().isHigher(blockingJob.getCurrentProiority()))//判斷優先權
            {
                blockingJob.inheritPriority(blockedJob.getCurrentProiority());   
            }
            rg.addJobForSuspensionQueue(blockedJob);
        }
    }

    @Override
    public void jobUnlockAction(Job j, SharedResource r) 
    {
        ResourceGroup rg = searchResourceGroup(r);
        j.unLockSharedResource(r);
        
        println("J = "+j.getParentTask().getID());
        println("R = "+r.getID());
        println("CurrentTime = "+j.getCurrentCore().getCurrentTime());
        //判斷需不需要解鎖群組 預設值為要解鎖
        int lockCount=0;
        for(int i = 0;i<rg.size();i++)
        {
            if(rg.get(i).R.getIdleResourceNum()==0)
            {
                lockCount++; 
            }
        }
        
        if(lockCount==0)
        {
            println("lockCount==0");
            rg.removeJobForFIFOJobQueue(j);
            if(rg.isShort())
            {
                println("isShort");
                
                j.getCurrentCore().isPreemption = true;  
                for(int i=0;i<j.getEnteredCriticalSectionArray().size();i++)
                {
                    SharedResource s = searchResourceForCriticalSectionArray(j,i);
                    
                    if(searchResourceGroup(s).isShort())
                    {    
                        j.getCurrentCore().isPreemption = false; 
                    }
                }
            }
            else
            {
                println("isLong");
                
                rg.releaseJobForSuspensionQueue(rg.getFirstJobForFIFOJobQueue());
                j.endInheritance();
                //重新尋找優先權
                j.isInherit = false;
                if(j.getEnteredCriticalSectionArray().size()>0)//如果還有R
                {
                    Priority p = Ohm;
                    
                    for(int i=0;i<j.getEnteredCriticalSectionArray().size();i++)
                    {
                        SharedResource s = searchResourceForCriticalSectionArray(j,i);
                        ResourceGroup rgs = searchResourceGroup(s);
                        if(!rgs.isShort())
                        {    
                            for(Job job : rgs.suspensionQueue)
                            {
                                if(job.isInherit)
                                {
                                    if(job.getInheritPriority().isHigher(p))
                                    {
                                        p = job.getInheritPriority();
                                    }
                                }
                                else
                                {
                                    if(job.getOriginalPriority().isHigher(p))
                                    {
                                        p = job.getOriginalPriority();
                                    }
                                }
                            }
                        }    
                    }
                    if(p.isHigher(Ohm) && p.isHigher(j.getCurrentProiority()))
                    {
                        j.inheritPriority(p);
                    }
                } 
            }
        }   
        println("isPreemption = "+j.getCurrentCore().isPreemption);
        println("j.getCurrentProiority() = "+j.getCurrentProiority().getValue());
        
        if(j.getEnteredCriticalSectionArray().isEmpty())
        {
            println("Ｊ"+j.getParentTask().getID()+" 沒有鎖定 Ｒ 了");
        }
        else
        {
            for(CriticalSection cs :j.getEnteredCriticalSectionArray())
            {
                println("Ｊ"+j.getParentTask().getID()+" 還鎖定著 Ｒ"+cs.getUseSharedResource().getID());
                ResourceGroup rg2 = searchResourceGroup(cs.getUseSharedResource());
                println("Ｒ"+cs.getUseSharedResource().getID()+" 的群組是被 Ｊ"+rg2.getFirstJobForFIFOJobQueue().getParentTask().getID()+"鎖定著");           
            }
        }
    }

    @Override
    public void jobCompletedAction(Job j) 
    {
    }

    @Override
    public void jobMissDeadlineAction(Job j) 
    {
        SharedResource blockingResource = j.getBlockingResource();
        if(blockingResource != null)
        {
            ResourceGroup rg = this.searchResourceGroup(blockingResource);
            rg.FIFOJobQueue.remove(j);
            rg.suspensionQueue.remove(j);
        }
    }
}
