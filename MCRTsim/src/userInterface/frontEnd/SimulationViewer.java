/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface.frontEnd;

import concurrencyControlProtocol.ConcurrencyControlProtocol;
import dynamicVoltageAndFrequencyScaling.DynamicVoltageAndFrequencyScalingMethod;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import schedulingAlgorithm.PriorityDrivenSchedulingAlgorithm;
import simulation.DataReader;
import simulation.DataSetting;
import simulation.Simulator;
import taskToCore.TaskToCore;
import userInterface.UserInterface;
import userInterface.backEnd.TimeLineResult;

/**
 *
 * @author ShiuJia
 */
public class SimulationViewer extends JPanel
{        
    public UserInterface parent;
    private JButton startBtn, exporeBtn, drawBtn;
    private JButton dataGeneratorBtn;
    private JButton readSourceFileBtn;
    private JButton readEnvironmentFileBtn;
    private JTextField sourceTextField;
    private JTextField processorTextField;
    private JTextField simTimeField;
    private JComboBox<String> taskComboBox;
    private JComboBox<String> schedulingComboBox;
    private JComboBox<String> controlComboBox;
    private JComboBox<String> energyComboBox;
    private JFrame popupWin;
    private ButtonGroup simTimeBG;
    private JRadioButton lcmRB, customRB;
    private DataReader dr;
    
    public SimulationViewer(UserInterface ui)
    {
        super();
        this.parent = ui;
        this.initialize();
        
        this.dataGeneratorBtn.addMouseListener
        (
            new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    sourceTextField.setText("Use DataGenerator");
                }
            }
        );
        
        this.readSourceFileBtn.addMouseListener
        (
            new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    FileDialog fileDialog = new FileDialog(parent.getFrame(), "new", FileDialog.LOAD);
                    fileDialog.setVisible(true);
                    sourceTextField.setText(fileDialog.getDirectory() + fileDialog.getFile());
                }
            }
        );
        
        this.readEnvironmentFileBtn.addMouseListener
        (new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    FileDialog fileDialog = new FileDialog(parent.getFrame(), "new", FileDialog.LOAD);
                    fileDialog.setVisible(true);
                    processorTextField.setText(fileDialog.getDirectory() + fileDialog.getFile());
                }
            }
        ); 
        
        this.lcmRB.addItemListener
        (
            new ItemListener()
            {
                @Override
                public void itemStateChanged(ItemEvent e)
                {
                    simTimeField.setEditable(false);
                }
            }
        );
        
        this.customRB.addItemListener
        (
            new ItemListener()
            {
                @Override
                public void itemStateChanged(ItemEvent e)
                {
                    simTimeField.setEditable(true);
                }
            }
        );
        
        this.startBtn.addMouseListener
        (new MouseAdapter()
            {
                public void mousePressed(MouseEvent e)
                {
                   popupWin.setVisible(true);
                }
                
                public void mouseReleased(MouseEvent e)
                {
                    
                    try
                    {
                        dr = new DataReader();
                        dr.read(sourceTextField.getText());
                        dr.read(processorTextField.getText());
                        Simulator sim = new Simulator(SimulationViewer.this);
                        sim.setSimulationTime(getSimulationTime());
                        sim.loadDataSetting(dr.getDataSetting()); 
                        
                        if(!dr.getDataSetting().getProcessor().isGlobalScheduling)
                        {
                            sim.setTaskToCore(getTaskToCoreMethod());
                        }
                        else
                        {
                            sim.setTaskToProcessor();
                        }
                      
                        sim.setSchedAlgorithm(getDynamicPrioritySchedulingAlgorithm());
                        sim.setCCProtocol(getConcurrencyControlProtocol());
                        sim.setDVFSMethod(getDynamicVoltageScalingMethod());
                        sim.setBlockTimeOfTasks();
                        JOptionPane.showMessageDialog(SimulationViewer.this, sim.showBlockTimeOfTasks());
                        popupWin.setVisible(true);
                        sim.start();
                    }
                    catch (Exception ex) 
                    {
                        popupWin.dispose();
                        JOptionPane.showMessageDialog(parent.getFrame(), "Error!!" ,"Error!!" ,WARNING_MESSAGE);
                        Logger.getLogger(SimulationViewer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    popupWin.dispose();
                }
            }
        );

        this.drawBtn.addMouseListener
        (
            new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    SimulationViewer.this.parent.getInfoWin().pressDrawButton();
                }
            }
        );

        this.exporeBtn.addMouseListener
        (new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    TimeLineResult panel = SimulationViewer.this.parent.getInfoWin().getCurCoreResult().getResultViewer();
                    BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
                    panel.paint(image.createGraphics());
                    
                    FileDialog fileDialog = new FileDialog(parent.getFrame(), "new", FileDialog.SAVE);
                    fileDialog.setVisible(true);
                    try 
                    {
                        ImageIO.write(image,"jpg", new File(fileDialog.getDirectory()+fileDialog.getFile()+".jpg"));
                    } 
                    catch (IOException ex) 
                    {
                        Logger.getLogger(SimulationViewer.class.getName()).log(Level.SEVERE, null, ex);
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
        
        JToolBar dataToolBar = new JToolBar();
        dataToolBar.setFloatable(false);
        JLabel dataLabel = new JLabel("Workload: ");
        this.dataGeneratorBtn = new JButton("DataGenerator");
        this.readSourceFileBtn = new JButton("Open File...");
        this.sourceTextField = new JTextField();
        
        jt.add(dataToolBar,d);
        dataToolBar.setLayout(new GridBagLayout());
        {
            GridBagConstraints c = new GridBagConstraints();
            c.weightx = 0.5;
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
            toolBar2.add(this.sourceTextField);
            
            dataToolBar.add(toolBar2,c);
        }
        //----
        JToolBar processorToolBar = new JToolBar();
        processorToolBar.setFloatable(false);
        JLabel environmentLabel = new JLabel("Processor： ");
        this.readEnvironmentFileBtn = new JButton("Open File...");
        this.processorTextField = new JTextField();
        
        d.gridx = 0;
        d.gridy = 1;
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
        JToolBar configToolBar = new JToolBar();
        configToolBar.setFloatable(false);
        d.gridx = 0;
        d.gridy = 2;
        jt.add(configToolBar,d);
        
        JLabel taskToCoreLabel = new JLabel("TaskToCore: ");
        this.taskComboBox = new JComboBox<String>();
        Vector<String> fileName = new Vector<String>();
        fileName = this.getFolderFile("./src/taskToCore/implementation");
        for(int i = 0; i < fileName.size(); i++)
        {
            this.taskComboBox.addItem(fileName.get(i));
        }
        
        
        JLabel energyLabel = new JLabel("DVFSMethod: ");
        this.energyComboBox = new JComboBox<String>();
        fileName = this.getFolderFile("./src/dynamicVoltageAndFrequencyScaling/implementation");
        for(int i = 0; i < fileName.size(); i++)
        {
            this.energyComboBox.addItem(fileName.get(i));
        }
        
        
        JLabel schedulerLabel = new JLabel("SchedAlgorithm: ");
        this.schedulingComboBox = new JComboBox<String>();
        fileName = this.getFolderFile("./src/schedulingAlgorithm/implementation");
        for(int i = 0; i < fileName.size(); i++)
        {
            this.schedulingComboBox.addItem(fileName.get(i));
        }
        
        
        JLabel controllerLabel = new JLabel("CCProtocol: ");
        this.controlComboBox = new JComboBox<String>();
        fileName = this.getFolderFile("./src/concurrencyControlProtocol/implementation");
        for(int i = 0; i < fileName.size(); i++)
        {
            this.controlComboBox.addItem(fileName.get(i));
        }
        
        configToolBar.setLayout(new GridBagLayout());
        {
            GridBagConstraints c = new GridBagConstraints();
            c.weightx = 0.5;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            configToolBar.add(taskToCoreLabel,c);
            c.gridy+=1;
            configToolBar.add(energyLabel,c);
            c.gridy+=1;
            configToolBar.add(schedulerLabel,c);
            c.gridy+=1;
            configToolBar.add(controllerLabel,c);
            c.gridx = 1;
            c.gridy = 0;
            configToolBar.add(this.taskComboBox,c);
            c.gridy+=1;
            configToolBar.add(this.energyComboBox,c);
            c.gridy+=1;
            configToolBar.add(this.schedulingComboBox,c);
            c.gridy+=1;
            configToolBar.add(this.controlComboBox,c);
        }
        //-----
        JToolBar simTimeToolBar = new JToolBar();
        simTimeToolBar.setFloatable(false);
        JLabel simTimeLabel = new JLabel("SimulationTime: ");
        this.lcmRB = new JRadioButton();
        this.lcmRB.setText("Lcm of Period for TaskSet");
        this.lcmRB.setSelected(true);
        this.customRB = new JRadioButton();
        this.customRB.setText("Custom Time");
        this.simTimeBG = new ButtonGroup();
        this.simTimeBG.add(this.lcmRB);
        this.simTimeBG.add(this.customRB);
        this.simTimeField = new JTextField();
        this.simTimeField.setEditable(false);

        
        simTimeToolBar.setLayout(new GridBagLayout());
        {
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.WEST;
            c.weightx = 100;
            c.gridx = 0;
            c.gridy = 0;
            
            simTimeToolBar.add(simTimeLabel,c);
            c.gridx = 0;
            c.gridy = 1;
            JToolBar toolBar1 = new JToolBar();
            toolBar1.add(this.lcmRB);
            toolBar1.setFloatable(false);
            simTimeToolBar.add(toolBar1,c);
            
            c.gridx = 0;
            c.gridy = 2;
            JToolBar toolBar2 = new JToolBar();
            toolBar2.setFloatable(false);
            toolBar2.add(this.customRB);
            toolBar2.add(this.simTimeField);
            
            simTimeToolBar.add(toolBar2,c);
        }
        
        d.gridx = 0;
        d.gridy = 3;
        jt.add(simTimeToolBar,d);
        
        //-----
        
        JToolBar scheduleToolBar = new JToolBar();
        scheduleToolBar.setLayout(new GridLayout(1,3));
        
        this.add(scheduleToolBar,BorderLayout.SOUTH);

        this.startBtn = new JButton("Start");
        scheduleToolBar.add(this.startBtn);
        this.startBtn.setForeground(Color.red);

        this.drawBtn = new JButton("Draw");
        scheduleToolBar.add(this.drawBtn);
        this.drawBtn.setForeground(Color.BLUE);

        this.exporeBtn = new JButton("Expore");
        scheduleToolBar.add(this.exporeBtn);
        
        popupWin = new JFrame("排程中...");
        popupWin.setBounds(parent.getFrame().getX()+parent.getFrame().getWidth()/2 -100,
                            parent.getFrame().getY()+parent.getFrame().getHeight()/2 -100, 100, 100);
        popupWin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popupWin.setLayout(new BorderLayout());
        popupWin.add(new JLabel("排程中..."),BorderLayout.CENTER);
        //popupWin.setVisible(true);
    }
    
    public DataReader getDataReader()
    {
        return this.dr;
    }
    
    public Vector<String> getFolderFile(String path)
    {
        Vector<String> fileName = new Vector<String>();
        File folder = new File(path);
        String[] list = folder.list();
        for(int i = 0; i < list.length; i++)
        {
            fileName.add(list[i].split("\\.")[0]);
        }
        return fileName;
    }
    
    public TaskToCore getTaskToCoreMethod() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException
    {
        return (TaskToCore)Class.forName("taskToCore.implementation." + this.taskComboBox.getSelectedItem().toString()).getConstructor(DataSetting.class).newInstance(dr.getDataSetting());                
    }
    
    public PriorityDrivenSchedulingAlgorithm getDynamicPrioritySchedulingAlgorithm() throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        return (PriorityDrivenSchedulingAlgorithm)Class.forName("schedulingAlgorithm.implementation." + this.schedulingComboBox.getSelectedItem().toString()).newInstance();                
    }
    
    public DynamicVoltageAndFrequencyScalingMethod getDynamicVoltageScalingMethod() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException
    {
        return (DynamicVoltageAndFrequencyScalingMethod)Class.forName("dynamicVoltageAndFrequencyScaling.implementation." + this.energyComboBox.getSelectedItem().toString()).newInstance();
    }
    
    public ConcurrencyControlProtocol getConcurrencyControlProtocol() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException
    {
        return (ConcurrencyControlProtocol)Class.forName("concurrencyControlProtocol.implementation." + this.controlComboBox.getSelectedItem().toString()).getConstructor(DataSetting.class).newInstance(dr.getDataSetting());
    }
    
    public long getSimulationTime()
    {
        if(this.lcmRB.isSelected())
        {
            return this.dr.getDataSetting().getTaskSet().getScheduleTimeForTaskSet();
        }
        else if(this.customRB.isSelected())
        {
            return  Double.valueOf(
                        Double.valueOf(
                            this.simTimeField.getText().toString()
                        )*100000
                    ).longValue();
        }
        return 0;
    }
}
