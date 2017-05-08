/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface.frontEnd;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DecimalFormat;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import simulation.Result;
import userInterface.UserInterface;
import userInterface.backEnd.MouseTimeLine;

/**
 *
 * @author ShiuJia
 */
public class AttributeViewer extends JPanel
{
    private UserInterface parent;
    private JComboBox<MouseTimeLine> timeLineSet;
    private JToolBar toolBar;
    private JTable table;
    
    private String StartTime = "", EndTime = "", Speed = "",PowerConsumption = "", CoreStatus = "", LockedResource = "",
                    TaskID = "", JobID = "", CoreID = "", JobMissDeadlineNum="", JobCompletedNum="", AveragePowerConsumption="",
                    JobStatus="", pendingTime="",responseTime="";
    private int numAbt;

    public AttributeViewer(UserInterface ui)
    {
        super();
        parent = ui;
        init();
    }

    public void setAtbData(Result rd)
    {
        try
        {
            this.CoreID="" + rd.getCore().getID();
        }
        catch (Exception ex)
        {
            this.CoreID="Null";
        }
        
        try
        {
            this.TaskID="" + rd.getJob().getTask().getID() + " (" + (double)rd.getJob().getTask().getComputationAmount()/100000
                    + "," + (double)rd.getJob().getTask().getPeriod()/100000+")";
            this.JobMissDeadlineNum = ""+rd.getJobMissDeadlineNum();
            this.JobCompletedNum = ""+rd.getJobCompletedNum();
        }
        catch (Exception ex)
        {
            this.TaskID="Null";
            this.JobMissDeadlineNum = "Null";
            this.JobCompletedNum = "Null";
        }
        
        try
        {
            this.JobID="" + rd.getJob().getID();
            this.JobStatus=""+rd.getJob().getStatus() + " at " + rd.getJob().getTimeOfStatus() + " (s)";
            this.responseTime=""+rd.getJob().getResponseTime()/100000;
            this.pendingTime=""+rd.getJob().getPendingTime()/100000;
        }
        catch (Exception ex)
        {
            this.JobID="Null";
            this.JobStatus="Null";
            this.responseTime="Null";
            this.pendingTime="Null";
        }
        
        
        try
        {
            this.StartTime="" + rd.getStartTime()+" (s)";
            this.EndTime="" + rd.getEndTime()+" (s)";
        }
        catch (Exception ex)
        {
            this.StartTime="Null";
            this.EndTime="Null";
        }
        
        try
        {
            this.Speed="" + rd.getFrequencyOfSpeed() + "_(" + rd.getNormalizationOfSpeed() + ")";
        }
        catch (Exception ex)
        {
            this.Speed="Null";
        }
        DecimalFormat df = new DecimalFormat("##.00000");
        try
        {
            this.PowerConsumption = "" + Double.parseDouble(df.format(rd.getTotalPowerConsumption())) + " (mW)";

            this.AveragePowerConsumption = ""+Double.parseDouble(df.format(rd.getAveragePowerConsumption())) + " (mW/s)";
            this.CoreStatus="" + rd.getStatus();
        }
        catch (Exception ex)
        {
            this.PowerConsumption ="Null";
            this.AveragePowerConsumption ="Null";
            this.CoreStatus="Null";
        }
        String str = "";
        if (rd.getLockedResource().size() != 0)
        {
            for (int i = 0; i < rd.getLockedResource().size(); i++)
            {
                str = str + 'R' + rd.getLockedResource().get(i).getResources().getID() +
                        "(" + rd.getLockedResource().get(i).getResource().getID() +"/" + 
                        rd.getLockedResource().get(i).getResources().getResourcesAmount() +")" + "<br>";
            }
        }
        else
        {
            str = "Null";
        }
        this.LockedResource="<html>" + str + "<html>";
        
        table.getModel().setValueAt(this.StartTime,0,1);
        table.getModel().setValueAt(this.EndTime,1,1);
        
        table.getModel().setValueAt(this.CoreID,3,1);
        table.getModel().setValueAt(this.CoreStatus,4,1);
        table.getModel().setValueAt(this.Speed,5,1);
        table.getModel().setValueAt(this.PowerConsumption,6,1);
        table.getModel().setValueAt(this.AveragePowerConsumption,7,1);
        
        table.getModel().setValueAt(this.TaskID,9,1);
        table.getModel().setValueAt(this.JobCompletedNum,10,1);
        table.getModel().setValueAt(this.JobMissDeadlineNum,11,1);
        
        table.getModel().setValueAt(this.JobID,13,1);
        table.getModel().setValueAt(this.pendingTime,14,1);
        table.getModel().setValueAt(this.responseTime,15,1);
        table.getModel().setValueAt(this.JobStatus,16,1);
        table.getModel().setValueAt(this.LockedResource,17,1);
    }

    private void init()
    {
        this.setLayout(new BorderLayout());
        toolBar = new JToolBar();
        timeLineSet = new JComboBox<MouseTimeLine>();

        toolBar.add(new JLabel("Time:"));
        toolBar.add(timeLineSet);
        this.add(toolBar, BorderLayout.NORTH);

        
        String[] str = {"StartTime:", "EndTime:", "", "CoreID:", "CoreStatus:", "Speed:","PowerConsumption:", "AveragePowerConsumption:",
                    "", "TaskID(C,P):", "JobCompletedNum:","JobMissDeadlineNum:", "", "JobID:" ,"PendingTime:","ResponseTime:",
                    "JobStatus:", "LockedResource:"};
        
        table = new JTable(str.length, 2)
        {
            public boolean isCellEditable(int row, int col)
            {
                return false;
            }
        };

        table.setBackground(Color.white);
        table.setTableHeader(null);
        table.getColumnModel().getColumn(0).setMinWidth(170);
        table.getColumnModel().getColumn(0).setMaxWidth(170);
        table.getColumnModel().getColumn(1).setMinWidth(90);
        table.setRowHeight(30);
        table.setGridColor(Color.BLACK);

        for (numAbt = 0; numAbt < str.length; numAbt++)
        {
            table.getModel().setValueAt(str[numAbt], numAbt, 0);
        }
        
        MyCellRenderer renderer = new MyCellRenderer();
        renderer.setHorizontalAlignment(JLabel.RIGHT);
        table.setDefaultRenderer(Object.class, renderer);

        table.setRowHeight(str.length-1, 100);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(table);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(Color.getHSBColor((float)0.7,(float)0,(float)0.9));
        
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public JComboBox<MouseTimeLine> getTimeLineSet()
    {
        return this.timeLineSet;
    }
    
    public void setTimeLineSet(JComboBox<MouseTimeLine> jb)
    {
        this.toolBar.remove(this.timeLineSet);
        this.timeLineSet = jb;
        this.toolBar.add(timeLineSet);
        this.toolBar.repaint();
    }
    
    public class MyCellRenderer extends javax.swing.table.DefaultTableCellRenderer 
    {
        
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, java.lang.Object value, boolean isSelected, boolean hasFocus, int row, int column) 
        {
            final java.awt.Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, 0, column);

            Object val = table.getValueAt(row, 0);
            String sval = val.toString();
            sval = sval.replaceAll(":", "");
            if (sval == "") 
            {   
                cellComponent.setBackground(Color.white);
                cellComponent.setForeground(Color.black);
            } 
            else 
            {
                cellComponent.setBackground(new Color(230, 230, 230));
                cellComponent.setForeground(Color.black);
            }
            
            if (isSelected) 
            {
                cellComponent.setForeground(table.getSelectionForeground());
                cellComponent.setBackground(table.getSelectionBackground());
            }
            return cellComponent;
        }
    }
}
