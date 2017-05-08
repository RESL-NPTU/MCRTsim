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
public class wgResourcesSet extends Vector<wgResources>
{
    public wgWorkload parent;
    
    public wgResourcesSet ()
    {
        super();
    }
    
    public wgResourcesSet (wgWorkload p)
    {
        super();
        this.parent = p;
    }
    
    public void addResources(wgResources r)
    {
        this.add(r);
        r.setID(this.size());
    }
    
/*setValue*/ 
    
/*getValue*/
    public wgResources getResources(int i)
    {
        return this.get(i);
    }
}
