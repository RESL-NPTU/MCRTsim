/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//加兩個按鈕在這裡
package workloadgenerator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import userInterface.frontEnd.SimulationViewer;
import java.awt.event.ItemEvent;  
import java.awt.event.ItemListener;  

/**
 *
 * @author YC
 */
public class WorkloadGenerator extends JFrame
{
    public SimulationViewer parent;
    private wgWorkload workload;
    public  int accuracy = 100;
    public  int exportAccuracy = 100;
    private JTextField utilization;
    private JTextField MinNumOftask, MaxNumOftask;
    private JTextField Minperiod , Maxperiod;
    private JTextField MincomputationTime , MaxcomputationTime;
    private JTextField MinNumOfresources, MaxNumOfresources;
    private JTextField MinNumOfaccessedResources, MaxNumOfaccessedResources;
    private JTextField csr;
    private JTextField frequency;
    private ButtonGroup unit;
    private JRadioButton msRB,sRB;
    private JButton creatBtn;
    private JCheckBox jCB;   
    
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
                        workload = new wgWorkload(WorkloadGenerator.this);
                        try 
                        {
                            workload.setUtilization(Double.valueOf(utilization.getText()));
                            workload.setTaskPeriodMin(Double.valueOf(Minperiod.getText()) * accuracy);
                            workload.setTaskPeriodMax(Double.valueOf(Maxperiod.getText()) * accuracy);
                            workload.setTaskComputationTimeMin(Double.valueOf(MincomputationTime.getText()) * accuracy);
                            workload.setTaskComputationTimeMax(Double.valueOf(MaxcomputationTime.getText()) * accuracy);
                            workload.setTaskNumberMin(Integer.valueOf(MinNumOftask.getText()));
                            workload.setTaskNumberMax(Integer.valueOf(MaxNumOftask.getText()));
                            workload.setTaskNumber();
                            workload.setResourcesNumbermin(Integer.valueOf(MinNumOfresources.getText()));
                            workload.setResourcesNumbermax(Integer.valueOf(MaxNumOfresources.getText()));
                            workload.setResourcesNumber();
                            workload.setAccessedResourceNumberMax(Integer.valueOf(MaxNumOfaccessedResources.getText()));
                            workload.setAccessedResourceNumberMin(Integer.valueOf(MinNumOfaccessedResources.getText()));
                            workload.setCriticalSectionRatio(Double.valueOf(csr.getText()));
                            workload.setFrequency(Integer.valueOf(frequency.getText()));
                            workload.showInitInfo();
                            workload.creatResources();
                            workload.creatTask();
                            workload.creatCriticalSection();
                            workload.showInfo();
                            
                                               /*0,1,2*/
                            Object[] options ={ "Save","Again","Cancel"};  
                            int option = JOptionPane.showOptionDialog(null, new JLabel(workload.checkQuality()), "Workload Quality",JOptionPane.YES_NO_CANCEL_OPTION,
                                                JOptionPane.QUESTION_MESSAGE, null, options, options[0]); 
//                   int   option =1;//迴圈測試
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
                                    
                                    
                                    Transformer transformer = TransformerFactory.newInstance().newTransformer();
                                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                                    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                                    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); 

                                    DOMSource source = new DOMSource(XMLWriter.creatXML(workload));
                                    File file = new File(fileDialog.getDirectory()+fileDialog.getFile()+".xml");
                                    StreamResult result = new StreamResult(file);
                                    transformer.transform(source, result);
                                    WorkloadGenerator.this.parent.getSourceTextField().setText(file.getAbsolutePath());
                                    
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
                            System.out.println("File saved!");
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
        this.setBounds(100, 100, 600, 280);
        this.setMinimumSize(new Dimension(600, 280));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
        this.setLayout(new GridBagLayout());
        
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
        csr = new JTextField();
        frequency = new JTextField();
        creatBtn = new JButton("Creat");      
        sRB = new JRadioButton(" s");
        sRB.setSelected(true);
        msRB = new JRadioButton(" ms");
        unit = new ButtonGroup();
        unit.add(msRB);
        unit.add(sRB);
        jCB = new JCheckBox("ExtraTask",false);
       
        sRB.addItemListener
        (
            new ItemListener()
            {
                public void itemStateChanged(ItemEvent e) 
                {
                    if (e.getStateChange() == ItemEvent.SELECTED) 
                    {
//                        double mp = Double.parseDouble(Minperiod.getText());
//                        Minperiod.setText(String.valueOf(mp/1000));
//                        double Mp = Double.parseDouble(Maxperiod.getText());
//                        Maxperiod.setText(String.valueOf(Mp/1000));
//                        double mc = Double.parseDouble(MincomputationTime.getText());
//                        MincomputationTime.setText(String.valueOf(mc/1000));
//                        double Mc = Double.parseDouble(MaxcomputationTime.getText());
//                        MaxcomputationTime.setText(String.valueOf(Mc/1000));
//                        
//                        WorkloadGenerator.this.accuracy = 1000;
                        
                    }
                    else if (e.getStateChange() == ItemEvent.DESELECTED) 
                    {
                        // Your deselected code here.
                    }
                }
            }
        );
        
        msRB.addItemListener
        (
            new ItemListener()
            {
                public void itemStateChanged(ItemEvent e) 
                {
                    if (e.getStateChange() == ItemEvent.SELECTED) 
                    {
//                        double mp = Double.parseDouble(Minperiod.getText());
//                        Minperiod.setText(String.valueOf(mp*1000));
//                        double Mp = Double.parseDouble(Maxperiod.getText());
//                        Maxperiod.setText(String.valueOf(Mp*1000));
//                        double mc = Double.parseDouble(MincomputationTime.getText());
//                        MincomputationTime.setText(String.valueOf(mc*1000));
//                        double Mc = Double.parseDouble(MaxcomputationTime.getText());
//                        MaxcomputationTime.setText(String.valueOf(Mc*1000));
                        
                        WorkloadGenerator.this.accuracy = 1;
                    }
                    else if (e.getStateChange() == ItemEvent.DESELECTED) 
                    {
                        // Your deselected code here.
                    }
                }
            }
        );
        
        GridBagConstraints bag = new GridBagConstraints();
        bag.anchor=GridBagConstraints.WEST;        
//        bag.gridx = 0;
//        bag.gridy = 0;          
//        this.add(sRB,bag);       
//        bag.gridx = 0;
//        bag.gridy = 1;     
//        this.add(msRB,bag);
        
        
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
        
        String[] str  ={"Utilization","The Number of Task" ,"Period","Computation Time","The Number of Resources"
                        ,"The Number of Accessed Resources","Critical Section Ratio","Base Speed"};
        
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
        this.add(jCB,bag);
        
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
        
        bag.gridx = 2;
        bag.gridy = 7;
        this.add(csr, bag);
        
        bag.gridx = 2;
        bag.gridy = 8;
        this.add(frequency,bag);
        
        
        this.utilization.setText("1.0");
        this.MinNumOftask.setText("4");
        this.MaxNumOftask.setText("7");
        this.Minperiod.setText("4");
        this.Maxperiod.setText("10.0");
        this.MincomputationTime.setText("1.0");
        this.MaxcomputationTime.setText("5.0");
        this.MinNumOfresources.setText("2");
        this.MaxNumOfresources.setText("5");
        this.MinNumOfaccessedResources.setText("1");
        this.MaxNumOfaccessedResources.setText("3");
        this.csr.setText("0.6");
        this.frequency.setText("300");
        
        bag.gridx = 2;
        bag.gridy = 9;
        creatBtn.setForeground(Color.red);
        this.add(creatBtn, bag);
    }
    
    public boolean isExtraTask()
    {
        return this.jCB.isSelected();
    }
    
}
