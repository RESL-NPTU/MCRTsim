/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workloadgenerator;

/**
 *
 * @author YC
 */
public class wgResource 
{
    public wgResources parent;
    private final String resourceHeader = "Resource";
    private final String IDHeader = "ID";
    private int ID = 0;
    
    public wgResource (wgResources p)
    {
        this.parent = p;
    }
    
/*setValue*/
    public void setID(int id)
    {
        this.ID = id;
    }
    
/*getValue*/    
    public int getID()
    {
        return this.ID;
    }
    
    public String getResourcAHeader()
    {
        return this.resourceHeader;
    }
    
    public String getIDHeader()
    {
        return this.IDHeader;
    }
 
}
