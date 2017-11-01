/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemEnvironment;

import WorkLoad.CoreSpeed;
import WorkLoad.CriticalSection;
import WorkLoad.SharedResource;
import WorkLoad.Task;
import WorkLoadSet.CoreSet;
import WorkLoadSet.DataSetting;
import java.util.Iterator;
import mcrtsim.Definition.DVFSType;
import static mcrtsim.Definition.magnificationFactor;
import mcrtsim.MCRTsimMath;
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
    private DataSetting dataSetting;
    private Element root;
    
    public DataReader()
    {
        this.dataSetting = new DataSetting();
    }
    
    public void loadSource(String sourcePath) throws DocumentException
    {
        SAXReader reader = new SAXReader();
        Document document = reader.read(sourcePath);
        
        this.root = document.getRootElement();
        switch(this.root.getName())
        {
            case "workload":
            {
                this.createWorkload();
                break;
            }
            case "processor":
            {
                this.createProcessor();
                break;
            }
            default:
            {
                System.out.println("You loaded source is not supported(It's not Workload or Processor):" + this.root.getName().toString());
            }
        }
    }
    
    /*Workload*/
    private void createWorkload()
    {
        Iterator it = root.elementIterator();
        this.dataSetting.getTaskSet().setMaxProcessingSpeed(Double.valueOf(this.root.attribute("baseSpeed").getText()));
        while(it.hasNext())
        {
            Element typeElement = (Element)it.next();
            
            switch(typeElement.getQualifiedName())
            {
                case "task":
                {
                    this.createTask(typeElement);
                    break;
                }
                
                case "resources":
                {
                    this.createSharedResource(typeElement);
                    break;
                }
            }
        }
    }
    
    private void createSharedResource(Element re)
    {
        SharedResource sharedResource = new SharedResource();
        sharedResource.setID(Integer.valueOf(re.attribute("ID").getText()));
        sharedResource.createResources(Integer.valueOf(re.attribute("quantity").getText()));
        this.dataSetting.addSharedResource(sharedResource);
    }
    
    private void createTask(Element te)
    {
        Task task = new Task();
        task.setID(Integer.valueOf(te.attribute("ID").getText()));
        //需建置 type="periodic"
        
        MCRTsimMath math = new MCRTsimMath();
        task.setEnterTime((long)(Double.parseDouble(te.elementText("arrivalTime"))*magnificationFactor));
        task.setPeriod((long)(Double.parseDouble(te.elementText("period"))*magnificationFactor));
        task.setRelativeDeadline((long)(Double.parseDouble(te.elementText("relativeDeadline"))*magnificationFactor));
        task.setComputationAmount((long)(Double.parseDouble(te.elementText("computationAmount"))*magnificationFactor));
        
        if(te.element("criticalSections") != null)
        {
            Iterator cssIt = te.element("criticalSections").elementIterator("criticalSection");
            while(cssIt.hasNext())
            {
                Element cse = (Element)cssIt.next();
                CriticalSection cs = new CriticalSection();
                cs.setUseSharedResource(this.dataSetting.getSharedResource(Integer.valueOf(cse.attribute("resourceID").getText()) - 1));
                this.dataSetting.getSharedResource(Integer.valueOf(cse.attribute("resourceID").getText()) - 1).addAccessTask(task);
                cs.setRelativeStartTime((long)(Double.parseDouble(cse.attribute("startTime").getText())*magnificationFactor));
                cs.setRelativeEndTime((long)(Double.parseDouble(cse.attribute("endTime").getText())*magnificationFactor));
                task.addCriticalSection(cs);
            }
        }
        task.setParentTaskSet(this.dataSetting.getTaskSet());
        this.dataSetting.addTask(task);
    }
    
    /*Processor*/
    private void createProcessor()
    {
        Iterator it = root.elementIterator();
        this.dataSetting.getProcessor().setModelName(root.attribute("model").getText());
        this.dataSetting.getProcessor().getDynamicVoltageRegulator().setDVFSType(root.attribute("DVFStype").getText());
        
        
        while(it.hasNext())
        {
            /*Core*/
            Element processorElement = (Element)it.next();
            Iterator coreAtb = processorElement.attributeIterator();
            CoreSet coreSet = new CoreSet();
            
            while(coreAtb.hasNext())
            {
                Attribute atb = (Attribute)coreAtb.next();
                switch(atb.getQualifiedName())
                {
                    case "quantity":
                    {
                        for(int i = 0; i < Integer.valueOf(atb.getText()); i++)
                        {
                            Core core = new Core();
                            core.setParentProcessor(this.dataSetting.getProcessor());
                            core.setParentCoreSet(coreSet);
                            coreSet.addCore(core);
                            this.dataSetting.getProcessor().addCore(core);
                        }
                        break;
                    }
                    
                    case "type":
                    {
                        coreSet.setCoreType(atb.getText());
                        break;
                    }
                    default:
                        System.out.println("SetCoreAttribute Error!!");
                }
            }
            
            /*CoreSpeed*/
            Iterator coreElement = processorElement.elementIterator();
            while(coreElement.hasNext())
            {
                Element element = (Element)coreElement.next();
                switch(element.getQualifiedName())
                {
                    case "availableSpeeds":
                    {
                        this.setAvailableSpeed(element ,coreSet);
                        break;
                    }
                    case "powerConsumptionFunction":
                    {
                        this.setPowerConsumptionFunction(element ,coreSet);
                        break;
                    }
                    default:
                    {
                        System.out.println("SeetCoreAttribute Error!!");
                    }
                }
            }
            /**/
            if(this.dataSetting.getProcessor().getDynamicVoltageRegulator().getDVFSType().equals(DVFSType.PerCore))
            {
                for(int i = 0 ; i<coreSet.size() ; i++)
                {
                    CoreSet cSet = new CoreSet(coreSet);
                    cSet.addCore(coreSet.getCore(i));
                    this.dataSetting.getProcessor().addCoreSet(cSet);
                }
            }
            else
            {
                this.dataSetting.getProcessor().addCoreSet(coreSet);
            }
        }
        System.out.println("!!!!!CoreSet::" + this.dataSetting.getProcessor().getCoresSets().size());
    }
    
    private void setPowerConsumptionFunction(Element e ,CoreSet coreSet)
    {
        Iterator it = e.attributeIterator();
        while(it.hasNext())
        {
            Attribute PCFatb = (Attribute)it.next();
            switch(PCFatb.getQualifiedName())
            {
                case "alpha":
                    coreSet.setAlphaValue(Double.valueOf(PCFatb.getText()));
                    break;
                case "beta":
                    coreSet.setBetaValue(Double.valueOf(PCFatb.getText()));
                    break;
                case "gamma":
                    coreSet.setGammaValue(Double.valueOf(PCFatb.getText()));
                    break;
                default:
                    System.out.println("setPowerConsumptionFunction Error!!!!!");
            }
        }
    }
    
    private void setAvailableSpeed(Element e,CoreSet coreSet)
    {
        Iterator it = e.elementIterator();
        while(it.hasNext())
        {
            CoreSpeed s = new CoreSpeed();
            Element sElement = (Element)it.next();
            
            if(!sElement.getText().equals("idle"))
            {
                s.setSpeed(Double.valueOf(sElement.getText()));
            }
            else
            {
                s.setSpeed(0);
            }
            //需多加入idle判斷
            Iterator atb_it = sElement.attributeIterator();
            while(atb_it.hasNext())
            {
                Attribute atb = (Attribute)atb_it.next();
                switch(atb.getQualifiedName())
                {
                    case "powerConsumption":
                        s.setPowerConsumption(Double.valueOf(atb.getText()));
                    break;
                    default:
                        System.out.println("setAvailableSpeed Error!!!");
                }
            }
            
            
            coreSet.addCoreSpeed(s);
        }
    }
    
    public DataSetting getDataSetting()
    {
        return this.dataSetting;
    }
}
