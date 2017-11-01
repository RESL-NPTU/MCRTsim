/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WorkLoadSet;

import WorkLoad.Job;
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
        while(this.size()!=0)
        {
            newQ.add(this.poll());
        } 
        this.addAll(newQ);
    }
    
    
}
