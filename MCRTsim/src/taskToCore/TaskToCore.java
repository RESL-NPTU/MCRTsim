/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taskToCore;

import simulation.DataSetting;

/**
 *
 * @author ShiuJia
 */
public abstract class TaskToCore
{
    private String methodName;
    private DataSetting dataSetting;
    
    public TaskToCore(DataSetting ds)
    {
        this.dataSetting = ds;
    }
    
    public void setName(String name)
    {
        this.methodName = name;
    }
    
    public String getName()
    {
        return this.methodName;
    }
    
    public void setDataSetting(DataSetting ds)
    {
        this.dataSetting = ds;
    }
    
    public DataSetting getDataSetting()
    {
        return this.dataSetting;
    }
    
    public abstract void assign();
}
