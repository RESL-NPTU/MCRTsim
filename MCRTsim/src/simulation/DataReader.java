/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
/**
 *
 * @author ShiuJia
 */
public class DataReader
{
    private DataSetting ds;
    Element root;
    
    public DataReader()
    {
        this.ds = new DataSetting();
    }
    
    public void read(String source) throws FileNotFoundException, IOException, DocumentException
    {
        SAXReader reader = new SAXReader();
        Document document = reader.read(source);
        root = document.getRootElement();
        switch(root.getName())
        {
            case "workload":
            {
                this.readWorkload();
                break;
            }
            case "processor":
            {
                this.readProcessor();
                break;
            }
            default:
                System.out.println("Input Document Error!!!!!");
        }
    }
    
    public void readWorkload() throws IOException
    {
        Iterator it = root.elementIterator();
        
        while(it.hasNext())
        {
            Element typeElement = (Element)it.next();
            
            switch(typeElement.getQualifiedName())
            {
                case "resources":
                {
                    this.creatResourcesSet(typeElement);
                    break;
                }
                case "task":
                {
                    this.creatTaskSet(typeElement);
                    break;
                }
            }
        }
    }
    
    private void creatResourcesSet(Element resourceElement)
    {
        Resources resources = new Resources();
        resources.setID(Integer.valueOf(resourceElement.attribute("ID").getText()));
        resources.setResourcesAmount(Integer.valueOf(resourceElement.attribute("quantity").getText()));
        this.ds.getResourceSet().add(resources);
    }
    
    private void creatTaskSet(Element taskElement)
    {
        Task task = new Task();
        task.setID(Integer.valueOf(taskElement.attribute("ID").getText()));
    //尚未加入    //task.setType(taskElement.attribute("type").getText());
        
        task.setEnterTime((int)(Double.parseDouble(taskElement.elementText("arrivalTime"))*100000));
        task.setPeriod((int)(Double.parseDouble(taskElement.elementText("period"))*100000));
        task.setRelativeDeadline((int)(Double.parseDouble(taskElement.elementText("relativeDeadline"))*100000));
        task.setComputationAmount((int)(Double.parseDouble(taskElement.elementText("computationAmount"))*100000));
        
        CriticalSectionSet css = new CriticalSectionSet();
        if(taskElement.element("criticalSections") != null)
        {
            Iterator cssIterator = taskElement.element("criticalSections").elementIterator("criticalSection");
            while(cssIterator.hasNext())
            {
                Element cssElement = (Element)cssIterator.next();
                CriticalSection c = new CriticalSection();
                c.setResources(this.ds.getResourceSet().getResources(Integer.valueOf(cssElement.attribute("resourceID").getText()) - 1));
                this.ds.getResourceSet().getResources(Integer.valueOf(cssElement.attribute("resourceID").getText()) - 1).getAccessSet().add(task);
                c.setStartTime((int)(Double.parseDouble(cssElement.attribute("startTime").getText())*100000));
                c.setEndTime((int)(Double.parseDouble(cssElement.attribute("endTime").getText())*100000));
                css.add(c);
            }
        }
        task.setCriticalSectionSet(css);
        task.setMaxProcessingSpeed(Double.valueOf(root.attribute("baseSpeed").getText()));
        task.setUtilization();
        this.ds.getTaskSet().add(task);
    }
    
    
    
    public void readProcessor() throws IOException//讀取處理器
    {
        Iterator it = root.elementIterator();
        
        //建立Processor
        this.ds.getProcessor().setProcessorModel(root.attribute("model").getText());
        this.ds.getProcessor().setDVFSType(root.attribute("DVFStype").getText());
        
        //建立Core
        while(it.hasNext())
        {
            Element processorElement = (Element)it.next();
            Vector<Core> cores = new Vector<Core>();
            DynamicVoltageRegulator dynamicVoltageRegulator = new DynamicVoltageRegulator();
            
            Iterator coreAtb = processorElement.attributeIterator();
            while(coreAtb.hasNext())
            {
                Attribute atb = (Attribute)coreAtb.next();
                switch(atb.getQualifiedName())
                {
                    case "quantity":
                        for(int i = 0 ; i < Integer.valueOf(atb.getText());i++)
                        {
                            cores.add(new Core());
                        }
                        break;
                        
                    case "type":
                        dynamicVoltageRegulator.setCoreType(atb.getText());
                        break;
                        
                    default:
                        System.out.println("setCoreAttribute Error!!!");
                }
            }
            
            
            Iterator coreElement = processorElement.elementIterator();
            while(coreElement.hasNext())
            {
                Element element = (Element)coreElement.next();
                switch(element.getQualifiedName())
                {
                    
                    case "availableSpeeds":
                        setAvailableSpeed(dynamicVoltageRegulator,element);
                        break;
                    case "powerConsumptionFunction":
                        setPowerConsumptionFunction(dynamicVoltageRegulator,element);
                        break;
                    default:
                        System.out.println("setCoreAttribute Error!!!");
                }
            }

            if(this.ds.getProcessor().isPerCore)
            {
                for(Core c : cores)
                {
                    DynamicVoltageRegulator DVR = new DynamicVoltageRegulator(dynamicVoltageRegulator);
                    c.setDynamicVoltageRegulator(DVR);
                    this.ds.getProcessor().addDynamicVoltageRegulator(DVR);
                }
            }
            else
            {
                for(Core c : cores)
                {
                    c.setDynamicVoltageRegulator(dynamicVoltageRegulator);
                }
                this.ds.getProcessor().addDynamicVoltageRegulator(dynamicVoltageRegulator);
            }
            
            this.ds.getProcessor().addCores(cores);
        }
    }
    
    private void setAvailableSpeed(DynamicVoltageRegulator dynamicVoltageRegulator, Element element)
    {
        Iterator it = element.elementIterator();
        while(it.hasNext())
        {
            Element ASElement = (Element)it.next();
            Speed speed = new Speed(); 

            //需多加入idle判斷
            speed.setSpeed(Integer.valueOf(ASElement.getText()));
            Iterator atb_it = ASElement.attributeIterator();
            if(atb_it.hasNext())
            {
                Attribute atb = (Attribute)atb_it.next();
                switch(atb.getQualifiedName())
                {
                    case "powerConsumption":
                        speed.setPowerConsumption(Double.valueOf(atb.getText()));
                    break;
                    default:
                        System.out.println("setAvailableSpeed Error!!!");
                }
            }
            dynamicVoltageRegulator.addSpeed(speed);

        }
    }
    
    
    private void setPowerConsumptionFunction(DynamicVoltageRegulator ss, Element element)
    {
        Iterator it = element.attributeIterator();
        while(it.hasNext())
        {
            Attribute PCFatb = (Attribute)it.next();
            switch(PCFatb.getQualifiedName())
            {
                case "alpha":
                    ss.setAlpha(Double.valueOf(PCFatb.getText()));
                    break;
                case "beta":
                    ss.setBeta(Double.valueOf(PCFatb.getText()));
                    break;
                case "gamma":
                    ss.setGamma(Double.valueOf(PCFatb.getText()));
                    break;
                default:
                    System.out.println("setProwerConsumptionFunction Error!!!!!");
            }
        }
    }
    
    public DataSetting getDataSetting()
    {
        return this.ds;
    }
}
