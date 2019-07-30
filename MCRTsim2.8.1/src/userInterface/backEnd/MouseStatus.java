/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userInterface.backEnd;

/**
 *
 * @author ShiuJia
 */
public class MouseStatus
{
    private ViewerStatus sta;
    public  MouseStatus()
    {
       sta = ViewerStatus.IDLE;
    }
    
    public void chengeMouseStatus()
    {
       sta = sta==ViewerStatus.IDLE ? ViewerStatus.EXECUTION : ViewerStatus.IDLE;
    }
    
    public void setMouseStatus(ViewerStatus vs)
    {
       sta = vs;
    }
    
    public ViewerStatus getMouseStatus()
    {
        return sta;
    }
}