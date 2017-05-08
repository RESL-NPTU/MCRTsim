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
public class MaximumSpeed extends DynamicVoltageAndFrequencyScalingMethod
{
    double maxSpeed;
    
    public MaximumSpeed()
    {
        this.setName("Maximum Speed");
    }
    
    @Override
    public void definedSpeed()
    {
        this.maxSpeed = this.getDynamicVoltageRegulator().getMaxFrequencyOfSpeed();
    }
    
    @Override
    public void scalingVoltage()
    {
        this.getDynamicVoltageRegulator().setCurrentSpeed(this.maxSpeed);
    }
}
