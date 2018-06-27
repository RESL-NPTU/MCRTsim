/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WorkLoad;

import java.util.Vector;

/**
 *
 * @author admin
 */
public class Nest extends Vector<CriticalSection>
{
    public Task parentTask = null;
    public Nest(Task t)
    {
        this.parentTask = t;
    }
    
    public void addCriticalSection(CriticalSection c)
    {
        this.add(c);
    }

    public CriticalSection getCriticalSection(int i)
    {
        return this.get(i);
    }
}
    

