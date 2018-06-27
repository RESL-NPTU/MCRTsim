/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scriptsetter;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
                    
                    resultElement.setAttribute("maximumUtilization",String.valueOf(s.getAverageMaximumUtilization()));
                    resultElement.setAttribute("actualUtilization",String.valueOf(s.getAverageActualUtilization()));
                    
                    resultElement.setAttribute("maximumCriticalSectionRatio",String.valueOf(s.getAverageMaximumCriticalSectionRatio()));
                    resultElement.setAttribute("actualCriticalSectionRatio",String.valueOf(s.getAverageActualCriticalSectionRatio()));
                    
                    resultElement.setAttribute("partitionAlgorithm",s.getPartitionAlgorithm());
                    resultElement.setAttribute("DVFSMethod",s.getDVFSMethod());
                    resultElement.setAttribute("schedulingAlgorithm",s.getSchedulingAlgorithm());
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
                    
                    element = doc.createElement("completedRatio");
                    element.appendChild(doc.createTextNode(String.valueOf(s.getAverageCompletedRatio())));
                    resultElement.appendChild(element);
                    
                    element = doc.createElement("deadlineMissRatio");
                    element.appendChild(doc.createTextNode(String.valueOf(s.getAverageDeadlineMissRatio())));
                    resultElement.appendChild(element);
                    
                    element = doc.createElement("beBlockedTimeRatio");
                    element.appendChild(doc.createTextNode(String.valueOf(s.getAverageActualBeBlockedTimeRatio())));
                    resultElement.appendChild(element);
                    
                    element = doc.createElement("averagePendingTime");
                    element.appendChild(doc.createTextNode(String.valueOf(s.getAveragePendingTime())));
                    resultElement.appendChild(element);
                    
                    element = doc.createElement("averageResponseTime");
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
