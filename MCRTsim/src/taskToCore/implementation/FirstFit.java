/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taskToCore.implementation;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import simulation.Core;
import simulation.DataSetting;
import simulation.Resource;
import simulation.Resources;
import simulation.ResourcesSet;
import simulation.Task;
import simulation.TaskSet;
import taskToCore.TaskToCore;

/**
 *
 * @author ShiuJia
 */
public class FirstFit extends TaskToCore
{
    Vector<Task> decreasingTaskSet;
    
    public FirstFit(DataSetting ds)
    {
        super(ds);
        this.setName("FirstFit");
    }

    @Override
    public void assign()
    {
        this.decreasingTaskSet = new Vector<Task>();
        this.decreasingTaskSet.addAll(this.getDataSetting().getTaskSet());
        
        for(Task t : this.decreasingTaskSet)
        {
            Core core = this.getDataSetting().getProcessor().getCore(0);
            
            for(Core c : this.getDataSetting().getProcessor().getCores())
            {
                if(c.leftUtilizationCompareTo(t) == 1)
                {
//                    System.out.println("1"+c.getLeftUtilization() + t.getUtilization());
//                    System.out.println("2"+c.getLeftUtilization() + core.getLeftUtilization());
                    core = c;
                    break;
                }
            }
            
            if(core.leftUtilizationCompareTo(t) == 1)
            {
                core.addTask(t);
                System.out.println("C"+core.getID()+":U= "+core.getTotalUtilization()+
                                    ", T"+t.getID() + ":U= " + t.getUtilization());
            }
            else
            {
                System.out.println("The case can't be scheduled!!");
                core.addTask(t);
                System.out.println("C"+core.getID()+":U= "+core.getTotalUtilization()+
                                    ", T"+t.getID() + ":U= " + t.getUtilization());
            }
        }
    }
}
