/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicVoltageAndFrequencyScaling;

import simulation.DataSetting;
import simulation.DynamicVoltageRegulator;

/**
 *
 * @author ShiuJia
 */
public abstract class DynamicVoltageAndFrequencyScalingMethod implements Cloneable
{
    private String methodName;
    private DataSetting dataSetting;//暫無用到
    private DynamicVoltageRegulator regulator;
    
    public DynamicVoltageAndFrequencyScalingMethod()
    {
        
    }
    
    public void setName(String name)
    {
        this.methodName = name;
    }
    
    public String getName()
    {
        return this.methodName;
    }
    
    public void setDynamicVoltageRegulator(DynamicVoltageRegulator s)
    {
        this.regulator = s;
    }
    
    public DynamicVoltageRegulator getDynamicVoltageRegulator()
    {
        return this.regulator;
    }
    
    public void setDataSetting(DataSetting ds)
    {
        this.dataSetting = ds;
    }
    
    public DataSetting getDataSetting()
    {
        return this.dataSetting;
    }
    
    public abstract void definedSpeed();
    
    public abstract void scalingVoltage();
    
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
