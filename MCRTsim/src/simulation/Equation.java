/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

/**
 *
 * @author ShiuJia
 */
public class Equation
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
    
    
}
