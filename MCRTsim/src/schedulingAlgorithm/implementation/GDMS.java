/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingAlgorithm.implementation;

import java.util.Comparator;
import schedulingAlgorithm.FixedPrioritySchedulingAlgorithm;
import simulation.Priority;
import simulation.Task;
import simulation.TaskSet;

/**
 *
 * @author ShiuJia
 */
public class GDMS extends FixedPrioritySchedulingAlgorithm
{
    public GDMS()
    {
        this.setName("Global Deadline Monotonic Scheduling Algorithm");
        this.isGlobalScheduling = true;
    }

    @Override
    public void setPriority(TaskSet ts)
    {
        TaskSet temp = new TaskSet();
        temp.addAll(ts);
        temp.sort
        (
            new Comparator<Task>()
            {
                public int compare(Task t1, Task t2)
                {
                    if(t1.getRelativeDeadline() < t2.getRelativeDeadline())
                    {
                        return -1;
                    }
                    else if(t1.getRelativeDeadline() >= t2.getRelativeDeadline())
                    {
                        return 1;
                    }
                    return 0;
                }
            }
        );
        
       
        int priorityValue = 1;
        
        ts.get(ts.indexOf(temp.get(0))).setPriority(new Priority(priorityValue));
        
        for(int i = 1; i < temp.size(); i++)
        {
            if(temp.get(i).getRelativeDeadline() == temp.get(i-1).getRelativeDeadline())
            {
                ts.get(ts.indexOf(temp.get(i))).setPriority(new Priority(priorityValue));
            }
            else
            {
                ts.get(ts.indexOf(temp.get(i))).setPriority(new Priority(++priorityValue));
            }
            System.out.println(ts.indexOf(temp.get(i)));
        }
    }
}
