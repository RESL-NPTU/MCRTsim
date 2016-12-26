/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface.backEnd;

import javax.swing.JPanel;
import simulation.LockInfo;

/**
 *
 * @author ShiuJia
 */
public class ResourcePanel extends JPanel
{
    private String resourcesID;
    private String resourceID;
    private String resourcesAmount;
    
    public ResourcePanel(LockInfo lockInfo)
    {
        super();
        resourcesID = String.valueOf(lockInfo.getResources().getID());
        resourcesAmount = String.valueOf(lockInfo.getResources().getResourcesAmount());
        resourceID = String.valueOf(lockInfo.getResource().getID());
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