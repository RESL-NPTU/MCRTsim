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
            case "Workload":
            {
                this.readWorkload();
                break;
            }
            case "Processor":
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
                case "Resources":
                {
                    this.creatResourcesSet(typeElement);
                    break;
                }
                case "Task":
                {
                    this.creatTaskSet(typeElement);
                    break;
                }
            }
        }
    }
    
    public void readProcessor() throws IOException
    {
        Iterator it = root.elementIterator();
        
        //建立Processor
        //System.out.println("ProcessorModel:" + root.attribute("ProcessorModel").getText());
        this.ds.getProcessor().setProcessorModel(root.attribute("Model").getText());
        this.ds.getProcessor().setDVFSType(root.attribute("DVFSType").getText());
        
        //建立Core
        while(it.hasNext())
        {
            Element processorElement = (Element)it.next();
            //System.out.println("Core:");
            Vector<Core> cores = new Vector<Core>();
            DynamicVoltageRegulator dynamicVoltageRegulator = new DynamicVoltageRegulator();
            //System.out.println("  CoreID:" + coreElement.attribute("ID").getText());
//            for(int i = 0 ; i < Integer.valueOf(processorElement.attribute("Number").getText()) ;i++)
//            {
//                cores.add(new Core());
//            }
//            
            Iterator coreAtb = processorElement.attributeIterator();
            while(coreAtb.hasNext())
            {
                Attribute atb = (Attribute)coreAtb.next();
                switch(atb.getQualifiedName())
                {
                    case "Number":
                        for(int i = 0 ; i < Integer.valueOf(atb.getText());i++)
                        {
                            cores.add(new Core());
                        }
                        break;
                        
                    case "Type":
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
                    
                    case "AvailableSpeed":
                        setAvailableSpeed(dynamicVoltageRegulator,element);
                        break;
                    case "PowerConsumptionFunction":
                        setPowerConsumptionFunction(dynamicVoltageRegulator,element);
                        break;
                    default:
                        System.out.println("setCoreAttribute Error!!!");
                }
            }

//            for(int i = 0; i < ss.size(); i++)
//            {
//                ss.get(i).setNormalization((double)ss.get(i).getFrequency() / ss.get(ss.size() - 1).getFrequency());
//                //System.out.println("NF = " + ss.get(i).getNormalization());
//            }
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
    
    private void creatResourcesSet(Element resourceElement)
    {
        Resources resources = new Resources();
        resources.setID(Integer.valueOf(resourceElement.attribute("ID").getText()));
        resources.setResourcesAmount(Integer.valueOf(resourceElement.elementText("ResourceAmount")));
        //System.out.println("Resoruce"+res.getID()+" = "+ Integer.valueOf(resourceElement.elementText("ResourceAmount")));
        this.ds.getResourceSet().add(resources);
    }
    
    private void creatTaskSet(Element taskElement)
    {
        Task task = new Task();
        //System.out.println("  TaskID:" + taskElement.attribute("ID").getText());
        task.setID(Integer.valueOf(taskElement.attribute("ID").getText()));
        //System.out.println("  TaskPeriod:" + taskElement.elementText("Period"));
        task.setEnterTime((int)(Double.parseDouble(taskElement.elementText("EnterTime"))*100000));
        task.setPeriod((int)(Double.parseDouble(taskElement.elementText("Period"))*100000));
        //System.out.println("  TaskDeadline:" + taskElement.elementText("RelativeDeadline"));
        task.setRelativeDeadline((int)(Double.parseDouble(taskElement.elementText("RelativeDeadline"))*100000));
        //System.out.println("  TaskComputationAmount:" + taskElement.elementText("ComputationAmount"));
        task.setComputationAmount((int)(Double.parseDouble(taskElement.elementText("ComputationAmount"))*100000));

        Iterator cssIterator = taskElement.elementIterator("CriticalSection");
        CriticalSectionSet css = new CriticalSectionSet();
        while(cssIterator.hasNext())
        {
            Element cssElement = (Element)cssIterator.next();
            CriticalSection c = new CriticalSection();
            //System.out.println("    Resource:" + cssElement.elementText("Resource"));
            c.setResources(this.ds.getResourceSet().getResources(Integer.valueOf(cssElement.elementText("Resource")) - 1));
            this.ds.getResourceSet().getResources(Integer.valueOf(cssElement.elementText("Resource")) - 1).getAccessSet().add(task);
            //System.out.println("      StartTime:" + cssElement.elementText("StartTime"));
            c.setStartTime((int)(Double.parseDouble(cssElement.elementText("StartTime"))*100000));
            //System.out.println("      EndTime:" + cssElement.elementText("EndTime"));
            c.setEndTime((int)(Double.parseDouble(cssElement.elementText("EndTime"))*100000));
            css.add(c);
        }
        task.setCriticalSectionSet(css);
        //System.out.println("  Frequency:" + root.attribute("Frequency").getText());
        task.setMaxProcessingSpeed(Double.valueOf(root.attribute("Frequency").getText()));
        task.setUtilization();
        this.ds.getTaskSet().add(task);
    }
    
    private void setSpeedRange(DynamicVoltageRegulator ss, Element element)
    {
        Iterator it = element.elementIterator();
        while(it.hasNext())
        {
            Element SRElement = (Element)it.next();
            Speed speed = new Speed();
            switch(SRElement.getQualifiedName())
            {
                case "MaxFrequency":
                    speed.setFrequency(Double.valueOf(SRElement.getText()));
                    ss.addSpeed(speed);
                    break;
                case "MinFrequency":
                    speed.setFrequency(Double.valueOf(SRElement.getText()));
                    ss.addSpeed(speed);
                    break;
                default:
                    System.out.println("setSpeedRange Error!!!!!");
            }
        }
    }
    
    private void setAvailableSpeed(DynamicVoltageRegulator dynamicVoltageRegulator, Element element)
    {
        Iterator it = element.elementIterator();
        while(it.hasNext())
        {
            Element ASElement = (Element)it.next();
            
            switch(ASElement.getQualifiedName())
            {
                case "SpeedRange":
                    setSpeedRange(dynamicVoltageRegulator,ASElement);
                    break;
                case "Speed":
                    setSpeed(dynamicVoltageRegulator,ASElement);
                    break;
                default:
                    System.out.println("setAvailableSpeed Error!!!!!");
            }
        }
    }
    
    private void setSpeed(DynamicVoltageRegulator dynamicVoltageRegulator, Element element)
    {
        Iterator it = element.elementIterator();
        //System.out.println(element.getQualifiedName());
        Speed speed = new Speed(); 
        while(it.hasNext())
        {
            Element SElement = (Element)it.next();
            switch(SElement.getQualifiedName())
            {
                case "Frequency":
                    speed.setFrequency(Integer.valueOf(SElement.getText()));
                    break;
                case "ProwerConsumption":
                    speed.setPowerConsumption(Double.valueOf(SElement.getText()));
                    break;
                default:
                    System.out.println("setSpeed Error!!!!!");
            }
        }
        
        dynamicVoltageRegulator.addSpeed(speed);
    }
    
    private void setPowerConsumptionFunction(DynamicVoltageRegulator ss, Element element)
    {
        Iterator it = element.elementIterator();
        while(it.hasNext())
        {
            Element PCFElement = (Element)it.next();
            switch(PCFElement.getQualifiedName())
            {
                case "Alpha":
                    ss.setAlpha(Double.valueOf(PCFElement.getText()));
                    break;
                case "Beta":
                    ss.setBeta(Double.valueOf(PCFElement.getText()));
                    break;
                case "Gamma":
                    ss.setGamma(Double.valueOf(PCFElement.getText()));
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
