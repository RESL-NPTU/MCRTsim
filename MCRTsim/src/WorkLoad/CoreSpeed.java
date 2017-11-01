/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WorkLoad;

/**
 *
 * @author ShiuJia
 */
public class CoreSpeed
{
    private double speed;
    private double powerConsumption;
    
    public CoreSpeed()
    {
        this.speed = -1;
        this.powerConsumption = -1;
    }
    
    /*SetValue*/
    public void setSpeed(double f)
    {
        this.speed = f;
    }
    
    public void setPowerConsumption(double p)
    {
        this.powerConsumption = p;
    }
    
    /*GetValue*/
    public double getSpeed()
    {
        return this.speed;
    }
    
    public double getPowerConsumption()
    {
        return this.powerConsumption;
    }
}
