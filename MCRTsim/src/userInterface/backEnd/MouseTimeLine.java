/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface.backEnd;

import ResultSet.SchedulingInfo;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 *
 * @author ShiuJia
 */
public class MouseTimeLine extends JPanel
{
    public TimeLineResult parent;
    private Double curTime;
    private SchedulingInfo result; 
    
    public MouseTimeLine ()
    {
       super(); 
    }
    
    public MouseTimeLine (TimeLineResult rv, Double x, SchedulingInfo re)
    {
        super();
        this.parent = rv;
        this.curTime = x;
        this.result = re;
        
        if(this.parent.parent.isCoreTimeLine)
        {   
            try
            {
                this.setBackground(this.parent.getResourceColor()[20 - re.getJob().getParentTask().getID()]);
            }
            catch(Exception ex)
            {
                this.setBackground(Color.BLACK);
            }
        }
        else
        {   
            try
            {
                this.setBackground(this.parent.getResourceColor()[20 - re.getCore().getID()]);
            }
            catch(Exception ex)
            {
                this.setBackground(Color.BLACK);
            }
        }
        
        
        this.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                MouseTimeLine.this.parent.getTimeLineSet().removeItem(MouseTimeLine.this);
                MouseTimeLine.this.parent.remove(MouseTimeLine.this);
                MouseTimeLine.this.parent.repaint();
            }
        });
    }
    
    public void reSetItself()
    {
        this.setBounds(Double.valueOf(curTime * this.parent.parent.getBaseunit()).intValue() + 100 - 5, 10, 10, 10);
    }
    
    public Double getCurTime()
    {
        return this.curTime;
    }
    
    public int getCurPoint()
    {
        return Double.valueOf(curTime * this.parent.parent.getBaseunit()).intValue() + 100;
    } 
    
    public String toString()
    {
        String str = "";
        if(result.getCore() == null)
        {
            str += this.curTime+ " , " + "Null";
        }
        else
        {
            str += this.curTime+ " , " +  result.getCore().getID();
        }
        return str;
    }
    
    public SchedulingInfo getResult()
    {
        return this.result;
    }
    
}