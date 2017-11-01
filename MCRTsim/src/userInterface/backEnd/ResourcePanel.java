/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface.backEnd;

import WorkLoad.CriticalSection;
import javax.swing.JPanel;

/**
 *
 * @author ShiuJia
 */
public class ResourcePanel extends JPanel
{
    private String resourcesID;
    private String resourceID;
    private String resourcesAmount;
    
    public ResourcePanel(CriticalSection cs)
    {
        super();
        resourcesID = String.valueOf(cs.getUseSharedResource().getID());
        resourcesAmount = String.valueOf(cs.getUseSharedResource().getResourcesAmount());
        resourceID = String.valueOf(cs.getResourceID());
        this.setToolTipText("R"+resourcesID+"(" + resourceID +"/" + resourcesAmount +")");
    }
    
    public String getResourcesID()
    {
        return this.resourcesID;
    }
    
    public String getResourceID()
    {
        return this.resourceID;
    }
            
    public String getResourcesAmount()
    {
        return this.resourcesAmount;
    }        
}