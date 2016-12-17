/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taskToCore.implementation;

import simulation.DataSetting;
import simulation.Task;
import taskToCore.TaskToCore;

/**
 *
 * @author ShiuJia
 */
public class SingleCore extends TaskToCore
{
    public SingleCore(DataSetting ds)
    {
        super(ds);
        this.setName("SingleCore");
    }

    @Override
    public void assign()
    {
        for(Task t : this.getDataSetting().getTaskSet())
        {
            this.getDataSetting().getProcessor().getCore(0).addTask(t);
        }
    }
}
