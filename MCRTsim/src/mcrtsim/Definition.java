/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcrtsim;

import WorkLoad.Priority;

/**
 *
 * @author ShiuJia
 */
public class Definition
{
    public static final Priority Ohm = new Priority(Long.MAX_VALUE);
    public static long magnificationFactor = 1;
    public static String magnificationFormat = "##.#####";//格式
    
    
    public enum CoreStatus
    {
        IDLE, EXECUTION, WRONG, WAIT, STOP, CONTEXTSWITCH, MIGRATION
    }
    
    public enum JobStatus
    {
        NONCOMPUTE,COMPUTING,COMPLETED,MISSDEADLINE
    }
    
    public enum DVFSType
    {
        FullChip, PerCore, VFI
    }
    
    public enum SchedulingType
    {
        SingleCore, Partition, Global, Hybrid
    }
    
    public enum PriorityType
    {
        Fixed, Dynamic
    }
}
