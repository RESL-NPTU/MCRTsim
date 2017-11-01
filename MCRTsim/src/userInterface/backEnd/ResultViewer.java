/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface.backEnd;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Vector;
import javax.swing.JScrollPane;
import SystemEnvironment.Core;
import userInterface.frontEnd.InfoWin;

/**
 *
 * @author YC
 */
public class ResultViewer extends JScrollPane {
    public InfoWin parent;
    private Vector<Core> Cores;
    private ScheduleResult sr;
    private TimeLineResult tlr;
    public int scale;

    public ResultViewer(InfoWin win , Vector<Core> cores , String type)
    {
        this.parent = win;
        this.init();
        this.Cores = new Vector<>();
        this.Cores.addAll(cores);
        this.sr = new ScheduleResult(this);
        
        if(type.equals("AllTasks"))
        {
            this.sr.startTaskTimeLineSchedule();
        }
        else if(type.equals("AllCores"))
        {
            this.sr.startCoreTimeLineSchedule();
        }
        
        this.tlr = new TimeLineResult(sr);
        this.setViewportView(tlr);
    }
    
    public ResultViewer(InfoWin win , Core c)
    {
        this.parent = win;
        this.init();
        this.Cores = new Vector<>();
        this.Cores.add(c);
        this.sr = new ScheduleResult(this);
        this.sr.startTaskTimeLineSchedule();
        this.tlr = new TimeLineResult(sr);
        this.setViewportView(tlr);
    }
    
    private void init()
    {
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.getHorizontalScrollBar().setUnitIncrement(16);
        this.getVerticalScrollBar().setUnitIncrement(16);
        this.scale = 1 ;
        
        this.getHorizontalScrollBar().addAdjustmentListener
        (new AdjustmentListener()
            {
                public void adjustmentValueChanged(AdjustmentEvent e)
                {
                    ResultViewer.this.tlr.repaint();
                }
            }
        );

        this.getVerticalScrollBar().addAdjustmentListener
        (new AdjustmentListener()
            {
                public void adjustmentValueChanged(AdjustmentEvent e)
                {
                    ResultViewer.this.tlr.repaint();
                }
            }
        );
    }
    
    public Vector<Core> getCores()
    {
        return this.Cores;
    }
    
    public Core getCore(int i)
    {
        return this.Cores.get(i);
    }
    
    public ScheduleResult getScheduleResult()
    {
        return this.sr;
    }
    
    public TimeLineResult getTimeLineResult()
    {
        return this.tlr;
    }
    
    public int getScale()
    {
        return this.scale;
    }
    
    public void setScale(int i)
    {
        this.scale = i;
    }
   
}
