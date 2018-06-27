/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcrtsim;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import static mcrtsim.Definition.magnificationFormat;

/**
 *
 * @author ShiuJia
 */
public class MCRTsimMath
{
    
    public long Math_lcm(long m, long n)
    {
        return m * n / Math_gcd(m, n);
    }
    
    public long Math_gcd(long m, long n)
    {
        if(n != 0)
        {
            return Math_gcd(n, m % n); 
        }
        else
        {
            return m;
        }
    }
    
    public double changeDecimalFormat(double d)
    {
        DecimalFormat df = new DecimalFormat(magnificationFormat);
        return Double.parseDouble(df.format(d));
    }
    
    public double changeDecimalFormatFor5(double d)
    {
        DecimalFormat df = new DecimalFormat("##.00000");
        return Double.parseDouble(df.format(d));
    }
    
    public static double add(double value1,double value2)
    {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return b1.add(b2).doubleValue();
    }
    
    public static double sub(double value1,double value2)
    {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return b1.subtract(b2).doubleValue();
    }
    
    public static double mul(double value1,double value2)
    {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return b1.multiply(b2).doubleValue();
    }
    
    public static double div(double value1,double value2) 
    {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return b1.divide(b2,10,BigDecimal.ROUND_HALF_UP).doubleValue();    
    }
}
