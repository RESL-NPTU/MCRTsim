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
public class HalfSpeed extends DynamicVoltageAndFrequencyScalingMethod
{
    double halfSpeed;
    
    public HalfSpeed()
    {
        this.setName("HalfSpeed");
    }
    
    @Override
    public void definedSpeed()
    {
        this.halfSpeed = this.getDynamicVoltageRegulator().getCore(0).getTaskSet(0).getMaxProcessingSpeed()/2;
    }
    
    @Override
    public void scalingVoltage()
    {
        this.getDynamicVoltageRegulator().setCurrentSpeed(this.halfSpeed);
    }
}
