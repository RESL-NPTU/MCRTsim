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
public class Priority
{
    private long value;
    
    public Priority()
    {
        this.value = 0;
    }
    
    public Priority(long v)
    {
        this.value = v;
    }
    
    public void setValue(long v)
    {
        this.value = v;
    }
    
    public long getValue()
    {
        return -(this.value);
    }
    
    public boolean isHigher(Priority p)
    {
        if(this.getValue() > p.getValue())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public int compare(Priority p)
    {
        if(this.getValue() > p.getValue())
        {
            return 1;
        }
        else if(this.getValue() < p.getValue())
        {
            return -1;
        }
        else 
        {
            return 0;
        }
    }
    
}
