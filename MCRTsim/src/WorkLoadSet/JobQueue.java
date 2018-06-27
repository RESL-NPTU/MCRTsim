/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WorkLoadSet;

import WorkLoad.Job;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 *
 * @author ShiuJia
 */
public class JobQueue extends PriorityQueue<Job>
{
    public JobQueue()
    {
        super();
    }
    
    public void reSort()
    {
        JobQueue newQ = new JobQueue();
        while(this.size()!=0)
        {
            newQ.add(this.poll());
        } 
        this.addAll(newQ);
    }
    
    public void setBlockingTime(Job blockingJob)
    {
        if(!this.isEmpty())
        {
            Object[] jobs = this.toArray();
            for(int i = 0 ; i<jobs.length ; i++)
            {
                if(blockingJob != ((Job)jobs[i]))
                {
                    if(((Job)jobs[i]).getOriginalPriority().compare(blockingJob.getOriginalPriority()) == 1)
                    {
                        ((Job)jobs[i]).setBeBlockedTime(1);
                    }
                    else 
                    {
                        break;
                    }
                }
            }
        }
    }
    
    @Override
    public Job peek() 
    {
        Job j = super.peek();
        if(j==null || j.isSuspended)
        {
            return null;
        }
        return j;
    }
}
