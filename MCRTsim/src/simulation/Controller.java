/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import concurrencyControlProtocol.ConcurrencyControlProtocol;

/**
 *
 * @author ShiuJia
 */
public class Controller
{
    private ConcurrencyControlProtocol concurrencyControlProtocol;
    
    public Controller()
    {
        
    }
    
    public void setCCProtocol(ConcurrencyControlProtocol cc)
    {
        this.concurrencyControlProtocol = cc;
    }
    
    public ConcurrencyControlProtocol getCCProtocol()
    {
        return this.concurrencyControlProtocol;
    }
    
    public boolean lockControl(Job j)
    {
        if(this.concurrencyControlProtocol.leadLock(j))
        {
            while((j.getLockResource().peek() != null) && (j.getLockResource().peek().getStartTime() <= Math.floor(j.getProgressAmount())))
            {
                CriticalSection cs = j.getLockResource().peek();
                if(this.concurrencyControlProtocol.lock(j, cs.getResources()))
                {
                    j.getLockResource().poll();
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
        return true;
    }
    
    public void unlockControl(Job j)
    {
        while((j.getLockedResource().size() > 0) && (j.getLockedResource().peek().getEndTime() <= Math.ceil(j.getProgressAmount())))
        {
            this.concurrencyControlProtocol.unlock(j, j.getLockedResource().pop());
        }
    }
    
    public void unlockControlForMissDeadline(Job j)
    {
        while(j.getLockedResource().size() > 0 && j.getLockedResource().peek() != null)
        {
            this.concurrencyControlProtocol.unlock(j, j.getLockedResource().pop());
        }
    }
}