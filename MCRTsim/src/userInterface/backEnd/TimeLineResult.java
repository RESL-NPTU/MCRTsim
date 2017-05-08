/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface.backEnd;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

/**
 *
 * @author ShiuJia
 */

public class TimeLineResult extends JPanel
{
	public ScheduleResult parent;
        private int mouseX;
        private int mouseY;
        public MouseStatus mouseStatus;
        private Double curTime;
        private JComboBox<MouseTimeLine> mouseTimeLineSet;
	Color[] resourceColor;
        
        /**
	 * Create the panel.
	 */
	public TimeLineResult(ScheduleResult sr)
        {
            super();
            this.mouseStatus = new MouseStatus();
            this.parent = sr;
            this.setLayout(null);
            this.setBackground(Color.white);
            this.mouseTimeLineSet = new JComboBox<>();
            
            if(this.parent.isCoreTimeLine)
            {
                this.setPreferredSize(new Dimension((int)((parent.getFinalTime() + 1) * parent.getBaseunit() + 200) , parent.getCoreTimeLines().size() * this.parent.getTaskGap() + 100));
            }
            else
            {
                this.setPreferredSize(new Dimension((int)((parent.getFinalTime() + 1) * parent.getBaseunit() + 200) , parent.getTaskTimeLines().size() * this.parent.getTaskGap() + 100));
            }
            
            this.setVisible(true);
            ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE); //工作標籤顯示時間
            
            resourceColor = new Color[] //宣告色彩配置
            {
                Color.getHSBColor((float) 0.1, 1, 1),Color.getHSBColor((float) 0.2, 1, 1),
                Color.getHSBColor((float) 0.3, 1, 1),Color.getHSBColor((float) 0.45,(float) 0.2, 1),
                Color.getHSBColor((float) 0.5, 1, 1),Color.getHSBColor((float) 0.58, 1, 1),
                Color.getHSBColor((float) 0.7, 1, 1),Color.getHSBColor((float) 0.8, 1, 1),
                Color.getHSBColor((float) 0.15, 1, 1),Color.getHSBColor((float) 1, 1, 1),

                Color.getHSBColor((float) 0.1, (float)0.5, (float)0.8),Color.getHSBColor((float) 0.2, (float)0.5, (float)0.8),
                Color.getHSBColor((float) 0.3, (float)0.5, (float)0.8),Color.getHSBColor((float) 0.45, (float)0.5, (float)0.8),
                Color.getHSBColor((float) 0.5, (float)0, (float)0.8),Color.getHSBColor((float) 0.6, (float)0.3, (float)1),
                Color.getHSBColor((float) 0.7, (float)0.2, (float)0.8),Color.getHSBColor((float) 0.17, (float)0.5, (float)1),
                Color.getHSBColor((float) 0.85, (float)0.3, (float)0.9),Color.getHSBColor((float) 1, (float)0.8, (float)0.6)
            };

            

            if(this.parent.isCoreTimeLine)
            {
                Enumeration<String> keys= parent.getCoreTimeLines().keys();
                for(int i=parent.getCoreTimeLines().size()-1;i>=0;i--)
                {   
                    String str = keys.nextElement();
                    CoreTimeLine Core = parent.getCoreTimeLines().get(str);
                    Core.drawResources(this);
                }
            }
            else
            {
                Enumeration<String> keys= parent.getTaskTimeLines().keys();
                for(int i=parent.getTaskTimeLines().size()-1;i>=0;i--)
                {   
                    String str = keys.nextElement();
                    TaskTimeLine task = parent.getTaskTimeLines().get(str);
                    task.drawResources(this);
                }
            }
            this.addMouseMotionListener
            (new MouseAdapter()
                {
                    public void mouseMoved(MouseEvent e)
                    {
                        mouseY = e.getY();
                        mouseX = e.getX();
                        
                        if(mouseX < 100)
                        {
                            mouseX = 100;
                        }

                        if(mouseX > TimeLineResult.this.parent.getFinalTime() * TimeLineResult.this.parent.getBaseunit() + 100)
                        {
                            mouseX = (int)(TimeLineResult.this.parent.getFinalTime() * TimeLineResult.this.parent.getBaseunit() + 100);
                        }
                        
                        if(mouseY < 60)
                        {
                            mouseY = 60;
                        }
                        
                        if(mouseY > TimeLineResult.this.getHeight() - 66)
                        {
                            mouseY = TimeLineResult.this.getHeight() - 66;
                        }
                        
                        TimeLineResult.this.curTime = (double)(mouseX - 100) / TimeLineResult.this.parent.getBaseunit();
                        TimeLineResult.this.parent.parent.parent.getTimeMessage().setText("Time:" + curTime);

                        if(TimeLineResult.this.parent.isMultiCore)
                        {
                            int task = mouseY < 285 ? 0 : ((mouseY-285)/TimeLineResult.this.parent.getTaskGap())+1;
                            
                            try
                            {
                                TimeLineResult.this.parent.parent.parent.parent.getAttributes().setAtbData
                                (TimeLineResult.this.parent.getAtbSet
                                    (task,(int)(TimeLineResult.this.curTime * parent.getAccuracy())
                                    )
                                );
                            }
                            catch(Exception ex)
                            {
                                TimeLineResult.this.parent.parent.parent.parent.getAttributes().setAtbData
                                (TimeLineResult.this.parent.getAtbSet
                                    (TimeLineResult.this.parent.parent.getCores().size()-1,(int)(TimeLineResult.this.curTime * parent.getAccuracy())
                                    )
                                );
                            }
                            
                        }
                        else
                        {
                            TimeLineResult.this.parent.parent.parent.parent.getAttributes().setAtbData
                            (TimeLineResult.this.parent.getAtbSet
                                (0,(int)(TimeLineResult.this.curTime * parent.getAccuracy())
                                )
                            );
                        }
                        
                        if(TimeLineResult.this.mouseStatus.getMouseStatus() == ViewerStatus.EXECUTION)
                        {
                            TimeLineResult.this.repaint();
                        }
                    }
                }
            );

            this.addMouseListener
            (new MouseAdapter()
                {
                    public void mouseClicked(MouseEvent e)
                    {
                        if(TimeLineResult.this.mouseStatus.getMouseStatus() == ViewerStatus.EXECUTION)
                        {
                            MouseTimeLine mtl;
                                    
                            if(TimeLineResult.this.parent.isMultiCore)
                            {
                                int TimeLineID = mouseY < 285 ? 0 : ((mouseY-285)/TimeLineResult.this.parent.getTaskGap())+1;
                            
                                mtl = new MouseTimeLine
                                (
                                    TimeLineResult.this, TimeLineResult.this.curTime, TimeLineResult.this.parent.getAtbSet
                                    (TimeLineID,(int)(TimeLineResult.this.curTime * parent.getAccuracy())
                                    )
                                );
                            }
                            else
                            {
                                mtl = new MouseTimeLine
                                (
                                    TimeLineResult.this, TimeLineResult.this.curTime, TimeLineResult.this.parent.getAtbSet
                                    (
                                        0, (int)(curTime * parent.getAccuracy())
                                    )
                                );
                            }
                            
                            TimeLineResult.this.add(mtl);
                            TimeLineResult.this.mouseTimeLineSet.addItem(mtl);
                            TimeLineResult.this.mouseTimeLineSet.setSelectedIndex(TimeLineResult.this.mouseTimeLineSet.getItemCount()-1);
                            TimeLineResult.this.repaint();
                        }
                    }
                }
            );
            
            this.mouseTimeLineSet.addActionListener
            (new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        TimeLineResult.this.reSetAttributes();
                    }
                }
            );

            this.mouseTimeLineSet.addItemListener
            (new ItemListener()
                {
                    @Override
                    public void itemStateChanged(ItemEvent e)
                    {
                        if (e.getStateChange() == ItemEvent.SELECTED)
                        {
                            MouseTimeLine mtl = (MouseTimeLine)TimeLineResult.this.mouseTimeLineSet.getSelectedItem();
                            TimeLineResult.this.parent.parent.getHorizontalScrollBar().setValue
                            (Double.valueOf
                                (Double.valueOf
                                    (
                                        mtl.getCurTime()
                                    ).doubleValue() * TimeLineResult.this.parent.getBaseunit()
                                ).intValue() + 100 - (TimeLineResult.this.parent.parent.getWidth() / 2)
                            );
                        }
                    }
                }
            ); 
	}
        
	public void paintComponent(Graphics g) 
        {
            super.paintComponent(g);
            if(this.parent.isCoreTimeLine)
            {
                this.drawCoreTimeLine(g);
            }
            else
            {
                this.drawTaskTimeLine(g);
            }
        }
        
        public void paint(Graphics g)
        {
            this.paintComponent(g);
        }

        
        private void drawCoreTimeLine(Graphics g)//繪製出CoreTimeLine
        {
            g.setPaintMode();
            g.setColor(Color.black);

            Enumeration<String> keys= parent.getCoreTimeLines().keys();

            for(int i=parent.getCoreTimeLines().size()-1;i>=0;i--)
            {
                CoreTimeLine core = parent.getCoreTimeLines().get(keys.nextElement());
                core.drawItself(g);
                core.reDrawResources(this, g);
            }

            //-----MouseTimeLine   v
            for(int i = 0 ; i < this.mouseTimeLineSet.getItemCount() ; i++)
            {
                g.setColor(this.mouseTimeLineSet.getItemAt(i).getBackground());
                g.drawLine(this.mouseTimeLineSet.getItemAt(i).getCurPoint(),60,this.mouseTimeLineSet.getItemAt(i).getCurPoint(),this.getHeight()-30);
                this.mouseTimeLineSet.getItemAt(i).reSetItself();
            }
            //------MouseTimeLine   ^ 

            g.setColor(Color.red);
            
            if(this.mouseStatus.getMouseStatus()==ViewerStatus.EXECUTION)
            {
                g.drawLine(mouseX, 60, mouseX, this.getHeight()-30);
                
                if(this.parent.isMultiCore)
                {
                    g.drawLine(100, mouseY, (int)(this.parent.getFinalTime() * this.parent.getBaseunit() + 100), mouseY);
                }
            }
            
            int x = this.parent.parent.getHorizontalScrollBar().getValue();
            int y = this.parent.parent.getVerticalScrollBar().getValue();

            g.setColor(Color.WHITE);
            g.fillRect(0,0+y,this.getWidth(),60);
            //-----MouseTimeLine   v
            g.setColor(Color.red);
            for(int i = 0 ; i < this.mouseTimeLineSet.getItemCount() ; i++)
            {
                g.setColor(this.mouseTimeLineSet.getItemAt(i).getBackground());
                g.fillRect(Double.valueOf(this.mouseTimeLineSet.getItemAt(i).getCurTime() * this.parent.getBaseunit()).intValue()+100-5, 10, 10, 10);
            }
            //------MouseTimeLine   ^
            g.setColor(Color.black);

            for(int i = 0 ;i < parent.parent.parent.getDataSetting().getResourceSet().size() ; i++)
            {
                int resourcesWidth = 50;
                g.setColor(resourceColor[i]);
                g.fillRect(x + resourcesWidth * (i), y+18, resourcesWidth, 13);
                g.setColor(this.reverseColor(resourceColor[i]));
                g.drawString("R" + (i+1) + "("+ parent.parent.parent.getDataSetting().getResourceSet().getResources(i).getResourcesAmount() +")" ,x + resourcesWidth*(i), y+30);
                g.setColor(Color.black);
            }
        }
        
        private void drawTaskTimeLine(Graphics g)//繪製出TaskTimeLine
        {
            g.setPaintMode();
            g.setColor(Color.black);

            Enumeration<String> keys= parent.getTaskTimeLines().keys();

            for(int i=parent.getTaskTimeLines().size()-1;i>=0;i--)
            {
                TaskTimeLine task = parent.getTaskTimeLines().get(keys.nextElement());
                task.drawItself(g);
                task.reDrawResources(this, g);
            }


            //-----MouseTimeLine   v
            
            for(int i = 0 ; i < this.mouseTimeLineSet.getItemCount() ; i++)
            {
                g.setColor(this.mouseTimeLineSet.getItemAt(i).getBackground());
                g.drawLine(this.mouseTimeLineSet.getItemAt(i).getCurPoint(),60,this.mouseTimeLineSet.getItemAt(i).getCurPoint(),this.getHeight()-30);
                this.mouseTimeLineSet.getItemAt(i).reSetItself();
            }
            //------MouseTimeLine   ^ 

            g.setColor(Color.red);
            
            if(this.mouseStatus.getMouseStatus()==ViewerStatus.EXECUTION)
            {
                g.drawLine(mouseX, 60, mouseX, this.getHeight()-30);
                
                if(this.parent.isMultiCore)
                {
                    g.drawLine(100, mouseY, (int)(this.parent.getFinalTime() * this.parent.getBaseunit() + 100), mouseY);
                }
            }
            
            int x = this.parent.parent.getHorizontalScrollBar().getValue();
            int y = this.parent.parent.getVerticalScrollBar().getValue();

            g.setColor(Color.WHITE);
            g.fillRect(0,0+y,this.getWidth(),60);
            //-----MouseTimeLine   v
            g.setColor(Color.red);
            for(int i = 0 ; i < this.mouseTimeLineSet.getItemCount() ; i++)
            {
                g.setColor(this.mouseTimeLineSet.getItemAt(i).getBackground());
                g.fillRect(Double.valueOf(this.mouseTimeLineSet.getItemAt(i).getCurTime() * this.parent.getBaseunit()).intValue()+100-5, 10, 10, 10);
            }
            //------MouseTimeLine   ^
            g.setColor(Color.black);

            for(int i = 0 ;i < parent.parent.parent.getDataSetting().getResourceSet().size() ; i++)
            {
                int resourcesWidth = 50;
                g.setColor(resourceColor[i]);
                g.fillRect(x + resourcesWidth * (i), y+18, resourcesWidth, 13);
                g.setColor(this.reverseColor(resourceColor[i]));
                g.drawString("R" + (i+1) + "("+ parent.parent.parent.getDataSetting().getResourceSet().getResources(i).getResourcesAmount() +")" ,x + resourcesWidth*(i), y+30);
                g.setColor(Color.black);
            }
        }
        
        
        public Double getCurTime()
        {
            return this.curTime;
        }
        
        public JComboBox<MouseTimeLine> getTimeLineSet()
        {
            return this.mouseTimeLineSet;
        }
       
        public void reSetAttributes()//重新設置Attributes的欄位
        {
            if (this.mouseTimeLineSet.getItemCount() != 0) 
            {
                MouseTimeLine mtl = (MouseTimeLine)this.mouseTimeLineSet.getSelectedItem();
                try
                {
                    this.parent.parent.parent.parent.getAttributes().setAtbData
                    (
                        mtl.getResult()
                    );
                }
                catch (Exception ex)
                {

                }
            }
        }
        
        public Color[] getResourceColor()
        {
            return this.resourceColor;
        }
        
        public Color reverseColor(Color color)
        {  
            int r = color.getRed();  
            int g = color.getGreen();  
            int b = color.getBlue();  
            int r_ = 255-r;  
            int g_ = 255-g;  
            int b_ = 255-b;  
            
            Color newColor = new Color(r_,g_,b_);  
            
            return newColor;  
        }
        
}
