/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface.frontEnd;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import simulation.TaskSet;

/**
 *
 * @author YC
 */
public class TotalDataPopupWin extends JFrame
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
    
    
    public TotalDataPopupWin(SimulationViewer sv)
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
        
        this.setTitle("Total Data");
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
                    TotalDataPopupWin.this.setVisible(false);
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
            TaskSet ts = this.parent.getDataReader().getDataSetting().getTaskSet();
            DecimalFormat df = new DecimalFormat("##.00000");
        
            simulationTime.setText(""+Double.parseDouble(df.format((double)this.parent.getSimulationTime()/100000)) + " (s)");
            coreNumber.setText(""+this.parent.getDataReader().getDataSetting().getProcessor().getCores().size());
            taskNumber.setText(""+ts.size());
            resourceNumber.setText(""+this.parent.getDataReader().getDataSetting().getResourceSet().size());
            totalUtilization.setText("" + Double.parseDouble(df.format(ts.getTotalUtilization())));
            patitionMethod.setText(""+this.parent.getTaskToCoreMethod().getName());
            DVFSMethod.setText(""+this.parent.getDynamicVoltageScalingMethod().getName());
            schdulingAlgorithm.setText(""+this.parent.getPrioritySchedulingAlgorithm().getName());
            concurrencyControlProtocol.setText(""+this.parent.getConcurrencyControlProtocol().getName());
            
            totalJobCompeletedNumber.setText(""+ts.getTotalJobCompletedNumber()+"/"+ts.getTotalJobNumber());
            totalJobMissDeadlineNumber.setText(""+ts.getTotalJobMissDeadlineNumber()+"/"+ts.getTotalJobNumber());
            totalPowerConsumption.setText(""+Double.parseDouble(df.format(this.parent.getDataReader().getDataSetting().getProcessor().getTotalPowerConsumption()/100000)) + " (mW)");
            totalAveragePowerConsumption.setText(""+Double.parseDouble(df.format(((this.parent.getDataReader().getDataSetting().getProcessor().getTotalPowerConsumption()/100000)
                                                    /((double)this.parent.getSimulationTime()/100000))))+" (mW/s)");
            totalPendingTime.setText(""+Double.parseDouble(df.format(this.parent.getDataReader().getDataSetting().getTaskSet().getTotalJobPendingTime())));
            totalAveragePendingTime.setText(""+Double.parseDouble(df.format(this.parent.getDataReader().getDataSetting().getTaskSet().getTotalAverageJobPendingTime())));
            totalResponseTime.setText(""+Double.parseDouble(df.format(this.parent.getDataReader().getDataSetting().getTaskSet().getTotalJobResponseTime())));
            totalAverageResponseTime.setText(""+Double.parseDouble(df.format(this.parent.getDataReader().getDataSetting().getTaskSet().getTotalAverageJobResponseTime())));
        }
        catch (Exception ex) 
        {
            Logger.getLogger(TotalDataPopupWin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void changeVisible()
    {
            this.setVisible(!this.isVisible());
    }
    
}
