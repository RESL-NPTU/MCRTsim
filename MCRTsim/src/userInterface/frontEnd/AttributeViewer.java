/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface.frontEnd;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableCellRenderer;
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
    
    private String StartTime = "", EndTime = "", Speed = "",PowerConsumption = "", Status = "", Resource = "",
                    TaskID = "", JobID = "", CoreID = "";
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
            //+ "SystemCeiling = " + rd.getSystemCeiling();
        }
        catch (Exception ex)
        {
            this.CoreID="Null";
        }
        
        try
        {
            this.TaskID="" + rd.getJob().getTask().getID();
            //+ "SystemCeiling = " + rd.getSystemCeiling();
        }
        catch (Exception ex)
        {
            this.TaskID="Null";
        }
        
        try
        {
            this.JobID="" + rd.getJob().getID();
            //+ "SystemCeiling = " + rd.getSystemCeiling();
        }
        catch (Exception ex)
        {
            this.JobID="Null";
        }

        this.StartTime="" + rd.getStartTime();
        this.EndTime="" + rd.getEndTime();
        try
        {
            this.Speed="" + rd.getFrequencyOfSpeed() + "_(" + rd.getNormalizationOfSpeed() + ")";
        }
        catch (Exception ex)
        {
            this.Speed="Null";
        }
        
        this.PowerConsumption = "" + rd.getTotalPowerConsumption() + " (mW)";
        
        this.Status="" + rd.getStatus();

        String str = "";
        if (rd.getLockedResource().size() != 0)
        {
            for (int i = 0; i < rd.getLockedResource().size(); i++)
            {
                str = str + 'R' + rd.getLockedResource().get(i).getResources().getID() +
                        "(" + rd.getLockedResource().get(i).getResource().getID() +"/" + 
                        rd.getLockedResource().get(i).getResources().getResourcesAmount() +")" + "<br>";
                        //+ "(" + (-rd.getLockedResource().get(i).getPriority().getValue()) + "," + (-rd.getPriorityCeiling(i)) + "," + (-rd.getPreemptibleCeiling(i)) + ")" + "<br>";
            }
        }
        else
        {
            str = "Null";
        }
        //this.table.setRowHeight(5, 30+30*(rd.getLockedResource().size()-1));
        this.Resource="<html>" + str + "<html>";
        
        table.getModel().setValueAt(this.CoreID,0,1);
        table.getModel().setValueAt(this.TaskID,1,1);
        table.getModel().setValueAt(this.JobID,2,1);
        table.getModel().setValueAt(this.StartTime,3,1);
        table.getModel().setValueAt(this.EndTime,4,1);
        table.getModel().setValueAt(this.Speed,5,1);
        table.getModel().setValueAt(this.PowerConsumption,6,1);
        table.getModel().setValueAt(this.Status,7,1);
        table.getModel().setValueAt(this.Resource,8,1);
    }

    private void init()
    {
        this.setLayout(new BorderLayout());
        toolBar = new JToolBar();
        //timeLineSet = new JComboBox<Double>();
        timeLineSet = new JComboBox<MouseTimeLine>();

        toolBar.add(new JLabel("Time:"));
        toolBar.add(timeLineSet);
        this.add(toolBar, BorderLayout.NORTH);

        String[] str = {"CoreID:", "TaskID:", "JobID", "StartTime:", "EndTime:", "Speed:","PowerConsumption:",
                        "Status:", "Resource:"};
        
        table = new JTable(str.length, 2)
        {
            public boolean isCellEditable(int row, int col)
            {
                return false;
            }
        };

        table.setBackground(Color.white);
        table.setTableHeader(null);
        table.getColumnModel().getColumn(0).setMinWidth(115);
        table.getColumnModel().getColumn(0).setMaxWidth(115);
        table.getColumnModel().getColumn(1).setMinWidth(90);
        table.setRowHeight(30);
        table.setGridColor(Color.BLACK);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.RIGHT);
        renderer.setBackground(Color.LIGHT_GRAY);
        table.getColumnModel().getColumn(0).setCellRenderer(renderer);
        
        for (numAbt = 0; numAbt < str.length; numAbt++)
        {
            table.getModel().setValueAt(str[numAbt], numAbt, 0);
        }
        
        table.setRowHeight(str.length-1, 100);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(table);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(Color.getHSBColor((float)0.7,(float)0,(float)0.9));
        
        this.add(scrollPane, BorderLayout.CENTER);
    }

    //public JComboBox<Double> getTimeLineSet()
    public JComboBox<MouseTimeLine> getTimeLineSet()
    {
        return this.timeLineSet;
    }
    
    //public void setTimeLineSet(JComboBox<Double> jb)
    public void setTimeLineSet(JComboBox<MouseTimeLine> jb)
    {
        this.toolBar.remove(this.timeLineSet);
        this.timeLineSet = jb;
        this.toolBar.add(timeLineSet);
        this.toolBar.repaint();
    }
}
