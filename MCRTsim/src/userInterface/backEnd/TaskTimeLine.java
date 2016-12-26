/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface.backEnd;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import simulation.Task;

/**
 *
 * @author ShiuJia
 */
public class TaskTimeLine
{
    int resourceHeight=13;
    int taskHeight = 80;
    Point o; //時間軸起始位置
    int ID;
    ScheduleResult parent;
    ArrayList<TaskExecution> executions; 
    
    Color[] resourceColor = new Color[]
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

    public TaskTimeLine()
    {
        
    }

    public TaskTimeLine(ScheduleResult sr, int i, String id)
    {
        parent = sr;
        ID = new Integer(id).intValue();
        o = new Point(100, 200 + i * this.parent.getTaskGap());
        executions = new ArrayList<TaskExecution>();
    }

    public void addExecution(TaskExecution te) //E,X 狀態
    {
        executions.add(te);
    }
    
    public void drawItself(Graphics g)
    {
        int baseunit = this.parent.getBaseunit();
        double finalTime = this.parent.getFinalTime();
        int yHeight = o.y+65;
        
        if(this.parent.isMultiCore)
        {
            for(int i = -1 ; i<=1 ; i++) //畫時間軸
            {
                //System.out.println("X = "+o.x +", Y = " + o.y+", finalTime = "+finalTime);
                
                g.drawLine(o.x, yHeight + i, (int)(o.x + baseunit * (finalTime + 1)), yHeight + i);

                g.drawLine((int)(o.x + baseunit * (finalTime + 1)), yHeight + i, 
                               (int)(o.x + baseunit * (finalTime + 1) - 10), yHeight + i + 10);
                g.drawLine((int)(o.x + baseunit * (finalTime+1)),  yHeight+i, 
                               (int)(o.x + baseunit * (finalTime+ 1 ) - 10), yHeight + i - 10);
            }
            
            for(int i = 0 ; i <= finalTime ; i++) //畫時間軸刻度
            { 
                g.drawLine( o.x + i * baseunit, yHeight, o.x + i * baseunit, yHeight + 5);
            }
            
            g.drawString("Core", o.x - 50, yHeight);
        }
        
        for(int i = -1 ; i<=1 ; i++) //畫時間軸
        {
            //System.out.println("X = "+o.x +", Y = " + o.y+", finalTime = "+finalTime);
            
            g.drawLine(o.x, o.y + i, (int)(o.x + baseunit * (finalTime + 1)), o.y + i);

            g.drawLine((int)(o.x + baseunit * (finalTime + 1)), o.y + i, 
                           (int)(o.x + baseunit * (finalTime + 1) - 10), o.y + i + 10);
            g.drawLine((int)(o.x + baseunit * (finalTime+1)),  o.y+i, 
                           (int)(o.x + baseunit * (finalTime+ 1 ) - 10), o.y + i - 10);
        }
        
        drawPeriod(g);

        for(int i = 0 ; i <= finalTime ; i++) //畫時間軸刻度
        { 
            g.drawLine( o.x + i * baseunit, o.y, o.x + i * baseunit, o.y + 5);
            
            if(parent.parent.scale >= 1)
            {
                g.drawString("" + i, o.x + i * baseunit - 2, o.y + 20);
            }
            else if(parent.parent.scale == (-8))
            {
                if(i % 10 == 0)
                {
                    g.drawString("" + i, o.x + i * baseunit - 2, o.y + 20);
                }
            }
            else if(i%5==0)
            {
                g.drawString("" + i, o.x + i * baseunit - 2, o.y + 20);			
            }
        }

        for(TaskExecution te : executions)
        {
            if(te.getStatus().equals("W"))
            {
                g.fillRect((int)(o.x + te.getStartTime() * baseunit ), o.y - this.taskHeight, (int)(te.getExecutionTime() * baseunit), this.taskHeight);
                
                if(this.parent.isMultiCore)
                {
                    g.drawRect((int)(o.x + te.getStartTime() * baseunit ), yHeight - 16, (int)(te.getExecutionTime() * baseunit), 16);
                    g.drawLine((int)(o.x + te.getStartTime() * baseunit ), yHeight, (int)(o.x + te.getStartTime() * baseunit), yHeight + 5);
                    
                    g.setColor(resourceColor[19 - (te.getCoreID()%19)-1]);
                    g.fillRect((int)(o.x + te.getStartTime() * baseunit)+1, yHeight - 15, (int)(te.getExecutionTime() * baseunit) - 1, 14);
                    g.setColor(reverseColor(resourceColor[19 - (te.getCoreID()%19)-1]));
    
                    char[] data = String.valueOf(te.getCoreID()).toCharArray();

                    if((int)(te.getExecutionTime() * baseunit) > data.length * 8)
                    {
                        g.drawChars(data, 0, data.length, (int)(o.x + te.getStartTime() * baseunit) + 2, yHeight - 2);
                    }
                
                    g.setColor(Color.black);
                }
                
                DecimalFormat df = new DecimalFormat("##.00");
                double time = Double.parseDouble(df.format(te.getStartTime()));
                
                if(((int)(time*10)%10)!=0)
                {
                    g.drawString(""+ time, (int)(o.x - 4 + te.getStartTime() * baseunit), o.y + 40);
                    g.drawLine((int)(o.x + te.getStartTime() * baseunit), o.y, (int)(o.x + te.getStartTime() * baseunit), o.y + 25);
                }

                time = Double.parseDouble(df.format(te.getEndTime()));

                if(( (int)(time * 10) % 10) != 0)
                {
                    g.drawString(""+ time, (int)(o.x - 4 + te.getEndTime() * baseunit), o.y + 40);
                    g.drawLine((int)(o.x + te.getEndTime() * baseunit), o.y, (int)(o.x + te.getEndTime() * baseunit), o.y + 25);
                }
            }
            else if(te.getStatus().equals("X"))
            {
                g.setColor(Color.red);
                g.drawString("X", (int) (o.x - 3 + te.getStartTime() * baseunit), o.y - 75);
                g.setColor(Color.BLACK);
            }
            else if( te.getStatus().equals("E") )
            {
                g.drawRect((int)(o.x + te.getStartTime() * baseunit ), o.y - this.taskHeight, (int)(te.getExecutionTime() * baseunit), this.taskHeight);
                
                if(this.parent.isMultiCore)
                {
                    g.drawRect((int)(o.x + te.getStartTime() * baseunit ), yHeight - 16, (int)(te.getExecutionTime() * baseunit), 16);
                    g.drawLine((int)(o.x + te.getStartTime() * baseunit ), yHeight, (int)(o.x + te.getStartTime() * baseunit), yHeight + 5);
                    
                    g.setColor(resourceColor[19 - (te.getCoreID()%19)-1]);
                    g.fillRect((int)(o.x + te.getStartTime() * baseunit)+1, yHeight - 15, (int)(te.getExecutionTime() * baseunit) - 1, 14);
                    g.setColor(reverseColor(resourceColor[19 - (te.getCoreID()%19)-1]));
    
                    char[] data = String.valueOf(te.getCoreID()).toCharArray();

                    if((int)(te.getExecutionTime() * baseunit) > data.length * 8)
                    {
                        g.drawChars(data, 0, data.length, (int)(o.x + te.getStartTime() * baseunit) + 2, yHeight - 2);
                    }
                
                    g.setColor(Color.black);
                
                }
                
                
                DecimalFormat df = new DecimalFormat("##.00");
                double time = Double.parseDouble(df.format(te.getStartTime()));
                
                if(((int)(time*10)%10)!=0)
                {
                    g.drawString(""+ time, (int)(o.x - 4 + te.getStartTime() * baseunit), o.y + 40);
                    g.drawLine((int)(o.x + te.getStartTime() * baseunit), o.y, (int)(o.x + te.getStartTime() * baseunit), o.y + 25);
                }

                time = Double.parseDouble(df.format(te.getEndTime()));

                if(( (int)(time * 10) % 10) != 0)
                {
                    g.drawString(""+ time, (int)(o.x - 4 + te.getEndTime() * baseunit), o.y + 40);
                    g.drawLine((int)(o.x + te.getEndTime() * baseunit), o.y, (int)(o.x + te.getEndTime() * baseunit), o.y + 25);
                }
            }
        }

        g.drawString("Task" + ID, o.x - 50, o.y);
        g.drawString("Time", (int)(o.x + baseunit * (finalTime + 1) + 15), o.y + 5);
    }


    public void drawPeriod(Graphics g)
    {
        int baseunit = this.parent.getBaseunit();
        double finalTime = this.parent.getFinalTime();
        Task task = this.parent.parent.parent.getDataSetting().getTaskSet().getTask(this.ID-1);
        double curPeriod = 0 + (double)(task.getEnterTime() / 100000.0);
        double curDeadline = curPeriod - (double)((task.getPeriod() - task.getRelativeDeadline()) / 100000.0);
        
        for(int j = 0 ; j < (finalTime / (double)(task.getPeriod() / 100000.0)) ; j++)
        {  
            if(curPeriod <= finalTime)
            {
                //g.setColor(g.getColor() == Color.BLUE ? Color.RED : Color.BLUE); //紅藍標記
                g.setColor(Color.BLUE);
                g.fillRect(o.x-1+(int)(baseunit * curPeriod), o.y-115, 3, 15);
                for(int k=1 ; k<3 ; k++)
                {
                    g.drawLine(o.x+(int)(baseunit * curPeriod), o.y-115-k, o.x-5 + (int)(baseunit * curPeriod), o.y-112-k);
                    g.drawLine(o.x+(int)(baseunit * curPeriod), o.y-115-k, o.x+5 + (int)(baseunit * curPeriod), o.y-112-k);
                }
            }

            curPeriod +=  (double)(task.getPeriod() / 100000.0);
            curDeadline = curPeriod - (double)((task.getPeriod() - task.getRelativeDeadline()) / 100000.0);
            
            if(curDeadline <= finalTime)
            {
                g.fillRect(o.x-1 + (int)(baseunit * curDeadline), o.y-100, 3, 15);
                for(int k=1 ; k<3 ; k++)
                {
                    g.drawLine(o.x + (int)(baseunit * curDeadline), o.y-85-k, o.x-5+ (int)(baseunit * curDeadline), o.y-88-k);
                    g.drawLine(o.x + (int)(baseunit * curDeadline), o.y-85-k, o.x+5+ (int)(baseunit * curDeadline), o.y-88-k);
                }
            }
        }
        
        g.setColor(Color.BLACK);
    }

    public void drawResources(TimeLineResult rv)
    {
        for(TaskExecution te : executions)
        {
            if(te.getStatus().equals("E"))
            {
                int i=0;
                for(ResourcePanel reP : te.getResourcePanels())
                {
                    i++;
                    rv.add(reP);
                    reP.setBounds((int)(o.x + te.getStartTime() * parent.getBaseunit()), o.y - i * this.resourceHeight, (int)(te.getExecutionTime() * parent.getBaseunit()), this.resourceHeight);
                }
            }
        }
    }



    public void reDrawResources(TimeLineResult rv ,Graphics g)
    {
        int baseunit = parent.getBaseunit();
        
        for(TaskExecution te : executions)
        {
            if(te.getStatus().equals("E"))
            {
                int i=0;
                for(ResourcePanel reP : te.getResourcePanels())
                {
                    i++;
                    reP.setBounds((int)(o.x + te.getStartTime() * baseunit), o.y-i * this.resourceHeight, (int)(te.getExecutionTime() * baseunit), this.resourceHeight);
                  
                    g.setColor(resourceColor[ new Integer(reP.getResourcesID()).intValue() -1]);
                    g.fillRect((int)(o.x + te.getStartTime() * baseunit)+1, o.y-i * this.resourceHeight, (int)(te.getExecutionTime() * baseunit) - 1, this.resourceHeight-1);
                    g.setColor(reverseColor(resourceColor[ new Integer(reP.getResourcesID()).intValue() -1]));
    
                    char[] data = String.valueOf("R"+reP.getResourcesID()+"(" + reP.getResourceID() +"/" + reP.getResourcesAmount() +")").toCharArray();

                    if((int)(te.getExecutionTime() * baseunit) > data.length * 8)
                    {
                        g.drawChars(data, 0, data.length, (int)(o.x + te.getStartTime() * baseunit) + 2, o.y - i * this.resourceHeight + this.resourceHeight - 2);
                    }
                    else if((int)((te.getExecutionTime() * baseunit) / 8) > 1)
                    {
                            g.drawChars(data, 0, (int)((te.getExecutionTime() * baseunit) / 8), (int)(o.x + te.getStartTime() * baseunit) + 2, o.y - i * this.resourceHeight + this.resourceHeight - 2);
                    }
                    g.setColor(Color.BLACK);
                }
            }
        }
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
