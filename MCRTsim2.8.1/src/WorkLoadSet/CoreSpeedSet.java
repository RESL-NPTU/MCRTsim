/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WorkLoadSet;

import WorkLoadSet.CoreSet;
import WorkLoad.CoreSpeed;
import java.util.Comparator;
import java.util.Vector;

/**
 *
 * @author ShiuJia
 */
public class CoreSpeedSet extends Vector<CoreSpeed>
{
    public CoreSet parent;
    
    public CoreSpeedSet(CoreSet coreSet)
    {
        super();
        this.parent = coreSet;
    }
    
    public void addSpeed(CoreSpeed coreSpeed)
    {
        this.add(coreSpeed);
        this.sort
        (
            new Comparator<CoreSpeed>()
            {
                public int compare(CoreSpeed s1, CoreSpeed s2)
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
    
    public CoreSpeed getIDELSpeed()
    {
        return this.get(0);
    }
    
    public CoreSpeed getMinSpeed()
    {
        return this.get(1);
    }
    
    public double getMinFrequencyOfSpeed()
    {
        return this.get(1).getSpeed();
    }
    
    public CoreSpeed getMaxSpeed()
    {
        return this.get(this.size()-1);
    }
    
    public double getMaxFrequencyOfSpeed()
    {
        return this.get(this.size()-1).getSpeed();
    }
    

    
    public double getCurrentSpeed(double s)
    {
        if(this.parent.isIdeal)
        {
            if(s >= this.getMaxFrequencyOfSpeed())
            {
                return this.getMaxFrequencyOfSpeed();
            }
            else if(s <= this.getMinFrequencyOfSpeed())
            {
                return this.getMinFrequencyOfSpeed();
            }
            else
            {
                return s;
            }
        }
        else
        {
            if(s >= this.getMaxFrequencyOfSpeed())
            {
                return this.getMaxFrequencyOfSpeed();
            }
            else
            {
                for(CoreSpeed speed : this)
                {
                    if(speed.getSpeed() >= s)
                    {
                        return speed.getSpeed();
                    }
                }
            }
        }
        return 0;
    }
    
    
    
}
