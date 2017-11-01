/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface.frontEnd;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import SystemEnvironment.Core;
import WorkLoadSet.DataSetting;
import userInterface.UserInterface;
import userInterface.backEnd.ResultViewer;
import userInterface.backEnd.TimeLineResult;
import userInterface.backEnd.ScheduleResult;
import userInterface.backEnd.ViewerStatus;

/**
 *
 * @author ShiuJia
 */
public class InfoWin extends JPanel
{
        
    public UserInterface parent;
    private JLabel message,timeMessage;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JButton leftWinBtn,rightWinBtn ,btnZoomIn, btnZoomOut, timeLineBtn, totalDataWinBtn;
    private DataSetting ds;
    private ResultViewer curResultViewer;
    
    public InfoWin(UserInterface ui) 
    {
        super();
        this.parent = ui;
        this.setLayout(new BorderLayout()); 
        this.initialize();
        
        this.totalDataWinBtn.addMouseListener
        (
            new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    if(InfoWin.this.parent.getSimulationViewer().getTotalDataPopupWin() != null)
                        InfoWin.this.parent.getSimulationViewer().getTotalDataPopupWin().changeVisible();
                }
            }
        );
        
        this.timeLineBtn.addMouseListener
        (
            new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    try
                    {
                        InfoWin.this.pressTimeLineButton();
                        InfoWin.this.timeLineBtn.setForeground(InfoWin.this.timeLineBtn.getForeground()==Color.red? Color.GREEN : Color.red);
                    }
                    catch(Exception ex)
                    {
                        
                    }
                }
            }
        );
        
        btnZoomIn.addMouseListener
        (
            new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    try
                    {
                        InfoWin.this.pressZoomInButton();
                    }
                    catch(Exception ex)
                    {
                        
                    }  
                }
            }
        );
	
        btnZoomOut.addMouseListener
        (
            new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    try
                    {
                        InfoWin.this.pressZoomOutButton();
                    }
                    catch(Exception ex)
                    {
                        
                    }   
                }

            }
        );
        
        this.leftWinBtn.addMouseListener
        (
            new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    try
                    {
                        InfoWin.this.parent.extendSimulationViewer();
                    }
                    catch(Exception ex)
                    {
                        
                    }       
                }
            }
        );
        
        this.rightWinBtn.addMouseListener
        (
            new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    try
                    {
                        InfoWin.this.parent.extendAttributeViewer();
                    }
                    catch(Exception ex)
                    {
                        
                    }   
                }
            }
        );
        
        this.tabbedPane.addChangeListener
        (new ChangeListener() 
            {
                @Override
                public void stateChanged(ChangeEvent e) 
                {
                    InfoWin.this.timeLineBtn.setForeground(Color.red);
                    InfoWin.this.curResultViewer = (ResultViewer) InfoWin.this.tabbedPane.getSelectedComponent();
                    
                    if(InfoWin.this.curResultViewer != null)
                    {
                        InfoWin.this.curResultViewer.getTimeLineResult().reSetAttributes();
                        InfoWin.this.curResultViewer.getTimeLineResult().mouseStatus.setMouseStatus(ViewerStatus.IDLE);
                        InfoWin.this.parent.getAttributes().setTimeLineSet(InfoWin.this.curResultViewer.getTimeLineResult().getTimeLineSet());
                        
                        int scale = InfoWin.this.curResultViewer.getScale();
                        if(scale<0)
                            InfoWin.this.message.setText("1/" + Math.abs(scale) + "x");
                        else
                            InfoWin.this.message.setText("" + scale + "x");
                    }
                }
            }
        );
    }
    
    /**
     * Initialize the contents of the frame.
     */
    private void initialize() 
    {
        JPanel nPanel = new JPanel();
        this.add(nPanel, BorderLayout.NORTH);
        nPanel.setLayout(new BorderLayout(0, 0));
        this.leftWinBtn = new JButton("|<");
        this.rightWinBtn = new JButton(">|");
        this.btnZoomIn = new JButton("Zoom In"); 
        this.btnZoomOut= new JButton("Zoom Out");
        this.timeLineBtn= new JButton("Time Line");
        this.timeLineBtn.setForeground(Color.red);
        this.totalDataWinBtn = new JButton("TotalDataWin");
        
        JToolBar WJTB = new JToolBar();
        JToolBar EJTB = new JToolBar();
        JToolBar CJTB = new JToolBar();
        CJTB.setLayout(new GridBagLayout());
        
        WJTB.setFloatable(false);
        EJTB.setFloatable(false);
        CJTB.setFloatable(false);
        WJTB.add(this.leftWinBtn);
        EJTB.add(this.rightWinBtn);
        CJTB.add(this.btnZoomOut);
        CJTB.add(this.btnZoomIn);
        CJTB.add(this.timeLineBtn);
        CJTB.add(this.totalDataWinBtn);
        nPanel.add(WJTB,BorderLayout.WEST);
        nPanel.add(EJTB,BorderLayout.EAST);
        nPanel.add(CJTB,BorderLayout.CENTER);
        this.add(nPanel,BorderLayout.NORTH);
        
        
        
        
        JPanel sPanel = new JPanel();
        this.add(sPanel, BorderLayout.SOUTH);
        sPanel.setLayout(new BorderLayout(0, 0));

        message = new JLabel("Message Here");
        message.setHorizontalAlignment(SwingConstants.CENTER);
        sPanel.add(message, BorderLayout.WEST);
        timeMessage = new JLabel("Message Here");
        timeMessage.setHorizontalAlignment(SwingConstants.CENTER);
        sPanel.add(timeMessage, BorderLayout.CENTER);
        this.add(this.tabbedPane, BorderLayout.CENTER);
        
        
        
        
    }

    public void pressDrawButton()
    {
        this.timeLineBtn.setForeground(Color.red);
        this.ds = parent.getSimulationViewer().getDataReader().getDataSetting();
        this.tabbedPane.removeAll();
        
        this.tabbedPane.addTab("AllCores",new ResultViewer(this,ds.getProcessor().getAllCore(),"AllCores"));
            
        if(ds.getProcessor().getAllCore().size() > 1)
        {
            this.tabbedPane.addTab("AllTasks",new ResultViewer(this,ds.getProcessor().getAllCore(),"AllTasks"));
        }
        
        for(Core c : ds.getProcessor().getAllCore())
        {
            this.tabbedPane.addTab("Core "+c.getID(),new ResultViewer(this,c));
        }
        
        this.message.setText("1x");
        this.parent.getAttributes().getTimeLineSet().removeAllItems();
    }
    
    public void pressTimeLineButton()
    {
        this.curResultViewer.getTimeLineResult().mouseStatus.chengeMouseStatus();
        this.curResultViewer.getTimeLineResult().repaint();
    }
    
    public void pressZoomInButton()
    {
        int scale = this.curResultViewer.getScale();
        ScheduleResult sr = this.curResultViewer.getScheduleResult();
        TimeLineResult tlr = this.curResultViewer.getTimeLineResult();
        
        if(scale<0)
        {
            scale/=2;
            if(scale==(-1))
            {
                scale=1;
            }
            sr.setBaseunit((double)2);
            tlr.repaint();
        }
        else if(scale < 16)
        {
            sr.setBaseunit((double)2);
            tlr.repaint();
            scale*=2;
            tlr.repaint();
            
        }

        if(scale <= 16)
        {
            if(scale<0)
                InfoWin.this.message.setText("1/" + Math.abs(scale) + "x");
            else
                InfoWin.this.message.setText("" + scale + "x");
        }
        
        this.curResultViewer.setScale(scale);
        
        if(sr.isCoreTimeLine)
        {
            tlr.setPreferredSize(new Dimension((int)((sr.getFinalTime()+1)*sr.getBaseunit()+200), 
                                                    sr.getCoreTimeLines().size() * sr.getTaskGap() + 100));
        }
        else
        {
            tlr.setPreferredSize(new Dimension((int)((sr.getFinalTime()+1)*sr.getBaseunit()+200), 
                                                    sr.getTaskTimeLines().size() * sr.getTaskGap() + 100));
        }
        
        tlr.revalidate();
    }
    
    public void pressZoomOutButton()
    {
        int scale = this.curResultViewer.getScale();
        ScheduleResult sr = InfoWin.this.curResultViewer.getScheduleResult();
        TimeLineResult tlr = InfoWin.this.curResultViewer.getTimeLineResult();
        
        if(scale>1)
        {
            scale/=2;
            sr.setBaseunit(0.5);
            tlr.repaint();
        }
        else if(scale==1)
        {
            scale=(-2);
            sr.setBaseunit(0.5);
            tlr.repaint();
        }
        else if((scale<0)&&(scale>(-8)))
        {
            scale*=2;
            sr.setBaseunit(0.5);
            tlr.repaint();
        }

        if(scale>=(-8))
        {
            if(scale<0)
                InfoWin.this.message.setText("1/" + Math.abs(scale) + "x");
            else
                InfoWin.this.message.setText("" + scale + "x");
        }
        
        this.curResultViewer.setScale(scale);
        
        if(sr.isCoreTimeLine)
        {
            tlr.setPreferredSize(new Dimension((int)((sr.getFinalTime()+1)*sr.getBaseunit()+200), 
                                                    sr.getCoreTimeLines().size() * sr.getTaskGap() + 100));
        }
        else
        {
            tlr.setPreferredSize(new Dimension((int)((sr.getFinalTime()+1)*sr.getBaseunit()+200), 
                                                    sr.getTaskTimeLines().size() * sr.getTaskGap() + 100));
        }
        
        tlr.revalidate();           
    }
    
    public DataSetting getDataSetting()
    {
        return this.ds;
    }
    
    public JLabel getMessage()
    {
        return this.message;
    }
    
    public JLabel getTimeMessage()
    {
        return this.timeMessage;
    }
    
    public JTabbedPane getTabbedPane()
    {
        return this.tabbedPane;
    }
    
    public ResultViewer getCurCoreResult()
    {
        return this.curResultViewer;
    }
}