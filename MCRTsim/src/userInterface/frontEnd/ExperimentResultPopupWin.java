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
public class ExperimentResultPopupWin extends JFrame
{
    public SimulationViewer parent;
    
    private Vector<JLabel> data;
    String[] str ={"Simulation Time : ","The Number Of Core : ","The Number Of Task : ","The Number Of Resource : ","Total Utilization Of Task : ","Patition Method : "
        ,"DVFS Method : ","Schduling Algorithm : ","Concurrency Control Protocol : ","The Number Of Total Job Compeleted : "
        ,"The Number Of Total Job MissDeadline : ","Total Power Consumption : ", "Total Average Power Consumption : "
        ,"Total Pending Time : ","Total Average Pending Time : ","Total Response Time : ","Total Average Response Time : "};
    private JLabel simulationTime,coreNumber,taskNumber,resourceNumber,totalUtilization,patitionMethod,DVFSMethod,schdulingAlgorithm
                ,concurrencyControlProtocol,totalJobCompeletedNumber,totalJobMissDeadlineNumber,totalPowerConsumption
                ,totalAveragePowerConsumption,totalPendingTime,totalAveragePendingTime,totalResponseTime,totalAverageResponseTime;
    
    
    public ExperimentResultPopupWin(SimulationViewer sv)
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
        coreNumber = new JLabel();
        taskNumber = new JLabel();
        resourceNumber = new JLabel();
        totalUtilization = new JLabel();
        patitionMethod = new JLabel();
        DVFSMethod = new JLabel();
        schdulingAlgorithm = new JLabel();
        concurrencyControlProtocol = new JLabel();
        totalJobCompeletedNumber = new JLabel();
        totalJobMissDeadlineNumber = new JLabel();
        totalPowerConsumption = new JLabel();
        totalAveragePowerConsumption = new JLabel();
        totalPendingTime = new JLabel();
        totalAveragePendingTime = new JLabel();
        totalResponseTime = new JLabel();
        totalAverageResponseTime = new JLabel();
        
        data.add(simulationTime);
        data.add(coreNumber);
        data.add(taskNumber);
        data.add(resourceNumber);
        data.add(totalUtilization);
        data.add(patitionMethod);
        data.add(DVFSMethod);
        data.add(schdulingAlgorithm);
        data.add(concurrencyControlProtocol);
        data.add(totalJobCompeletedNumber);
        data.add(totalJobMissDeadlineNumber);
        data.add(totalPowerConsumption);
        data.add(totalAveragePowerConsumption);
        data.add(totalPendingTime);
        data.add(totalAveragePendingTime);
        data.add(totalResponseTime);
        data.add(totalAverageResponseTime);
        
        this.setTitle("Experiment Result");
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
                    ExperimentResultPopupWin.this.setVisible(false);
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
        
            simulationTime.setText(""+math.changeDecimalFormat(((double)this.parent.getSimulationTime()/magnificationFactor)) + " (time)");
            coreNumber.setText(""+this.parent.getDataReader().getDataSetting().getProcessor().getAllCore().size());
            taskNumber.setText(""+ts.size());
            resourceNumber.setText(""+this.parent.getDataReader().getDataSetting().getSharedResourceSet().size());
//            totalUtilization.setText("" + Double.parseDouble(df.format(ts.getTotalUtilization())));
            patitionMethod.setText(""+this.parent.getPartitionComboBox().getSelectedItem().toString());
            DVFSMethod.setText(""+this.parent.getDVFSComboBox().getSelectedItem().toString());
            schdulingAlgorithm.setText(""+this.parent.getSchedulingComboBox().getSelectedItem().toString());
            concurrencyControlProtocol.setText(""+this.parent.getCCPComboBox().getSelectedItem().toString());
            
            totalJobCompeletedNumber.setText(""+ts.getTotalJobCompletedNumber()+"/"+ts.getTotalJobNumber());
            totalJobMissDeadlineNumber.setText(""+ts.getTotalJobMissDeadlineNumber()+"/"+ts.getTotalJobNumber());
            totalPowerConsumption.setText(""+math.changeDecimalFormat(this.parent.getDataReader().getDataSetting().getProcessor().getTotalPowerConsumption()/magnificationFactor) + " (mW)");
            totalAveragePowerConsumption.setText(""+math.changeDecimalFormat(((this.parent.getDataReader().getDataSetting().getProcessor().getTotalPowerConsumption()/magnificationFactor)
                                                    /((double)this.parent.getSimulationTime()/magnificationFactor)))+" (mW/time)");
            totalPendingTime.setText(""+math.changeDecimalFormat(this.parent.getDataReader().getDataSetting().getTaskSet().getTotalJobPendingTime())+" (time)");
            totalAveragePendingTime.setText(""+math.changeDecimalFormat(this.parent.getDataReader().getDataSetting().getTaskSet().getTotalAverageJobPendingTime())+" (time/Job)");
            totalResponseTime.setText(""+math.changeDecimalFormat(this.parent.getDataReader().getDataSetting().getTaskSet().getTotalJobResponseTime())+" (time)");
            totalAverageResponseTime.setText(""+math.changeDecimalFormat(this.parent.getDataReader().getDataSetting().getTaskSet().getTotalAverageJobResponseTime())+" (time/Job)");
        }
        catch (Exception ex) 
        {
            Logger.getLogger(ExperimentResultPopupWin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void changeVisible()
    {
            this.setVisible(!this.isVisible());
    }
    
}
