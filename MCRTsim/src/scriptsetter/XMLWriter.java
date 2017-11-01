/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scriptsetter;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 *
 * @author YC
 */
public class XMLWriter 
{
    public XMLWriter()
    {
        
    }

    public static Document creatXML(ScriptSetter ss)
    {
        try 
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
        /*創建statisticalResult根元素*/
            Element statisticalResultElement = doc.createElement("statisticalResult");

            doc.appendChild(statisticalResultElement);
            statisticalResultElement.setAttribute("numGroup", String.valueOf(ss.getTableTabbedPane().getComponentCount()));
        /*創建Group*/
            for(int i = 0 ; i < ss.getTableTabbedPane().getComponentCount() ; i++)
            {
                ScriptTable st = (ScriptTable)ss.getTableTabbedPane().getComponent(i);
                Element groupElement = doc.createElement("group");
                groupElement.setAttribute("groupID", st.getGroupID());
                groupElement.setAttribute("numResult", String.valueOf(st.getScriptCount()));
                
                for(int j = 0 ; j < st.getScriptCount() ; j++)
                {
                    Script s = st.getScriptSet().get(j);
                    Element resultElement = doc.createElement("result");
                    resultElement.setAttribute("resultID",s.getID());
                    
                    resultElement.setAttribute("partitionAlgorithm",s.getPartitionAlgorithm());
                    resultElement.setAttribute("DVFSMethod",s.getDVFSMethod());
                    resultElement.setAttribute("schedulingAlorithm",s.getSchedulingAlorithm());
                    resultElement.setAttribute("CCProtocol",s.getCCProtocol());
                    resultElement.setAttribute("simulationTime",s.getSimulationTime());
                    resultElement.setAttribute("workloadCount",String.valueOf(s.getWorkloadCount()));
                    resultElement.setAttribute("taskCount",String.valueOf(s.getAverageTaskCount()));
                    
                    Element element;
                    
                    element = doc.createElement("schedulableCount");
                    element.appendChild(doc.createTextNode(String.valueOf(s.getSchedulableCount())));
                    resultElement.appendChild(element);
                    
                    element = doc.createElement("nonSchedulableCount");
                    element.appendChild(doc.createTextNode(String.valueOf(s.getNonSchedulableCount())));
                    resultElement.appendChild(element);
                    
                    
                    element = doc.createElement("powerConsumption");
                    element.appendChild(doc.createTextNode(String.valueOf(s.getAveragePowerConsumption())));
                    resultElement.appendChild(element);
                    
                    element = doc.createElement("jobCompeletedCount");
                    element.appendChild(doc.createTextNode(String.valueOf(s.getAverageJobCompeletedCount())));
                    resultElement.appendChild(element);
                    
                    element = doc.createElement("jobMissDeadlineCount");
                    element.appendChild(doc.createTextNode(String.valueOf(s.getAverageJobMissDeadlineCount())));
                    resultElement.appendChild(element);
                    
                    element = doc.createElement("pendingTime");
                    element.appendChild(doc.createTextNode(String.valueOf(s.getAveragePendingTime())));
                    resultElement.appendChild(element);
                    
                    element = doc.createElement("responseTime");
                    element.appendChild(doc.createTextNode(String.valueOf(s.getAverageResponseTime())));
                    resultElement.appendChild(element);
                    
                    groupElement.appendChild(resultElement);
                }   
                statisticalResultElement.appendChild(groupElement);
            }
            return doc;
	}
        catch (ParserConfigurationException pce) 
        {
            pce.printStackTrace();
	}
        return null;
    }
}
