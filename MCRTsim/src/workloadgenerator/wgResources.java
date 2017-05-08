/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workloadgenerator;

import java.util.Vector;

/**
 *
 * @author YC
 */
public class wgResources extends Vector<wgResource>
{
    public wgResourcesSet parent;
    private final String resourcesHeader = "resources";
    private final String IDHeader = "ID";
    private int ID = 0;
    private final String resourceAmountHeader = "quantity";
    private int resourceAmount = 0;
    
    public wgResources(wgResourcesSet p)
    {
        super();
        this.parent = p;
    }
    
    public void addResource(wgResource resource)
    {
        this.add(resource);
        resource.setID(this.size());
        this.resourceAmount = this.size();
    }
    
/*setValue*/
    public void setID(int id)
    {
        this.ID = id;
    }
    
    public void setResourceAmount(int ResourceAmount)
    {
        this.resourceAmount = ResourceAmount;
    }
    
/*getValue*/    
    public int getID()
    {
        return this.ID;
    }
    
    public int getResourceAmount()
    {
        return this.resourceAmount;
    }
    
    public String getResourcesHeader()
    {
        return this.resourcesHeader;
    }
    
    public String getIDHeader()
    {
        return this.IDHeader;
    }
    
    public String getResourceAmountHeader()
    {
        return this.resourceAmountHeader;
    }
}
