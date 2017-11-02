/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scriptsetter;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author YC
 */
public class ScriptTable extends JPanel
{
    public ScriptSetter parent;
    private String groupID = "";
    private JTable table;
    private TableModel tableModel;
    private Vector<Script> scriptSet;
    
    public ScriptTable(ScriptSetter s)
    {
        this.parent = s;
        this.scriptSet = new Vector<>();
        this.init();
    }
    
    private void init()
    {
        this.setLayout(new GridLayout(1,1));
//        tableModel = new DefaultTableModel() 
//        { 
//            String[] str = {"ScripID","Workload","Processor","TaskToCore","DVFSMethod","SchedAlorithm","CCProtocol","SimulationTime"};
//        
//            @Override 
//            public int getColumnCount() 
//            { 
//                return str.length; 
//            } 
//
//            @Override 
//            public String getColumnName(int index) 
//            { 
//                return str[index]; 
//            } 
//            
//            @Override
//            public boolean isCellEditable(int row, int col)
//            {
//                return false;
//            }
//        };
        this.tableModel = new TableModel();
        table = new JTable(tableModel);
        
        this.add(new JScrollPane(table));
        //table.set
        table.setBackground(Color.white);
        table.setGridColor(Color.BLACK);
        
        
    }
    
    public DefaultTableModel getTableModel()
    {
        return this.tableModel;
    }
    
    public JTable getTable()
    {
        return this.table;
    }
    
    public void setGroupID(String s)
    {
        this.groupID = s;
    }
    
    public String getGroupID()
    {
        return this.groupID;
    }
    
    public int getScriptCount()
    {
       return this.scriptSet.size();
    }
    
    public Vector<Script> getScriptSet()
    {
        return this.scriptSet;
    }
    
    public void addScript(ScriptPanel scriptPanel)
    {
        int row = this.table.getSelectedRow();
        
        Script script = new Script(this,scriptPanel);
        this.scriptSet.add(script);
        this.updateTable();
        
        if(row != -1)
        {
            this.table.setRowSelectionInterval(row,row);
        }
    }
    
    public void modifyScript(ScriptPanel scriptPanel)
    {
        int row = this.table.getSelectedRow();
        if(row != -1)
        {
            this.scriptSet.get(row).modifyScript(scriptPanel);
            this.updateTable();
            this.table.setRowSelectionInterval(row,row);
        }
    }
    
    public void removeScript()
    {
        int[] row = this.table.getSelectedRows();
        
        if(row.length !=0)
        {
            for(int i = 0; i<row.length ; i++)
            {
                this.scriptSet.remove(row[0]);//重複刪除同一個位置，就可以達到刪除所選取之範圍。
            }
            
            this.updateTable();

            if(this.table.getRowCount()>0)
            {
                if(row[0] <= this.table.getRowCount()-1)
                {
                    this.table.setRowSelectionInterval(row[0],row[0]);
                }
                else
                {
                    this.table.setRowSelectionInterval(this.table.getRowCount()-1,this.table.getRowCount()-1);
                }
            }
        }
        
        System.out.println("!!!!!!!!!!");
        for(Script s : this.scriptSet)
        {
            System.out.println(s.getID());
        }
        System.out.println("!!!!!!!!!!");
        
    }
    
    public void updateTable()
    {
        this.removeAllRow();
        
        for(Script script : this.scriptSet)
        {
            this.addRow(script);
        }
    }
    
    private void removeAllRow()
    {
        while(this.tableModel.getRowCount()!=0)
        {
            this.tableModel.removeRow(0);
        }
    }
        
    private void addRow(Script script)
    {
        Object[] object = new Object[]{script.getID(),script.getWorkloadSite()
                ,script.getProcessorSite(),script.getPartitionAlgorithm(),script.getDVFSMethod(),script.getSchedulingAlgorithm()
                ,script.getCCProtocol(),script.getSimulationTime()};
        this.tableModel.addRow(object);
    }
    
    public void modifyRow(Script script)
    {
//        int row = this.table.getSelectedRow();
//        if(row != -1)
//        {
//            this.tableModel.setValueAt(script.ID(), row,0);
//            this.tableModel.setValueAt(script.getWorkloadSite(), row,1);
//            this.tableModel.setValueAt(script.getProcessorSite(), row,2);
//            this.tableModel.setValueAt(script.getTaskToCore(), row,3);
//            this.tableModel.setValueAt(script.getDVFSMethod(), row,4);
//            this.tableModel.setValueAt(script.getSchedAlorithm(), row,5);
//            this.tableModel.setValueAt(script.getCCProtocol(), row,6);
//            this.tableModel.setValueAt(script.getSimulationTime(), row,7);
//        }
//    }
//    
//    public void removeRow(int row)
//    {
//        if(row != -1)
//        {
//            this.tableModel.removeRow(row);
//            
//            if(this.table.getRowCount()>0)
//            {
//                if(row <= this.table.getRowCount()-1)
//                {
//                    this.table.setRowSelectionInterval(row,row);
//                }
//                else
//                {
//                    this.table.setRowSelectionInterval(this.table.getRowCount()-1,this.table.getRowCount()-1);
//                }
//            }
//        }
    }
}

class TableModel extends DefaultTableModel
{
    String[] str = {"ScripID","Workload","Processor","TaskToCore","DVFSMethod","SchedAlorithm","CCProtocol","SimulationTime"};

    TableModel() 
    { 

    };
        
    public int getColumnCount() 
    { 
        return str.length; 
    } 

    public String getColumnName(int index) 
    { 
        return str[index]; 
    } 

    public boolean isCellEditable(int row, int col)
    {
        return false;
    }
}
