/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcrtsim;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import userInterface.UserInterface;



/**
 *
 * @author ShiuJia
 */
public class MCRTsim {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        System.out.println("" + MCRTsim.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        //IDE : /Users/YC/Documents/LabResearch/MCRTsim/MCRTsimV2/MCRTsim/build/classes/
        //jar : /Users/YC/Documents/LabResearch/MCRTsim/MCRTsimV2/MCRTsim/dist/MCRTsim2.0.jar
        
        System.out.println("" + System.getProperties().getProperty("user.dir"));
        
//        InputStream is = MCRTsim.class.getClass().getResourceAsStream("/AlgorithmName/test");
//        
//        try {
//            String fileContent = IOUtils.toString(is, "UTF-8");
//            System.out.println(fileContent);
//        } catch (IOException ex) {
//            Logger.getLogger(MCRTsim.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        UserInterface ui = new UserInterface();
    }
}
