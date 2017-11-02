/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface.frontEnd;

import PartitionAlgorithm.PartitionAlgorithm;
import SystemEnvironment.Core;
import concurrencyControlProtocol.ConcurrencyControlProtocol;
import dynamicVoltageAndFrequencyScalingMethod.DynamicVoltageAndFrequencyScalingMethod;
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
import SystemEnvironment.DataReader;
import SystemEnvironment.Simulator;
import WorkLoad.Task;
import WorkLoadSet.DataSetting;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import mcrtsim.Definition;
import static mcrtsim.Definition.magnificationFactor;
import mcrtsim.MCRTsim;
import mcrtsim.MCRTsimMath;
import org.apache.commons.io.IOUtils;
import scriptsetter.Script;
import scriptsetter.ScriptResult;
import scriptsetter.ScriptSetter;

import userInterface.UserInterface;
import userInterface.backEnd.TimeLineResult;
import workloadgenerator.WorkloadGenerator;

/**
 *
 * @author ShiuJia
 */
public class SimulationViewer extends JPanel
{        
    public UserInterface parent;
    private JButton startBtn, exporeBtn, drawBtn;
    private JButton dataGeneratorBtn;
    private JButton scriptSetterBtn;
    private JButton readSourceFileBtn;
    private JButton readEnvironmentFileBtn;
    private JTextField workloadTextField;
    private JTextField processorTextField;
    private JTextField simTimeField;
    private JTextField accuracyField;
    private JTextField contextSwitchCost;
    private JTextField migrationCost;
    private JComboBox<String> partitionComboBox;
    private JComboBox<String> schedulingComboBox;
    private JComboBox<String> CCPComboBox;
    private JComboBox<String> DVFSComboBox;
    private JFrame popupWin;
    private ButtonGroup simTimeBG;
    private JRadioButton lcmRB, customRB;
    private DataReader dr;
    private Simulator sim;
    private ExperimentResultPopupWin totalDataPopupWin;
    private ScriptSetter scriptSetter;
    
    public SimulationViewer(UserInterface ui)
    {
        super();
        this.parent = ui;
        this.initialize();
        this.scriptSetter = new ScriptSetter(SimulationViewer.this);
        
        this.dataGeneratorBtn.addMouseListener
        (new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    new WorkloadGenerator(SimulationViewer.this);
                }
            }
        );
        
        this.scriptSetterBtn.addMouseListener
        (new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    scriptSetter.setVisible(!scriptSetter.isVisible());
                }
            }
        );
        
        this.readSourceFileBtn.addMouseListener
        (new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    FileDialog fileDialog = new FileDialog(parent.getFrame(), "new", FileDialog.LOAD);
                    fileDialog.setVisible(true);
                    if(fileDialog.getDirectory() != null)
                    {
                        workloadTextField.setText(fileDialog.getDirectory() + fileDialog.getFile());
                    }
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
                    if(fileDialog.getDirectory() != null)
                    {
                        processorTextField.setText(fileDialog.getDirectory() + fileDialog.getFile());
                    }
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
                   if(totalDataPopupWin != null)
                   {
                       totalDataPopupWin.setVisible(false);
                   }
                }
                
                public void mouseReleased(MouseEvent e)
                {
                    long time1,time2;
                    time1 = System.currentTimeMillis();
                    try
                    {
                        setMagnificationFactor(accuracyField.getText());
                        
                        dr = new DataReader();
                        dr.loadSource(workloadTextField.getText());
                        dr.loadSource(processorTextField.getText());
                        sim = new Simulator(SimulationViewer.this);
                        sim.setSimulationTime(getSimulationTime());
                        sim.setContextSwitchTime(getContextSwitchTime());
                        sim.setMigrationTime(getMigrationTime());
                        sim.loadDataSetting(dr.getDataSetting());
                        
                        sim.getProcessor().setSchedAlgorithm(getPrioritySchedulingAlgorithm(schedulingComboBox.getSelectedItem().toString()));
                        sim.getProcessor().setPartitionAlgorithm(getPartitionAlgorithm(partitionComboBox.getSelectedItem().toString()));
                        sim.getProcessor().setCCProtocol(getConcurrencyControlProtocol(CCPComboBox.getSelectedItem().toString()));
                        sim.getProcessor().setDVFSMethod(getDynamicVoltageScalingMethod(DVFSComboBox.getSelectedItem().toString()));
                        
                        dr.getDataSetting().getProcessor().showInfo();
                        System.out.println("Workload:" + dr.getDataSetting().getTaskSet().getMaxProcessingSpeed());
                        for(Task t : dr.getDataSetting().getTaskSet())
                        {
                            t.showInfo();
                        }
                        
                        sim.start();
                        
                        totalDataPopupWin = new ExperimentResultPopupWin(SimulationViewer.this);
                    }
                    catch (Exception ex) 
                    {
                        popupWin.dispose();
                        JOptionPane.showMessageDialog(parent.getFrame(), "Error!!" ,"Error!!" ,WARNING_MESSAGE);
                        Logger.getLogger(SimulationViewer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    popupWin.dispose();
                    
                    time2 = System.currentTimeMillis();
                    System.out.println("！！！！ Spend：" + (double)(time2-time1)/1000 + " second. ！！！！");
                }
            }
        );

        this.drawBtn.addMouseListener
        (new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    //SimulationViewer.this.timeLineBtn.setForeground(Color.red);
                    SimulationViewer.this.parent.getInfoWin().pressDrawButton();
                }
            }
        );

        this.exporeBtn.addMouseListener
        (new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    TimeLineResult panel = SimulationViewer.this.parent.getInfoWin().getCurCoreResult().getTimeLineResult();
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
        this.workloadTextField = new JTextField();
        
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
            toolBar2.add(this.dataGeneratorBtn);
            toolBar2.add(this.workloadTextField);
            
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
        
        JLabel taskToCoreLabel = new JLabel("PartitionAlgorithm: ");
        this.partitionComboBox = new JComboBox<String>();
        
        JLabel energyLabel = new JLabel("DVFSMethod: ");
        this.DVFSComboBox = new JComboBox<String>();
        
        JLabel schedulerLabel = new JLabel("SchedulingAlgorithm: ");
        this.schedulingComboBox = new JComboBox<String>();

        JLabel controllerLabel = new JLabel("CCProtocol: ");
        this.CCPComboBox = new JComboBox<String>();

        
        this.setComboBox();
        
        this.CCPComboBox.setSelectedItem("None");
        this.partitionComboBox.setSelectedItem("None");
        this.DVFSComboBox.setSelectedItem("None");
        
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
            configToolBar.add(this.partitionComboBox,c);
            c.gridy+=1;
            configToolBar.add(this.DVFSComboBox,c);
            c.gridy+=1;
            configToolBar.add(this.schedulingComboBox,c);
            c.gridy+=1;
            configToolBar.add(this.CCPComboBox,c);
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
        
        JToolBar costToolBar = new JToolBar();
        costToolBar.setFloatable(false);
        d.gridx = 0;
        d.gridy = 4;
        jt.add(costToolBar,d);
        
        
        JLabel contextSwitchCostLabel = new JLabel("ContextSwitchCost:");
        this.contextSwitchCost = new JTextField();
        JLabel migrationCostLabel = new JLabel("MigrationCost:");
        this.migrationCost = new JTextField();
        
        JLabel accuracyLabel = new JLabel("Accuracy:");
        this.accuracyField = new JTextField();
        
        costToolBar.setLayout(new GridBagLayout());
        {
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.WEST;
            c.weightx = 100;
            c.gridx = 0;
            c.gridy = 0;
            JToolBar toolBar1 = new JToolBar();
            toolBar1.setFloatable(false);
            toolBar1.add(contextSwitchCostLabel,c);
            toolBar1.add(this.contextSwitchCost);
            costToolBar.add(toolBar1,c);
            
            c.gridx = 0;
            c.gridy = 1;
            JToolBar toolBar2 = new JToolBar();
            toolBar2.setFloatable(false);
            toolBar2.add(migrationCostLabel,c);
            toolBar2.add(this.migrationCost);
            costToolBar.add(toolBar2,c);
            
            c.gridx = 0;
            c.gridy = 2;
            JToolBar toolBar3 = new JToolBar();
            toolBar3.setFloatable(false);
            toolBar3.add(accuracyLabel,c);
            toolBar3.add(this.accuracyField);
            costToolBar.add(toolBar3,c);
        }
        
        //---
        
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
        
        this.scriptSetterBtn = new JButton("Script");
        scheduleToolBar.add(this.scriptSetterBtn);
        
        popupWin = new JFrame("排程中...");
        popupWin.setBounds(parent.getFrame().getX()+parent.getFrame().getWidth()/2 -100,
                            parent.getFrame().getY()+parent.getFrame().getHeight()/2 -100, 100, 100);
        popupWin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popupWin.setLayout(new BorderLayout());
        popupWin.add(new JLabel("排程中..."),BorderLayout.CENTER);
        //popupWin.setVisible(true);
    }
    
    public void startScript(Script script)
    {
        try
        {
            Vector<String> workloadFileNames = this.getFolderFile(script.getWorkloadSite());
            //Vector<String> processorfileNames = this.getFolderFile(script.getProcessorSite());
            String processorFileName = script.getProcessorSite();
            
            for(String workloadFileName : workloadFileNames)
            {
                System.out.println(script.getWorkloadSite()+"/"+workloadFileName + ".xml");
            }
            
            System.out.println(processorFileName);
            
            
            int i = 0;
            
            for(String workloadFileName : workloadFileNames)
            {
                System.out.println(script.getWorkloadSite()+"/"+workloadFileName + ".xml");
                System.out.println(processorFileName);
                dr = new DataReader();
                dr.loadSource(script.getWorkloadSite()+"/"+workloadFileName + ".xml");
                dr.loadSource(processorFileName);
                sim = new Simulator(SimulationViewer.this);
                sim.setSimulationTime
                (
                    Double.valueOf
                    (
                        Double.valueOf
                        (
                            script.getSimulationTime()
                        )*magnificationFactor
                    ).longValue()
                );

                sim.loadDataSetting(dr.getDataSetting());
                sim.getProcessor().setSchedAlgorithm(getPrioritySchedulingAlgorithm(script.getSchedulingAlgorithm()));
                sim.getProcessor().setPartitionAlgorithm(getPartitionAlgorithm(script.getPartitionAlgorithm()));
                sim.getProcessor().setCCProtocol(getConcurrencyControlProtocol(script.getCCProtocol()));
                sim.getProcessor().setDVFSMethod(getDynamicVoltageScalingMethod(script.getDVFSMethod()));

                dr.getDataSetting().getProcessor().showInfo();
                System.out.println("Workload:" + dr.getDataSetting().getTaskSet().getMaxProcessingSpeed());
                for(Task t : dr.getDataSetting().getTaskSet())
                {
                    t.showInfo();
                }

                sim.start();

            //setScriptResult---------{
                {
                    ScriptResult sr = new ScriptResult(script);
                    sr.setWorkloadFile(workloadFileName);
                    sr.setProcessorFile(processorFileName);
                    
                    MCRTsimMath math = new MCRTsimMath();
                    for(Core c : sim.getProcessor().getAllCore())
                    {
                        sr.addPowerConsumption(math.changeDecimalFormat((double)c.getPowerConsumption()/magnificationFactor));
                    }

                    DataSetting ds = dr.getDataSetting();

                    sr.setTaskCount(ds.getTaskSet().size());
                    sr.setTotalJobCompeletedCount(ds.getTaskSet().getTotalJobCompletedNumber());
                    sr.setTotalJobMissDeadlineCount(ds.getTaskSet().getTotalJobMissDeadlineNumber());
                    sr.setTotalPendingTime(ds.getTaskSet().getTotalJobPendingTime());
                    sr.setTotalResponseTime(ds.getTaskSet().getTotalJobResponseTime());

                    script.addScriptResult(sr);
                }
            //------------------------}
                
                System.out.println("~~~~~~~ "+(++i)+" ~~~~~~~");
                System.out.println("");
            }
        }
        catch (Exception ex) 
        {
            popupWin.dispose();
            JOptionPane.showMessageDialog(parent.getFrame(), "Error!!" ,"Error!!" ,WARNING_MESSAGE);
            Logger.getLogger(SimulationViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setComboBox()
    {
        
        String path = MCRTsim.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        System.out.println("" + MCRTsim.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        //IDE : /Users/YC/Documents/LabResearch/MCRTsim/MCRTsimV2/MCRTsim/build/classes/
        //jar : /Users/YC/Documents/LabResearch/MCRTsim/MCRTsimV2/MCRTsim/dist/MCRTsim2.0.jar
        
        
        InputStream is = MCRTsim.class.getClass().getResourceAsStream("/AlgorithmName/algorithmName");
        
        if(path.contains("jar"))//路徑包含jar代表是執行jar檔的情況
        {
            try 
            {
                String fileContent = IOUtils.toString(is, "UTF-8");
                String[] algorithmType = fileContent.split("\r|\n");
                //System.out.println(name[0]);

                for(int i = 0 ; i<algorithmType.length ; i++)
                {
                    String[] algorithmName = algorithmType[i].split(",");
                    for(int j = 1; j<algorithmName.length ; j++)
                    {
                        switch(algorithmName[0])
                        {
                            case "PartitionAlgorithm":
                                this.partitionComboBox.addItem(algorithmName[j]);
                            break;

                            case "DVFSMethod":
                                this.DVFSComboBox.addItem(algorithmName[j]);
                            break;

                            case "SchedulingAlgorithm":
                                this.schedulingComboBox.addItem(algorithmName[j]);
                            break;

                            case "CCProtocol":
                                this.CCPComboBox.addItem(algorithmName[j]);
                            break;
                            default:
                        }
                    }
                }

            } 
            catch (IOException ex) 
            {
                Logger.getLogger(MCRTsim.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            try 
            {
                String dirPath = System.getProperties().getProperty("user.dir");
                                                    
                                                          //資料夾路徑                      創建的檔名
                PrintStream out = new PrintStream(new File(dirPath+"/src/AlgorithmName/"+"algorithmName"));
                String str = "";
                
                Vector<String> fileName = new Vector<String>();
                
                str += "PartitionAlgorithm";
                fileName = this.getFolderFile(dirPath+"/src/PartitionAlgorithm/implementation");
                for(int i = 0; i < fileName.size(); i++)
                {
                    str += ","+fileName.get(i);
                    this.partitionComboBox.addItem(fileName.get(i));
                }
                out.println(str);
                str="";
                
                str += "DVFSMethod";
                fileName = this.getFolderFile(dirPath+"/src/dynamicVoltageAndFrequencyScalingMethod/implementation");
                for(int i = 0; i < fileName.size(); i++)
                {
                    str += ","+fileName.get(i);
                    this.DVFSComboBox.addItem(fileName.get(i));
                }
                out.println(str);
                str="";

                str += "SchedulingAlgorithm";
                fileName = this.getFolderFile(dirPath+"/src/schedulingAlgorithm/implementation");
                for(int i = 0; i < fileName.size(); i++)
                {
                    str += ","+fileName.get(i);
                    this.schedulingComboBox.addItem(fileName.get(i));
                }  
                out.println(str);
                str="";

                str += "CCProtocol";
                fileName = this.getFolderFile(dirPath+"/src/concurrencyControlProtocol/implementation");
                for(int i = 0; i < fileName.size(); i++)
                {
                    str += ","+fileName.get(i);
                    this.CCPComboBox.addItem(fileName.get(i));
                }
                out.println(str);
                str="";

                out.close();
            }
            catch (FileNotFoundException ex) 
            {
                Logger.getLogger(SimulationViewer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
            String name = list[i].split("\\.")[0];
            
            if(name != null && !name.contains("$") && !name.isEmpty())
            {
                fileName.add(name);
            }
        }
        return fileName;
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
                        )*magnificationFactor
                    ).longValue();
        }
        return 0;
    }
    
    private long getContextSwitchTime()
    {
        MCRTsimMath math = new MCRTsimMath();
        
        double time = 0;
        try
        {
            time = Double.valueOf(this.contextSwitchCost.getText());
            if(time < 0)
            {
                time = 0;
            }
        }
        catch (Exception ex) 
        {
        }
        return (long)(time*magnificationFactor);
    }
    
    private long getMigrationTime()
    {
        MCRTsimMath math = new MCRTsimMath();
        double time = 0;
        try
        {
            time = Double.valueOf(this.migrationCost.getText());
            if(time < 0)
            {
                time = 0;
            }
        }
        catch (Exception ex) 
        {
        }
        
        return (long)(time * magnificationFactor);
    }
    
    public Simulator getSimulator()
    {
        return this.sim;
    }
    
    public JTextField getSourceTextField()
    {
        return this.workloadTextField;
    }
    
    public ExperimentResultPopupWin getTotalDataPopupWin()
    {
        return this.totalDataPopupWin;
    }
    
    public JComboBox<String> getPartitionComboBox()
    {
        return this.partitionComboBox;
    }
    public JComboBox<String> getSchedulingComboBox()
    {
        return this.schedulingComboBox;
    }
    public JComboBox<String> getCCPComboBox()
    {
        return this.CCPComboBox;
    }
    public JComboBox<String> getDVFSComboBox()
    {
        return this.DVFSComboBox;
    }
    
    private void setMagnificationFactor(String str)
    {
        String s = "##";

        int magnificationFactor = 5;
        
        try
        {
            magnificationFactor = Integer.valueOf(str);
            if(magnificationFactor < 0)
            {
                magnificationFactor = 5;
            }
        }
        catch (Exception ex) 
        {
            
        }
    
        
        Definition.magnificationFactor = (long)Math.pow(10, magnificationFactor);

        s += ".";
        for(int i = 0 ; i < magnificationFactor ; i++)
        {
            s += "0";
        }
        Definition.magnificationFormat = s;
    }
}
