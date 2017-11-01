/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scriptsetter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
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
public class ScriptSetter extends JFrame
{
    public SimulationViewer parent;
    private JSplitPane splitPane;
    private JSplitPane bottomSplitPane ;
    private ScriptPanel scriptPanel;
    private JTabbedPane tableTabbedPane = new JTabbedPane();//切換scriptTable的標籤頁面物件
   // private ScriptTable scriptTable;
    private SciptToolBar sciptToolBar;
   // private Vector<Script> scriptSet;
    
    public ScriptSetter(SimulationViewer SV)
    {
        this.parent = SV;
        this.init();
    //    this.scriptSet = new Vector();
        this.revalidate();
        
        
        this.tableTabbedPane.addContainerListener
        (
            new ContainerListener() 
            {

                @Override
                public void componentAdded(ContainerEvent e) 
                {
                    for(int i = 0; i<tableTabbedPane.getComponentCount() ; i++)
                    {
                        tableTabbedPane.setTitleAt(i, Integer.toString(i+1)+"("+((ScriptTable)tableTabbedPane.getComponent(i)).getScriptCount()+")");
                        
                        ((ScriptTable)tableTabbedPane.getComponent(i)).setGroupID(Integer.toString(i+1));
                    }
                    
                    
                    System.out.println("A"+tableTabbedPane.getComponentCount());
                }

                @Override
                public void componentRemoved(ContainerEvent e) 
                {
                    for(int i = 0; i<tableTabbedPane.getComponentCount() ; i++)
                    {
                        tableTabbedPane.setTitleAt(i, Integer.toString(i+1)+"("+((ScriptTable)tableTabbedPane.getComponent(i)).getScriptCount()+")");
                        
                        ((ScriptTable)tableTabbedPane.getComponent(i)).setGroupID(Integer.toString(i+1));
                    }
                    
                    System.out.println("B"+tableTabbedPane.getComponentCount());
                }
                
            }
        );
    }
    
    private void init() 
    {
        this.setTitle("Script Setter");
        this.setBounds(100, 100, 800, 500);
        this.setMinimumSize(new Dimension(600, 280));
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setVisible(false);
        this.setLayout(new BorderLayout()); 
        
        
        this.splitPane = new JSplitPane();
        this.add(splitPane,BorderLayout.CENTER);
        this.splitPane.setOrientation(0); //上下 切割方法
        this.splitPane.setContinuousLayout(false);//??
        this.splitPane.setDividerLocation(this.getHeight()/3);
        
        this.bottomSplitPane = new JSplitPane();
        this.bottomSplitPane.setOrientation(1);//左右 切割方法
        this.bottomSplitPane.setContinuousLayout(false);
        this.bottomSplitPane.setDividerLocation(this.getWidth()/2);
        this.splitPane.setBottomComponent(bottomSplitPane);
        
        this.splitPane.setTopComponent(this.tableTabbedPane);
        ScriptTable scriptTable = new ScriptTable(this);
        scriptTable.setGroupID("1");//預設第一個scriptTable之ID;
        this.tableTabbedPane.addTab("1(0)",scriptTable);
        
        this.scriptPanel = new ScriptPanel(this);
        this.bottomSplitPane.setTopComponent(this.scriptPanel);
        
        this.sciptToolBar = new SciptToolBar(this);
        this.bottomSplitPane.setBottomComponent(this.sciptToolBar);
        
        
    }
    
    public JSplitPane getSplitPane()
    {
        return this.splitPane;
    }
    
    public JSplitPane getBottomSplitPane()
    {
        return this.bottomSplitPane;
    }
    
    public JTabbedPane getTableTabbedPane()
    {
        return this.tableTabbedPane;
    }
    
    public ScriptTable getScriptTable(int i)
    {
        return (ScriptTable)this.tableTabbedPane.getComponent(i);
    }
    
    public ScriptPanel getScriptPanel()
    {
        return this.scriptPanel;
    }
    
    public SciptToolBar getSciptToolBar()
    {
        return this.sciptToolBar;
    }
    
    public void addGroup()
    {
        this.tableTabbedPane.addTab(Integer.toString(this.tableTabbedPane.getComponentCount()+1), new ScriptTable(this));
    }
    
    public void removeGroup()
    {
        if(this.tableTabbedPane.getComponentCount()>0)
        {
            this.tableTabbedPane.remove(this.tableTabbedPane.getSelectedIndex());
        }
    }
    
    public void addScript()
    {
        if(this.tableTabbedPane.getComponentCount()>0)
        {
            ScriptTable st = (ScriptTable)this.tableTabbedPane.getSelectedComponent();
            st.addScript(this.scriptPanel);
            this.tableTabbedPane.setTitleAt(Integer.valueOf(st.getGroupID())-1,st.getGroupID() + "("+st.getScriptCount()+")");
        }
    }
    
    public void modifyScript()
    {
        ((ScriptTable)this.tableTabbedPane.getSelectedComponent()).modifyScript(this.scriptPanel);
    }
    
    public void removeScript()
    {
        if(this.tableTabbedPane.getComponentCount()>0 && ((ScriptTable)this.tableTabbedPane.getSelectedComponent()).getScriptCount()>0)
        {
            ScriptTable st = (ScriptTable)this.tableTabbedPane.getSelectedComponent();
            st.removeScript();
            this.tableTabbedPane.setTitleAt(Integer.valueOf(st.getGroupID())-1,st.getGroupID() + "("+st.getScriptCount()+")");
        }
    }
    
    public void startScript() throws FileNotFoundException
    {
        try 
        {
            FileDialog fileDialog = new FileDialog(new JFrame(), "new", FileDialog.SAVE);
            
            fileDialog.setVisible(true);
            if(fileDialog.getFile() != null)//如果取消存檔則fileDialog.getFile()會是null
            {
                for(int i = 0 ; i<this.tableTabbedPane.getComponentCount() ; i++)
                {
                    ScriptTable st = (ScriptTable)this.tableTabbedPane.getComponent(i);
                    for(Script s : st.getScriptSet())
                    {
                        s.removeAllScriptResult();
                        this.parent.startScript(s);
                    }
                }
                
                for(int i = 0 ; i<this.tableTabbedPane.getComponentCount() ; i++)
                {
                    ScriptTable st = (ScriptTable)this.tableTabbedPane.getComponent(i);
                    System.out.println("GroupID :"+st.getGroupID());
                    for(Script s : st.getScriptSet())
                    {
                        System.out.println(", ScriptID :"+s.getID() + ", ScriptResultSize :"+ s.getScriptResultSet().size());
                    }
                }
                
                //output showInfo
                for(int i = 0 ; i<this.tableTabbedPane.getComponentCount() ; i++)
                {
                    ScriptTable st = (ScriptTable)this.tableTabbedPane.getComponent(i);
                    for(Script s : st.getScriptSet())
                    {
                        for(ScriptResult sr : s.getScriptResultSet())
                        {
                            sr.showInfo();
                        }
                    }
                }
                
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); 

                DOMSource source = new DOMSource(XMLWriter.creatXML(this));
                File file = new File(fileDialog.getDirectory()+fileDialog.getFile()+".xml");
                StreamResult result = new StreamResult(file);
                transformer.transform(source, result);
                
            }
        }
        catch (TransformerException ex) 
        {
            Logger.getLogger(ScriptSetter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
