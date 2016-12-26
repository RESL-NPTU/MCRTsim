/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import java.util.Vector;

/**
 *
 * @author ShiuJia
 */
public class CriticalSectionSet extends Vector<CriticalSection>
{
    public ResourcesSet getResourcesSet(Task t) // 取得所有t所會使用到的資源
    {
        ResourcesSet resourcesSet = new ResourcesSet();
        for(int i = 0; i < this.size(); i++)
        {
            resourcesSet.add(this.get(i).getResources());
        }
        return resourcesSet;
    }
}
