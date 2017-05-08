/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import dynamicVoltageAndFrequencyScaling.DynamicVoltageAndFrequencyScalingMethod;
import java.util.Comparator;
import java.util.Vector;

/**
 *
 * @author ShiuJia
 */
public class DynamicVoltageRegulator extends Vector<Speed>
{
    private DynamicVoltageAndFrequencyScalingMethod dynamicVoltageAndFrequencyScaling;
    private Vector<Core> cores;
    private double currentSpeed;
    private double alpha, beta, gamma;
    public boolean hasPowerConsumptionFunction;
    public boolean isIdeal;
    
    public DynamicVoltageRegulator() 
    {
        super();
        this.cores = new Vector<Core>();
        this.currentSpeed = 0;
        this.alpha = 0;
        this.beta = 0;
        this.gamma = 0;
        this.hasPowerConsumptionFunction = false;
        this.isIdeal = false;
    }
    
    public DynamicVoltageRegulator(DynamicVoltageRegulator dynamicVoltageRegulator) 
    {
        super();
        this.cores = new Vector<Core>();
        this.currentSpeed = dynamicVoltageRegulator.getCurrentSpeed();
        this.alpha = dynamicVoltageRegulator.getAlpha();
        this.beta = dynamicVoltageRegulator.getBeta();
        this.gamma = dynamicVoltageRegulator.getGamma();
        this.hasPowerConsumptionFunction = dynamicVoltageRegulator.hasPowerConsumptionFunction;
        this.isIdeal = dynamicVoltageRegulator.isIdeal;
        
        for(Speed s : dynamicVoltageRegulator)
        {
            this.addSpeed(s);
        }
    }
    
    public void addSpeed(Speed s)
    {
        this.add(s);
        this.sort
        (
            new Comparator<Speed>()
            {
                public int compare(Speed s1, Speed s2)
                {
                    if(s1.getSpeed() < s2.getSpeed())
                    {
                        return -1;
                    }
                    else if(s1.getSpeed() > s2.getSpeed())
                    {
                        return 1;
                    }
                    return 0;
                }
            }
        );
    }
    
    public void addCore(Core c)
    {
        this.cores.add(c);
    }
    
    public Vector<Core> getCores()
    {
        return this.cores;
    }
    
    public Core getCore(int i)
    {
        return this.cores.get(i);
    }
    
    public void setAlpha(double a)
    {
        this.alpha = a;
        this.hasPowerConsumptionFunction = true;
    }
    
    public double getAlpha()
    {
        return this.alpha;
    }
    
    public void setBeta(double b)
    {
        this.beta = b;
        this.hasPowerConsumptionFunction = true;
    }
    
    public double getBeta()
    {
        return this.beta;
    }
    
    public void setGamma(double g)
    {
        this.gamma = g;
        this.hasPowerConsumptionFunction = true;
    }
    
    public double getGamma()
    {
        return this.gamma;
    }
    
    public double getMinFrequencyOfSpeed()
    {
        return this.get(0).getSpeed();
    }
    
    public double getMaxFrequencyOfSpeed()
    {
        return this.get(this.size()-1).getSpeed();
    }
    
    public void setCurrentSpeed(double s)
    {
        if(this.isIdeal)
        {
            if(s >= this.getMaxFrequencyOfSpeed())
            {
                this.currentSpeed = this.getMaxFrequencyOfSpeed();
            }
            else if(s <= this.getMinFrequencyOfSpeed())
            {
                this.currentSpeed = this.getMinFrequencyOfSpeed();
            }
            else
            {
                this.currentSpeed = s;
            }
        }
        else
        {
            if(s >= this.getMaxFrequencyOfSpeed())
            {
                this.currentSpeed = this.getMaxFrequencyOfSpeed();
            }
            else
            {
                for(Speed speed : this)
                {
                    if(speed.getSpeed() >= s)
                    {
                        this.currentSpeed = speed.getSpeed();
                        break;
                    }
                }
            }
        }
    }
    
    public double getCurrentSpeed()
    {
        return this.currentSpeed;
    }
    
    public double getPowerConsumption()
    {
        if(this.isIdeal)
        {
            return (this.alpha + (this.beta * Math.pow(this.currentSpeed, this.gamma) ) );
        }
        else
        {
            if(this.hasPowerConsumptionFunction)
            {
                return (this.alpha + (this.beta * Math.pow(this.currentSpeed, this.gamma) ) );
            }
            else
            {
                for(Speed s : this)
                {
                    if( Double.valueOf(this.currentSpeed).equals(s.getSpeed()) )
                    {
                        return s.getPowerConsumption();
                    }
                }
                System.out.println("Not found PowerConsumption of Speed");
                return -1;
            }
        }
    }
    
    public double getNormalizationOfSpeed()
    {
        return this.currentSpeed / this.getMaxFrequencyOfSpeed();
    }
    
    public void setCoreType(String s)
    {
        if(s.equals("Ideal"))
        {
            this.isIdeal = true;
        }
        else if(s.equals("nonIdeal"))
        {
            this.isIdeal = false;
        }
        else
        {
            System.out.println("CoreType Error!!!!!");
        }
    }
    
    
    public void setDVFSMethod(DynamicVoltageAndFrequencyScalingMethod method)
    {
        this.dynamicVoltageAndFrequencyScaling = method;
        this.dynamicVoltageAndFrequencyScaling.setDynamicVoltageRegulator(this);
        this.dynamicVoltageAndFrequencyScaling.definedSpeed();
    }
    
    public DynamicVoltageAndFrequencyScalingMethod getDVFSMethod()
    {
        return this.dynamicVoltageAndFrequencyScaling;
    }
    
    public void scalingVoltage()
    {
        this.dynamicVoltageAndFrequencyScaling.scalingVoltage();
    }
}

