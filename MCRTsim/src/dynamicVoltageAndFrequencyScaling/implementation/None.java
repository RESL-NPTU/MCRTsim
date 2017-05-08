/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicVoltageAndFrequencyScaling.implementation;

import dynamicVoltageAndFrequencyScaling.DynamicVoltageAndFrequencyScalingMethod;
import simulation.Speed;

/**
 *
 * @author ShiuJia
 */
public class None extends DynamicVoltageAndFrequencyScalingMethod
{
    public None()
    {
        this.setName("None");
    }
    
    @Override
    public void definedSpeed()
    {
    }
    
    @Override
    public void scalingVoltage()
    {
        this.getDynamicVoltageRegulator().setCurrentSpeed
        (
            this.getDataSetting().getTaskSet(0).getMaxProcessingSpeed()
        );
    }

}
