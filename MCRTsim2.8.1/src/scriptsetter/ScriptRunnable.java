/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scriptsetter;

import PartitionAlgorithm.PartitionAlgorithm;
import SystemEnvironment.Core;
import SystemEnvironment.DataReader;
import SystemEnvironment.Simulator;
import WorkLoad.Task;
import WorkLoadSet.DataSetting;
import concurrencyControlProtocol.ConcurrencyControlProtocol;
import dynamicVoltageAndFrequencyScalingMethod.DynamicVoltageAndFrequencyScalingMethod;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static mcrtsim.Definition.magnificationFactor;
import static mcrtsim.MCRTsim.println;
import mcrtsim.MCRTsimMath;
import schedulingAlgorithm.PriorityDrivenSchedulingAlgorithm;
import userInterface.frontEnd.SimulationViewer;

/**
 *
 * @author YC
 */



public class ScriptRunnable implements Runnable
{
    private Thread t;
    private Script script;
    private String workloadFileName;
    public ScriptSetter parent;
    
    ScriptRunnable(ScriptSetter p, Script s, String name) 
    {
        this.parent = p;
        this.script = s;
        this.workloadFileName = name;
        System.out.println("creat : "+script.getID()+" , "+workloadFileName);
    }
   
    public void run() 
    {
        
        System.out.println("Start : "+script.getID()+" , "+workloadFileName);
        try
        {
//            Vector<String> workloadFileNames = this.getFolderFile(script.getWorkloadSite());
            //Vector<String> processorfileNames = this.getFolderFile(script.getProcessorSite());
            String processorFileName = script.getProcessorSite();
            
            println(script.getWorkloadSite()+"/"+workloadFileName + ".xml");
            
            println(processorFileName);
            
            
            int i = 0;
            
                DataReader dataReader = new DataReader();
                Simulator simulator = new Simulator();
                println(script.getWorkloadSite()+"/"+workloadFileName + ".xml");
                println(processorFileName);
                
                dataReader.loadSource(script.getWorkloadSite()+"/"+workloadFileName + ".xml");
                dataReader.loadSource(processorFileName);
                simulator.setSimulationTime
                (
                    Double.valueOf
                    (
                        Double.valueOf
                        (
                            script.getSimulationTime()
                        )*magnificationFactor
                    ).longValue()
                );

                simulator.loadDataSetting(dataReader.getDataSetting());
                simulator.getProcessor().setSchedAlgorithm(this.getPrioritySchedulingAlgorithm(script.getSchedulingAlgorithm()));
                simulator.getProcessor().setPartitionAlgorithm(this.getPartitionAlgorithm(script.getPartitionAlgorithm()));
                simulator.getProcessor().setCCProtocol(this.getConcurrencyControlProtocol(script.getCCProtocol()));
                simulator.getProcessor().setDVFSMethod(this.getDynamicVoltageScalingMethod(script.getDVFSMethod()));

                dataReader.getDataSetting().getProcessor().showInfo();
                println("Workload:" + dataReader.getDataSetting().getTaskSet().getProcessingSpeed());
                for(Task t : dataReader.getDataSetting().getTaskSet())
                {
                    t.showInfo();
                }

                simulator.start();

            //setScriptResult---------{
                {
                    ScriptResult sr = new ScriptResult(script);
                    sr.setWorkloadFile(workloadFileName);
                    sr.setProcessorFile(processorFileName);
                    
                    MCRTsimMath math = new MCRTsimMath();
                    for(Core c : simulator.getProcessor().getAllCore())
                    {
                        sr.addPowerConsumption(math.changeDecimalFormatFor5((double)c.getPowerConsumption()/magnificationFactor));
                    }

                    DataSetting ds = dataReader.getDataSetting();

                    sr.setTaskCount(ds.getTaskSet().size());
                    sr.setTotalJobCompeletedCount(ds.getTaskSet().getTotalJobCompletedNumber());
                    sr.setTotalJobMissDeadlineCount(ds.getTaskSet().getTotalJobMissDeadlineNumber());
                    sr.setCompletedRatio(ds.getTaskSet().getJobCompletedRatio());
                    sr.setDeadlineMissRatio(ds.getTaskSet().getJobMissDeadlineRatio());
                    sr.setAveragePendingTime(ds.getTaskSet().getAveragePendingTimeOfTask());
                    sr.setAverageResponseTime(ds.getTaskSet().getAverageResponseTimeOfTask());
                    sr.setMaximumUtilization(ds.getTaskSet().getMaximumUtilization());
                    sr.setActualUtilization(ds.getTaskSet().getActualUtilization());
                    sr.setMaximumCriticalSectionRatio(ds.getTaskSet().getMaximumCriticalSectionRatio());
                    sr.setActualCriticalSectionRatio(ds.getTaskSet().getActualCriticalSectionRatio());
                    sr.setBeBlockedTimeRatio(ds.getTaskSet().getAverageBeBlockedTimeRatioOfTask());
                    script.addScriptResult(sr);
                }
            //------------------------}
                
                System.out.println("End :"+script.getID()+" , "+workloadFileName);
//            }
        }
        catch (Exception ex) 
        {
            JOptionPane.showMessageDialog(parent.parent.parent.getFrame(), "Error!!" ,"Error!!" ,WARNING_MESSAGE);
            Logger.getLogger(SimulationViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
   
    public void start () 
    {
        System.out.println("Starting ");
        if (t == null) 
        {
            t = new Thread (this);
            t.start ();
        }
    }
    
    public PartitionAlgorithm getPartitionAlgorithm(String name) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException
    {
        return (PartitionAlgorithm)Class.forName("PartitionAlgorithm.implementation." + name).newInstance();                
    }
    
    public PriorityDrivenSchedulingAlgorithm getPrioritySchedulingAlgorithm(String name) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        return (PriorityDrivenSchedulingAlgorithm)Class.forName("schedulingAlgorithm.implementation." + name).newInstance();                
    }
    
    public DynamicVoltageAndFrequencyScalingMethod getDynamicVoltageScalingMethod(String name) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException
    {
        return (DynamicVoltageAndFrequencyScalingMethod)Class.forName("dynamicVoltageAndFrequencyScalingMethod.implementation." + name).newInstance();
    }
    
    public ConcurrencyControlProtocol getConcurrencyControlProtocol(String name) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException
    {
        return (ConcurrencyControlProtocol)Class.forName("concurrencyControlProtocol.implementation." + name).newInstance();
    }
}    
    
    

