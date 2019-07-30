/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencyControlProtocol.implementation;

import SystemEnvironment.Processor;
import WorkLoad.Job;
import WorkLoad.SharedResource;
import concurrencyControlProtocol.ConcurrencyControlProtocol;

/**
 *
 * @author ShiuJia
 */
public class None extends ConcurrencyControlProtocol
{
    public None()
    {
        this.setName("None");
    }

    @Override
    public void preAction(Processor p)
    {
        
    }

    @Override
    public void jobArrivesAction(Job j)
    {
    }

    @Override
    public  void jobPreemptedAction(Job preemptedJob , Job newJob)
    {
    }
    
    @Override
    public void jobFirstExecuteAction(Job j)
    {
    }

    @Override
    public SharedResource checkJobLockAction(Job j, SharedResource r)
    {
        return null;
    }

    @Override
    public void jobUnlockAction(Job j, SharedResource r)
    {
    }

    @Override
    public void jobCompletedAction(Job j)
    {
    }

    @Override
    public void jobMissDeadlineAction(Job j)
    {
    }

    @Override
    public void jobBlockedAction(Job blockedJob, SharedResource blockingRes)
    {
    }

    @Override
    public boolean checkJobFirstExecuteAction(Job j) 
    {
        return true;
    }
}
