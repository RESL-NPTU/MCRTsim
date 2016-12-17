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
public class BasicSpeed extends DynamicVoltageAndFrequencyScalingMethod
{
    double basicSpeed;
    
    public BasicSpeed()
    {
        this.setName("None");
    }
    
    @Override
    public void definedSpeed()
    {
        this.basicSpeed = this.getDynamicVoltageRegulator().getCore(0).getTaskSet(0).getMaxProcessingSpeed();
    }
    
    @Override
    public void scalingVoltage()
    {
        this.getDynamicVoltageRegulator().setCurrentSpeed(this.basicSpeed);
    }
}
