/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface.frontEnd;

import WorkLoadSet.TaskSet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import static mcrtsim.Definition.magnificationFactor;
import mcrtsim.MCRTsimMath;

/**
 *
 * @author YC
 */
public class SimulationResultPopupWin extends JFrame
{
    public SimulationViewer parent;
    
    private Vector<JLabel> data;
    String[] str ={"Simulation Time : ","The Number Of Cores : ","The Number Of Tasks : ","The Number Of Resources : ","Total Utilization Of Tasks : ","Patition Algorithm : "
        ,"DVFS Method : ","Schduling Algorithm : ","Synchronization Protocol : ","The Number Of Jobs Compeleted : "
        ,"The Number Of Jobs Missed Deadline : ","Energy Consumption : ", "Average Energy Consumption Per Job : "
        ,"Average Pending Time Per Job: ","Average Response Time Per Job: "};
    private JLabel simulationTime,coreCount,taskCount,resourceCount,totalUtilization,patitionMethod,DVFSMethod,schdulingAlgorithm
                ,concurrencyControlProtocol,jobCompeletedCount,jobMissDeadlineCount,energyConsumption
                ,averageEnergyConsumption,averagePendingTime,averageResponseTime;
    
    
    public SimulationResultPopupWin(SimulationViewer sv)
    {
        super();
        this.parent = sv;
        init();
        this.revalidate();
    }
    
    private void init()
    {
        data = new Vector<>();
        simulationTime = new JLabel();
        coreCount = new JLabel();
        taskCount = new JLabel();
        resourceCount = new JLabel();
        totalUtilization = new JLabel();
        patitionMethod = new JLabel();
        DVFSMethod = new JLabel();
        schdulingAlgorithm = new JLabel();
        concurrencyControlProtocol = new JLabel();
        jobCompeletedCount = new JLabel();
        jobMissDeadlineCount = new JLabel();
        energyConsumption = new JLabel();
        averageEnergyConsumption = new JLabel();
        averagePendingTime = new JLabel();
        averageResponseTime = new JLabel();
        
        data.add(simulationTime);
        data.add(coreCount);
        data.add(taskCount);
        data.add(resourceCount);
        data.add(totalUtilization);
        data.add(patitionMethod);
        data.add(DVFSMethod);
        data.add(schdulingAlgorithm);
        data.add(concurrencyControlProtocol);
        data.add(jobCompeletedCount);
        data.add(jobMissDeadlineCount);
        data.add(energyConsumption);
        data.add(averageEnergyConsumption);
        data.add(averagePendingTime);
        data.add(averageResponseTime);
        
        this.setTitle("Simulation Results");
        this.setBounds(300, 300, 600, 350);
        this.setMinimumSize(new Dimension(600, 350));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
        this.setLayout(new GridBagLayout());
        
        GridBagConstraints bag = new GridBagConstraints();
        bag.gridx = 0;
        bag.gridy = 0;
        bag.gridwidth = 1;
        bag.gridheight = 1;
        bag.weightx = 0;
        bag.weighty = 1;
        bag.fill = GridBagConstraints.NONE;
        bag.anchor = GridBagConstraints.EAST;
        
        for(int i = 0 ; i<str.length ; i++)
        {
            this.add(new JLabel(str[i]), bag);
            bag.gridy +=1;
        }
        
        this.setData();
        bag.anchor = GridBagConstraints.WEST;
        bag.gridx = 1;
        bag.gridy = 0;
        for(int i = 0 ; i<data.size() ; i++)
        {
            this.add(data.get(i), bag);
            bag.gridy +=1;
        }
        
        JButton closeBtn = new JButton("Close");
        closeBtn.setForeground(Color.red);
        closeBtn.addMouseListener
        (new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    SimulationResultPopupWin.this.setVisible(false);
                }
            }
        );
        
        bag.anchor = GridBagConstraints.EAST;
        bag.gridy +=1;
        this.add(closeBtn,bag);   
    }
    
    public void setData()
    {
        
        try 
        {
            MCRTsimMath math = new MCRTsimMath();
        
            TaskSet ts = this.parent.getDataReader().getDataSetting().getTaskSet();
        
            simulationTime.setText(""+math.changeDecimalFormat((double)this.parent.getSimulationTime()/magnificationFactor));
            coreCount.setText(""+this.parent.getDataReader().getDataSetting().getProcessor().getAllCore().size());
            taskCount.setText(""+ts.size());
            resourceCount.setText(""+this.parent.getDataReader().getDataSetting().getSharedResourceSet().size());
            totalUtilization.setText("" + math.changeDecimalFormatFor5(ts.getTotalUtilization()));
            patitionMethod.setText(""+this.parent.getPartitionComboBox().getSelectedItem().toString());
            DVFSMethod.setText(""+this.parent.getDVFSComboBox().getSelectedItem().toString());
            schdulingAlgorithm.setText(""+this.parent.getSchedulingComboBox().getSelectedItem().toString());
            concurrencyControlProtocol.setText(""+this.parent.getCCPComboBox().getSelectedItem().toString());
            
            jobCompeletedCount.setText(""+ts.getTotalJobCompletedNumber()+"/"+ts.getTotalJobNumber());
            jobMissDeadlineCount.setText(""+ts.getTotalJobMissDeadlineNumber()+"/"+ts.getTotalJobNumber());
            energyConsumption.setText(""+math.changeDecimalFormatFor5(this.parent.getDataReader().getDataSetting().getProcessor().getTotalPowerConsumption()/magnificationFactor) + " (mW)");
            averageEnergyConsumption.setText(""+math.changeDecimalFormatFor5(((this.parent.getDataReader().getDataSetting().getProcessor().getTotalPowerConsumption()/magnificationFactor)
                                                    /((double)this.parent.getSimulationTime()/magnificationFactor)))+" (mW)");
            averagePendingTime.setText(""+math.changeDecimalFormatFor5(this.parent.getDataReader().getDataSetting().getTaskSet().getAveragePendingTimeOfTask()));
            averageResponseTime.setText(""+math.changeDecimalFormatFor5(this.parent.getDataReader().getDataSetting().getTaskSet().getAverageResponseTimeOfTask()));
        }
        catch (Exception ex) 
        {
            Logger.getLogger(SimulationResultPopupWin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void changeVisible()
    {
            this.setVisible(!this.isVisible());
    }
    
}
