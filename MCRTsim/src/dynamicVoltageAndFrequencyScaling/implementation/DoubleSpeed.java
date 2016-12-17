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
public class DoubleSpeed extends DynamicVoltageAndFrequencyScalingMethod
{
    double doubleSpeed;
    
    public DoubleSpeed()
    {
        this.setName("None");
    }
    
    @Override
    public void definedSpeed()
    {
        this.doubleSpeed = this.getDynamicVoltageRegulator().getCore(0).getTaskSet(0).getMaxProcessingSpeed()*2;
    }
    
    @Override
    public void scalingVoltage()
    {
        this.getDynamicVoltageRegulator().setCurrentSpeed(this.doubleSpeed);
    }
}
