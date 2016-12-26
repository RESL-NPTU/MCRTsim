/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import java.util.Collection;
import java.util.PriorityQueue;

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
        while(this.peek()!=null)
        {
            newQ.add(this.poll());
        }
        
        this.addAll(newQ);
    }
    
    public void jobPriorityOfInheritOrRevert()
    {
        JobQueue newQ = new JobQueue();
        while(this.peek()!=null)
        {
            Job j = this.poll();
            j.currentPriorityOfInheritOrRevert();
            System.out.println("isMissDeadline");
            newQ.add(j);
        }
        this.addAll(newQ);
    }
}
