/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scriptsetter;


import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;


/**
 *
 * @author ShiuJia
 */
public class ScriptPanel extends JPanel
{        
    public ScriptSetter parent;
    private JButton readSourceFileBtn;
    private JButton readEnvironmentFileBtn;
    private JTextField scriptIDTextField;
    private JTextField workloadTextField;
    private JTextField processorTextField;
    private JTextField simTimeField;
    private JTextField accuracyField;
    private JTextField threadAmountField;
    private JComboBox<String> partitionComboBox;
    private JComboBox<String> schedulingComboBox;
    private JComboBox<String> CCPComboBox;
    private JComboBox<String> DVFSComboBox;

    public ScriptPanel(ScriptSetter s)
    {
        super();
        this.parent = s;
        this.initialize();
      
        this.readSourceFileBtn.addMouseListener
        (
            new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    JFileChooser Directory = new JFileChooser();
                    Directory.setDialogTitle("Workload Directory");
                    Directory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    Directory.setAcceptAllFileFilterUsed(false);  
                    
                    if (Directory.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
                    {
                        workloadTextField.setText(Directory.getSelectedFile().toString());
                    }
                }
            }
        );
        
        this.readEnvironmentFileBtn.addMouseListener
        (new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    JFileChooser Directory = new JFileChooser();
                    Directory.setDialogTitle("Workload Directory");
                   // Directory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    Directory.setAcceptAllFileFilterUsed(false);  
                    
                    if (Directory.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
                    {
                        processorTextField.setText(Directory.getSelectedFile().toString());
                    }
                }
            }
        ); 
    }
    
    private void initialize()
    {
        this.setLayout(new BorderLayout());
        JToolBar jt = new JToolBar();
        jt.setFloatable(false);
        this.add(jt,BorderLayout.NORTH);
        jt.setLayout(new GridBagLayout());
        GridBagConstraints d = new GridBagConstraints();
        
        d.fill = GridBagConstraints.HORIZONTAL;
        d.weightx = 0.5;
        d.ipady = 0;
        d.gridx = 0;
        d.gridy = 0;
        
        JToolBar IDToolBar = new JToolBar();
        IDToolBar.setFloatable(false);
        JLabel IDLabel = new JLabel("ScriptID: ");
        this.scriptIDTextField = new JTextField();
        IDToolBar.add(IDLabel);
        IDToolBar.add(this.scriptIDTextField);
        jt.add(IDToolBar,d);
        
        d.gridy++;
        JToolBar dataToolBar = new JToolBar();
        dataToolBar.setFloatable(false);
        JLabel dataLabel = new JLabel("Workload: ");
        this.readSourceFileBtn = new JButton("Open File...");
        this.workloadTextField = new JTextField();
        
        jt.add(dataToolBar,d);
        dataToolBar.setLayout(new GridBagLayout());
        {
            GridBagConstraints c = new GridBagConstraints();
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.WEST;
            c.gridx = 0;
            c.gridy = 0;
            dataToolBar.add(dataLabel,c);
            c.gridx = 0;
            c.gridy = 1;
            JToolBar toolBar2 = new JToolBar();
            toolBar2.setFloatable(false);
            toolBar2.add(this.readSourceFileBtn);
            toolBar2.add(this.workloadTextField);
            
            dataToolBar.add(toolBar2,c);
        }
        //----
        JToolBar processorToolBar = new JToolBar();
        processorToolBar.setFloatable(false);
        JLabel environmentLabel = new JLabel("Processor： ");
        this.readEnvironmentFileBtn = new JButton("Open File...");
        this.processorTextField = new JTextField();
        
        d.gridy++;
        jt.add(processorToolBar,d);
        processorToolBar.setLayout(new GridBagLayout());
        {
            GridBagConstraints c = new GridBagConstraints();
            c.weightx = 0.5;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.WEST;
            c.gridx = 0;
            c.gridy = 0;
            processorToolBar.add(environmentLabel,c);
            
            c.gridx = 0;
            c.gridy = 1;
            JToolBar toolBar2 = new JToolBar();
            toolBar2.setFloatable(false);
            toolBar2.add(this.readEnvironmentFileBtn);
            toolBar2.add(this.processorTextField);
            processorToolBar.add(toolBar2,c);
        }
        
        //----
        JToolBar configToolBar1 = new JToolBar();
        JToolBar configToolBar2 = new JToolBar();
        JToolBar configToolBar3 = new JToolBar();
        JToolBar configToolBar4 = new JToolBar();
        configToolBar1.setFloatable(false);
        configToolBar2.setFloatable(false);
        configToolBar3.setFloatable(false);
        configToolBar4.setFloatable(false);
        d.gridy++;
        jt.add(configToolBar1,d);
        d.gridy++;
        jt.add(configToolBar2,d);
        d.gridy++;
        jt.add(configToolBar3,d);
        d.gridy++;
        jt.add(configToolBar4,d);
        
        
            
        Vector<String> fileName = new Vector<String>();

        JLabel partitionLabel = new JLabel("PartitionAlgorithm: ");
        this.partitionComboBox = new JComboBox<String>();
        configToolBar1.add(partitionLabel);
        configToolBar1.add(partitionComboBox);
        
        for(int i = 0; i < fileName.size(); i++)
        {
            this.partitionComboBox.addItem(fileName.get(i));
        }
        
        for(int i = 0 ; i<this.parent.parent.getPartitionComboBox().getItemCount() ; i++)
        {
            this.partitionComboBox.addItem(this.parent.parent.getPartitionComboBox().getItemAt(i));
        }

        this.partitionComboBox.setSelectedItem("None");


        JLabel DVFSLabel = new JLabel("DVFSMethod: ");
        this.DVFSComboBox = new JComboBox<String>();
        configToolBar2.add(DVFSLabel);
        configToolBar2.add(DVFSComboBox);
        
        for(int i = 0 ; i<this.parent.parent.getDVFSComboBox().getItemCount() ; i++)
        {
            this.DVFSComboBox.addItem(this.parent.parent.getDVFSComboBox().getItemAt(i));
        }
        
        
        this.DVFSComboBox.setSelectedItem("None");

        JLabel schedulerLabel = new JLabel("SchedAlgorithm: ");
        this.schedulingComboBox = new JComboBox<String>();
        configToolBar3.add(schedulerLabel);
        configToolBar3.add(schedulingComboBox);
        
        for(int i = 0 ; i<this.parent.parent.getSchedulingComboBox().getItemCount() ; i++)
        {
            this.schedulingComboBox.addItem(this.parent.parent.getSchedulingComboBox().getItemAt(i));
        }
        
        

        JLabel controllerLabel = new JLabel("CCProtocol: ");
        this.CCPComboBox = new JComboBox<String>();
        configToolBar4.add(controllerLabel);
        configToolBar4.add(CCPComboBox);
        
        for(int i = 0 ; i<this.parent.parent.getCCPComboBox().getItemCount() ; i++)
        {
            this.CCPComboBox.addItem(this.parent.parent.getCCPComboBox().getItemAt(i));
        }
        this.CCPComboBox.setSelectedItem("None");
        
        
        
        //-----
        JToolBar simTimeToolBar = new JToolBar();
        simTimeToolBar.setFloatable(false);
        JLabel simTimeLabel = new JLabel("SimulationTime: ");
        this.simTimeField = new JTextField();
        simTimeToolBar.add(simTimeLabel);
        simTimeToolBar.add(this.simTimeField);
        d.gridy ++;
        jt.add(simTimeToolBar,d);
        
        //-----
        
        
        //-----
        JToolBar accuracyToolBar = new JToolBar();
        accuracyToolBar.setFloatable(false);
        JLabel accuracyLabel = new JLabel("Accuracy: ");
        this.accuracyField = new JTextField();
        accuracyToolBar.add(accuracyLabel);
        accuracyToolBar.add(this.accuracyField);
        d.gridy ++;
        jt.add(accuracyToolBar,d);
        
        //-----
        
        
        //-----
        JToolBar threadAmountToolBar = new JToolBar();
        threadAmountToolBar.setFloatable(false);
        JLabel threadAmountLabel = new JLabel("ThreadAmount: ");
        this.threadAmountField = new JTextField();
        threadAmountToolBar.add(threadAmountLabel);
        threadAmountToolBar.add(this.threadAmountField);
        d.gridy ++;
        jt.add(threadAmountToolBar,d);
        
        //-----
        
    }
 
    public String getScriptID()
    {
        return this.scriptIDTextField.getText();
    }
    
    
    public String getWorkloadSite()
    {
        return this.workloadTextField.getText();
    }
    
    public String getProcessorSite()
    {
        return this.processorTextField.getText();
    }
    
    public String getSimulationTime()
    {
        return this.simTimeField.getText();
    }
    
    public String getAccuracy()
    {
        return this.accuracyField.getText();
    }
    
    public String getThreadAmount()
    {
        return this.threadAmountField.getText();
    }
    
    
    
    public String getPartitionMethod()
    {
        return (String)this.partitionComboBox.getSelectedItem();
    }
    
    public String getDVFSMethod()
    {
        return (String)this.DVFSComboBox.getSelectedItem();
    }
    
    public String getSchedAlorithm()
    {
        return (String)this.schedulingComboBox.getSelectedItem();
    }
   
    public String getCCProtocol()
    {
        return (String)this.CCPComboBox.getSelectedItem();
    }
        
    public JTextField getSourceTextField()
    {
        return this.workloadTextField;
    }
    
    
}
