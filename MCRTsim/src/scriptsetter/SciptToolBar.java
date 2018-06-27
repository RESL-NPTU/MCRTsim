/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scriptsetter;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 *
 * @author YC
 */
public class SciptToolBar extends JToolBar
{
    public ScriptSetter parent;
    private JButton addGroupBtn,removeGroupBtn,addSciptBtn,modifySciptBtn,removeSciptBtn,startBtn;
    
    public SciptToolBar(ScriptSetter s)
    {
        this.parent = s;
        this.init();
        this.revalidate();
        
        this.addGroupBtn.addMouseListener
        (
            new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    SciptToolBar.this.parent.addGroup();
                }
            }
        );
        
        this.removeGroupBtn.addMouseListener
        (
            new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    SciptToolBar.this.parent.removeGroup();
                }
            }
        );
        
        
        this.addSciptBtn.addMouseListener
        (
            new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    SciptToolBar.this.parent.addScript();
                }
            }
        );
        
        this.modifySciptBtn.addMouseListener
        (
            new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    SciptToolBar.this.parent.modifyScript();
                }
            }
        );
        
        this.removeSciptBtn.addMouseListener
        (
            new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    SciptToolBar.this.parent.removeScript();
                }
            }
        );
        
        this.startBtn.addMouseListener
        (
            new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    try {
                        SciptToolBar.this.parent.startScript();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(SciptToolBar.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        );
    }
    
    private void init()
    {
        this.setFloatable(false);
        this.setLayout(new GridLayout(3,2));
        
        this.addGroupBtn = new JButton("AddGroup");
        this.removeGroupBtn = new JButton("RemoveGroup");
        this.addSciptBtn = new JButton("AddScipt");
        this.modifySciptBtn = new JButton("ModifyScipt");
        this.removeSciptBtn = new JButton("RemoveScipt");
        this.startBtn = new JButton("Start Script");
        this.startBtn.setForeground(Color.red);
        this.add(this.addGroupBtn);
        this.add(this.removeGroupBtn);
        this.add(this.addSciptBtn);
        this.add(this.removeSciptBtn);
        this.add(this.modifySciptBtn);
        this.add(this.startBtn);
    }
}
