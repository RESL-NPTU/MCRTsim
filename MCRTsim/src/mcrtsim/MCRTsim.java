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
    
    static boolean println = false;
    
    public static void print(String s)
    {
        if(println)
        {
            System.out.print(""+s);
        }
    }
    
    public static void println()
    {
        if(println)
        {
            System.out.println();
        }
    }
    
    public static void println(String s)
    {
        if(println)
        {
            System.out.println(""+s);
        }
    }
    
    public static void main(String[] args) 
    {
        UserInterface ui = new UserInterface();
    }
}
