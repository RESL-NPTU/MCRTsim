/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package workloadgenerator;
import static mcrtsim.MCRTsim.println;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import userInterface.frontEnd.SimulationViewer;

/**
 *
 * @author YC
 */
public class WorkloadGenerator extends JFrame
{
    public SimulationViewer parent;
    public  int taskAccuracy = 100;
    public  int criticalSectionAccuracy = 1000;
    
    private JTextField utilization;
    private JTextField MinNumOftask, MaxNumOftask;
    private JTextField Minperiod , Maxperiod;
    private JTextField MincomputationTime , MaxcomputationTime;
    private JTextField MinNumOfresources, MaxNumOfresources;
    private JTextField MinNumOfaccessedResources, MaxNumOfaccessedResources;
    private JTextField minCSR,maxCSR;
    private JTextField frequency;
    private JTextField taskAccuracyTextField;
    private JTextField criticalSectionAccuracyTextField;
    private JButton creatBtn;
    private JCheckBox jCB;
    
    private boolean repeatCreate = false;
    private JTextField numWorkload;
    
    public WorkloadGenerator(SimulationViewer p)
    {
        this.parent = p;
        this.init();
        this.revalidate();

        this.creatBtn.addMouseListener
        (new MouseAdapter()
            {
                @Override
                public void mouseClicked(MouseEvent e) 
                {
                    boolean quit = false;
                    
                    while(!quit)
                    {
                        wgWorkload workload = new wgWorkload(WorkloadGenerator.this);
                        
                        try 
                        {
                            taskAccuracy = (int) Math.pow(10,Integer.valueOf(taskAccuracyTextField.getText()));
                            criticalSectionAccuracy = (int) Math.pow(10,Integer.valueOf(criticalSectionAccuracyTextField.getText()));
                            
                            workload.setUtilization(Double.valueOf(utilization.getText()));
                            workload.setTaskPeriodMin((long)(Double.valueOf(Minperiod.getText()) * taskAccuracy));
                            workload.setTaskPeriodMax((long)(Double.valueOf(Maxperiod.getText()) * taskAccuracy));
                            workload.setTaskComputationTimeMin((long)(Double.valueOf(MincomputationTime.getText()) * taskAccuracy));
                            workload.setTaskComputationTimeMax((long)(Double.valueOf(MaxcomputationTime.getText()) * taskAccuracy));
                            workload.setTaskNumberMin(Integer.valueOf(MinNumOftask.getText()));
                            workload.setTaskNumberMax(Integer.valueOf(MaxNumOftask.getText()));
                            workload.setTaskNumber();
                            workload.setResourcesNumbermin(Integer.valueOf(MinNumOfresources.getText()));
                            workload.setResourcesNumbermax(Integer.valueOf(MaxNumOfresources.getText()));
                            workload.setResourcesNumber();
                            workload.setAccessedResourceNumberMax(Integer.valueOf(MaxNumOfaccessedResources.getText()));
                            workload.setAccessedResourceNumberMin(Integer.valueOf(MinNumOfaccessedResources.getText()));
                            workload.setMinCriticalSectionRatio(Double.valueOf(minCSR.getText()));
                            workload.setMaxCriticalSectionRatio(Double.valueOf(maxCSR.getText()));
                            workload.setFrequency(Integer.valueOf(frequency.getText()));
                            workload.showInitInfo();
                            workload.creatResources();
                            workload.creatTask();
                            workload.creatCriticalSection();
                            workload.showInfo();
                            
                            
                                               /*0,1,2*/
                            Object[] options ={ "Save","Re-generate","Cancel"};  
                            int option = JOptionPane.showOptionDialog(null, new JLabel(workload.showQuality()), "Confirmation",JOptionPane.YES_NO_CANCEL_OPTION,
                                                JOptionPane.QUESTION_MESSAGE, null, options, options[0]); 
//
//                            int option =1;
                            switch(option)
                            {
                                case 0:
                                    quit = true;
                                    FileDialog fileDialog = new FileDialog(WorkloadGenerator.this, "new", FileDialog.SAVE);
                                    fileDialog.setVisible(true);
                                    
                                    if(fileDialog.getFile() == null)//如果取消存檔則fileDialog.getFile()會是null
                                    {
                                        break;
                                    }
                                    
                                    if(repeatCreate)
                                    {
                                        for(int i=0 ; i<Integer.valueOf(numWorkload.getText()) ; i++)
                                        {
                                            if(!repeatCreateWorkload(fileDialog))
                                            {
                                                i--;
                                            }
                                        }
                                    }
                                    else
                                    {
                                        Transformer transformer = TransformerFactory.newInstance().newTransformer();
                                        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                                        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                                        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); 

                                        DOMSource source = new DOMSource(XMLWriter.creatXML(workload));
                                        File file = new File(fileDialog.getDirectory()+fileDialog.getFile()+".xml");
                                        StreamResult result = new StreamResult(file);
                                        transformer.transform(source, result);
                                        WorkloadGenerator.this.parent.getSourceTextField().setText(file.getAbsolutePath());
                                    }
                                break;
                                
                                case 1:
                                break;
                                
                                case 2:
                                    quit = true;
                                break;
                                
                                default :
                                    quit = true;
                                break;

                            }
                            workload = null;
                            println("File saved!");
                        }
                        catch (TransformerException ex) 
                        {
                            Logger.getLogger(WorkloadGenerator.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        );
    }
    
    private void init() 
    {
        this.setTitle("Workload Generator");
        this.setBounds(100, 100, 600, 400);
        this.setMinimumSize(new Dimension(600, 400));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
        this.setLayout(new GridBagLayout());
        println("");
        utilization = new JTextField();
        MinNumOftask = new JTextField();
        MaxNumOftask = new JTextField();
        Minperiod = new JTextField();
        Maxperiod = new JTextField();
        MincomputationTime = new JTextField(); 
        MaxcomputationTime = new JTextField();
        MinNumOfresources = new JTextField(); 
        MaxNumOfresources = new JTextField();
        MinNumOfaccessedResources = new JTextField();
        MaxNumOfaccessedResources = new JTextField();
        minCSR = new JTextField();
        maxCSR = new JTextField();
        frequency = new JTextField();
        creatBtn = new JButton("Create");      
        taskAccuracyTextField = new JTextField();
        criticalSectionAccuracyTextField = new JTextField();
        jCB = new JCheckBox("Extra Task",false);
       
        GridBagConstraints bag = new GridBagConstraints();
        bag.anchor=GridBagConstraints.WEST;        
        
        bag.anchor = GridBagConstraints.CENTER;  
        bag.fill = GridBagConstraints.NONE;
        bag.gridx = 1;
        bag.gridy = 0;
        bag.gridwidth = 1;
        bag.gridheight = 1;
        bag.weightx = 0;
        bag.weighty = 0;
        
        
        this.add(new JLabel("       Min       "), bag);

        bag.gridx = 2;
        bag.gridy = 0;
        this.add(new JLabel("       Max       "), bag);
        
        String[] str  ={"Utilization","The Number of Tasks" ,"Period","Computation Amount","The Number of Resources"
                        ,"The Number of Accessed Resources","Critical Section Ratio","Base Speed","Task Accuracy","Critical Section Accuracy"};
        
        bag.gridx = 0;
        bag.anchor = GridBagConstraints.EAST;
        for(int i = 0 ; i<str.length ; i++)
        {
            bag.gridy +=1;
            this.add(new JLabel(str[i]), bag);
        }
        
        bag.anchor=GridBagConstraints.WEST;
        bag.gridx = 3;
        bag.gridy = 2;     
        //this.add(jCB,bag);
        
        bag.anchor = GridBagConstraints.CENTER;
        bag.fill = GridBagConstraints.HORIZONTAL;
        bag.gridx = 2;
        bag.gridy = 1;
        this.add(utilization, bag);
        
        bag.gridx = 1;
        bag.gridy = 2;
        this.add(MinNumOftask, bag);
        
        bag.gridx = 2;
        bag.gridy = 2;
        this.add(MaxNumOftask, bag);
                
        bag.gridx = 1;
        bag.gridy = 3;
        this.add(Minperiod, bag);
        
        bag.gridx = 2;
        bag.gridy = 3;
        this.add(Maxperiod, bag);
        
        bag.gridx = 1;
        bag.gridy = 4;
        this.add(MincomputationTime, bag);
        
        bag.gridx = 2;
        bag.gridy = 4;
        this.add(MaxcomputationTime, bag);
        
        bag.gridx = 1;
        bag.gridy = 5;
        this.add(MinNumOfresources, bag);
        
        bag.gridx = 2;
        bag.gridy = 5;
        this.add(MaxNumOfresources, bag);
        
        bag.gridx = 1;
        bag.gridy = 6;
        this.add(MinNumOfaccessedResources, bag);
        
        bag.gridx = 2;
        bag.gridy = 6;
        this.add(MaxNumOfaccessedResources, bag);
        
        
        bag.gridx = 1;
        bag.gridy = 7;
        this.add(minCSR, bag);
        
        bag.gridx = 2;
        bag.gridy = 7;
        this.add(maxCSR, bag);
        
        bag.gridx = 2;
        bag.gridy = 8;
        this.add(frequency,bag);
        
        bag.gridx = 2;
        bag.gridy = 9;
        this.add(taskAccuracyTextField,bag);
        
        bag.gridx = 2;
        bag.gridy = 10;
        this.add(criticalSectionAccuracyTextField,bag);
        
        
        this.utilization.setText("0.8");
        this.MinNumOftask.setText("10");
        this.MaxNumOftask.setText("30");
        this.Minperiod.setText("0.5");
        this.Maxperiod.setText("5");
        this.MincomputationTime.setText("0.001");
        this.MaxcomputationTime.setText("0.1");
        this.MinNumOfresources.setText("5");
        this.MaxNumOfresources.setText("5");
        this.MinNumOfaccessedResources.setText("0");
        this.MaxNumOfaccessedResources.setText("5");
        this.minCSR.setText("0.4");
        this.maxCSR.setText("0.5");
        this.frequency.setText("624");
        this.taskAccuracyTextField.setText("3");
        this.criticalSectionAccuracyTextField.setText("4");
        
        
        if(repeatCreate)
        {
            bag.gridx = 1;
            bag.gridy = 11;
            this.numWorkload = new JTextField(); 
            this.add(this.numWorkload,bag);
            
            this.numWorkload.setText("10");
        }

        bag.gridx = 2;
        bag.gridy = 11;
        creatBtn.setForeground(Color.red);
        this.add(creatBtn, bag);
        
        
        
    }
    
    private boolean repeatCreateWorkload(FileDialog fileDialog)
    {
        wgWorkload workload = new wgWorkload(WorkloadGenerator.this);
                        
        try 
        {
            workload.setUtilization(Double.valueOf(utilization.getText()));
            workload.setTaskPeriodMin((long)(Double.valueOf(Minperiod.getText()) * taskAccuracy));
            workload.setTaskPeriodMax((long)(Double.valueOf(Maxperiod.getText()) * taskAccuracy));
            workload.setTaskComputationTimeMin((long)(Double.valueOf(MincomputationTime.getText()) * taskAccuracy));
            workload.setTaskComputationTimeMax((long)(Double.valueOf(MaxcomputationTime.getText()) * taskAccuracy));
            workload.setTaskNumberMin(Integer.valueOf(MinNumOftask.getText()));
            workload.setTaskNumberMax(Integer.valueOf(MaxNumOftask.getText()));
            workload.setTaskNumber();
            workload.setResourcesNumbermin(Integer.valueOf(MinNumOfresources.getText()));
            workload.setResourcesNumbermax(Integer.valueOf(MaxNumOfresources.getText()));
            workload.setResourcesNumber();
            workload.setAccessedResourceNumberMax(Integer.valueOf(MaxNumOfaccessedResources.getText()));
            workload.setAccessedResourceNumberMin(Integer.valueOf(MinNumOfaccessedResources.getText()));
            workload.setMinCriticalSectionRatio(Double.valueOf(minCSR.getText()));
            workload.setMaxCriticalSectionRatio(Double.valueOf(maxCSR.getText()));
            workload.setFrequency(Integer.valueOf(frequency.getText()));
            workload.showInitInfo();
            workload.creatResources();
            workload.creatTask();
            workload.creatCriticalSection();
            workload.showInfo();

            if(workload.checkQuality())
            {   
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); 

                DOMSource source = new DOMSource(XMLWriter.creatXML(workload));
                
                File file = new File(fileDialog.getDirectory()+fileDialog.getFile()+'1'+".xml");
                
                for (int i = 1; file.exists() ; i++) 
                {
                    file = new File(fileDialog.getDirectory(), fileDialog.getFile() + i +".xml");
                }
                
                StreamResult result = new StreamResult(file);
                transformer.transform(source, result);
                WorkloadGenerator.this.parent.getSourceTextField().setText(file.getAbsolutePath());
            
                println("File saved!");
                return true;
            }
        }
        catch (TransformerException ex) 
        {
            Logger.getLogger(WorkloadGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return false;
    }
    
    public boolean isExtraTask()
    {
        return this.jCB.isSelected();
    }
    
    
}
