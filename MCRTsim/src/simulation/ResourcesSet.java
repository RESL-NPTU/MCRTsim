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
public class ResourcesSet extends Vector<Resources>
{
    public Resources getResources(int i)
    {
        return this.get(i);
    }
}
