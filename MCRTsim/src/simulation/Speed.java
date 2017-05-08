/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

/**
 *
 * @author ShiuJia
 */
public class Speed
{
    private double speed;
    private double powerConsumption;
    private double normalization;
    
    public Speed()
    {
        this.speed = -1;
        this.powerConsumption = -1;
    }
    
    public void setSpeed(double f)
    {
        this.speed = f;
    }
    
    public double getSpeed()
    {
        return this.speed;
    }
    
    public void setPowerConsumption(Double p)
    {
        this.powerConsumption = p;
    }
    
    public Double getPowerConsumption()
    {
        return this.powerConsumption;
    }
    
    public void setNormalization(double n)
    {
        this.normalization = ((int)(Math.ceil(n * 100000))) / 100000.0;
    }
    
    public double getNormalization()
    {
        return this.normalization;
    }
}

