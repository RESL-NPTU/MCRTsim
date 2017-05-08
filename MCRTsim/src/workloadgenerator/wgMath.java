/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workloadgenerator;

import java.math.BigDecimal;

/**
 *
 * @author YC
 */
public class wgMath 
{
    public wgMath()
    {
        
    }
    
    public static int rangeRandom(int min, int max)
    {
        if(min<=max)
            return ((int)(Math.random()*(max-min+1)+min));
        else
            return 0;
    }
    
    public static int rangeRandom(double min, double max)
    {
        
        if(min<=max)
            return ((int)(Math.random()*(max-min+1)+min));
        else
            return 0;
        
    }
    public static double add(double min,double max){
        BigDecimal m = new BigDecimal(Double.toString(min));
        BigDecimal M = new BigDecimal(Double.toString(max));
        return m.add(M).doubleValue();
    }
    public static double sub(double min,double max){
        BigDecimal m = new BigDecimal(Double.toString(min));
        BigDecimal M = new BigDecimal(Double.toString(max));
        return m.subtract(M).doubleValue();
    }
    public static double mul(double min,double max){
        BigDecimal m = new BigDecimal(Double.toString(min));
        BigDecimal M = new BigDecimal(Double.toString(max));
        return m.multiply(M).doubleValue();
    }
    public static double div(double min,double max,int scale){
        if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
	}
        BigDecimal m = new BigDecimal(Double.toString(min));
        BigDecimal M = new BigDecimal(Double.toString(max));
        return m.divide(M,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
