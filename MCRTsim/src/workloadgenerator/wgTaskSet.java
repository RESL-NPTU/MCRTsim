/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workloadgenerator;

import java.text.DecimalFormat;
import java.util.Vector;

/**
 *
 * @author YC
 */
public class wgTaskSet extends Vector<wgTask>
{
    public wgWorkload parent;
    private double totalUtilization = 0;
    
    public wgTaskSet(wgWorkload p)
    {
        super();
        this.parent = p;
    }
    
    public void removeTask(wgTask t)
    {
        this.totalUtilization=wgMath.sub(this.totalUtilization,t.getUtilization());
        this.remove(t);
    }
    
/*setValue*/ 
    public void addTask(wgTask t)
    {
        this.add(t);
        t.setID(this.size());
        this.totalUtilization=wgMath.add(this.totalUtilization,t.getUtilization());
    }
/*getValue*/
    public wgTask getTask(int i)
    {
        return this.get(i);
    }
    
    public double getTotalUtilization()
    {
        DecimalFormat df = new DecimalFormat("##.000");
        return Double.parseDouble(df.format(this.totalUtilization));
    }
    
}
