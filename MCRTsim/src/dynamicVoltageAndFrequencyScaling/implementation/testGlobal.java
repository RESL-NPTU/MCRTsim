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
public class testGlobal extends DynamicVoltageAndFrequencyScalingMethod
{
    double basicSpeed;
    
    public testGlobal()
    {
        this.setName("testGlobal");
    }
    
    @Override
    public void definedSpeed()
    {
      //  this.basicSpeed = this.getDynamicVoltageRegulator().getCore(0).getLocalReadyQueue().peek().getTask().getMaxProcessingSpeed();
    }
    
    @Override
    public void scalingVoltage()
    {
        if(this.getDynamicVoltageRegulator().getCore(0).getLocalReadyQueue().peek()!=null)
        {
        this.getDynamicVoltageRegulator().setCurrentSpeed(
                this.getDynamicVoltageRegulator().getCore(0).getLocalReadyQueue().peek().getTask().getMaxProcessingSpeed());
//this.basicSpeed);
        }
        else
        {
            this.getDynamicVoltageRegulator().setCurrentSpeed(0);
        }
    }
}
