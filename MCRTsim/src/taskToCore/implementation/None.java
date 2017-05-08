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
public class None extends TaskToCore
{
    public None(DataSetting ds)
    {
        super(ds);
        this.setName("None");
    }

    @Override
    public void assign()
    {
        
    }
}
