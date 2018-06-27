/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WorkLoadSet;

import SystemEnvironment.*;
import WorkLoad.CoreSpeed;
import java.util.Vector;
import static mcrtsim.MCRTsim.println;
import mcrtsim.MCRTsimMath;

/**
 *
 * @author YC
 */
public class CoreSet extends Vector<Core>
{
    private CoreSpeedSet coreSpeedSet;
    private Processor parentProcessor;
    private int groupID;
    private double currentSpeed;
    private double currentPowerConsumption;
    private double alpha;
    private double beta;
    private double gamma;
    private boolean hasPowerConsumptionFunction;
    public boolean isIdeal;
    
    public CoreSet()
    {
        super();
        this.coreSpeedSet = new CoreSpeedSet(this);
        this.groupID = 0;
        this.currentSpeed = 0;
        this.currentPowerConsumption = 0;
        this.alpha = 0;
        this.beta = 0;
        this.gamma = 0;
        this.hasPowerConsumptionFunction = false;
        this.isIdeal = false;
    }
    
    public CoreSet(CoreSet cSet)
    {
        super();
        this.coreSpeedSet = new CoreSpeedSet(this);
        this.groupID = 0;
        this.currentSpeed = 0;
        this.currentPowerConsumption = 0;
        this.alpha = cSet.getAlphaValue();
        this.beta = cSet.getBetaValue();
        this.gamma = cSet.getGammaValue();
        this.isIdeal = cSet.isIdeal;
        this.hasPowerConsumptionFunction = cSet.hasPowerConsumptionFunction;
        
        for(CoreSpeed cSpeed : cSet.getCoreSpeedSet())
        {
            this.coreSpeedSet.addSpeed(cSpeed);
        }
    }
    
    public void addCore(Core core) 
    {
        this.add(core);
        core.setParentCoreSet(this);
    }
    
    public void addCoreSpeed(CoreSpeed s)
    {
        this.coreSpeedSet.addSpeed(s);
    }
    
    public void addCoreSpeedToSet(CoreSpeed s, int i)
    {
        
    }
    
    
    /*SetValue*/
    public void setGroupID(int id)
    {
        this.groupID = id;
    }
    
    public void setParentProcessor(Processor p)
    {
        this.parentProcessor = p;
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
            println("CoreType Error!!!!!");
        }
    }
    
    public void setAlphaValue(double a)
    {
        this.alpha = a;
        this.hasPowerConsumptionFunction = true;
    }
    
    public void setBetaValue(double b)
    {
        this.beta = b;
        this.hasPowerConsumptionFunction = true;
    }
    
    public void setGammaValue(double r)
    {
        this.gamma = r;
        this.hasPowerConsumptionFunction = true;
    }
    
    public void setCoreSpeedSet(CoreSpeedSet ss)
    {
        this.coreSpeedSet = ss;
    }
    
    public void setCurrentSpeed()
    {
        double speed = -1;
        
        for(Core core : this)
        {
            switch(core.getStatus())
            {
                //請加入CONTEXTSWITCH ＆ MIGRATION 的使用速度
                
                case WAIT :
                    core.setCurrentSpeed(this.getCoreSpeedSet().getMinSpeed().getSpeed());
                break;
                case IDLE : 
                    core.setCurrentSpeed(this.getCoreSpeedSet().getIDELSpeed().getSpeed());
                break;  
                default:
            }
        }
        
        for(Core core : this)
        {
            speed = core.getCurrentSpeed() > speed ? core.getCurrentSpeed() : speed;
        }
        double s = this.coreSpeedSet.getCurrentSpeed(speed);
        
        if(this.currentSpeed != s)
        {
            this.currentSpeed = s;
            
            for(Core core : this)
            {
                core.isChangeSpeed = true;
            }
        }
        this.setPowerConsumption();
    }
    
    public void setPowerConsumption()
    {
        if(this.isIdeal)
        {
            this.currentPowerConsumption = MCRTsimMath.mul((this.alpha + (this.beta * Math.pow(MCRTsimMath.div(this.currentSpeed, 1000), this.gamma) ) ),1000);
        }
        else
        {
            if(this.hasPowerConsumptionFunction)
            {
                this.currentPowerConsumption = MCRTsimMath.mul((this.alpha + (this.beta * Math.pow(MCRTsimMath.div(this.currentSpeed, 1000), this.gamma) ) ),1000);
            }
            else
            {
                boolean isFound = false;
                for(CoreSpeed s : this.coreSpeedSet)
                {
                    if( this.currentSpeed == s.getSpeed() )
                    {
                        this.currentPowerConsumption = s.getPowerConsumption();
                        isFound = true;
                    }
                }
                if(!isFound)//檢查是否取得相對應的 PowerConsumption
                {
                    println("Not found PowerConsumption of Speed");
                    this.currentPowerConsumption = -1;
                }
            }
        }
    }
    
    /*GetValue*/
    public int getGroupID()
    {
        return this.groupID;
    }
    
    public Processor getParentProcessor()
    {
        return this.parentProcessor;
    }
    
    public double getAlphaValue()
    {
        return this.alpha;
    }
    
    public double getBetaValue()
    {
        return this.beta;
    }
    
    public double getGammaValue()
    {
        return this.gamma;
    }
    
    public Core getCore(int i)
    {
        return this.get(i);
    }
    
    public CoreSpeedSet getCoreSpeedSet()
    {
        return this.coreSpeedSet;
    }
    
    public double getCurrentSpeed()
    {
        return this.currentSpeed;
    }
    
    public double getNormalizationOfSpeed()
    {
        return this.currentSpeed / this.coreSpeedSet.getMaxFrequencyOfSpeed();
    }
    
    public double getPowerConsumption()
    {
        return this.currentPowerConsumption;
    }
}
