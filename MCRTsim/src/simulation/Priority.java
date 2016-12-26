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
public class Priority
{
    private int value;
    
    public Priority()
    {
        this.value = Integer.MAX_VALUE;
    }
    
    public void clonePriority(Priority p)
    {
        this.value = p.value;
    }
    
    public Priority(int i)
    {
        this.value = i;
    }
    
    public void setValue(int p)
    {
        this.value = p;
    }
    
    public int getValue()
    {
        return -this.value;
    }
}
