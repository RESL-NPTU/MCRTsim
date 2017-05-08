/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taskToCore.implementation;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import simulation.Core;
import simulation.DataSetting;
import simulation.Resource;
import simulation.Resources;
import simulation.ResourcesSet;
import simulation.Task;
import simulation.TaskSet;
import taskToCore.TaskToCore;

/**
 *
 * @author ShiuJia
 */
public class SimilarityBased extends TaskToCore
{
    Vector<tempSet> tsSet;
    
    public class tempSet
    {
        Vector<tempSet> fullSet = new Vector<tempSet>();
        TaskSet ts = new TaskSet();
        ResourcesSet rs = new ResourcesSet();
        
        public tempSet()
        {
            
        }
        
        public void addTask(Task t)
        {
            ts.add(t);
        }
        
        public void addResource(Resources r)
        {
            rs.add(r);
        }
        
        public double getUtilization()
        {
            return ts.getUtilization();
        }
        
        public void addFullSet(tempSet f)
        {
            this.fullSet.add(f);
        }
    }

    public SimilarityBased(DataSetting ds)
    {
        super(ds);
        this.setName("Similarity-based");
    }

    @Override
    public void assign()
    {
        tsSet = new Vector<tempSet>();

        for(int i = 0; i < this.getDataSetting().getTaskSet().size(); i++)
        {
            tempSet temp = new tempSet();
            temp.addTask(this.getDataSetting().getTaskSet().getTask(i));
            for(int j = 0; j < this.getDataSetting().getTaskSet().getTask(i).getCriticalSectionSet().getResourcesSet(this.getDataSetting().getTaskSet().getTask(i)).size(); j++)
            {
                temp.addResource(this.getDataSetting().getTaskSet().getTask(i).getCriticalSectionSet().getResourcesSet(this.getDataSetting().getTaskSet().getTask(i)).getResources(j));
            }
            tsSet.add(temp);
        }
        
        boolean quit = false;
        while(!quit)
        {
            int array[][] = new int[tsSet.size()][tsSet.size()];
            int max = Integer.MIN_VALUE;
            int x = 0;
            int y = 0;
            int z = 0;
            
            for(int i = 0; i < tsSet.size() - 1; i++)
            {
                for(int j = i + 1; j < tsSet.size(); j++)
                {
                    if(!tsSet.get(i).fullSet.contains(tsSet.get(j)))
                    {
                        array[i][j] = res(tsSet.get(i).rs, tsSet.get(j).rs);
                        array[j][i] = array[i][j];
                    
                        z = res(tsSet.get(i).rs, tsSet.get(j).rs);
                    
                        if(z > max)
                        {
                            max = z;
                            x = i;
                            y = j;
                        }
                    }
                    else
                    {
                        array[i][j] = 0;
                        array[j][i] = 0;
                    }
                }
            }
            
            if(max > 0)
            {
                if(tsSet.get(x).getUtilization() + tsSet.get(y).getUtilization() <= 1)//U Ceiling
                {
                    for(int i = 0; i < tsSet.get(y).ts.size(); i++)
                    {
                        tsSet.get(x).ts.add(tsSet.get(y).ts.getTask(i));
                    }
                    for(int i = 0; i < tsSet.get(y).rs.size(); i++)
                    {
                        if(!tsSet.get(x).rs.contains(tsSet.get(y).rs.getResources(i)))
                        {
                            tsSet.get(x).rs.add(tsSet.get(y).rs.getResources(i));
                        }
                    }
                    tsSet.remove(y);
                }
                else//U Ceiling
                {
                    tsSet.get(x).addFullSet(tsSet.get(y));
                    tsSet.get(y).addFullSet(tsSet.get(x));
                }
            }
            else
            {
                quit = true;
            }
        }
        
        System.out.println("Step1:");
        int q = 0;
        for(Core c : this.getDataSetting().getProcessor().getCores())
        {
            System.out.println("C" + c.getID());
            if(tsSet.size() > q)
            {
                for(Task t : tsSet.get(q).ts)
                {
                    System.out.println("   T" + t.getID());
                }
                c.addTaskSet(tsSet.get(q++).ts);
            }
        }
        
        quit = false;
        while(this.getDataSetting().getProcessor().getCores().size() < tsSet.size() && !quit)
        {
            quit = true;
            for(int i = 0; i < this.tsSet.size(); i++)
            {   
                if(this.tsSet.get(i).getUtilization() < 1)
                {
                    for(int j = 0; j < this.tsSet.size(); j++)
                    {
                        if(this.tsSet.get(i) != this.tsSet.get(j) && !this.tsSet.get(i).fullSet.contains(this.tsSet.get(j)))
                        {
                            quit = false;
                            if(this.tsSet.get(i).getUtilization() + this.tsSet.get(j).getUtilization() <= 1)
                            {
                                for(int x = 0; x < this.tsSet.get(j).ts.size(); x++)
                                {
                                    this.tsSet.get(i).ts.add(this.tsSet.get(j).ts.getTask(x));
                                }
                                for(int x = 0; x < this.tsSet.get(j).rs.size(); x++)
                                {
                                    if(!this.tsSet.get(i).rs.contains(this.tsSet.get(j).rs.getResources(x)))
                                    {
                                        this.tsSet.get(i).rs.add(this.tsSet.get(j).rs.getResources(x));
                                    }
                                }
                                tsSet.remove(this.tsSet.get(j));
                                j--;
                            }
                            else
                            {
                                this.tsSet.get(i).addFullSet(this.tsSet.get(j));
                                this.tsSet.get(j).addFullSet(this.tsSet.get(i));
                            }
                        }
                    }
                }
            }
        }
        
        System.out.println("Step2:");
        q = 0;
        for(Core c : this.getDataSetting().getProcessor().getCores())
        {
            System.out.println("C" + c.getID());
            if(tsSet.size() > q)
            {
                for(Task t : tsSet.get(q).ts)
                {
                    System.out.println("   T" + t.getID());
                }
                c.addTaskSet(tsSet.get(q++).ts);
            }
        }
        
        while(this.getDataSetting().getProcessor().getCores().size() < tsSet.size())
        {
            Double tempU1 = Double.MAX_VALUE;
            tempSet tempTs1 = null;
            Double tempU2 = Double.MAX_VALUE;
            tempSet tempTs2 = null;
            
            for(tempSet tts : tsSet)
            {
                if(tts.getUtilization() < tempU1)
                {
                    if(tempU1 < tempU2)
                    {
                        tempU2 = tempU1;
                        tempTs2 = tempTs1;
                    }
                    tempU1 = tts.getUtilization();
                    tempTs1 = tts;
                }
                else if(tts.getUtilization() < tempU2)
                {
                    tempU2 = tts.getUtilization();
                    tempTs2 = tts;
                }
            }
            
            for(int i = 0; i < tempTs2.ts.size(); i++)
            {
                tempTs1.ts.add(tempTs2.ts.getTask(i));
            }
            for(int i = 0; i < tempTs2.rs.size(); i++)
            {
                if(!tempTs1.rs.contains(tempTs2.rs.getResources(i)))
                {
                    tempTs1.rs.add(tempTs2.rs.getResources(i));
                }
            }
            tsSet.remove(tempTs2);
        }
        
        System.out.println("Step3:");
        q = 0;
        for(Core c : this.getDataSetting().getProcessor().getCores())
        {
            System.out.println("C" + c.getID());
            if(tsSet.size() > q)
            {
                for(Task t : tsSet.get(q).ts)
                {
                    System.out.println("   T" + t.getID());
                }
                c.addTaskSet(tsSet.get(q++).ts);
            }
        }
    }
    
    public int res(ResourcesSet r1, ResourcesSet r2)
    {
        int num = 0;
        
        for(int x = 0; x < r1.size(); x++)
        {
            for(int y = 0; y < r2.size(); y++)
            {
                if(r1.get(x).getID() == r2.get(y).getID())
                {
                    num++;
                }
            }
        }
        return num;
    }
}
