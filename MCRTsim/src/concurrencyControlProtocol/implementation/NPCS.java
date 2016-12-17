/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencyControlProtocol.implementation;

import concurrencyControlProtocol.ConcurrencyControlProtocol;
import simulation.DataSetting;
import simulation.Job;
import simulation.LockInfo;
import simulation.Resources;

/**
 *
 * @author ShiuJia
 */
public class NPCS extends ConcurrencyControlProtocol
{
    public NPCS(DataSetting ds)
    {
        super(ds);
        this.setName("Non-Preemptible Critical Section");
    }

    public boolean leadLock(Job j) 
    {
        return true;
    }
    
    public boolean lock(Job j, Resources r)
    {
        j.getLocationCore().setPreemptible(false);
        j.lock(r);
        return true;
    }
    
    @Override
    public void unlock(Job j, LockInfo l)
    {
        j.unLock(l.getResources());
        if(j.getLockedResource().size() == 0)
        {
            j.getLocationCore().setPreemptible(true);
        }
    }
}